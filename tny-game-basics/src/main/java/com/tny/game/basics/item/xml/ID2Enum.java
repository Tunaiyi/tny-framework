package com.tny.game.basics.item.xml;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.tny.game.common.enums.*;
import org.apache.commons.lang3.EnumUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

import static com.tny.game.common.utils.StringAide.*;

/**
 * string转枚举
 *
 * @param <T>
 * @author KGTny
 */
public class ID2Enum<ID, T extends Enum<T> & Enumerable<ID>> extends AbstractSingleValueConverter {

    private List<Class<T>> enumClassList = new ArrayList<>();

    private HashMap<Object, Enum<?>> enumMap = new HashMap<>();

    private Function<String, ID> fn;

    public ID2Enum(Function<String, ID> fn, Collection<Class<T>> enumClasses) {
        super();
        this.fn = fn;
        for (Class<T> clazz : enumClasses) {
            List<T> enums = EnumUtils.getEnumList(clazz);
            for (T e : enums) {
                Enum<?> oldEnum = this.enumMap.put(e.getId(), e);
                if (oldEnum != null) {
                    throw new IllegalArgumentException(format("{}.{} 与 {}.{} name 相同!",
                            oldEnum.getClass(), oldEnum.name(),
                            e.getClass(), e.name()));
                }
            }
        }
        this.enumClassList.addAll(enumClasses);
    }

    @SuppressWarnings("unchecked")
    public ID2Enum(Function<String, ID> fn, Class<T>... enumClasses) {
        this(fn, Arrays.asList(enumClasses));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public boolean canConvert(Class clazz) {
        for (Class<T> enumClass : this.enumClassList) {
            if (clazz.isAssignableFrom(enumClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object fromString(String value) {
        Object enumObject = this.enumMap.get(this.fn.apply(value));
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", value));
        }
        return enumObject;
    }

}
