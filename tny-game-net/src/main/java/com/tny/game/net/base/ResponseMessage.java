package com.tny.game.net.base;

public interface ResponseMessage {

    /**
     * 响应信息体
     *
     * @return
     */
    public Object getMessage();

    /**
     * 获取响应结果码
     *
     * @return
     */
    public int getResult();

    /**
     * 模块操作
     *
     * @return
     */
    public Object getProtocol();

}
