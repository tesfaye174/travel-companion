package com.example.travelcompanion.data.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\b"}, d2 = {"Lcom/example/travelcompanion/data/db/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "journeyDao", "Lcom/example/travelcompanion/data/db/dao/JourneyDao;", "tripDao", "Lcom/example/travelcompanion/data/db/dao/TripDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.example.travelcompanion.data.db.entities.TripEntity.class, com.example.travelcompanion.data.db.entities.JourneyEntity.class, com.example.travelcompanion.data.db.entities.PointEntity.class, com.example.travelcompanion.data.db.entities.NoteEntity.class, com.example.travelcompanion.data.db.entities.PhotoEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.example.travelcompanion.data.db.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.travelcompanion.data.db.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.travelcompanion.data.db.dao.TripDao tripDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.example.travelcompanion.data.db.dao.JourneyDao journeyDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/travelcompanion/data/db/AppDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/example/travelcompanion/data/db/AppDatabase;", "getDatabase", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.travelcompanion.data.db.AppDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}