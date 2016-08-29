package com.tny.game.net.dispatcher;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface MessageBuilderFactory {

    RequestBuilder newRequestBuilder(Session session);

    ResponseBuilder newResponseBuilder(Session session);

}
