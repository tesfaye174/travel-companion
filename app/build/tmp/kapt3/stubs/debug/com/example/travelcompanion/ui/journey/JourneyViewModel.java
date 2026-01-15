package com.example.travelcompanion.ui.journey;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001a\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u00112\u0006\u0010\u0014\u001a\u00020\u0007J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018J\u000e\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u001a\u001a\u00020\u0007J\u0006\u0010\u001b\u001a\u00020\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0019\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/example/travelcompanion/ui/journey/JourneyViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/travelcompanion/domain/repository/TravelRepository;", "(Lcom/example/travelcompanion/domain/repository/TravelRepository;)V", "_activeTripId", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_currentJourney", "Lcom/example/travelcompanion/domain/model/Journey;", "activeTripId", "Lkotlinx/coroutines/flow/StateFlow;", "getActiveTripId", "()Lkotlinx/coroutines/flow/StateFlow;", "currentJourney", "getCurrentJourney", "getPointsForJourney", "Landroidx/lifecycle/LiveData;", "", "Lcom/example/travelcompanion/domain/model/Point;", "journeyId", "insertPhoto", "Lkotlinx/coroutines/Job;", "photo", "Lcom/example/travelcompanion/domain/model/Photo;", "startJourney", "tripId", "stopJourney", "app_debug"})
public final class JourneyViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.travelcompanion.domain.repository.TravelRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.travelcompanion.domain.model.Journey> _currentJourney = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.travelcompanion.domain.model.Journey> currentJourney = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _activeTripId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> activeTripId = null;
    
    public JourneyViewModel(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.repository.TravelRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.travelcompanion.domain.model.Journey> getCurrentJourney() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getActiveTripId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job startJourney(long tripId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job stopJourney() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.travelcompanion.domain.model.Point>> getPointsForJourney(long journeyId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertPhoto(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Photo photo) {
        return null;
    }
}