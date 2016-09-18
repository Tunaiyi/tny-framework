package com.tny.game.suite.initer;

import com.tny.game.LogUtils;
import com.tny.game.base.item.Ability;
import com.tny.game.base.item.ItemType;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.Behavior;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.OpenMode;
import com.tny.game.common.RunningChecker;
import com.tny.game.common.enums.EnumUtils;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.scanner.ClassScanner;
import com.tny.game.scanner.filter.SubOfClassFilter;
import com.tny.game.suite.core.ScopeType;
import com.tny.game.suite.core.ServerType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Kun Yang on 16/9/9.
 */
public class EnumCheckerIniter implements ServerPreStart {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumCheckerIniter.class);

    private RuntimeException exception;

    private CountDownLatch latch = new CountDownLatch(1);

    private List<String> scanPaths = new ArrayList<>();

    public EnumCheckerIniter(List<String> scanPaths) {
        this.scanPaths.addAll(scanPaths);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initSchemaAsync() {
        LOGGER.info("启动初始化ProtoSchema任务!");
        Runnable task = () -> {
            Class<?> clazz = null;
            try {
                RunningChecker.start(this.getClass());
                ClassScanner scanner = new ClassScanner().
                        addFilter(SubOfClassFilter.ofInclude(
                                ResultCode.class,
                                Ability.class,
                                ScopeType.class,
                                ServerType.class,
                                ItemType.class,
                                Action.class,
                                Behavior.class,
                                DemandType.class,
                                DemandParam.class,
                                Module.class,
                                Feature.class,
                                OpenMode.class));
                LOGGER.info("开始初始化 ProtoSchema .......");
                Set<Class<?>> classes = scanner.getClasses(scanPaths.toArray(new String[scanPaths.size()]));
                classes.stream()
                        .filter(Class::isEnum)
                        .map(c -> (Class<Enum>) c)
                        .forEach(EnumUtils::getEnumList);
                LOGGER.info("扫描到枚举类型 : \n{}", StringUtils.join(classes, "\n"));
                LOGGER.info("开始初始化 ProtoSchema 完成! 耗时 {} ms", RunningChecker.end(this.getClass()).cost());
            } catch (Throwable e) {
                EnumCheckerIniter.this.exception = new IllegalStateException(e);
                throw new RuntimeException(LogUtils.format("获取 {} 类 ProtoExSchema 错误", clazz), exception);
            } finally {
                EnumCheckerIniter.this.latch.countDown();
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
