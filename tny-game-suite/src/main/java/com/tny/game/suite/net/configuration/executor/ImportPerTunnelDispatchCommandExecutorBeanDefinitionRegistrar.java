package com.tny.game.suite.net.configuration.executor;

import com.tny.game.net.command.executor.PerTunnelDispatchCommandExecutor;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportPerTunnelDispatchCommandExecutorBeanDefinitionRegistrar extends ImportDispatchCommandExecutorBeanDefinitionRegistrar<PerTunnelDispatchCommandExecutor> {

    public ImportPerTunnelDispatchCommandExecutorBeanDefinitionRegistrar() {
        super(PerTunnelDispatchCommandExecutor.class, PerTunnelDispatchCommandExecutor::new);
    }

    @Override
    protected boolean isDefaultClass() {
        return true;
    }

}
