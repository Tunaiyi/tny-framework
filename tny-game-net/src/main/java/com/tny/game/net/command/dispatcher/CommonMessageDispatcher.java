package com.tny.game.net.command.dispatcher;


import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;

@Unit("CommonMessageDispatcher")
public class CommonMessageDispatcher extends AbstractMessageDispatcher {

    public CommonMessageDispatcher(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

}
