package com.tny.game.basics.item.xml;

import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
public class EnumKeyDeserializer<T extends Enum<T>> extends KeyDeserializer {

    private final HashMap<String, Enum<?>> enumMap = new HashMap<>();

    public EnumKeyDeserializer(Class<T> enumClass) {
        this(Collections.singletonList(enumClass));
    }

    public EnumKeyDeserializer(List<Class<T>> enumClasses) {
        for (Class<T> clazz : enumClasses) {
            List<T> enums = EnumUtils.getEnumList(clazz);
            for (T e : enums) {
                Enum<?> oldEnum = this.enumMap.put(e.name(), e);
                if (oldEnum != null) {
                    throw new IllegalArgumentException(format("{}.{} 与 {}.{} name 相同!",
                            oldEnum.getClass(), oldEnum.name(),
                            e.getClass(), e.name()));
                }
            }
        }
    }

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        Object enumObject = this.enumMap.get(key);
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", key));
        }
        return enumObject;
    }

}
