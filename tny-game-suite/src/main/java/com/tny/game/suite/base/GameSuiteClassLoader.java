package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.base.module.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import com.tny.game.suite.base.module.*;
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
public class GameSuiteClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(GameSuiteClassLoader.class);

    @SuppressWarnings("unchecked")
    private static <E, A extends Enum<A>> ClassSelector createSelector(Class<E> type, Consumer<E> handler) {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(type))
                .setHandler((classes) -> classes.forEach(codeClass -> {
                    if (codeClass.isEnum()) {
                        List<A> enumList = EnumUtils.getEnumList((Class<A>)codeClass);
                        enumList.forEach(v -> handler.accept(as(v)));
                    }
                    LOGGER.info("NetClassLoader.appTypeSelector : {}", codeClass);
                }));
    }

    @ClassSelectorProvider
    static ClassSelector itemTypesSelector() {
        return createSelector(ItemType.class, ItemTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector demandTypesSelector() {
        return createSelector(DemandType.class, DemandTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector actionsSelector() {
        return createSelector(Action.class, Actions::register);
    }

    @ClassSelectorProvider
    static ClassSelector behaviorsSelector() {
        return createSelector(Behavior.class, Behaviors::register);
    }

    @ClassSelectorProvider
    static ClassSelector abilitiesSelector() {
        return createSelector(Ability.class, Abilities::register);
    }

    @ClassSelectorProvider
    static ClassSelector demandParamsSelector() {
        return createSelector(DemandParam.class, DemandParams::register);
    }

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
