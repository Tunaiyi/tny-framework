package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public abstract class AbstractResponseBuilder implements ResponseBuilder {

    /**
     * 响应的响应Id
     */
    protected int id = Session.PUSH_RESPONSE_ID;
    /**
     * 返回信息
     */
    protected Object message;
    /**
     * 错误码
     */
    protected int result;
    /**
     * 响应操作
     */
    protected int protocol;

    /**
     * 创建构建器
     *
     * @return 返沪构建器
     */

    protected AbstractResponseBuilder() {
    }

    /**
     * 设置响应ID
     *
     * @param id 响应ID
     * @return 返回构建器本身
     */
    @Override
    public ResponseBuilder setID(int id) {
        this.id = id;
        return this;
    }

    /**
     * 设置响应模块
     *
     * @param module 响应模块名
     * @return 返回构建器本身
     */
    @Override
    public ResponseBuilder setProtocol(Protocol protocol) {
        if (protocol instanceof Request) {
            this.id = ((Request) protocol).getID();
        }
        this.protocol = protocol.getProtocol();
        return this;
    }

    /**
     * 设置操作
     *
     * @param protocol 操作名称
     * @return 返回构建器本身
     */
    @Override
    public ResponseBuilder setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * 设置结果码
     *
     * @param result
     * @return
     */
    @Override
    public ResponseBuilder setResult(int result) {
        this.result = result;
        return this;
    }

    /**
     * 设置结果码
     *
     * @param result
     * @return
     */
    @Override
    public ResponseBuilder setResult(ResultCode resultCode) {
        this.result = resultCode.getCode();
        return this;
    }

    /**
     * 设置命令结果
     *
     * @param result
     * @return
     */
    @Override
    public ResponseBuilder setCommandResult(CommandResult result) {
        this.result = result.getResultCode().getCode();
        this.message = result.getBody();
        return this;
    }

    /**
     * 设置响应消息
     *
     * @param message
     * @return
     */
    @Override
    public ResponseBuilder setBody(Object message) {
        this.message = message;
        return this;
    }

}
