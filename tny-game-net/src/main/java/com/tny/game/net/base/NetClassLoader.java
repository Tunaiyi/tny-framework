package com.tny.game.net.base;

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
public class NetClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetClassLoader.class);

    @ClassSelectorProvider
    static ClassSelector appTypeSelector() {
        return createSelector(AppType.class, AppTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector scopeTypeSelector() {
        return createSelector(AppScope.class, AppScopes::register);
    }

    @ClassSelectorProvider
    static ClassSelector messagerTypesSelector() {
        return createSelector(MessagerType.class, MessagerTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector serviceTypesSelector() {
        return createSelector(RpcServiceType.class, RpcServiceTypes::register);
    }

}
