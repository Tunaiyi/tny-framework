package com.tny.game.loader.lifecycle;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.annotaion.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 */
public final class LifecycleLoader {

    private static Logger LOGGER = LoggerFactory.getLogger(LifecycleLoader.class);

    private static volatile List<StaticIniter> initers = Collections.emptyList();

    private static ClassSelector selector = ClassSelector.instance()
                                                         .addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
                                                         .setHandler(classes ->
                                                                 initers = ImmutableList.copyOf(classes.stream()
                                                                                                       .sorted((one, other) -> {
                                                                                                           AsLifecycle oneLifecycle = one
                                                                                                                   .getAnnotation(AsLifecycle.class);
                                                                                                           AsLifecycle otherLifecycle = other
                                                                                                                   .getAnnotation(AsLifecycle.class);
                                                                                                           return otherLifecycle.order() -
                                                                                                                  oneLifecycle.order();
                                                                                                       })
                                                                                                       .map(StaticIniter::instance)
                                                                                                       .collect(Collectors.toList())));

    @ClassSelectorProvider
    private static ClassSelector selector() {
        return selector;
    }

    private LifecycleLoader() {
    }

    public static List<StaticIniter> getStaticIniters() {
        return initers;
    }

}
