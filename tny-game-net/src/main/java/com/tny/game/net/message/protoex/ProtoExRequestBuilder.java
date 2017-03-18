package com.tny.game.net.message.protoex;

import com.tny.game.net.dispatcher.AbstractRequestBuilder;

import java.util.function.Supplier;

public class ProtoExRequestBuilder extends AbstractRequestBuilder<ProtoExRequest> {

    private static Supplier<ProtoExRequest> CREATOR = ProtoExRequest::new;

    protected ProtoExRequestBuilder() {
        super(CREATOR);
    }

    @Override
    protected void doBuild(ProtoExRequest request) {
    }

}
