package com.epam.jmp.stats;

import java.util.Objects;

public final class Stats {
    private double sum;
    private double avg;
    private double max;
    private double min;

    public Stats(double sum, double avg, double max, double min) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
    }

    public Stats() {
        this(0, 0, 0, 0);
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Stats) obj;
        return Double.doubleToLongBits(this.sum) == Double.doubleToLongBits(that.sum) &&
                Double.doubleToLongBits(this.avg) == Double.doubleToLongBits(that.avg) &&
                Double.doubleToLongBits(this.max) == Double.doubleToLongBits(that.max) &&
                Double.doubleToLongBits(this.min) == Double.doubleToLongBits(that.min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum, avg, max, min);
    }

    @Override
    public String toString() {
        return "Stats[" +
                "sum=" + sum + ", " +
                "avg=" + avg + ", " +
                "max=" + max + ", " +
                "min=" + min + ']';
    }

}
