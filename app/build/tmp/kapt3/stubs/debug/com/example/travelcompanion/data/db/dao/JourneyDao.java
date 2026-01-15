package com.example.travelcompanion.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\n\bg\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00040\u00032\u0006\u0010\n\u001a\u00020\u0007H\'J\u001c\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001c\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u00040\u00032\u0006\u0010\n\u001a\u00020\u0007H\'J\u0016\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0016\u0010\u0017\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\u0016\u0010\u001d\u001a\u00020\u00142\u0006\u0010\u0011\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0012\u00a8\u0006\u001e"}, d2 = {"Lcom/example/travelcompanion/data/db/dao/JourneyDao;", "", "getJourneysByTrip", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/travelcompanion/data/db/entities/JourneyEntity;", "tripId", "", "getNotesByJourney", "Lcom/example/travelcompanion/data/db/entities/NoteEntity;", "journeyId", "getNotesByTrip", "getPhotosByTrip", "Lcom/example/travelcompanion/data/db/entities/PhotoEntity;", "getPointsByJourney", "Lcom/example/travelcompanion/data/db/entities/PointEntity;", "insertJourney", "journey", "(Lcom/example/travelcompanion/data/db/entities/JourneyEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertNote", "", "note", "(Lcom/example/travelcompanion/data/db/entities/NoteEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPhoto", "photo", "(Lcom/example/travelcompanion/data/db/entities/PhotoEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPoint", "point", "(Lcom/example/travelcompanion/data/db/entities/PointEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateJourney", "app_debug"})
@androidx.room.Dao()
public abstract interface JourneyDao {
    
    @androidx.room.Query(value = "SELECT * FROM journeys WHERE tripId = :tripId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.data.db.entities.JourneyEntity>> getJourneysByTrip(long tripId);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertJourney(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.entities.JourneyEntity journey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateJourney(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.entities.JourneyEntity journey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM points WHERE journeyId = :journeyId ORDER BY timestamp ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.data.db.entities.PointEntity>> getPointsByJourney(long journeyId);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPoint(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.entities.PointEntity point, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM notes WHERE tripId = :tripId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.data.db.entities.NoteEntity>> getNotesByTrip(long tripId);
    
    @androidx.room.Query(value = "SELECT * FROM notes WHERE journeyId = :journeyId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.data.db.entities.NoteEntity>> getNotesByJourney(long journeyId);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertNote(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.entities.NoteEntity note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM photos WHERE tripId = :tripId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.data.db.entities.PhotoEntity>> getPhotosByTrip(long tripId);
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPhoto(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.data.db.entities.PhotoEntity photo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}