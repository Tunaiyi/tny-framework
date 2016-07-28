package com.tny.game.asyndb;

public class DefaultReleaseStrategyFactory implements ReleaseStrategyFactory {

    private final long defaultLifeTime;

    public DefaultReleaseStrategyFactory() {
        this.defaultLifeTime = 1000 * 60 * 60;
    }

    public DefaultReleaseStrategyFactory(long life) {
        this.defaultLifeTime = life;
    }

    @Override
    public ReleaseStrategy createStrategy(Object object, long addLife) {
        return new TimeoutReleaseStrategy(addLife == Long.MIN_VALUE ? defaultLifeTime : addLife);
    }

}
