package com.tny.game.suite.cluster;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PostStarter;
import com.tny.game.common.lifecycle.ServerPostStart;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.List;

/**
 * Created by Kun Yang on 2017/8/3.
 */
public class SpringBaseCluster extends BaseCluster implements ServerPostStart, ApplicationContextAware {

    private List<ZKMonitorInitHandler> initHandlers = ImmutableList.of();

    private ApplicationContext applicationContext;

    public SpringBaseCluster(String... monitorServerTypes) {
        super(monitorServerTypes);
    }

    public SpringBaseCluster(Collection<String> monitorServerTypes) {
        super(monitorServerTypes);
    }

    @Override
    public PostStarter getPostStarter() {
        return PostStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_1);
    }

    @Override
    public void postStart() throws Throwable {
        initHandlers = ImmutableList.copyOf(applicationContext.getBeansOfType(ZKMonitorInitHandler.class).values());
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
