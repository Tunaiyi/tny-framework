package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface ResponseBuilder {

    //	/**
    //	 * 通过Request 设置Response
    //	 *
    //	 * @param request
    //	 * @return
    //	 */
    //	public ResponseBuilder setByRequset(final Request request);

    /**
     * 设置响应ID
     *
     * @param id 响应ID
     * @return 返回构建器本身
     */
    public ResponseBuilder setID(int id);

    /**
     * 设置响应模块
     *
     * @param module 响应模块名
     * @return 返回构建器本身
     */
    public ResponseBuilder setProtocol(Protocol protocol);

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
    public ResponseBuilder setProtocol(int protocol);

    /**
     * 设置结果码
     *
     * @param result
     * @return
     */
    public ResponseBuilder setResult(int result);

    /**
     * 设置结果码
     *
     * @param result
     * @return
     */
    public ResponseBuilder setResult(ResultCode responseCode);

    /**
     * 设置命令结果
     *
     * @param result
     * @return
     */
    public ResponseBuilder setCommandResult(CommandResult result);

    /**
     * 设置响应消息
     *
     * @param message
     * @return
     */
    public ResponseBuilder setBody(Object message);

    /**
     * 构建响应
     *
     * @return 返回构建的响应
     */
    public Response build();
}
