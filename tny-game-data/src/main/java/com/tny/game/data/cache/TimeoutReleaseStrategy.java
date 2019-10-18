package com.tny.game.data.cache;

public class TimeoutReleaseStrategy<K extends Comparable<K>, O> implements ReleaseStrategy<K, O> {

    private volatile long timeOut;

    private long addLife;

    public TimeoutReleaseStrategy(long addLife) {
        this.addLife = addLife;
        if (this.addLife > 0)
            this.timeOut = System.currentTimeMillis() + this.addLife;
    }

    @Override
    public boolean release(CacheEntry<K, O> entity, long releaseAt) {
        if (this.addLife < 0)
            return false;
        return releaseAt > timeOut;
    }

    @Override
    public void visit() {
        if (this.addLife > 0) {
            this.timeOut = System.currentTimeMillis() + addLife;
        }
    }

}
