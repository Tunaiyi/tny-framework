package com.tny.game.protoex.field;

/**
 * protoEx类型编解码配置
 *
 * @param <T>
 * @author KGTny
 */
public interface IOConfiger<T> {

    /**
     * 字段配置相关名字
     *
     * @return
     */
    public String getName();

    /**
     * 字段索引
     *
     * @return
     */
    public int getIndex();

    /**
     * ProtoEx相对应类型
     *
     * @return
     */
    public Class<T> getDefaultType();

    /**
     * 整形数字类型编码方式
     *
     * @return
     */
    public FieldFormat getFormat();

    /**
     * 是否显式写入字段对应类型
     *
     * @return
     */
    public boolean isExplicit();

    /**
     * 若为Repeat(Collection)
     *
     * @return
     */
    public boolean isPacked();

}
