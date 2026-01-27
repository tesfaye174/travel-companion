package com.travelcompanion.ui.map

import android.content.Context
import android.graphics.Point
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Polygon
import com.travelcompanion.R

object MapManager {
    fun drawPolyline(map: MapView, points: List<GeoPoint>, color: Int = android.graphics.Color.BLUE, width: Float = 8f) {
        val polyline = Polyline().apply {
            outlinePaint.color = color
            outlinePaint.strokeWidth = width
        }
        // setPoints avoids using the deprecated getter
        polyline.setPoints(ArrayList(points))
        map.overlays.add(polyline)
    }

    fun addMarker(map: MapView, point: GeoPoint, title: String? = null) {
        // Avoid marker overlap by checking existing markers' screen positions
        val projection = map.projection
        val baseScreen = Point()
        projection.toPixels(point, baseScreen)

        val existing = map.overlays.filterIsInstance<Marker>()
        val minPixelDistance = 64 // minimum distance in pixels between markers

        var placedScreen = Point(baseScreen.x, baseScreen.y)
        var attempt = 0
        var radius = minPixelDistance

        fun tooClose(p: Point): Boolean {
            return existing.any { other ->
                val op = Point()
                projection.toPixels(other.position, op)
                val dx = (op.x - p.x).toDouble()
                val dy = (op.y - p.y).toDouble()
                Math.hypot(dx, dy) < minPixelDistance
            }
        }

        while (tooClose(placedScreen) && attempt < 12) {
            val angle = Math.toRadians((attempt * 30).toDouble())
            val ox = (radius * Math.cos(angle)).toInt()
            val oy = (radius * Math.sin(angle)).toInt()
            placedScreen = Point(baseScreen.x + ox, baseScreen.y + oy)
            radius += minPixelDistance / 2
            attempt++
        }

        val ip = projection.fromPixels(placedScreen.x, placedScreen.y)
        val finalGeo = if (ip is GeoPoint) ip else GeoPoint(ip.latitude, ip.longitude)
        val marker = Marker(map)
        marker.position = finalGeo
        marker.title = title

        // Use a simple, visible icon and anchor it so the tip points to the location
        val icon = ContextCompat.getDrawable(map.context, R.drawable.ic_location)
        if (icon != null) marker.icon = icon
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        map.overlays.add(marker)
    }

    fun addGeofenceCircle(map: MapView, center: GeoPoint, radius: Double, fillColor: Int = android.graphics.Color.argb(50, 255, 0, 0), strokeColor: Int = android.graphics.Color.RED, strokeWidth: Float = 3f) {
        val circle = Polygon().apply {
            // use setPoints to avoid deprecated setter/getter
            setPoints(Polygon.pointsAsCircle(center, radius))
            fillPaint.color = fillColor
            outlinePaint.strokeWidth = strokeWidth
            outlinePaint.color = strokeColor
        }
        map.overlays.add(circle)
    }

    fun centerMap(map: MapView, point: GeoPoint, zoom: Double = 12.0) {
        map.controller.setZoom(zoom)
        map.controller.setCenter(point)
    }

    fun clearPolylines(map: MapView) {
        map.overlays.removeAll { it is Polyline }
    }

    fun clearMarkers(map: MapView) {
        map.overlays.removeAll { it is Marker }
    }

    fun clearGeofences(map: MapView) {
        map.overlays.removeAll { it is Polygon }
    }
}
