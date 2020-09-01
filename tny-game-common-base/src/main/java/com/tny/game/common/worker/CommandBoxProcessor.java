package com.tny.game.common.worker;

/**
 * Created by Kun Yang on 16/4/29.
 */
public interface CommandBoxProcessor {

    boolean isWorking();

    boolean register(CommandBox<?> commandBox);

    boolean unregister(CommandBox<?> commandBox);

}
