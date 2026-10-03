/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.boot.log4j2;

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
        return null;
    }

}