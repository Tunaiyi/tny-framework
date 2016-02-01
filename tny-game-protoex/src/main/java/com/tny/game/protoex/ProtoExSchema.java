package com.tny.game.protoex;

import com.tny.game.protoex.field.IOConfiger;

/**
 * ProtoEx类型描述结构
 *
 * @param <T>
 * @author KGTny
 */
public interface ProtoExSchema<T> {

    /**
     * 结构所属的protoExID
     *
     * @return
     */
    public int getProtoExID();

    /**
     * 结构名字
     *
     * @return
     */
    public String getName();

    /**
     * 是否是原生类型
     *
     * @return
     */
    public boolean isRaw();

    /**
     * 按conf编码方式将value(包含tag)的protoEx序列化字节数组写入outputStream
     *
     * @param outputStream 目标流
     * @param value        值
     * @param conf         编码方式
     */
    public void writeMessage(ProtoExOutputStream outputStream, T value, IOConfiger<?> conf);

    /**
     * 按conf编码方式将value(不包含tag)的protoEx序列化字节数组写入outputStream
     *
     * @param outputStream 目标流
     * @param value        值
     * @param conf         编码方式
     */
    public void writeValue(ProtoExOutputStream outputStream, T value, IOConfiger<?> conf);

    /**
     * 按conf编码方式从inputStream读取Message(包括读取Tag),优先按Tag描述读取,若tag信息不全则按conf读取
     *
     * @param inputStream 源流
     * @param conf        默认编码方式
     * @return
     */
    public T readMessage(ProtoExInputStream inputStream, IOConfiger<?> conf);

    /**
     * 按conf编码方式从inputStream读取Message(不包括读取Tag),优先按Tag描述读取,若tag信息不全则按conf读取
     *
     * @param inputStream
     * @param tag
     * @param conf
     * @return
     */
    public T readValue(ProtoExInputStream inputStream, Tag tag, IOConfiger<?> conf);

}
