package com.tny.game.protoex.field;

import java.lang.reflect.Field;

/**
 * 字段描述工厂接口
 *
 * @param <T>
 * @author KGTny
 */
public interface FieldDescFactory<T> {

    /**
     * 创建field的字段描述
     *
     * @param field
     * @return
     */
    public abstract FieldDesc<T> create(Field field);

}
