package com.tny.game.net.dispatcher;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface MessageBuilderFactory {

    public RequestBuilder newRequestBuilder();

    public ResponseBuilder newResponseBuilder();

}
