package com.tny.game.net.dispatcher;

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
     * @return 响应信息体
     */
    public abstract <T> T getBody(Class<T> clazz);

    /**
     * @return 获取响应结果码
     */
    public abstract int getResult();

    /**
     * @return 获取序号
     */
    public abstract int getNumber();

}