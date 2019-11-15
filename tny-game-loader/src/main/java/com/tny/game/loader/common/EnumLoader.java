package com.tny.game.loader.common;

import com.tny.game.common.enums.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

/**
 * Created by Kun Yang on 16/9/9.
 */
public final class EnumLoader {

    private EnumLoader() {
    }

    @SuppressWarnings("unchecked")
    @ClassSelectorProvider
    public static ClassSelector selector() {
        return ClassSelector.instance()
                            .addFilter(SubOfClassFilter.ofInclude(AutoClassScanConfigure.getClasses(EnumLoader.class)))
                            .setHandler((classes) -> classes.stream()
                                                            .filter(Class::isEnum)
                                                            .map(c -> (Class<Enum>) c)
                                                            .forEach(EnumAide::getEnumList)
                            );
    }

}
