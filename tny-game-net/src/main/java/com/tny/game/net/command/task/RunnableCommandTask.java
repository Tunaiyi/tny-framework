package com.tny.game.net.command.task;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.dispatcher.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:33 下午
 */
public class RunnableCommandTask implements CommandTask {

    private final Runnable runnable;

    public RunnableCommandTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public Command createCommand() {
        return new RunnableCommand(this.runnable);
    }

}
