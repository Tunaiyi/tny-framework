package com.tny.game.suite.initer;

import com.tny.game.LogUtils;
import com.tny.game.common.RunningChecker;
import com.tny.game.lifecycle.LifecycleLevel;
import com.tny.game.lifecycle.PrepareStarter;
import com.tny.game.lifecycle.ServerPrepareStart;
import com.tny.game.net.message.protoex.ProtoExMessage;
import com.tny.game.protoex.ProtoExSchema;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.field.runtime.RuntimeProtoExSchema;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.AnnotationClassFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;

public class ProtoExSchemaIniter implements ServerPrepareStart {

    private static final Logger LOGGER = LoggerFactory.getLogger("proto");

    private static ClassSelector selector = ClassSelector
            .instance(AnnotationClassFilter.ofInclude(ProtoEx.class))
            .setHandler(ProtoExSchemaIniter::loadClasses);

    private static ForkJoinTask<?> forkJoinTask;

    public static ClassSelector selector() {
        return selector;
    }

    public ProtoExSchemaIniter() {
    }

    @SuppressWarnings("unchecked")
    private static void loadClasses(Set<Class<?>> classes) {
        LOGGER.info("启动初始化ProtoSchema任务!");
        // forkJoinTask = ForkJoinPool.commonPool().submit(() -> {
            Class<?> clazz;
            RunningChecker.start(ProtoExSchemaIniter.class);
            LOGGER.info("开始初始化 ProtoSchema .......");
            Map<Integer, Class<?>> classMap = new HashMap<>();
            RuntimeProtoExSchema.getProtoSchema(ProtoExMessage.class);
            // RuntimeProtoExSchema.getProtoSchema(ProtoExResponse.class);
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
            LOGGER.info("开始初始化 ProtoSchema 完成! 耗时 {} ms", RunningChecker.end(ProtoExSchemaIniter.class).cost());
            selector = null;
        // });
    }

    public Throwable getException() {
        return forkJoinTask.getException();
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    private void waitSuccess() {
        ForkJoinTask<?> forkJoinTask = ProtoExSchemaIniter.forkJoinTask;
        if (forkJoinTask == null)
            return;
        try {
            forkJoinTask.join();
        } finally {
            ProtoExSchemaIniter.forkJoinTask = null;
        }
    }

    @Override
    public void prepareStart() throws Exception {
        this.waitSuccess();
    }

}
