package com.tny.game.net.dispatcher;

import com.tny.game.net.dispatcher.command.UserCommand;
import com.tny.game.worker.Callback;
import com.tny.game.worker.DefaultCommandTask;

public class DispatchCommandTask<T> extends DefaultCommandTask<T, UserCommand<T>> {

    //	private ControllerDispatcherInfo dispatcherInfo;

    public DispatchCommandTask(UserCommand<T> command, Callback<T> callback) {
        super(command, callback);
    }

    @Override
    public void beforeExecute(UserCommand<T> command) {
        super.beforeExecute(command);
        //		this.dispatcherInfo = ControllerDispatcherInfo.getCurrent();
        CurrentCMD.setCurrent(command.getUserID(), command.getProtocol());
    }

}
