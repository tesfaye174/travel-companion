package com.example.travelcompanion.ui.stats;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r\u00a8\u0006\u0010"}, d2 = {"Lcom/example/travelcompanion/ui/stats/StatsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/travelcompanion/domain/repository/TravelRepository;", "(Lcom/example/travelcompanion/domain/repository/TravelRepository;)V", "_stats", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/travelcompanion/ui/stats/MonthlyStat;", "forecast", "Landroidx/lifecycle/LiveData;", "Lcom/example/travelcompanion/ui/stats/Forecast;", "getForecast", "()Landroidx/lifecycle/LiveData;", "tripStats", "getTripStats", "app_debug"})
public final class StatsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.travelcompanion.domain.repository.TravelRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.example.travelcompanion.ui.stats.MonthlyStat>> _stats = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.travelcompanion.ui.stats.MonthlyStat>> tripStats = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.example.travelcompanion.ui.stats.Forecast> forecast = null;
    
    public StatsViewModel(@org.jetbrains.annotations.NotNull()
    com.example.travelcompanion.domain.repository.TravelRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.travelcompanion.ui.stats.MonthlyStat>> getTripStats() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.travelcompanion.ui.stats.Forecast> getForecast() {
        return null;
    }
}