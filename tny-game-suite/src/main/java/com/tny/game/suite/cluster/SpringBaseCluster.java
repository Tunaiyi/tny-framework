package com.tny.game.suite.cluster;

import com.google.common.collect.*;
import com.tny.game.common.lifecycle.*;
import org.springframework.beans.*;
import org.springframework.context.*;

import java.util.*;

/**
 * Created by Kun Yang on 2017/8/3.
 */
public class SpringBaseCluster extends BaseCluster implements ServerPrepareStart, ServerPostStart, ApplicationContextAware {

    private List<ZKMonitorInitHandler> initHandlers = ImmutableList.of();

    private ApplicationContext applicationContext;

    public SpringBaseCluster(String... monitorAppTypes) {
        super(false, Arrays.asList(monitorAppTypes));
    }

    public SpringBaseCluster(Collection<String> monitorAppTypes) {
        super(false, monitorAppTypes);
    }

    public SpringBaseCluster(boolean monitorAllServices) {
        super(monitorAllServices, ImmutableList.of());
    }

    protected SpringBaseCluster(boolean monitorAllServices, Collection<String> monitorAppTypes) {
        super(monitorAllServices, monitorAppTypes);
    }

    @Override
    public PostStarter getPostStarter() {
        return PostStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_1);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_5);
    }

    @Override
    public void prepareStart() throws Exception {
        initHandlers = ImmutableList.copyOf(applicationContext.getBeansOfType(ZKMonitorInitHandler.class).values());
        this.monitor();
    }

    @Override
    public void postStart() {
        this.register();
    }

    @Override
    protected List<ZKMonitorInitHandler> initHandlers() {
        return initHandlers;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
