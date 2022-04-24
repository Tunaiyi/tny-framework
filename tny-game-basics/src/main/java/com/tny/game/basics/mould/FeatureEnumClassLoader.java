package com.tny.game.basics.mould;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import org.slf4j.*;

import static com.tny.game.scanner.selector.EnumClassSelector.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class FeatureEnumClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(FeatureEnumClassLoader.class);

    @ClassSelectorProvider
    static ClassSelector openModesSelector() {
        return createSelector(FeatureOpenMode.class, FeatureOpenModes::register);
    }

    @ClassSelectorProvider
    static ClassSelector featuresSelector() {
        return createSelector(Feature.class, Features::register);
    }

    @ClassSelectorProvider
    static ClassSelector mouldsSelector() {
        return createSelector(Mould.class, Moulds::register);
    }

}
