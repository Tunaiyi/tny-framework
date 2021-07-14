package com.tny.game.net.message.codec;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.tny.game.common.runtime.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;

public final class JProtobufSchemaLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JProtobufSchemaLoader.class);

    private static final ClassSelector selector = ClassSelector
            .instance(AnnotationClassFilter.ofInclude(ProtobufClass.class))
            .setHandler(JProtobufSchemaLoader::loadClasses);

    @ClassSelectorProvider
    protected static ClassSelector selector() {
        return selector;
    }

    private JProtobufSchemaLoader() {
    }

    private static void loadClasses(Collection<Class<?>> classes) {
        LOGGER.info("启动初始化JProtobufSchema任务!");
        // forkJoinTask = ForkJoinPool.commonPool().submit(() -> {
        RunChecker.trace(JProtobufSchemaLoader.class);
        LOGGER.info("开始初始化 JProtobufSchema .......");
        Map<Integer, Class<?>> classMap = new HashMap<>();
        ProtobufObjectCodecorFactory factory = ProtobufObjectCodecorFactory.getDefault();
        for (Class<?> cl : classes) {
            ProtobufType protobufType = cl.getAnnotation(ProtobufType.class);
            if (protobufType != null) {
                factory.getCodecor(cl);
            }
        }
        LOGGER.info("开始初始化 JProtobufSchema 完成! 耗时 {} ms", RunChecker.end(JProtobufSchemaLoader.class).costTime());
        selector.clear();
    }

}
