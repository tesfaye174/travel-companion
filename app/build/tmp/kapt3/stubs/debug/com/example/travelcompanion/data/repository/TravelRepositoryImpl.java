package com.example.travelcompanion.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0096@\u00a2\u0006\u0002\u0010\u000bJ\u0014\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u000e0\rH\u0016J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000e0\r2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u001c\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u000e0\r2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u001c\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u000e0\r2\u0006\u0010\u0017\u001a\u00020\u0012H\u0016J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0096@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010\u001cJ\u0016\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\u0014H\u0096@\u00a2\u0006\u0002\u0010\u001fJ\u0016\u0010 \u001a\u00020\b2\u0006\u0010!\u001a\u00020\u0016H\u0096@\u00a2\u0006\u0002\u0010\"J\u0016\u0010#\u001a\u00020\u00122\u0006\u0010\t\u001a\u00020\nH\u0096@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010$\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0096@\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/example/travelcompanion/data/repository/TravelRepositoryImpl;", "Lcom/example/travelcompanion/domain/repository/TravelRepository;", "tripDao", "Lcom/example/travelcompanion/data/db/dao/TripDao;", "journeyDao", "Lcom/example/travelcompanion/data/db/dao/JourneyDao;", "(Lcom/example/travelcompanion/data/db/dao/TripDao;Lcom/example/travelcompanion/data/db/dao/JourneyDao;)V", "deleteTrip", "", "trip", "Lcom/example/travelcompanion/domain/model/Trip;", "(Lcom/example/travelcompanion/domain/model/Trip;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTrips", "Lkotlinx/coroutines/flow/Flow;", "", "getJourneysByTrip", "Lcom/example/travelcompanion/domain/model/Journey;", "tripId", "", "getPhotosByTrip", "Lcom/example/travelcompanion/domain/model/Photo;", "getPointsByJourney", "Lcom/example/travelcompanion/domain/model/Point;", "journeyId", "getTripById", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertJourney", "journey", "(Lcom/example/travelcompanion/domain/model/Journey;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPhoto", "photo", "(Lcom/example/travelcompanion/domain/model/Photo;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPoint", "point", "(Lcom/example/travelcompanion/domain/model/Point;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTrip", "updateTrip", "app_debug"})
public final class TravelRepositoryImpl implements com.example.travelcompanion.domain.repository.TravelRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.travelcompanion.data.db.dao.TripDao tripDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.travelcompanion.data.db.dao.JourneyDao journeyDao = null;
    
    public TravelRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.dao.TripDao tripDao, @org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.dao.JourneyDao journeyDao) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Trip>> getAllTrips() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getTripById(long tripId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.travelcompanion.domain.model.Trip> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object deleteTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Journey>> getJourneysByTrip(long tripId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertJourney(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Journey journey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Point>> getPointsByJourney(long journeyId) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertPoint(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Point point, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object insertPhoto(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Photo photo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Photo>> getPhotosByTrip(long tripId) {
        return null;
    }
}