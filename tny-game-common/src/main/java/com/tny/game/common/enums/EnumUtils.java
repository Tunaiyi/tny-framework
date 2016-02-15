package com.tny.game.common.enums;

import com.tny.game.LogUtils;

/**
 * Created by Kun Yang on 16/2/3.
 */
public class EnumUtils extends org.apache.commons.lang3.EnumUtils {

    public static <I, E extends Enum<E> & EnumID<I>> E getByID(Class<E> enumClass, I id) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getID().equals(id))
                return e;
        }
        throw new NullPointerException(LogUtils.format("ID 为 {} 的 {} 枚举实例不存在", id, enumClass));
    }

}
