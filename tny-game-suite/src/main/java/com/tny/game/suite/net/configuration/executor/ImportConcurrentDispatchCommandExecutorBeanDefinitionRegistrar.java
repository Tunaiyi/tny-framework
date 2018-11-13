package com.tny.game.suite.net.configuration.executor;

import com.tny.game.net.command.executor.ConcurrentDispatchCommandExecutor;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-02 11:45
 */
public class ImportConcurrentDispatchCommandExecutorBeanDefinitionRegistrar extends
        ImportDispatchCommandExecutorBeanDefinitionRegistrar<ConcurrentDispatchCommandExecutor> {

    protected ImportConcurrentDispatchCommandExecutorBeanDefinitionRegistrar() {
        super(ConcurrentDispatchCommandExecutor.class, ConcurrentDispatchCommandExecutor::new);
    }

}
