package com.tny.game.net.message.simple;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.dispatcher.AbstractRequestBuilder;
import com.tny.game.net.dispatcher.NetRequest;

import java.util.function.Supplier;

/**
 * 客户端请求构建器
 *
 * @author Kun.y
 */
public class SimpleRequestBuilder extends AbstractRequestBuilder {

    private Attributes attributes;

    private static Supplier<SimpleRequest> CREATOR = SimpleRequest::new;

    protected SimpleRequestBuilder() {
        super(CREATOR);
    }

    public SimpleRequestBuilder setAttributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }


    @Override
    protected void doBuild(NetRequest request) {

    }
}
