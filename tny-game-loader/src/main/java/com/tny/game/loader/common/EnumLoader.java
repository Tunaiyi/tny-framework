package com.tny.game.loader.common;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.apache.commons.lang3.EnumUtils;

/**
 * 读取 tny-factory.properties 配置中
 * com.tny.game.loader.common.EnumLoader 配置相关的枚举会提前读取
 * Created by Kun Yang on 16/9/9.
 */
public final class EnumLoader {

    private EnumLoader() {
    }

    @SuppressWarnings("unchecked")
    @ClassSelectorProvider
    public static <E extends Enum<E>> ClassSelector selector() {
        return ClassSelector.instance()
                .addFilter(SubOfClassFilter.ofInclude(AutoClassScanConfigure.getClasses(EnumLoader.class)))
                .setHandler((classes) -> classes.stream()
                        .filter(Class::isEnum)
                        .map(c -> (Class<E>)c)
                        .forEach(EnumUtils::getEnumList)
                );
    }

}
