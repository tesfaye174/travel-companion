package com.example.travelcompanion.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH&J\u001c\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\t0\b2\u0006\u0010\f\u001a\u00020\rH&J\u001c\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\t0\b2\u0006\u0010\u0010\u001a\u00020\rH&J\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\t0\b2\u0006\u0010\f\u001a\u00020\rH&J\u001c\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\t0\b2\u0006\u0010\f\u001a\u00020\rH&J\u001c\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\t0\b2\u0006\u0010\u0010\u001a\u00020\rH&J\u0018\u0010\u0016\u001a\u0004\u0018\u00010\u00052\u0006\u0010\f\u001a\u00020\rH\u00a6@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u000fH\u00a6@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020\u0013H\u00a6@\u00a2\u0006\u0002\u0010 J\u0016\u0010!\u001a\u00020\u00032\u0006\u0010\"\u001a\u00020\u0015H\u00a6@\u00a2\u0006\u0002\u0010#J\u0016\u0010$\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010%\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010&\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\'"}, d2 = {"Lcom/example/travelcompanion/domain/repository/TravelRepository;", "", "deleteTrip", "", "trip", "Lcom/example/travelcompanion/domain/model/Trip;", "(Lcom/example/travelcompanion/domain/model/Trip;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTrips", "Lkotlinx/coroutines/flow/Flow;", "", "getJourneysByTrip", "Lcom/example/travelcompanion/domain/model/Journey;", "tripId", "", "getNotesByJourney", "Lcom/example/travelcompanion/domain/model/Note;", "journeyId", "getNotesByTrip", "getPhotosByTrip", "Lcom/example/travelcompanion/domain/model/Photo;", "getPointsByJourney", "Lcom/example/travelcompanion/domain/model/Point;", "getTripById", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertJourney", "journey", "(Lcom/example/travelcompanion/domain/model/Journey;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertNote", "note", "(Lcom/example/travelcompanion/domain/model/Note;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPhoto", "photo", "(Lcom/example/travelcompanion/domain/model/Photo;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPoint", "point", "(Lcom/example/travelcompanion/domain/model/Point;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTrip", "updateJourney", "updateTrip", "app_debug"})
public abstract interface TravelRepository {
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Trip>> getAllTrips();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTripById(long tripId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.travelcompanion.domain.model.Trip> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Journey>> getJourneysByTrip(long tripId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertJourney(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Journey journey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateJourney(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Journey journey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Point>> getPointsByJourney(long journeyId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPoint(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Point point, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPhoto(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Photo photo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Photo>> getPhotosByTrip(long tripId);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertNote(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Note note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Note>> getNotesByTrip(long tripId);
    
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.domain.model.Note>> getNotesByJourney(long journeyId);
}