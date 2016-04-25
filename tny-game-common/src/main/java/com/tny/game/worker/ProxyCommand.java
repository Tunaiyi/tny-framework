package com.tny.game.worker;

import com.tny.game.LogUtils;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyCommand<T, C extends Command<T>> implements Command<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    private C command;

    private Callback<T> callback;

    private T result;

    public ProxyCommand(C command, Callback<T> callback) {
        super();
        this.command = command;
        this.callback = callback;
    }

    protected void afterExecute() {
    }

    protected void afterException(Throwable e) {
    }

    protected void beforeExecute(C command) {
    }

//    @Override
//    public void fail(boolean executed) {
//        try {
//            callback.callback(false, executed, null);
//        } catch (Exception callbackException) {
//            LOGGER.error("#World#ActorCommand [{}.{}] 执行失败时回调方法 {} 异常", new Object[]{command.getClass(), command.getName(), callback.getClass(), callbackException});
//        }
//    }

    @Override
    public T execute() {
        if (command.isCompleted())
            return result;
        try {
            beforeExecute(command);
            this.result = command.execute();
            if (callback != null) {
                try {
                    callback.callback(true, true, result);
                } catch (Exception e) {
                    LOGGER.error("#World#ActorCommand [{}.{}] 执行回调方法 {} 异常", command.getClass(), command.getName(), callback.getClass(), e);
                }
            }
            afterExecute();
            return result;
        } catch (Throwable e) {
            try {
                if (callback != null)
                    callback.callback(false, true, null);
            } catch (Exception callbackException) {
                LOGGER.error("#World#ActorCommand [{}.{}] 执行回调方法 {} 异常", command.getClass(), command.getName(), callback.getClass(), callbackException);
            }
            LOGGER.error("#World#Command [{}.{}] 异常", command.getClass(), command.getName(), e);
            afterException(e);
        }
        return null;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }
}
