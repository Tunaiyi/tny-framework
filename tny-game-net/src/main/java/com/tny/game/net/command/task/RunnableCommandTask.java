package com.tny.game.net.command.task;

import com.tny.game.common.worker.command.*;
import com.tny.game.net.command.dispatcher.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/14 2:33 下午
 */
public class RunnableCommandTask extends RunnableCommand implements CommandTask {

    public RunnableCommandTask(Runnable runnable) {
        super(runnable);
    }

    @Override
    public Command createCommand() {
        return this;
    }

}
