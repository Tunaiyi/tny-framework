package com.tny.game.common.boot.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.*;

/**
 * <p>
 */
@Plugin(name = "spring", category = StrLookup.CATEGORY)
public class SpringEnvironmentLookup extends AbstractLookup {

    /**
     * Looks up the value of the spring environment variable.
     *
     * @param event The current LogEvent (is ignored by this StrLookup).
     * @param key   the key to be looked up, may be null
     * @return The value of the spring environment variable.
     */
    @Override
    public String lookup(final LogEvent event, final String key) {
        if (SpringContext.getEnvironment() != null) {
            String value = SpringContext.getEnvironment().getProperty(key);
            if (value == null) {
                throw new NullPointerException(key + " is null");
            }
            return value;
        }
        throw new NullPointerException("Spring Environment is null");
    }

}