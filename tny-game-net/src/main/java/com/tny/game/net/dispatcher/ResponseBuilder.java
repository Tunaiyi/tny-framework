package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface ResponseBuilder {

    /**
     * 设置响应ID
     *
     * @param id 响应ID
     * @return 返回构建器本身
     */
    ResponseBuilder setID(int id);

    /**
     * 设置响应模块
     *
     * @param protocol 响应模块名
     * @return 返回构建器本身
     */
    ResponseBuilder setProtocol(Protocol protocol);

    //	/**
    //	 * 设置用户ID
    //	 *
    //	 * @param userID
    //	 *            用户ID
    //	 * @return 用户ID
    //	 */
    //	public ResponseBuilder setRequset(Request request);

    /**
     * 设置操作
     *
     * @param protocol 操作名称
     * @return 返回构建器本身
     */
    ResponseBuilder setProtocol(int protocol);

    /**
     * 设置结果码
     *
     * @param result
     * @return
     */
    ResponseBuilder setResult(int result);

    /**
     * 设置结果码
     *
     * @param responseCode
     * @return
     */
    ResponseBuilder setResult(ResultCode responseCode);

    /**
     * 设置命令结果
     *
     * @param result
     * @return
     */
    ResponseBuilder setCommandResult(CommandResult result);

    /**
     * 设置响应消息
     *
     * @param message
     * @return
     */
    ResponseBuilder setBody(Object message);

    /**
     * 设置序号
     *
     * @param number
     * @return
     */
    ResponseBuilder setNumber(int number);

    /**
     * 构建响应
     *
     * @return 返回构建的响应
     */
    Response build();

}
