package com.tny.game.loader.lifecycle;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.annotaion.*;
import com.tny.game.common.utils.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 */
public final class LifecycleLoader {

    private static Logger LOGGER = LoggerFactory.getLogger(LifecycleLoader.class);

    private static ClassSelector selector = ClassSelector.instance()
            .addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
            .setHandler(classes -> classes.stream()
                    .sorted((one, other) -> {
                        AsLifecycle oneLifecycle = one.getAnnotation(AsLifecycle.class);
                        AsLifecycle otherLifecycle = other.getAnnotation(AsLifecycle.class);
                        return otherLifecycle.order() - oneLifecycle.order();
                    })
                    .forEach(c -> {
                                int number = 0;
                                long current = System.currentTimeMillis();
                                try {
                                    number++;
                                    LOGGER.info("服务生命周期 StaticInit # 处理器 [{}] index {}", c, number);
                                    ExeAide.runUnchecked(() -> StaticIniter.instance(c).init());
                                    LOGGER.info("服务生命周期 StaticInit # 处理器 [{}] index {} | -> 耗时 {} 完成", c, number, System.currentTimeMillis() - current);
                                } catch (Throwable e) {
                                    LOGGER.error("服务生命周期 StaticInit # 处理器 [{}] index {} | -> 异常", c, number, e);
                                }
                            }
                    ));

    @ClassSelectorProvider
    private static ClassSelector selector() {
        return selector;
    }

    private LifecycleLoader() {
    }

}
