/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.network.configuration;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.netty4.configuration.application.*;
import com.tny.game.net.netty4.network.configuration.event.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Nonnull;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/26 3:56 下午
 */
public class NetApplicationLifecycle implements ApplicationLauncher, ApplicationContextAware {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetApplicationLifecycle.class);

    private ApplicationContext applicationContext;

    private NetApplication netApplication;

    private volatile boolean running;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void start() {
        if (!this.running) {
            NetApplication application = this.applicationContext.getBean(NetApplication.class);
            ApplicationContext context = application.getApplicationContext();
            LOGGER.info("NetApplication {} starting ... ", context.getApplicationName());
            try {
                application.start();
            } catch (Throwable e) {
                throw new ApplicationContextException(context.getApplicationName(), e);
            }
            applicationContext.publishEvent(new NetApplicationStartEvent(application));
            LOGGER.info("NetApplication {} started success", context.getApplicationName());
            this.running = true;
        }
    }

    @Override
    public void stop() {
        this.running = false;
        if (this.running) {
            NetApplication application = this.applicationContext.getBean(NetApplication.class);
            ApplicationContext context = application.getApplicationContext();
            LOGGER.info("NetApplication {} stopping ... ", context.getApplicationName());
            try {
                application.close();
            } catch (Throwable e) {
                throw new ApplicationContextException(context.getApplicationName(), e);
            }
            applicationContext.publishEvent(new NetApplicationStopEvent(application));
            LOGGER.info("NetApplication {} stopped success", context.getApplicationName());
        }
    }

}
