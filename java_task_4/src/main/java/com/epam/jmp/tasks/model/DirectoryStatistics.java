package com.epam.jmp.tasks.model;

public class DirectoryStatistics {

    private int fileCount;
    private int directoryCount;
    private double totalSize;

    public int getFileCount() {
        return fileCount;
    }

    public void incrementFileCount() {
        fileCount++;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public void incrementDirectoryCount() {
        directoryCount++;
    }

    public double getTotalSize() {
        return totalSize;
    }

    public void addTotalSize(float size) {
        totalSize += size;
    }

    public void incrementDirectoryStatistics(DirectoryStatistics statistics) {
        fileCount += statistics.getFileCount();
        directoryCount += statistics.getDirectoryCount();
        totalSize += statistics.getTotalSize();
    }
}
