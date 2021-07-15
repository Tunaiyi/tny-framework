package com.tny.game.common.boot.log4j2;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>
 */
public class SpringContext {

    private static ConfigurableEnvironment environment;

    static void setEnvironment(ConfigurableEnvironment environment) {
        SpringContext.environment = environment;
    }

    public static ConfigurableEnvironment getEnvironment() {
        return SpringContext.environment;
    }

}