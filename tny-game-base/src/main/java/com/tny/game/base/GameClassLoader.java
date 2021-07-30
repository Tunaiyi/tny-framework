package com.tny.game.base;

import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.*;

import java.util.List;
import java.util.function.Consumer;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class GameClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(GameClassLoader.class);

    @SuppressWarnings("unchecked")
    public static <E, A extends Enum<A>> ClassSelector createSelector(Class<E> type, Consumer<E> handler) {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(type))
                .setHandler((classes) -> classes.forEach(codeClass -> {
                    if (codeClass.isEnum()) {
                        List<A> enumList = EnumUtils.getEnumList((Class<A>)codeClass);
                        enumList.forEach(v -> handler.accept(as(v)));
                    }
                    LOGGER.info("GameClassLoader.selector for {} at {}", type, codeClass);
                }));
    }

}
