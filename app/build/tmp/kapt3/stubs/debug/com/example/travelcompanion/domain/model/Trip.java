package com.example.travelcompanion.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u001d\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BO\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010!\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0013J\t\u0010\"\u001a\u00020\tH\u00c6\u0003J\t\u0010#\u001a\u00020\u000bH\u00c6\u0003J\t\u0010$\u001a\u00020\rH\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J`\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010\'J\u0013\u0010(\u001a\u00020\u000b2\b\u0010)\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010*\u001a\u00020+H\u00d6\u0001J\t\u0010,\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u0014\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0016R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d\u00a8\u0006-"}, d2 = {"Lcom/example/travelcompanion/domain/model/Trip;", "", "id", "", "destination", "", "startDate", "endDate", "type", "Lcom/example/travelcompanion/domain/model/TripType;", "isCompleted", "", "totalDistance", "", "totalTime", "(JLjava/lang/String;JLjava/lang/Long;Lcom/example/travelcompanion/domain/model/TripType;ZDJ)V", "getDestination", "()Ljava/lang/String;", "getEndDate", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getId", "()J", "()Z", "getStartDate", "getTotalDistance", "()D", "getTotalTime", "getType", "()Lcom/example/travelcompanion/domain/model/TripType;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(JLjava/lang/String;JLjava/lang/Long;Lcom/example/travelcompanion/domain/model/TripType;ZDJ)Lcom/example/travelcompanion/domain/model/Trip;", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class Trip {
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String destination = null;
    private final long startDate = 0L;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long endDate = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.travelcompanion.domain.model.TripType type = null;
    private final boolean isCompleted = false;
    private final double totalDistance = 0.0;
    private final long totalTime = 0L;
    
    public Trip(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String destination, long startDate, @org.jetbrains.annotations.Nullable()
    java.lang.Long endDate, @org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.TripType type, boolean isCompleted, double totalDistance, long totalTime) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDestination() {
        return null;
    }
    
    public final long getStartDate() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getEndDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.travelcompanion.domain.model.TripType getType() {
        return null;
    }
    
    public final boolean isCompleted() {
        return false;
    }
    
    public final double getTotalDistance() {
        return 0.0;
    }
    
    public final long getTotalTime() {
        return 0L;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    public final long component3() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.travelcompanion.domain.model.TripType component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    public final double component7() {
        return 0.0;
    }
    
    public final long component8() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.travelcompanion.domain.model.Trip copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String destination, long startDate, @org.jetbrains.annotations.Nullable()
    java.lang.Long endDate, @org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.TripType type, boolean isCompleted, double totalDistance, long totalTime) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}