package com.travelcompanion.ui.map

import android.content.Context
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Polygon

object MapManager {
    fun drawPolyline(map: MapView, points: List<GeoPoint>, color: Int = android.graphics.Color.BLUE, width: Float = 8f) {
        val polyline = Polyline().apply {
            outlinePaint.color = color
            outlinePaint.strokeWidth = width
            this.points.addAll(points)
        }
        map.overlays.add(polyline)
    }

    fun addMarker(map: MapView, point: GeoPoint, title: String? = null) {
        val marker = Marker(map)
        marker.position = point
        marker.title = title
        map.overlays.add(marker)
    }

    fun addGeofenceCircle(map: MapView, center: GeoPoint, radius: Double, fillColor: Int = android.graphics.Color.argb(50, 255, 0, 0), strokeColor: Int = android.graphics.Color.RED, strokeWidth: Float = 3f) {
        val circle = Polygon().apply {
            points = Polygon.pointsAsCircle(center, radius)
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
