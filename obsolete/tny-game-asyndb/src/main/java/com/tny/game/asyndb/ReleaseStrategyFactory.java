package com.tny.game.asyndb;

public interface ReleaseStrategyFactory {

    ReleaseStrategy createStrategy(Object object, long addLife);

}
