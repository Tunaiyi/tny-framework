package com.tny.game.common.worker;

/**
 * Created by Kun Yang on 16/4/29.
 */
public interface CommandBoxProcessor {

    boolean isWorking();

    // TODO 改为非注册试, 使用唤醒进行提交
    boolean register(CommandBox<?> commandBox);

    // TODO 改为非注册试, 使用唤醒进行提交
    boolean unregister(CommandBox<?> commandBox);

}
