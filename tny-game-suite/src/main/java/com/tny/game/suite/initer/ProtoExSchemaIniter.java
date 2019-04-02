package com.tny.game.suite.initer;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.runtime.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.runtime.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;

import static com.tny.game.common.utils.StringAide.*;

public class ProtoExSchemaIniter implements AppPrepareStart {

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

    private static void loadClasses(Collection<Class<?>> classes) {
        LOGGER.info("启动初始化ProtoSchema任务!");
        // forkJoinTask = ForkJoinPool.commonPool().submit(() -> {
            Class<?> clazz;
            RunningChecker.start(ProtoExSchemaIniter.class);
            LOGGER.info("开始初始化 ProtoSchema .......");
            Map<Integer, Class<?>> classMap = new HashMap<>();
            for (Class<?> cl : classes) {
                clazz = cl;
                ProtoExSchema<?> schema = RuntimeProtoExSchema.getProtoSchema(clazz);
                if (schema == null) {
                    throw new NullPointerException(format("{} 找不到对应的schema", clazz));
                }
                Class<?> old = classMap.put(schema.getProtoExID(), clazz);
                if (old != null) {
                    throw new IllegalArgumentException(format("{} protoID 与 {} protoID 都为 {}", clazz, old, schema.getProtoExID()));
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
    public void prepareStart() {
        this.waitSuccess();
    }

}
