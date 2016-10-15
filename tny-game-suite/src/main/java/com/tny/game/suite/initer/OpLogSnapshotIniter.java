package com.tny.game.suite.initer;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.tny.game.common.RunningChecker;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.log4j2.OpLogMapper;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.filter.ClassIncludeFilter;
import com.tny.game.scanner.filter.SubOfClassFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class OpLogSnapshotIniter implements ServerPreStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogSnapshotIniter.class);

    private ForkJoinTask<?> forkJoinTask;

    private List<String> scanPaths = new ArrayList<>();

    public OpLogSnapshotIniter(List<String> scanPaths) {
        this.scanPaths.addAll(scanPaths);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initSchemaAsync() {
        LOGGER.info("启动初始化ProtoSchema任务!");
        forkJoinTask = ForkJoinPool.commonPool().submit(() -> {

            // try {
            ClassScanner scanner = new ClassScanner()
                    .addFilter(SubOfClassFilter.ofInclude(Snapshot.class))
                    .addFilter(ClassIncludeFilter.of(r -> !r.getClassMetadata().isAbstract()));
            Set<Class<?>> classes = new HashSet<>();
            classes.addAll(scanner.getClasses(scanPaths.toArray(new String[scanPaths.size()])));
            RunningChecker.start(this.getClass());
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
            LOGGER.info("开始初始化 OpLogSnapshot 完成! 耗时 {} ms", RunningChecker.end(this.getClass()).cost());
        });
    }

    private boolean waitSuccess() {
        forkJoinTask.quietlyJoin();
        return forkJoinTask.isCompletedNormally();
    }

    public Throwable getException() {
        return forkJoinTask.getException();
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
            throw new RuntimeException(forkJoinTask.getException());
        return forkJoinTask.getException() == null;
    }

}
