package com.tny.game.common.lifecycle;

import com.tny.game.common.lifecycle.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * <p>
 */
public final class LifecycleLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleLoader.class);

	public static final Comparator<Class<?>> LIFECYCLE_COMPARATOR = (one, other) -> {
		AsLifecycle oneLifecycle = one
				.getAnnotation(
						AsLifecycle.class);
		AsLifecycle otherLifecycle = other
				.getAnnotation(
						AsLifecycle.class);
		return otherLifecycle.order() -
				oneLifecycle.order();
	};

	private static final Set<StaticInitiator> INITIATORS = new ConcurrentSkipListSet<>();

	private static final ClassSelector SELECTOR = ClassSelector.create()
			.addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
			.setHandler(classes -> classes.forEach(LifecycleLoader::register));

	@ClassSelectorProvider
	private static ClassSelector selector() {
		return SELECTOR;
	}

	private LifecycleLoader() {
	}

	public static void register(Class<?> clazz) {
		StaticInitiator initiator = StaticInitiator.instance(clazz);
		INITIATORS.add(initiator);
	}

	public static Set<StaticInitiator> getStaticInitiators() {
		return Collections.unmodifiableSet(INITIATORS);
	}

}
