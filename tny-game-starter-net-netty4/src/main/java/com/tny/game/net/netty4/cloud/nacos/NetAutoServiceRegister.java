package com.tny.game.net.netty4.cloud.nacos;

import com.tny.game.boot.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.netty4.cloud.*;
import com.tny.game.net.netty4.cloud.event.*;
import com.tny.game.net.netty4.configuration.application.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.serviceregistry.*;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/8 7:18 下午
 */
public class NetAutoServiceRegister implements ApplicationContextAware {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetAutoServiceRegister.class);

    private final AtomicBoolean running = new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    private final List<Registration> registrations = new CopyOnWriteArrayList<>();

    private final ServiceRegistry<Registration> serviceRegistry;

    private final List<ServerGuideRegistrationFactory> registrationFactories;

    public NetAutoServiceRegister(ServiceRegistry<Registration> serviceRegistry, List<ServerGuideRegistrationFactory> registrationFactories) {
        this.serviceRegistry = serviceRegistry;
        this.registrationFactories = registrationFactories;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void register() {
        NetApplication application = applicationContext.getBean(NetApplication.class);
        Set<ServerGuide> guides = SpringBeanUtils.beansOfType(applicationContext, ServerGuide.class);
        this.applicationContext.publishEvent(new NetApplicationPreRegisteredEvent(application));
        for (ServerGuide guide : guides) {
            this.applicationContext.publishEvent(new ServerGuidePreRegisteredEvent(application, guide));
            for (ServerGuideRegistrationFactory factory : registrationFactories) {
                Registration current = factory.create(guide, application.getAppContext());
                serviceRegistry.register(current);
            }
            this.applicationContext.publishEvent(new ServerGuideRegisteredEvent(application, guide));
        }
        this.applicationContext.publishEvent(new NetApplicationRegisteredEvent(application));
    }

    private void deregister() {
        for (Registration registration : registrations) {
            this.serviceRegistry.deregister(registration);
        }
    }

    public void restart() {
        this.stop();
        this.start();
    }

    public void start() {
        if (this.running.compareAndSet(false, true)) {
            register();
        }
    }

    public void stop() {
        if (this.running.compareAndSet(true, false)) {
            deregister();
            this.registrations.clear();
            this.serviceRegistry.close();
        }

    }

}
