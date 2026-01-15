package com.example.travelcompanion.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ&\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\u0017\u001a\u00020\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/travelcompanion/utils/GeofenceHelper;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext", "()Landroid/content/Context;", "geofencingClient", "Lcom/google/android/gms/location/GeofencingClient;", "addGeofence", "", "geofence", "Lcom/google/android/gms/location/Geofence;", "createGeofence", "id", "", "lat", "", "lng", "radius", "", "createGeofencingRequest", "Lcom/google/android/gms/location/GeofencingRequest;", "getGeofencePendingIntent", "Landroid/app/PendingIntent;", "app_debug"})
public final class GeofenceHelper {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.android.gms.location.GeofencingClient geofencingClient = null;
    
    public GeofenceHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.Context getContext() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.location.GeofencingRequest createGeofencingRequest(@org.jetbrains.annotations.NotNull()
    com.google.android.gms.location.Geofence geofence) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.google.android.gms.location.Geofence createGeofence(@org.jetbrains.annotations.NotNull()
    java.lang.String id, double lat, double lng, float radius) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.app.PendingIntent getGeofencePendingIntent() {
        return null;
    }
    
    public final void addGeofence(@org.jetbrains.annotations.NotNull()
    com.google.android.gms.location.Geofence geofence) {
    }
}