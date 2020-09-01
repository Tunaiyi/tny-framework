package com.tny.game.cache.spring;

import com.tny.game.cache.*;
import com.tny.game.cache.annotation.*;
import com.tny.game.common.concurrent.collection.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.lang.Nullable;

import java.util.*;

@SuppressWarnings("rawtypes")
public class SpringToCacheClassHolderFactory implements CacheTriggerFactory, ToCacheClassHolderFactory,
        ApplicationContextAware {

    private volatile boolean init = false;

    private final Map<Class<?>, CacheTrigger> triggerMap = new CopyOnWriteMap<>();

    private final Map<Class<?>, TriggerHolder> triggerHolderMap = new CopyOnWriteMap<>();

    private final Map<Class<?>, ToCacheClassHolder> holderMap = new CopyOnWriteMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void register(Class<?> objectClass) {
        ToCache toCache = objectClass.getAnnotation(ToCache.class);
        if (toCache == null) {
            Trigger trigger = objectClass.getAnnotation(Trigger.class);
            if (trigger == null) {
                return;
            }
            List<CacheTrigger<?, ?, ?>> cacheTriggers = this.getCacheTrigger0(trigger.triggers());
            TriggerHolder triggerHolder = new TriggerHolder(cacheTriggers);
            this.triggerHolderMap.put(objectClass, triggerHolder);
        } else {
            ToCacheClassHolder toCacheClassHolder = new ToCacheClassHolder(objectClass, this);
            this.holderMap.put(objectClass, toCacheClassHolder);
            this.triggerHolderMap.put(objectClass, toCacheClassHolder);
        }
        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            this.register(superClass);
        }
        for (Class<?> interfaceClass : objectClass.getInterfaces())
            this.register(interfaceClass);
    }

    @Override
    public ToCacheClassHolder getCacheClassHolder(Class<?> clazz) {
        ToCacheClassHolder holder = this.holderMap.get(clazz);
        if (holder == null) {
            this.register(clazz);
        }
        holder = this.holderMap.get(clazz);
        if (holder == null) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz == Object.class) {
                throw new NullPointerException("[" + clazz + "] toCachedClassHolder is null");
            }
            try {
                return this.getCacheClassHolder(superClazz);
            } catch (Exception e) {
                throw new IllegalArgumentException("[" + clazz + " super " + superClazz + "] toCachedClassHolder is null", e);
            }
        }
        return holder;
    }

    private List<CacheTrigger<?, ?, ?>> getCacheTrigger0(Class<? extends CacheTrigger<?, ?, ?>>[] triggerClass) {
        if (triggerClass == null || triggerClass.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<CacheTrigger<?, ?, ?>> triggerList = new ArrayList<>();
        for (Class<? extends CacheTrigger<?, ?, ?>> clazz : triggerClass) {
            CacheTrigger<?, ?, ?> trigger = this.getCacheTrigger0(clazz);
            triggerList.add(trigger);
        }
        if (triggerList.isEmpty()) {
            return Collections.emptyList();
        }
        return triggerList;
    }

    private CacheTrigger<?, ?, ?> getCacheTrigger0(Class<? extends CacheTrigger<?, ?, ?>> triggerClass) {
        this.init();
        CacheTrigger<?, ?, ?> trigger = this.triggerMap.get(triggerClass);
        if (trigger == null) {
            trigger = this.applicationContext.getBean(triggerClass);
            this.triggerMap.put(trigger.getClass(), trigger);
        }
        return trigger;
    }

    private TriggerHolder getTriggerHolder0(Class<?> clazz) {
        this.init();
        TriggerHolder holder = this.triggerHolderMap.get(clazz);
        if (holder == null) {
            Object object = this.applicationContext.getBean(clazz);
            if (!(object instanceof TriggerHolder)) {
                throw new NullPointerException("[" + clazz + "] formatterHolder is null");
            } else {
                holder = (TriggerHolder)object;
                this.triggerHolderMap.put(holder.getClass(), holder);
            }
        }
        return holder;
    }

    private void init() {
        if (this.init) {
            return;
        }
        synchronized (this) {
            if (this.init) {
                return;
            }
            this.init = true;
            Map<String, CacheTrigger> triggerMap = this.applicationContext.getBeansOfType(CacheTrigger.class);
            for (CacheTrigger handler : triggerMap.values()) {
                this.triggerMap.put(handler.getClass(), handler);
            }
            Map<String, Object> toCacheMap = this.applicationContext.getBeansWithAnnotation(ToCache.class);
            for (Object object : toCacheMap.values()) {
                this.register(object.getClass());
            }
        }
    }

    @Override
    public TriggerHolder getTriggerHolder(Class<?> clazz) {
        return this.getTriggerHolder0(clazz);
    }

    @Override
    public CacheTrigger<?, ?, ?> getCacheTrigger(Class<? extends CacheTrigger<?, ?, ?>> triggerClass) {
        return this.getCacheTrigger0(triggerClass);
    }

}
