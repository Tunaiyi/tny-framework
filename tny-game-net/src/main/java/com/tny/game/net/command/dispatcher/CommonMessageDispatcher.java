package com.tny.game.net.command.dispatcher;


import com.tny.game.suite.app.AppConfiguration;
import com.tny.game.suite.app.annotation.Unit;

import java.util.Collection;

@Unit("CommonMessageDispatcher")
public class CommonMessageDispatcher extends AbstractMessageDispatcher {

    public CommonMessageDispatcher(AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    public void addController(Collection<Object> controller) {
        controller.forEach(c -> addController(c));
    }

}
