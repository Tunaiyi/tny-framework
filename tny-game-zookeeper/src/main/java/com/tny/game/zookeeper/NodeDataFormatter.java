package com.tny.game.zookeeper;

public interface NodeDataFormatter {

    /**
     * 对象转字节
     *
     * @param data 数据
     * @return 字节数组
     */
    byte[] data2Bytes(Object data);


    /**
     * 字节转对象
     *
     * @param bytes 字节数组
     * @return
     */
    <D> D bytes2Data(byte[] bytes);

}
