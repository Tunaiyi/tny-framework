package com.tny.game.suite.cache;

public class UIDRange {

    private Long max;

    private Long min;

    public UIDRange() {
    }

    public UIDRange(long min, long max) {
        this.max = max;
        this.min = min;
    }

    public long getMax() {
        return this.max == null ? 0L : this.max;
    }

    public long getMin() {
        return this.min == null ? 0L : this.max;
    }

    public long size() {
        if (this.max == null && this.min == null)
            return 0;
        return (this.max - this.min) + 1;
    }

    public boolean inRange(int value) {
        return this.min <= value && value <= this.max;
    }

    protected void setMax(Long max) {
        this.max = max;
    }

    protected void setMin(Long min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return "UIDRange [max=" + this.max + ", min=" + this.min + "]";
    }

}
