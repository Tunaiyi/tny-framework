package com.tny.game.net.command;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.worker.Callback;
import com.tny.game.worker.command.BaseCommand;

/**
 * Created by Kun Yang on 16/6/15.
 */
public abstract class BaseDispatchCommand<T> extends BaseCommand implements DispatchCommand<T> {

    private Callback<T> callback;

    public BaseDispatchCommand(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setCallback(Callback<?> callback) {
        this.callback = (Callback<T>) callback;
    }

    @Override
    protected void action() {
        try {
            T result = invoke();
            if (callback != null)
                callback.callback(ResultCode.SUCCESS, result, null);
        } catch (Throwable e) {
            if (callback != null)
                callback.callback(CoreResponseCode.EXECUTE_EXCEPTION, null, e);
        }
    }
}
