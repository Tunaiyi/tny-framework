package com.tny.game.suite.initer;

import com.tny.game.LogUtils;
import com.tny.game.common.RunningChecker;
import com.tny.game.net.dispatcher.message.protoex.ProtoExRequest;
import com.tny.game.net.dispatcher.message.protoex.ProtoExResponse;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.protoex.ProtoExSchema;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.field.runtime.RuntimeProtoExSchema;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.filter.AnnotationClassFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class ProtoExSchemaIniter implements ServerPreStart {

    private static final Logger LOGGER = LoggerFactory.getLogger("proto");

    private RuntimeException exception;

    private CountDownLatch latch = new CountDownLatch(1);

    private List<String> scanPaths = new ArrayList<>();

    public ProtoExSchemaIniter(List<String> scanPaths) {
        this.scanPaths.addAll(scanPaths);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initSchemaAsync() {
        LOGGER.info("启动初始化ProtoSchema任务!");
        Runnable task = new Runnable() {

            @Override
            public void run() {
                Class<?> clazz = null;
                try {
                    ClassScanner scanner = new ClassScanner().
                            addFilter(AnnotationClassFilter.ofInclude(ProtoEx.class));
                    Set<Class<?>> classes = new HashSet<>();
                    classes.addAll(scanner.getClasses(scanPaths.toArray(new String[scanPaths.size()])));
                    RunningChecker.start(this.getClass());
                    LOGGER.info("开始初始化 ProtoSchema .......");
                    Map<Integer, Class<?>> classMap = new HashMap<>();
                    RuntimeProtoExSchema.getProtoSchema(ProtoExRequest.class);
                    RuntimeProtoExSchema.getProtoSchema(ProtoExResponse.class);
                    for (Class<?> cl : classes) {
                        clazz = cl;
                        ProtoExSchema<?> schema = RuntimeProtoExSchema.getProtoSchema(clazz);
                        if (schema == null) {
                            throw new NullPointerException(LogUtils.format("{} 找不到对应的schema", clazz));
                        }
                        Class<?> old = classMap.put(schema.getProtoExID(), clazz);
                        if (old != null) {
                            throw new IllegalArgumentException(LogUtils.format("{} protoID 与 {} protoID 都为 {}", clazz, old, schema.getProtoExID()));
                        }
                    }
                    LOGGER.info("开始初始化 ProtoSchema 完成! 耗时 {} ms", RunningChecker.end(this.getClass()).cost());

                } catch (Throwable e) {
                    ProtoExSchemaIniter.this.exception = new IllegalStateException(e);
                    throw new RuntimeException(LogUtils.format("获取 {} 类 ProtoExSchema 错误", clazz), ProtoExSchemaIniter.this.exception);
                } finally {
                    ProtoExSchemaIniter.this.latch.countDown();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private boolean waitSuccess() {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            this.exception = new RuntimeException(e);
        }
        return this.exception == null;
    }

    public Throwable getException() {
        return this.exception;
    }

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_10);
    }

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public boolean waitInitialized() {
        if (!this.waitSuccess())
            throw this.exception;
        return this.exception == null;
    }

}
