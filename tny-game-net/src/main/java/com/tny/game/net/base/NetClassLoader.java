package com.tny.game.net.base;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class NetClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetClassLoader.class);

    @ClassSelectorProvider
    @SuppressWarnings("unchecked")
    static <A extends Enum<A> & AppType> ClassSelector appTypeSelector() {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(AppTypes.class))
                .setHandler((classes) -> classes.forEach(codeClass -> {
                    if (codeClass.isEnum()) {
                        List<A> enumList = EnumUtils.getEnumList((Class<A>)codeClass);
                        enumList.forEach(AppTypes::register);
                    }
                    LOGGER.info("NetClassLoader.appTypeSelector : {}", codeClass);
                }));
    }

    @ClassSelectorProvider
    @SuppressWarnings("unchecked")
    static <S extends Enum<S> & ScopeType> ClassSelector scopeTypeSelector() {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(AppTypes.class))
                .setHandler((classes) -> classes.forEach(codeClass -> {
                    if (codeClass.isEnum()) {
                        List<S> enumList = EnumUtils.getEnumList((Class<S>)codeClass);
                        enumList.forEach(ScopeTypes::register);
                    }
                    LOGGER.info("NetClassLoader.appTypeSelector : {}", codeClass);
                }));
    }

}
