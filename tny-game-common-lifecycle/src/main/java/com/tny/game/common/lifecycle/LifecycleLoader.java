package com.tny.game.common.lifecycle;

import com.google.common.collect.ImmutableList;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleLoader.class);

    private static volatile List<StaticInitiator> Initiators = Collections.emptyList();

    private static final ClassSelector SELECTOR = ClassSelector.instance()
            .addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
            .setHandler(classes ->
                    Initiators = ImmutableList.copyOf(classes.stream()
                            .sorted((one, other) -> {
                                AsLifecycle oneLifecycle = one
                                        .getAnnotation(
                                                AsLifecycle.class);
                                AsLifecycle otherLifecycle = other
                                        .getAnnotation(
                                                AsLifecycle.class);
                                return otherLifecycle.order() -
                                        oneLifecycle.order();
                            })
                            .map(StaticInitiator::instance)
                            .collect(Collectors.toList())));

    @ClassSelectorProvider
    private static ClassSelector selector() {
        return SELECTOR;
    }

    private LifecycleLoader() {
    }

    public static List<StaticInitiator> getStaticInitiators() {
        return Initiators;
    }

}
