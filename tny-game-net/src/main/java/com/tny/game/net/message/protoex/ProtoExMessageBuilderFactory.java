package com.tny.game.net.message.protoex;

import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.dispatcher.RequestBuilder;
import com.tny.game.net.dispatcher.ResponseBuilder;
import com.tny.game.net.session.Session;

public class ProtoExMessageBuilderFactory implements MessageBuilderFactory {



    @Override
    public RequestBuilder newRequestBuilder(Session session) {
        return new ProtoExRequestBuilder();
    }


    @Override
    public ResponseBuilder newResponseBuilder(Session session) {
        return new ProtoExResponseBuilder();
    }

}