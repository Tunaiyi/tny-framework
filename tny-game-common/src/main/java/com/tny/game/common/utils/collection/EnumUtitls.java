package com.tny.game.common.utils.collection;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class EnumUtitls {

    @SafeVarargs
    public static <E extends Enum<E>> Set<E> exclude(Class<E> enumClass, E... enums) {
        Set<E> eSet = EnumSet.allOf(enumClass);
        for (E e : enums)
            eSet.remove(e);
        return eSet;
    }

    public static String nameFormat(Enum<?> enumObject) {
        return nameFormat(enumObject.name());
    }

    public static String nameFormat(String name) {
        String[] words = name.split("_");
        String formatedName = null;
        int index = 0;
        for (String word : words) {
            if (index == 0)
                formatedName = word.toLowerCase();
            else {
                formatedName += word.substring(0, 1);
                if (word.length() > 1) {
                    formatedName += word.substring(1).toLowerCase();
                }
            }
            index++;
        }
        return formatedName;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] splitEnum(String str, char separatorChar, Class<T> clazz) {
        String[] temps = StringUtils.split(str, separatorChar);
        T[] results = (T[]) Array.newInstance(clazz, temps.length);
        for (int i = 0; i < temps.length; i++) {
            results[i] = Enum.valueOf(clazz, temps[i]);
        }
        return results;
    }

    public static <T extends Enum<T>> Set<T> splitEnumSet(String str, char separatorChar, Class<T> clazz) {
        String[] temps = StringUtils.split(str, separatorChar);
        Set<T> enumSet = EnumSet.noneOf(clazz);
        for (int i = 0; i < temps.length; i++) {
            enumSet.add(Enum.valueOf(clazz, temps[i]));
        }
        return enumSet;
    }

    public static <T extends Enum<T>> List<T> splitEnumList(String str, char separatorChar, Class<T> clazz) {
        return Arrays.asList(splitEnum(str, separatorChar, clazz));
    }

}
