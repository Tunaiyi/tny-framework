package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.RequestBuilder;
import com.tny.game.net.dispatcher.ResponseBuilder;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public class SimpleMessageBuilderFactory implements MessageBuilderFactory {

    @Override
    public RequestBuilder newRequestBuilder() {
        return new SimpleRequestBuilder();
    }

    @Override
    public ResponseBuilder newResponseBuilder() {
        return new SimpleResponseBuilder();
    }

}
