package com.tny.game.zookeeper;

public interface NodeDataFormatter {

    /**
     * 对象转字节
     *
     * @param data 数据
     * @return 返回序列化字节数组
     */
    byte[] data2Bytes(Object data);

    /**
     * 字节转对象
     *
     * @param bytes 字节数组
     * @return 返回反序列化的对象
     */
    <D> D bytes2Data(byte[] bytes);

}
