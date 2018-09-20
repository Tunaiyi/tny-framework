package com.tny.game.net.command.dispatcher;


import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;

import java.util.Collection;

@Unit("CommonMessageDispatcher")
public class CommonMessageDispatcher extends AbstractMessageDispatcher {

    public CommonMessageDispatcher(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    @Override
    public void addController(Collection<Object> controller) {
        controller.forEach(this::addController);
    }

}
