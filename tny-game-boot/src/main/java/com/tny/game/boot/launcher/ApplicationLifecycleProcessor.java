/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.boot.launcher;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.annotation.*;
import com.tny.game.common.lifecycle.exception.*;
import com.tny.game.common.runtime.*;
import com.tny.game.scanner.*;
import org.slf4j.*;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 16/5/31.
 */
class ApplicationLifecycleProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLifecycleProcessor.class);

    private static final Map<Class<?>, Set<Lifecycle<?, ?>>> LIFECYCLE_MAP = new HashMap<>();

    private static final Map<Lifecycle<?, ?>, List<LifecycleHandler>> HANDLER_MAP = new HashMap<>();

    public void onStaticInit(ClassLoader loader, Collection<String> paths) {
        RunChecker.trace(this.getClass());
        LOGGER.info("开始初始化 Class Scan ...");
        ClassScanner.instance(loader, true).scan(paths);
        LOGGER.info("初始化 Class Scan 完成! 耗时 {} ms", RunChecker.end(this.getClass()).costMillisTime());
        //		AsLifecycle
        Set<Class<?>> lifecycleClass = AutoLoadClasses.getClasses(AsLifecycle.class);
        lifecycleClass.forEach(LifecycleLoader::register);
        for (StaticInitiator Initiator : LifecycleLoader.getStaticInitiators()) {
            Class<?> c = Initiator.getInitiatorClass();
            int number = 0;
            long current = System.currentTimeMillis();
            try {
                number++;
                LOGGER.info("服务生命周期 StaticInit # 处理器 [{}] index {}", c, number);
                ExeAide.runUnchecked(Initiator::init);
                LOGGER.info("服务生命周期 StaticInit # 处理器 [{}] index {} | -> 耗时 {} 完成", c, number, System.currentTimeMillis() - current);
            } catch (Throwable e) {
                LOGGER.error("服务生命周期 StaticInit # 处理器 [{}] index {} | -> 异常", c, number, e);
            }
        }
    }

    public void onPrepareStart(boolean errorContinue) throws Exception {
        this.process("prepareStart", AppPrepareStart.class, AppPrepareStart::prepareStart, errorContinue);
    }

    public void onPostStart(boolean errorContinue) throws Exception {
        this.process("postStart", AppPostStart.class, AppPostStart::postStart, errorContinue);
    }

    public void onClosed(boolean errorContinue) throws Exception {
        this.process("onClosed", AppClosed.class, AppClosed::onClosed, errorContinue);
    }

    @FunctionalInterface
    private interface ProcessorRunner<P extends LifecycleHandler> {

        void process(P p) throws Throwable;

    }

    public static void addAppPrepareStarts(AppPrepareStart... prepareStarts) {
        addAppPrepareStarts(Arrays.asList(prepareStarts));
    }

    public static void addAppPrepareStarts(Collection<? extends AppPrepareStart> prepareStartList) {
        addLifecycleHandler(AppPrepareStart.class, AppPrepareStart::getPrepareStarter, prepareStartList);
    }

    public static void addAppPostStart(AppPostStart... postStarts) {
        addAppPostStart(Arrays.asList(postStarts));
    }

    public static void addAppPostStart(Collection<? extends AppPostStart> postStartList) {
        addLifecycleHandler(AppPostStart.class, AppPostStart::getPostStarter, postStartList);
    }

    public static void addAppClosed(AppClosed... postStarts) {
        addAppClosed(Arrays.asList(postStarts));
    }

    public static void addAppClosed(Collection<? extends AppClosed> appClosedList) {
        addLifecycleHandler(AppClosed.class, AppClosed::getPostCloser, appClosedList);
    }

    private static <T extends LifecycleHandler> void addLifecycleHandler(
            Class<T> lifecycleClass, Function<T, ? extends Lifecycle<?, ?>> lifecycleGetter, Collection<? extends T> lifecycles) {
        List<Lifecycle<?, ?>> process = lifecycles.stream()
                .map(handle -> {
                    Lifecycle<?, ?> lifecycle = lifecycleGetter.apply(handle);
                    if (!lifecycle.getHandlerClass().isAssignableFrom(handle.getClass())) {
                        throw new IllegalArgumentException(format("{} 不符合 {}", handle.getClass(), lifecycle));
                    }
                    HANDLER_MAP.computeIfAbsent(lifecycle, l -> new ArrayList<>()).add(handle);
                    return lifecycle;
                })
                .collect(Collectors.toList());
        LIFECYCLE_MAP.computeIfAbsent(lifecycleClass, k -> new ConcurrentSkipListSet<>())
                .addAll(process);
    }

    public static void loadHandler(ApplicationContext context) {
        loadHandler(AppPrepareStart.class, AppPrepareStart::getPrepareStarter, context);
        loadHandler(AppPostStart.class, AppPostStart::getPostStarter, context);
        loadHandler(AppClosed.class, AppClosed::getPostCloser, context);
    }

    private static <T extends LifecycleHandler> void loadHandler(
            Class<T> processorClass, Function<T, ? extends Lifecycle<?, ?>> lifecycleGetter, ApplicationContext context) {
        Map<String, ? extends T> lifecycleMap = context.getBeansOfType(processorClass);
        addLifecycleHandler(processorClass, lifecycleGetter, lifecycleMap.values());
    }

    private <T extends LifecycleHandler> void process(String methodName, Class<T> processorClass, ProcessorRunner<T> runner, boolean errorContinue)
            throws Exception {
        String name = processorClass.getSimpleName();
        LOGGER.info("服务生命周期处理 {} ! 初始化开始......", name);
        // Map<String, ? extends T> InitiatorMap = this.appContext.getBeansOfType(processorClass);
        Set<Lifecycle<?, ?>> lifecycleList = LIFECYCLE_MAP.getOrDefault(processorClass, Collections.emptySet());
        int index = 0;
        Map<Lifecycle<?, ?>, List<LifecycleHandler>> cloneMap = HANDLER_MAP
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> new ArrayList<>(e.getValue())));
        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (Lifecycle<?, ?> lifecycle : lifecycleList) {
            long current = System.currentTimeMillis();
            Lifecycle<?, ?> currentLifecycle = lifecycle.head();
            while (currentLifecycle != null) {
                List<T> processors = as(cloneMap.remove(currentLifecycle));
                if (processors != null) {
                    for (T processor : processors) {
                        Method method = processor.getClass().getMethod(methodName);
                        AsyncProcess asyncProcess = method.getAnnotation(AsyncProcess.class);
                        LOGGER.info("服务生命周期 {} # 处理器 [{}] index {} |", name, processor.getClass(), index);
                        int currentIndex = index;
                        if (asyncProcess != null) {
                            ForkJoinTask<?> task = ForkJoinPool.commonPool().submit(() -> {
                                // TransactionManager.open();
                                try {
                                    runner.process(processor);
                                    // TransactionManager.close();
                                } catch (Throwable e) {
                                    LOGGER.error("服务生命周期 {} # 处理器 [{}] index {} | -> 异常", name, processor.getClass(), currentIndex, e);
                                    // TransactionManager.rollback(e);
                                    throw new LifecycleProcessException(e);
                                }
                            });
                            tasks.add(task);
                        } else {
                            try {
                                runner.process(processor);
                            } catch (Throwable e) {
                                LOGGER.error("服务生命周期 {} # 处理器 [{}] index {} | -> 异常", name, processor.getClass(), currentIndex, e);
                                if (!errorContinue) {
                                    throw new LifecycleProcessException(e);
                                }
                            }
                        }
                        LOGGER.info("服务生命周期 {} # 处理器 [{}] index {} | -> 耗时 {} 完成", name, processor.getClass(), index,
                                System.currentTimeMillis() - current);
                        index++;
                    }
                }
                currentLifecycle = currentLifecycle.getNext();
            }
        }
        for (ForkJoinTask<?> task : tasks) {
            task.join();
        }
        LOGGER.info("服务生命周期处理 {} 完成! 共 {} 个初始化器!", name, index);
    }

}
