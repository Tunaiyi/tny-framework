package com.tny.game.protoex.field;

/**
 * 字段描述接口
 *
 * @param <T>
 * @author KGTny
 */
public interface FieldDesc<T> extends FieldOptions<T> {

    /**
     * 将value设置到message中与当前描述对应的字段
     *
     * @param message
     * @param value
     */
    public void setValue(Object message, T value);

    /**
     * 读取message中与当前描述对应的字段的值
     *
     * @param message
     * @param value
     */
    public T getValue(Object message);

}
