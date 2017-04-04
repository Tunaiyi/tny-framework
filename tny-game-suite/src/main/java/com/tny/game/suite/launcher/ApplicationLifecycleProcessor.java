package com.tny.game.suite.launcher;

import com.tny.game.LogUtils;
import com.tny.game.common.RunningChecker;
import com.tny.game.common.concurrent.ExeUtils;
import com.tny.game.lifecycle.Lifecycle;
import com.tny.game.lifecycle.LifecycleHandler;
import com.tny.game.lifecycle.ServerClosed;
import com.tny.game.lifecycle.ServerPostStart;
import com.tny.game.lifecycle.ServerPrepareStart;
import com.tny.game.lifecycle.StaticIniter;
import com.tny.game.lifecycle.annotaion.AsLifecycle;
import com.tny.game.lifecycle.annotaion.AsyncProcess;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.AnnotationClassFilter;
import com.tny.game.suite.initer.EnumLoader;
import com.tny.game.suite.initer.OpLogSnapshotIniter;
import com.tny.game.suite.initer.ProtoExSchemaIniter;
import com.tny.game.suite.launcher.exception.LifecycleProcessException;
import com.tny.game.suite.transaction.TransactionManager;
import com.tny.game.suite.utils.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 16/5/31.
 */
public class ApplicationLifecycleProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(ServerLauncher.class);

    private static Map<Class<?>, List<Lifecycle>> lifecycleMap = new HashMap<>();

    private static Map<Lifecycle, LifecycleHandler> handlerMap = new HashMap<>();

    public void onStaticInit() throws Throwable {
        Class<?> clazz = null;
        try {
            RunningChecker.start(this.getClass());
            ClassScanner.instance()
                    .addSelector(this.selector())
                    .addSelector(EnumLoader.selector())
                    .addSelector(ProtoExSchemaIniter.selector())
                    .addSelector(OpLogSnapshotIniter.selector())
                    .scan(Configs.getScanPathArray());
            LOGGER.info("开始初始化 Class Scan 完成! 耗时 {} ms", RunningChecker.end(this.getClass()).cost());
        } catch (Throwable e) {
            throw new RuntimeException(LogUtils.format("获取 {} 类 ProtoExSchema 错误", clazz), e);
        }
    }

    private ClassSelector  () {
        return ClassSelector.instance()
                .addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
                .setHandler(classes -> classes.forEach(c ->
                        ExeUtils.runUnchecked(() -> StaticIniter.instance(c).init())
                ));
    }

    public void onPrepareStart(boolean errorContinue) throws Throwable {
        this.process("prepareStart", ServerPrepareStart.class, ServerPrepareStart::prepareStart, errorContinue);
    }

    public void onPostStart(boolean errorContinue) throws Throwable {
        this.process("postStart", ServerPostStart.class, ServerPostStart::postStart, errorContinue);
    }

    public void onClosed(boolean errorContinue) throws Throwable {
        this.process("onClosed", ServerClosed.class, ServerClosed::onClosed, errorContinue);
    }

    @FunctionalInterface
    private interface ProcessorRunner<P extends LifecycleHandler> {

        void process(P p) throws Throwable;

    }

    public static void loadHandler(ApplicationContext context) {
        loadHandler(ServerPrepareStart.class, ServerPrepareStart::getPrepareStarter, context);
        loadHandler(ServerPostStart.class, ServerPostStart::getPostStarter, context);
        loadHandler(ServerClosed.class, ServerClosed::getPostCloser, context);
    }

    private static <T extends LifecycleHandler> void loadHandler(Class<T> processorClass, Function<T, Lifecycle> lifecycleGetter, ApplicationContext context) {
        Map<String, ? extends T> initerMap = context.getBeansOfType(processorClass);
        List<Lifecycle> process = initerMap.values().stream()
                .peek(i -> {
                    Lifecycle<?, ?> lifecycle = lifecycleGetter.apply(i);
                    if (i.getClass() != lifecycle.getHandlerClass())
                        throw new IllegalArgumentException(LogUtils.format("{} 不符合 {}", i.getClass(), lifecycle));
                    handlerMap.put(lifecycle, i);
                })
                .map(lifecycleGetter)
                .sorted((o1, o2) -> 0 - (o1.getPriority() - o2.getPriority()))
                .collect(Collectors.toList());
        lifecycleMap.put(processorClass, process);
    }

    private <T extends LifecycleHandler> void process(String methodName, Class<T> processorClass, ProcessorRunner<T> runner, boolean errorContinue) throws Throwable {
        String name = processorClass.getSimpleName();
        LOGGER.info("服务生命周期处理 {} ! 初始化开始......", name);
        // Map<String, ? extends T> initerMap = this.appContext.getBeansOfType(processorClass);
        List<Lifecycle> lifecycleList = lifecycleMap.getOrDefault(processorClass, Collections.emptyList());
        int index = 0;

        Map<Lifecycle, ? extends LifecycleHandler> cloneMap = new HashMap<>(handlerMap);

        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (Lifecycle lifecycle : lifecycleList) {
            long current = System.currentTimeMillis();
            Lifecycle currentLifecycle = lifecycle.head();
            while (currentLifecycle != null) {
                T processor = (T) cloneMap.remove(currentLifecycle);
                if (processor != null) {
                    Method method = processor.getClass().getMethod(methodName);
                    AsyncProcess asyncProcess = method.getAnnotation(AsyncProcess.class);
                    LOGGER.info("服务生命周期 {} # 处理器 [{}] : {}", name, index, currentLifecycle);
                    int currentIndex = index;
                    if (asyncProcess != null) {
                        ForkJoinTask<?> task = ForkJoinPool.commonPool().submit(() -> {
                            TransactionManager.open();
                            try {
                                runner.process(processor);
                                TransactionManager.close();
                            } catch (Throwable e) {
                                LOGGER.error("服务生命周期 {} # 处理器 [{}] 异常", name, currentIndex, e);
                                TransactionManager.rollback(e);
                                throw new LifecycleProcessException(e);
                            }
                        });
                        tasks.add(task);
                    } else {
                        try {
                            runner.process(processor);
                        } catch (Throwable e) {
                            if (errorContinue)
                                LOGGER.error("服务生命周期 {} # 处理器 [{}] 异常", name, currentIndex, e);
                            else
                                throw new LifecycleProcessException(e);
                        }
                    }
                    LOGGER.info("服务生命周期 {} # 处理器 [{}] : 耗时 {} -> {} 完成", name, index, System.currentTimeMillis() - current, currentLifecycle);
                    index++;
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
