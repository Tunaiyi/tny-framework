package com.tny.game.net.base;

import com.tny.game.common.unit.annotation.Unit;

/**
 * Created by Kun Yang on 2017/3/31.
 */
public class AppUtils {

    public static final String getUnitName(Object value) {
        Class<?> clazz = value.getClass();
        Unit unit = clazz.getAnnotation(Unit.class);
        if (unit != null)
            return unit.value();
        return clazz.getSimpleName();
    }

}
