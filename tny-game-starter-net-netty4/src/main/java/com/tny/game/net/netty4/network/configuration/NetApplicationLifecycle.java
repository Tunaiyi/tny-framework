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
