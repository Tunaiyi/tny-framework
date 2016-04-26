package com.tny.game.net.dispatcher;

import com.tny.game.LogUtils;
import com.tny.game.worker.Callback;
import com.tny.game.worker.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatchCommand<T> implements Command {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.WORKER);

    private DispatcherCommand<T> command;

    private Callback<T> callback;

    private T result;

    private boolean done;

    //	private ControllerDispatcherInfo dispatcherInfo;

    public DispatchCommand(DispatcherCommand<T> command, Callback<T> callback) {
        this.command = command;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (isDone())
            return;
        try {
            beforeExecute(command);
            this.result = command.invoke();
            if (callback != null) {
                try {
                    callback.callback(true, true, result);
                } catch (Exception e) {
                    LOGGER.error("#World#ActorCommand [{}.{}] 执行回调方法 {} 异常", command.getClass(), command.getName(), callback.getClass(), e);
                }
            }
            afterExecute();
            return;
        } catch (Throwable e) {
            try {
                if (callback != null)
                    callback.callback(false, true, null);
            } catch (Exception callbackException) {
                LOGGER.error("#World#ActorCommand [{}.{}] 执行回调方法 {} 异常", command.getClass(), command.getName(), callback.getClass(), callbackException);
            }
            LOGGER.error("#World#Command [{}.{}] 异常", command.getClass(), command.getName(), e);
            afterException(e);
        } finally {
            done = true;
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void beforeExecute(DispatcherCommand<T> command) {
        CurrentCMD.setCurrent(command.getUserID(), command.getProtocol());
    }

    protected void afterExecute() {
    }

    protected void afterException(Throwable e) {
    }

}
