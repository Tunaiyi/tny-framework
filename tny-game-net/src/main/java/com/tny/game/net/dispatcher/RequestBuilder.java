package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestVerifier;

import java.util.List;

/**
 * 客户端请求构建器
 *
 * @author Kun.y
 */
public interface RequestBuilder {

    /**
     * 设置请求ID
     *
     * @param id 请求ID
     * @return 返回构建器本身
     */
    RequestBuilder setID(int id);

    /**
     * 设置请求模块
     *
     * @param protocol 请求协议
     * @return 返回构建器本身
     */
    RequestBuilder setProtocol(Protocol protocol);

    /**
     * 设置请求交验编码器
     *
     * @param checker 请求交验编码器
     * @return 返回构建器本身
     */
    RequestBuilder setRequestVerifier(RequestVerifier checker);

    //	/**
    //	 * 设置用户ID
    //	 *
    //	 * @param userID
    //	 *            用户ID
    //	 * @return 用户ID
    //	 */
    //	public RequestBuilder setUserID(long userID);

    /**
     * 设置操作
     *
     * @param protocol 操作名称
     * @return 返回构建器本身
     */
    RequestBuilder setProtocol(int protocol);

    /**
     * 设置Session
     *
     * @param session 会话
     * @return 返回构建器本身
     */
    RequestBuilder setSession(Session session);

    /**
     * 增加请求参数
     *
     * @param index     参数位置索引
     * @param parameter 参数
     * @return 返回构建器本身
     */
    RequestBuilder addParameter(int index, Object parameter);

    /**
     * 添加指定参数
     *
     * @param parameter 指定参数
     * @return 返回构建器本身
     */
    RequestBuilder addParameter(Object parameter);

    /**
     * 添加指定的参数列表
     *
     * @param parameterList 指定参数列表
     * @return 返回构建器本身
     */
    RequestBuilder addParameter(List<Object> parameterList);

    /**
     * 添加指定的参数数组
     *
     * @param parameters 指定参数数组
     * @return 返回构建器本身
     */
    RequestBuilder addParameter(Object... parameters);

    /**
     * 构建请求
     *
     * @return 返回构建的请求
     */
    Request build();

}
