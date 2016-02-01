package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import com.tny.game.worker.command.CommandTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCommandTask<T, C extends Command<T>> implements CommandTask<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    private C command;

    private Callback<T> callback;

    public DefaultCommandTask(C command, Callback<T> callback) {
        super();
        this.command = command;
        this.callback = callback;
    }

    @Override
    public Command<T> getCommand() {
        return command;
    }

    @Override
    public void run() {
        if (command.isCompleted())
            return;
        try {
            beforeExecute(command);
            T result = command.execute();
            if (callback != null) {
                try {
                    callback.callback(true, true, result);
                } catch (Exception e) {
                    LOGGER.error("#World#ActorCommand [{}.{}] 执行回调方法 {} 异常", new Object[]{command.getClass(), command.getName(), callback.getClass(), e});
                }
            }
            afterExecute();
        } catch (Throwable e) {
            try {
                callback.callback(false, true, null);
            } catch (Exception callbackException) {
                LOGGER.error("#World#ActorCommand [{}.{}] 执行回调方法 {} 异常", new Object[]{command.getClass(), command.getName(), callback.getClass(), callbackException});
            }
            LOGGER.error("#World#Command [{}.{}] 异常", new Object[]{command.getClass(), command.getName(), e});
            afterException(e);
        }
    }

    protected void afterExecute() {
    }

    protected void afterException(Throwable e) {
    }

    protected void beforeExecute(C command) {
    }

    @Override
    public void fail(boolean excuted) {
        try {
            callback.callback(false, excuted, null);
        } catch (Exception callbackException) {
            LOGGER.error("#World#ActorCommand [{}.{}] 执行失败时回调方法 {} 异常", new Object[]{command.getClass(), command.getName(), callback.getClass(), callbackException});
        }
    }
}
