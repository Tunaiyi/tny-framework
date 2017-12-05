package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.utils.Logs;
import org.apache.commons.lang3.EnumUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * string转枚举
 *
 * @param <T>
 * @author KGTny
 */
public class String2Enum<T extends Enum<T>> extends AbstractSingleValueConverter {

    private List<Class<T>> enumClassList = new ArrayList<>();

    private HashMap<String, Enum<?>> enumMap = new HashMap<>();

    public String2Enum(Collection<Class<T>> enumClasses) {
        super();
        for (Class<T> clazz : enumClasses) {
            List<T> enums = EnumUtils.getEnumList(clazz);
            for (T e : enums) {
                Enum<?> oldEnum = this.enumMap.put(e.name(), e);
                if (oldEnum != null) {
                    throw new IllegalArgumentException(Logs.format("{}.{} 与 {}.{} name 相同!",
                            oldEnum.getClass(), oldEnum.name(),
                            e.getClass(), e.name()));
                }
            }
        }
        this.enumClassList.addAll(enumClasses);
    }

    @SuppressWarnings("unchecked")
    public String2Enum(Class<T>... enumClasses) {
        this(Arrays.asList(enumClasses));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean canConvert(Class clazz) {
        for (Class<T> enumClass : this.enumClassList) {
            if (clazz != Object.class && clazz.isAssignableFrom(enumClass))
                return true;
        }
        return false;
    }

    @Override
    public Object fromString(String value) {
        Object enumObject = this.enumMap.get(value);
        if (enumObject == null)
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", value));
        return enumObject;
    }
}
