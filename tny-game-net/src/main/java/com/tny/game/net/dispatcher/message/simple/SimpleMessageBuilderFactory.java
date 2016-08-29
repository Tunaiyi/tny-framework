package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.RequestBuilder;
import com.tny.game.net.dispatcher.ResponseBuilder;
import com.tny.game.net.dispatcher.Session;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public class SimpleMessageBuilderFactory implements MessageBuilderFactory {

    @Override
    public RequestBuilder newRequestBuilder(Session session) {
        return new SimpleRequestBuilder();
    }

    @Override
    public ResponseBuilder newResponseBuilder(Session session) {
        return new SimpleResponseBuilder();
    }

}
