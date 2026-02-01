package com.travelcompanion.ui.map

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
            setPoints(points)
        }
        map.overlays.add(polyline)
    }

    fun addMarker(map: MapView, point: GeoPoint, title: String? = null) {
        val marker = Marker(map)
        marker.position = point
        marker.title = title
        map.overlays.add(marker)
    }

    fun addGeofenceCircle(
        map: MapView,
        center: GeoPoint,
        radius: Double,
        fillColor: Int = android.graphics.Color.argb(50, 255, 0, 0),
        strokeColor: Int = android.graphics.Color.RED,
        strokeWidth: Float = 3f
    ) {
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

    fun clearPolylines(map: MapView) = clearOverlaysOfType<Polyline>(map)
    fun clearMarkers(map: MapView) = clearOverlaysOfType<Marker>(map)
    fun clearGeofences(map: MapView) = clearOverlaysOfType<Polygon>(map)

    private inline fun <reified T> clearOverlaysOfType(map: MapView) {
        map.overlays.removeAll { it is T }
    }

    fun drawHeatmap(map: MapView, points: List<GeoPoint>, radius: Double = 120.0, intensity: Int = 50) {
        // Evita sovrapposizioni inutili: raggruppa per coordinate uniche
        points.distinctBy { Pair(it.latitude, it.longitude) }.forEach { point ->
            addGeofenceCircle(
                map,
                center = point,
                radius = radius,
                fillColor = android.graphics.Color.argb(intensity, 0, 150, 255),
                strokeColor = android.graphics.Color.TRANSPARENT,
                strokeWidth = 0f
            )
        }
    }
}
