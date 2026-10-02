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
package com.tny.game.asyndb.spring;

import com.tny.game.asyndb.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringSynchronizerHolder implements SynchronizerHolder, ApplicationContextAware {

    private ApplicationContext context;

    private final Map<Class<?>, Synchronizer<Object>> synchronizerMap = new ConcurrentHashMap<Class<?>, Synchronizer<Object>>();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Synchronizer<Object> getSynchronizer(Class<? extends Synchronizer> syncClass) {
        Synchronizer<Object> synchronizer = this.synchronizerMap.get(syncClass);
        if (synchronizer == null) {
            synchronizer = this.context.getBean(syncClass);
            if (synchronizer != null) {
                this.synchronizerMap.put(syncClass, synchronizer);
            }
        }
        return synchronizer;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}
