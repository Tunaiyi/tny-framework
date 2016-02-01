package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.RequestBuilder;
import com.tny.game.net.dispatcher.ResponseBuilder;

public class ProtoExMessageBuilderFactory implements MessageBuilderFactory {

    @Override
    public RequestBuilder newRequestBuilder() {
        return new ProtoExRequestBuilder();
    }

    @Override
    public ResponseBuilder newResponseBuilder() {
        return new ProtoExResponseBuilder();
    }

}
