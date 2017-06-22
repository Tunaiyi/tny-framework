package com.tny.game.suite.cache.spring;

import com.tny.game.LogUtils;
import com.tny.game.cache.CacheTrigger;
import com.tny.game.cache.CacheTriggerFactory;
import com.tny.game.cache.ToCacheClassHolder;
import com.tny.game.cache.ToCacheClassHolderFactory;
import com.tny.game.cache.TriggerHolder;
import com.tny.game.cache.annotation.ToCache;
import com.tny.game.cache.annotation.Trigger;
import com.tny.game.common.RunningChecker;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.lifecycle.LifecycleLevel;
import com.tny.game.lifecycle.PrepareStarter;
import com.tny.game.lifecycle.ServerPrepareStart;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.AnnotationClassFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@SuppressWarnings("unchecked")
public class SpringToCacheClassHolderAndLinkHandlerFactory implements CacheTriggerFactory, ToCacheClassHolderFactory, ApplicationContextAware, ServerPrepareStart {

    private volatile boolean init = false;

    private static final Logger LOGGER = LoggerFactory.getLogger("proto");

    private final Map<Class<?>, CacheTrigger> triggerMap = new CopyOnWriteMap<>();

    private final Map<Class<?>, TriggerHolder> triggerHolderMap = new CopyOnWriteMap<>();

    private final Map<Class<?>, ToCacheClassHolder> holderMap = new CopyOnWriteMap<>();

    private ForkJoinTask<Collection<Class<?>>> task;

    private ApplicationContext applicationContext;

    private List<String> scannerPath;

    public SpringToCacheClassHolderAndLinkHandlerFactory(List<String> scannerPath) {
        this.scannerPath = scannerPath;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void init() {
        if (this.init)
            return;
        synchronized (this) {
            if (this.init)
                return;
            this.init = true;
        }
    }

    public void register(Class<?> objectClass) {
        this.init();
        ToCache toCache = objectClass.getAnnotation(ToCache.class);
        if (toCache == null) {
            Trigger trigger = objectClass.getAnnotation(Trigger.class);
            if (trigger == null)
                return;
            List<CacheTrigger<?, ?, ?>> cacheTriggers = this.getCacheTrigger0(trigger.triggers());
            TriggerHolder triggerHolder = new TriggerHolder(cacheTriggers);
            this.triggerHolderMap.put(objectClass, triggerHolder);
        } else {
            ToCacheClassHolder toCacheClassHolder = new ToCacheClassHolder(objectClass, this);
            this.holderMap.put(objectClass, toCacheClassHolder);
            this.triggerHolderMap.put(objectClass, toCacheClassHolder);
        }
        Class<?> superClass = objectClass.getSuperclass();
        if (superClass != null && superClass != Object.class)
            this.register(superClass);
        for (Class<?> interfaceClass : objectClass.getInterfaces())
            this.register(interfaceClass);
    }

    @Override
    public ToCacheClassHolder getCacheClassHolder(Class<?> clazz) {
        this.init();
        ToCacheClassHolder holder = this.holderMap.get(clazz);
        if (holder == null) {
            this.register(clazz);
        }
        holder = this.holderMap.get(clazz);
        if (holder == null) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz == Object.class)
                throw new NullPointerException("[" + clazz + "] toCachedClassHolder is null");
            try {
                return this.getCacheClassHolder(superClazz);
            } catch (Exception e) {
                throw new IllegalArgumentException("[" + clazz + " super " + superClazz + "] toCachedClassHolder is null", e);
            }
        }
        return holder;
    }

    private List<CacheTrigger<?, ?, ?>> getCacheTrigger0(Class<? extends CacheTrigger<?, ?, ?>>[] triggerClass) {
        if (triggerClass == null || triggerClass.length == 0)
            return Collections.emptyList();
        ArrayList<CacheTrigger<?, ?, ?>> triggerList = new ArrayList<>();
        for (Class<?> clazz : triggerClass) {
            CacheTrigger<?, ?, ?> trigger = this.triggerMap.get(clazz);
            if (trigger != null)
                triggerList.add(trigger);
        }
        if (triggerList.isEmpty())
            return Collections.emptyList();
        return triggerList;
    }

    private TriggerHolder getTriggerHolder0(Class<?> clazz) {
        TriggerHolder holder = this.triggerHolderMap.get(clazz);
        if (holder == null)
            throw new NullPointerException("[" + clazz + "] formatterHolder is null");
        return holder;
    }

    @Override
    public TriggerHolder getTriggerHolder(Class<?> clazz) {
        this.init();
        return this.getTriggerHolder0(clazz);
    }

    @Override
    public CacheTrigger<?, ?, ?> getCacheTrigger(Class<? extends CacheTrigger<?, ?, ?>> triggerClass) {
        this.init();
        return this.triggerMap.get(triggerClass);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @PostConstruct
    private void loadClass() {
        this.task = ForkJoinPool.commonPool()
                .submit(() -> {
                    ClassSelector selector = ClassSelector.instance(AnnotationClassFilter.ofInclude(ToCache.class));
                    ClassScanner.instance()
                            .addSelector(selector)
                            .scan(scannerPath.toArray(new String[scannerPath.size()]));
                    return selector.getClasses();
                });
    }

    @Override
    public void prepareStart() throws Exception {
        Map<String, CacheTrigger> triggerMap = this.applicationContext.getBeansOfType(CacheTrigger.class, true, true);
        for (CacheTrigger handler : triggerMap.values()) {
            this.triggerMap.put(handler.getClass(), handler);
        }
        Class<?> clazz = null;
        try {
            RunningChecker.start(this.getClass());
            for (Class<?> cl : this.task.get()) {
                clazz = cl;
                SpringToCacheClassHolderAndLinkHandlerFactory.this.register(cl);
            }
            LOGGER.info("开始初始化 ToCacheClassHolder 完成! 耗时 {} ms", RunningChecker.end(this.getClass()).cost());
        } catch (Throwable e) {
            throw new RuntimeException(LogUtils.format("获取 {} 类 GameToCacheClassHolderAndLinkHandlerFactory 错误", clazz), e);
        }
    }

}