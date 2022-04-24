package com.tny.game.data.cache;

public class TimeoutReleaseStrategy<K extends Comparable<K>, O> implements ReleaseStrategy<K, O> {

    private volatile long timeout;

    private final long life;

    public TimeoutReleaseStrategy(long lifetime) {
        this.life = lifetime;
        if (this.life > 0) {
            this.timeout = System.currentTimeMillis() + this.life;
        }
    }

    @Override
    public boolean release(CacheEntry<K, O> entity, long releaseAt) {
        if (this.life < 0) {
            return false;
        }
        return releaseAt > timeout;
    }

    @Override
    public void visit() {
        if (this.life > 0) {
            this.timeout = System.currentTimeMillis() + life;
        }
    }

}
