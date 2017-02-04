package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;

import java.util.function.Supplier;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public abstract class AbstractResponseBuilder<RP extends NetResponse> implements ResponseBuilder {

    /**
     * 响应的响应Id
     */
    protected int id = Session.DEFAULT_RESPONSE_ID;

    protected RP response;

    protected Supplier<RP> creator;

    /**
     * 创建构建器
     *
     * @return 返沪构建器
     */

    protected AbstractResponseBuilder(Supplier<RP> creator) {
        this.creator = creator;
        this.response = creator.get();
    }

    /**
     * 设置响应ID
     *
     * @param id 响应ID
     * @return 返回构建器本身
     */
    @Override
    public ResponseBuilder setID(int id) {
        response.setID(id);
        return this;
    }

    /**
     * 设置响应协议
     *
     * @param protocol 响应协议名
     * @return 返回构建器本身
     */
    @Override
    public ResponseBuilder setProtocol(Protocol protocol) {
        if (protocol instanceof Request) {
            response.setID(((Request) protocol).getID());
        }
        // response.setPush(protocol.isPush());
        response.setProtocol(protocol.getProtocol());
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
        response.setProtocol(protocol);
        return this;
    }

    /**
     * 设置结果码
     *
     * @param result 结果码
     * @return self
     */
    @Override
    public ResponseBuilder setResult(int result) {
        response.setResult(result);
        return this;
    }

    /**
     * 设置结果码
     *
     * @param result 结果码
     * @return self
     */
    @Override
    public ResponseBuilder setResult(ResultCode result) {
        response.setResult(result.getCode());
        return this;
    }

    /**
     * 设置命令结果
     *
     * @param result 结果码
     * @return self
     */
    @Override
    public ResponseBuilder setCommandResult(CommandResult result) {
        response.setResult(result.getResultCode().getCode());
        response.setBody(result.getBody());
        return this;
    }

    /**
     * 设置响应消息
     *
     * @param body 响应消息
     * @return self
     */
    @Override
    public ResponseBuilder setBody(Object body) {
        response.setBody(body);
        return this;
    }

    @Override
    public ResponseBuilder setNumber(int number) {
        response.setNumber(number);
        return this;
    }

    /**
     * 构建响应
     *
     * @return 返回构建的响应
     */
    @Override
    public Response build() {
        RP response = this.response;
        if (response.getProtocol() == 0)
            throw new NullPointerException("protocol is 0");
        doBuild(response);
        this.response = creator.get();
        return response;
    }

    protected abstract void doBuild(RP request);


}
