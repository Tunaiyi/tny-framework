package com.tny.game.net.common.dispatcher;


import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.annotation.Unit;

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
