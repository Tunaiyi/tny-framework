package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestVerifyChecker;

import java.util.List;

/**
 * 客户端请求构建器
 *
 * @author Kun.y
 */
public abstract class AbstractRequestBuilder implements RequestBuilder {

    protected RequestVerifyChecker checker;

    protected NetRequest request;

    protected AbstractRequestBuilder(NetRequest request) {
        this.request = request;
        this.request.setTime(System.currentTimeMillis());
    }

    /**
     * 设置请求ID
     *
     * @param id 请求ID
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder setID(int id) {
        request.setID(id);
        return this;
    }

    /**
     * 设置请求协议
     *
     * @param protocol 请求协议名
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder setProtocol(Protocol protocol) {
        request.setProtocol(protocol.getProtocol());
        return this;
    }

    /**
     * 设置请求交验编码器
     *
     * @param checker 请求交验编码器
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder setRequestChecker(RequestVerifyChecker checker) {
        this.checker = checker;
        return this;
    }

    /**
     * 设置操作
     *
     * @param protocol 操作名称
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder setProtocol(int protocol) {
        this.request.setProtocol(protocol);
        return this;
    }

    /**
     * 增加请求参数
     *
     * @param index     参数位置索引
     * @param parameter 参数
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder addParameter(int index, Object parameter) {
        this.request.addParam(index, parameter);
        return this;
    }

    /**
     * 添加指定参数
     *
     * @param parameter 指定参数
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder addParameter(Object parameter) {
        this.request.addParam(parameter);
        return this;
    }

    /**
     * 添加指定的参数列表
     *
     * @param parameterList 指定参数列表
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder addParameter(List<Object> parameterList) {
        this.request.addAllParam(parameterList);
        return this;
    }

    /**
     * 添加指定的参数数组
     *
     * @param parameters 指定参数数组
     * @return 返回构建器本身
     */
    @Override
    public RequestBuilder addParameter(Object... parameters) {
        for (Object parameter : parameters)
            this.request.addParam(parameter);
        return this;
    }

    protected void setCheckKey() {
        request.setCheckKey(this.checker.generate(request));
    }

}
