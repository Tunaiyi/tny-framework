package com.tny.game.suite.oplog;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.tny.game.LogUtils;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.log4j2.OpLogMapper;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.filter.ClassFilterHelper;
import com.tny.game.suite.utils.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Component
@Profile({"suite.base", "suite.all"})
public class OpLogMapperIniter implements ServerPreStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogMapperIniter.class);

    private Exception exception;

    private CountDownLatch latch = new CountDownLatch(1);

    @PostConstruct
    public void init() throws InstantiationException, IllegalAccessException {
        LOGGER.info("启动初始化OpLogMapper任务!");
        Thread thread = new Thread(() -> {
            Class<?> clazz = null;
            try {
                ClassScanner scanner = new ClassScanner()
                        .addFilter(ClassFilterHelper.ofInclude((reader) ->
                                ClassFilterHelper.matchSuper(reader, Snapshot.class)
                        ));
                Set<Class<?>> classes = scanner.getClasses(Configs.getScanPathArray());
                for (Class<?> cl : classes) {
                    int modifier = cl.getModifiers();
                    if (Modifier.isAbstract(modifier))
                        continue;
                    Snapshot snapShot = (Snapshot) cl.newInstance();
                    OpLogMapper.getMapper().registerSubtypes(new NamedType(cl, snapShot.getType().toString()));
                }
            } catch (Throwable e) {
                OpLogMapperIniter.this.exception = new IllegalStateException(e);
                throw new RuntimeException(LogUtils.format("获取 {} 类错误", clazz), OpLogMapperIniter.this.exception);
            } finally {
                OpLogMapperIniter.this.latch.countDown();
            }
        });
        thread.start();
        LOGGER.info("初始化OpLogMapper任务完成!");
    }

    @Override
    public InitLevel getInitLevel() {
        return InitLevel.LEVEL_10;
    }

    @Override
    public boolean waitInitialized() throws Exception {
        this.latch.await();
        if (this.exception != null)
            throw this.exception;
        return true;
    }

    @Override
    public void initialize() throws Exception {
        waitInitialized();
    }

}
