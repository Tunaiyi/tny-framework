package com.tny.game.scanner.selector;

import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class EnumClassSelector {

    public static final Logger LOGGER = LoggerFactory.getLogger(EnumClassSelector.class);

    @SuppressWarnings("unchecked")
    public static <E, A extends Enum<A>> ClassSelector createSelector(Class<E> type, Consumer<E> handler) {
        return ClassSelector.create()
                .addFilter(SubOfClassFilter.ofInclude(type))
                .setHandler((classes) -> classes.forEach(codeClass -> {
                    if (codeClass.isEnum()) {
                        List<A> enumList = EnumUtils.getEnumList((Class<A>)codeClass);
                        enumList.forEach(v -> handler.accept((E)v));
                    }
                    LOGGER.info("EnumClassSelector.selector for {} at {}", type, codeClass);
                }));
    }

}
