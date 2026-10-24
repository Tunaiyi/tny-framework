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
package com.tny.game.cache;

import org.slf4j.*;

import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class TriggerHolder {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TriggerHolder.class);

    protected List<CacheTrigger> triggers = new ArrayList<CacheTrigger>();

    public TriggerHolder() {
    }

    public TriggerHolder(List<? extends CacheTrigger> triggers) {
        this.triggers.addAll(triggers);
    }

    public Object triggerLoad(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            if (value == null) {
                break;
            }
            value = trigger.triggerLoad(key, object, value);
        }
        return value;
    }

    public Object triggerUpdate(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            if (value == null) {
                break;
            }
            value = trigger.triggerReplace(key, object, value);
        }
        return value;
    }

    public Object triggerInsert(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            if (value == null) {
                break;
            }
            value = trigger.triggerAdd(key, object, value);
        }
        return value;
    }

    public Object triggerSave(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            if (value == null) {
                break;
            }
            value = trigger.triggerSet(key, object, value);
        }
        return value;
    }

    public void triggerDelete(String key, Object object) {
    }

}
