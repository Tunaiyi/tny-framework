package com.tny.game.suite.initer;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.tny.game.common.runtime.RunningChecker;
import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PrepareStarter;
import com.tny.game.common.lifecycle.AppPrepareStart;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.utils.OpLogMapper;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.ClassFilterHelper;
import com.tny.game.scanner.filter.SubOfClassFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class OpLogSnapshotIniter implements AppPrepareStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogSnapshotIniter.class);

    private static ForkJoinTask<?> forkJoinTask;

    private List<String> scanPaths = new ArrayList<>();

    private static ClassSelector selector = ClassSelector
            .instance(
                    SubOfClassFilter.ofInclude(Snapshot.class),
                    ClassFilterHelper.ofExclude(r -> r.getClassMetadata().isAbstract()))
            .setHandler(OpLogSnapshotIniter::loadClasses);

    public static ClassSelector selector() {
        return selector;
    }

    public OpLogSnapshotIniter(List<String> scanPaths) {
        this.scanPaths.addAll(scanPaths);
    }

    @SuppressWarnings("unchecked")
    public static void loadClasses(Collection<Class<?>> classes) {
        LOGGER.info("启动初始化ProtoSchema任务!");
        forkJoinTask = ForkJoinPool.commonPool().submit(() -> {
            // try {
            RunningChecker.start(OpLogSnapshotIniter.class);
            LOGGER.info("开始初始化 OpLogSnapshot .......");
            for (Class<?> cl : classes) {
                if (Snapshot.class.isAssignableFrom(cl)) {
                    Snapshot snapshot;
                    try {
                        snapshot = (Snapshot) cl.newInstance();
                        OpLogMapper.getMapper().registerSubtypes(new NamedType(cl, snapshot.getType().toString()));
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                    }
                }
            }
            OpLogSnapshotIniter.selector = null;
            LOGGER.info("开始初始化 OpLogSnapshot 完成! 耗时 {} ms", RunningChecker.end(OpLogSnapshotIniter.class).cost());
        });
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    private void waitSuccess() {
        ForkJoinTask<?> forkJoinTask = OpLogSnapshotIniter.forkJoinTask;
        if (forkJoinTask == null)
            return;
        try {
            forkJoinTask.join();
        } finally {
            OpLogSnapshotIniter.forkJoinTask = null;
        }
    }

    @Override
    public void prepareStart() throws Exception {
        this.waitSuccess();
    }

}
