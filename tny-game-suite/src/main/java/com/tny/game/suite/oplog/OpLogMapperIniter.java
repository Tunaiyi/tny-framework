package com.tny.game.suite.oplog;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.tny.game.common.lifecycle.*;
import com.tny.game.net.base.*;
import com.tny.game.oplog.*;
import com.tny.game.oplog.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM_OPLOG, GAME})
public class OpLogMapperIniter implements AppPrepareStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogMapperIniter.class);

    @Resource
    private AppContext appContext;

    private Exception exception;

    private ForkJoinTask<?> task;

    @PostConstruct
    public void init() {
        LOGGER.info("启动初始化OpLogMapper任务!");
        this.task = ForkJoinPool.commonPool().submit(() -> {
            Class<?> clazz = null;
            try {
                ClassSelector selector = ClassSelector.instance(ClassFilterHelper.ofInclude((reader) ->
                        ClassFilterHelper.matchSuper(reader, Snapshot.class)
                ));
                ClassScanner.instance()
                        .addSelector(selector)
                        .scan(appContext.getScanPackages());
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
                throw new RuntimeException(format("获取 {} 类错误", clazz), OpLogMapperIniter.this.exception);
            }
        });
        LOGGER.info("初始化OpLogMapper任务完成!");
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }

    @Override
    public void prepareStart() throws Exception {
        this.task.get();
        if (this.exception != null)
            throw this.exception;
    }

}
