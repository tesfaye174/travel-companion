package com.example.travelcompanion.ui.trips;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rJ\u000e\u0010\u0019\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rJ\u000e\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0007J\u0010\u0010\u001d\u001a\u00020\u001b2\b\u0010\u001e\u001a\u0004\u0018\u00010\tR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/example/travelcompanion/ui/trips/TripViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/travelcompanion/domain/repository/TravelRepository;", "(Lcom/example/travelcompanion/domain/repository/TravelRepository;)V", "_filterDestination", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_filterType", "Lcom/example/travelcompanion/domain/model/TripType;", "allTrips", "Landroidx/lifecycle/LiveData;", "", "Lcom/example/travelcompanion/domain/model/Trip;", "getAllTrips", "()Landroidx/lifecycle/LiveData;", "filterDestination", "Lkotlinx/coroutines/flow/StateFlow;", "getFilterDestination", "()Lkotlinx/coroutines/flow/StateFlow;", "filterType", "getFilterType", "deleteTrip", "Lkotlinx/coroutines/Job;", "trip", "insertTrip", "setFilterDestination", "", "filter", "setFilterType", "type", "app_debug"})
public final class TripViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.travelcompanion.domain.repository.TravelRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _filterDestination = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> filterDestination = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.travelcompanion.domain.model.TripType> _filterType = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.travelcompanion.domain.model.TripType> filterType = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.travelcompanion.domain.model.Trip>> allTrips = null;
    
    public TripViewModel(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.repository.TravelRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getFilterDestination() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.travelcompanion.domain.model.TripType> getFilterType() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.travelcompanion.domain.model.Trip>> getAllTrips() {
        return null;
    }
    
    public final void setFilterDestination(@org.jetbrains.annotations.NotNull()
    java.lang.String filter) {
    }
    
    public final void setFilterType(@org.jetbrains.annotations.Nullable()
    com.example.travelcompanion.domain.model.TripType type) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteTrip(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.model.Trip trip) {
        return null;
    }
}