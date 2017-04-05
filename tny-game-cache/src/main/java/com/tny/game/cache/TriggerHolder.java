package com.tny.game.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
            value = trigger.triggerLoad(key, object, value);
        }
        return value;
    }

    public Object triggerUpdate(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            value = trigger.triggerReplace(key, object, value);
        }
        return value;
    }

    public Object triggerInsert(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            value = trigger.triggerAdd(key, object, value);
        }
        return value;
    }

    public Object triggerSave(String key, Object object) {
        Object value = object;
        for (CacheTrigger trigger : this.triggers) {
            value = trigger.triggerSet(key, object, value);
        }
        return value;
    }

    public void triggerDelete(String key, Object object) {
    }

}
