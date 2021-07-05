package com.tny.game.starter.net.netty4.spring;

import com.tny.game.common.lifecycle.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.endpoint.*;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.Map;

public final class SuiteMessageDispatcher extends DefaultMessageDispatcher implements AppPrepareStart {

    @Resource
    private ApplicationContext applicationContext;

    public SuiteMessageDispatcher(NetAppContext appContext, EndpointKeeperManager endpointKeeperManager) {
        super(appContext, endpointKeeperManager);
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        final Map<String, Object> handlerMap = this.applicationContext.getBeansWithAnnotation(Controller.class);
        this.addController(handlerMap.values());
    }

}
