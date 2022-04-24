package com.tny.game.protoex;

/**
 * protoEx描述模式上下文接口
 *
 * @author KGTny
 */
public interface ProtoExSchemaContext {

    /**
     * 通过 Type 获取对应的Schema
     *
     * @param type
     * @return
     */
    public <T> ProtoExSchema<T> getSchema(Class<?> type);

    /**
     * 通过protoExID获取对应的Schema
     *
     * @param protoExID
     * @param raw
     * @return
     */
    public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw);

    /**
     * 通过protoExID获取对应的Schema
     * 若protoExID为0时返回 defaultClass对应的Schema
     *
     * @param protoExID
     * @param raw
     * @param defaultClass
     * @return
     */
    public <T> ProtoExSchema<T> getSchema(int protoExID, boolean raw, Class<?> defaultClass);

}
