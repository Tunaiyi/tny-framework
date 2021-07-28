package com.tny.game.boot.log4j2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.*;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * <p>
 */
@Component
public class SpringContextEnvironmentApplicationListener implements SmartApplicationListener {

    private static final int ORDER = -2147483643;

    private static final Class<?>[] EVENT_TYPES = {ApplicationEnvironmentPreparedEvent.class};

    private static final Class<?>[] SOURCE_TYPES = {SpringApplication.class, ApplicationContext.class};

    @Override
    public boolean supportsEventType(@NonNull Class<? extends ApplicationEvent> eventClass) {
        return isAssignableFrom(eventClass, EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        for (Class<?> supportedType : supportedTypes) {
            if (supportedType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            SpringContext.setEnvironment(((ApplicationEnvironmentPreparedEvent)event).getEnvironment());
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
