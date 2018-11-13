package com.tny.game.suite.launcher;

import com.tny.game.common.runtime.RunningChecker;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.annotaion.*;
import com.tny.game.common.number.IntLocalNum;
import com.tny.game.common.utils.ExeAide;
import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.AnnotationClassFilter;
import com.tny.game.suite.initer.*;
import com.tny.game.suite.launcher.exception.LifecycleProcessException;
import com.tny.game.suite.transaction.TransactionManager;
import com.tny.game.suite.utils.Configs;
import org.slf4j.*;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 16/5/31.
 */
public class ApplicationLifecycleProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(SuitApplication.class);

    private static Map<Class<?>, List<Lifecycle>> lifecycleMap = new HashMap<>();

    private static Map<Lifecycle, LifecycleHandler> handlerMap = new HashMap<>();

    public void onStaticInit() throws Exception {
        Class<?> clazz = null;
        try {
            RunningChecker.start(this.getClass());
            LOGGER.info("开始初始化 Class Scan ...");
            ClassScanner.instance()
                    .addSelector(this.selector())
                    .addSelector(EnumLoader.selector())
                    .addSelector(ProtoExSchemaIniter.selector())
                    .addSelector(OpLogSnapshotIniter.selector())
                    .addSelector(RandomCreatorIniter.selector())
                    .scan(Configs.getScanPathArray());
            LOGGER.info("初始化 Class Scan 完成! 耗时 {} ms", RunningChecker.end(this.getClass()).cost());
        } catch (Throwable e) {
            throw new RuntimeException(format("获取 {} 类 ProtoExSchema 错误", clazz), e);
        }
    }

    private ClassSelector selector() {
        IntLocalNum count = new IntLocalNum(0);
        return ClassSelector.instance()
                .addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
                .setHandler(classes -> classes.forEach(c -> {
                            int number = count.add(1);
                            long current = System.currentTimeMillis();
                            try {
                                LOGGER.info("服务生命周期 StaticInit # 处理器 [{}] : {}", number, c);
                                ExeAide.runUnchecked(() -> StaticIniter.instance(c).init());
                                LOGGER.info("服务生命周期 StaticInit # 处理器 [{}] : 耗时 {} -> {} 完成", number, System.currentTimeMillis() - current, c);
                            } catch (Throwable e) {
                                LOGGER.error("服务生命周期 StaticInit # 处理器 [{}] 异常", number, c, e);
                            }
                        }
                ));
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

    public static void loadHandler(ApplicationContext context) {
        loadHandler(AppPrepareStart.class, AppPrepareStart::getPrepareStarter, context);
        loadHandler(AppPostStart.class, AppPostStart::getPostStarter, context);
        loadHandler(AppClosed.class, AppClosed::getPostCloser, context);
    }

    private static <T extends LifecycleHandler> void loadHandler(Class<T> processorClass, Function<T, Lifecycle> lifecycleGetter, ApplicationContext context) {
        Map<String, ? extends T> initerMap = context.getBeansOfType(processorClass);
        List<Lifecycle> process = initerMap.values().stream()
                .peek(i -> {
                    Lifecycle<?, ?> lifecycle = lifecycleGetter.apply(i);
                    if (i.getClass() != lifecycle.getHandlerClass())
                        throw new IllegalArgumentException(format("{} 不符合 {}", i.getClass(), lifecycle));
                    handlerMap.put(lifecycle, i);
                })
                .map(lifecycleGetter)
                .sorted((o1, o2) -> 0 - (o1.getPriority() - o2.getPriority()))
                .collect(Collectors.toList());
        lifecycleMap.put(processorClass, process);
    }

    private <T extends LifecycleHandler> void process(String methodName, Class<T> processorClass, ProcessorRunner<T> runner, boolean errorContinue) throws Exception {
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
                @SuppressWarnings("unchecked")
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
