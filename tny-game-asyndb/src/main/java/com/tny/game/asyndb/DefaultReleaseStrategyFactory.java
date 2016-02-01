package com.tny.game.asyndb;

public class DefaultReleaseStrategyFactory implements ReleaseStrategyFactory {

    private final long addLife;

    public DefaultReleaseStrategyFactory() {
        this.addLife = 1000 * 60 * 60;
    }

    ;

    public DefaultReleaseStrategyFactory(long life) {
        this.addLife = life;
    }

    ;

    @Override
    public ReleaseStrategy createStrategy(Object object) {
        return new TimeReleaseStrategy();
    }

    private class TimeReleaseStrategy implements ReleaseStrategy {

        private volatile long timeOut = System.currentTimeMillis() + addLife;

        @Override
        public boolean release(AsyncDBEntity entity) {
            return System.currentTimeMillis() > timeOut;
        }

        @Override
        public void update() {
            timeOut += addLife;
        }

    }

}
