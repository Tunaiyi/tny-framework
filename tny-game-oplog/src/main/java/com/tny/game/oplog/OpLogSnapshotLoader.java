package com.tny.game.oplog;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.tny.game.common.runtime.*;
import com.tny.game.oplog.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.Collection;
import java.util.concurrent.*;

public class OpLogSnapshotLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogSnapshotLoader.class);

    private static ForkJoinTask<?> forkJoinTask;

    private static ClassSelector selector = ClassSelector
            .create(
                    SubOfClassFilter.ofInclude(Snapshot.class),
                    ClassFilterHelper.ofExclude(r -> r.getClassMetadata().isAbstract()))
            .setHandler(OpLogSnapshotLoader::loadClasses);

    @ClassSelectorProvider
    private static ClassSelector selector() {
        return selector;
    }

    private static void loadClasses(Collection<Class<?>> classes) {
        LOGGER.info("启动初始化 ProtoSchema 任务!");
        forkJoinTask = ForkJoinPool.commonPool().submit(() -> {
            // try {
            RunChecker.trace(OpLogSnapshotLoader.class);
            LOGGER.info("开始初始化 OpLogSnapshot .......");
            for (Class<?> cl : classes) {
                if (Snapshot.class.isAssignableFrom(cl)) {
                    Snapshot snapshot;
                    try {
                        snapshot = (Snapshot)cl.newInstance();
                        OpLogMapper.getMapper().registerSubtypes(new NamedType(cl, snapshot.getType().toString()));
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            }
            OpLogSnapshotLoader.selector = null;
            LOGGER.info("开始初始化 OpLogSnapshot 完成! 耗时 {} ms", RunChecker.end(OpLogSnapshotLoader.class).costMicroTime());
        });
    }

    public static void waitLoad() {
        ForkJoinTask<?> forkJoinTask = OpLogSnapshotLoader.forkJoinTask;
        if (forkJoinTask == null) {
            return;
        }
        try {
            forkJoinTask.join();
        } finally {
            OpLogSnapshotLoader.forkJoinTask = null;
        }
    }

}
