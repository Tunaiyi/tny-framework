package com.tny.game.base.module;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import org.slf4j.*;

import static com.tny.game.base.GameClassLoader.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class GameModuleEnumClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(GameModuleEnumClassLoader.class);

    @ClassSelectorProvider
    static ClassSelector openModesSelector() {
        return createSelector(OpenMode.class, OpenModes::register);
    }

    @ClassSelectorProvider
    static ClassSelector featuresSelector() {
        return createSelector(Feature.class, Features::register);
    }

    @ClassSelectorProvider
    static ClassSelector modulesSelector() {
        return createSelector(Module.class, Modules::register);
    }

}
