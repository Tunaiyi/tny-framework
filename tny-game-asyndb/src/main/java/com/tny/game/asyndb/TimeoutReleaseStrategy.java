package com.tny.game.asyndb;

public class TimeoutReleaseStrategy implements ReleaseStrategy {

    protected volatile long timeOut;

    protected long addLife;

    public TimeoutReleaseStrategy(long addLife) {
        this.addLife = addLife;
        if (this.addLife > 0)
            this.timeOut = System.currentTimeMillis() + this.addLife;
    }

    @Override
    public boolean release(AsyncDBEntity entity, long releaseAt) {
        if (this.addLife < 0)
            return false;
        return releaseAt > timeOut;
    }

    @Override
    public void update() {
        if (this.addLife > 0)
            this.timeOut = System.currentTimeMillis() + addLife;
    }

}
