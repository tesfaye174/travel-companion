package com.example.travelcompanion.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0014\u0010\t\u001a\u00020\b2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bJ\u0014\u0010\r\u001a\u00020\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a8\u0006\u000e"}, d2 = {"Lcom/example/travelcompanion/utils/PredictionEngine;", "", "()V", "getSuggestion", "", "predictedCount", "", "predictedDistance", "", "predictNextMonthDistance", "history", "", "Lcom/example/travelcompanion/ui/stats/MonthlyStat;", "predictNextMonthTrips", "app_debug"})
public final class PredictionEngine {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.travelcompanion.utils.PredictionEngine INSTANCE = null;
    
    private PredictionEngine() {
        super();
    }
    
    public final int predictNextMonthTrips(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.travelcompanion.ui.stats.MonthlyStat> history) {
        return 0;
    }
    
    public final double predictNextMonthDistance(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.travelcompanion.ui.stats.MonthlyStat> history) {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSuggestion(int predictedCount, double predictedDistance) {
        return null;
    }
}