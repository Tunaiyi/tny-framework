package com.tny.game.suite.net.spring;

import com.tny.game.common.lifecycle.AppPrepareStart;
import com.tny.game.common.unit.annotation.Unit;
import com.tny.game.net.annotation.Controller;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.command.dispatcher.DefaultMessageDispatcher;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.Map;

@Unit("DefaultMessageDispatcher")
public final class SuiteMessageDispatcher extends DefaultMessageDispatcher implements AppPrepareStart {

    @Resource
    private ApplicationContext applicationContext;

    public SuiteMessageDispatcher(AppContext appContext) {
        super(appContext);
    }

    @Override
    public void prepareStart() {
        super.prepareStart();
        final Map<String, Object> handlerMap = this.applicationContext.getBeansWithAnnotation(Controller.class);
        this.addController(handlerMap.values());
    }

}
