package com.tny.game.suite.oplog;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.tny.game.suite.base.Logs;
import com.tny.game.common.lifecycle.LifecycleLevel;
import com.tny.game.common.lifecycle.PrepareStarter;
import com.tny.game.common.lifecycle.ServerPrepareStart;
import com.tny.game.oplog.Snapshot;
import com.tny.game.oplog.utils.OpLogMapper;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.ClassSelector;
import com.tny.game.scanner.filter.ClassFilterHelper;
import com.tny.game.suite.utils.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM_OPLOG, GAME})
public class OpLogMapperIniter implements ServerPrepareStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogMapperIniter.class);

    private Exception exception;

    private CountDownLatch latch = new CountDownLatch(1);

    @PostConstruct
    public void init() throws InstantiationException, IllegalAccessException {
        LOGGER.info("启动初始化OpLogMapper任务!");
        Thread thread = new Thread(() -> {
            Class<?> clazz = null;
            try {
                ClassSelector selector = ClassSelector.instance(ClassFilterHelper.ofInclude((reader) ->
                        ClassFilterHelper.matchSuper(reader, Snapshot.class)
                ));
                ClassScanner.instance()
                        .addSelector(selector)
                        .scan(Configs.getScanPathArray());
                Collection<Class<?>> classes = selector.getClasses();
                for (Class<?> cl : classes) {
                    int modifier = cl.getModifiers();
                    if (Modifier.isAbstract(modifier))
                        continue;
                    Snapshot snapShot = (Snapshot) cl.newInstance();
                    OpLogMapper.getMapper().registerSubtypes(new NamedType(cl, snapShot.getType().toString()));
                }
            } catch (Throwable e) {
                OpLogMapperIniter.this.exception = new IllegalStateException(e);
                throw new RuntimeException(Logs.format("获取 {} 类错误", clazz), OpLogMapperIniter.this.exception);
            } finally {
                OpLogMapperIniter.this.latch.countDown();
            }
        });
        thread.start();
        LOGGER.info("初始化OpLogMapper任务完成!");
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() throws Exception {
        this.latch.await();
        if (this.exception != null)
            throw this.exception;
    }

}
