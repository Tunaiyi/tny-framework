package com.tny.game.net.dispatcher;

import com.tny.game.net.base.NetMessage;

import java.io.Serializable;

/**
 * 响应对象接口
 *
 * @author KGTny
 */
public abstract class Response extends NetMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 响应信息体
     *
     * @return
     */
    public abstract <T> T getBody(Class<T> clazz);

    /**
     * 获取响应结果码
     *
     * @return
     */
    public abstract int getResult();

    /**
     * 设置命令执行结果
     *
     * @param result
     */
//	public abstract void putBody(CommandResult result);

}