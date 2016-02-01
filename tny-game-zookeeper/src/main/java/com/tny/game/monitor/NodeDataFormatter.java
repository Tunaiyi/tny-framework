package com.tny.game.monitor;

public interface NodeDataFormatter {

    /**
     * 对象转字节
     *
     * @param data
     * @return
     */
    public byte[] data2Bytes(Object data);


    /**
     * 字节转对象
     *
     * @param data
     * @return
     */
    public <D> D bytes2Data(byte[] bytes);

}
