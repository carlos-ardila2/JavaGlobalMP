package com.epam.jmp;

public interface PerformanceMeasurable {
    void startPerformanceMeasurement();
    void stopPerformanceMeasurement();
    long getOperationsCount();
    void addToOperationsCount(long numberOfOperations);
}
