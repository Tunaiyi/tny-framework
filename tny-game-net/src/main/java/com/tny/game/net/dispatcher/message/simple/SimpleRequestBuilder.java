package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.dispatcher.AbstractRequestBuilder;
import com.tny.game.net.dispatcher.NetRequest;
import com.tny.game.net.dispatcher.Request;

/**
 * 客户端请求构建器
 *
 * @author Kun.y
 */
public class SimpleRequestBuilder extends AbstractRequestBuilder {

    private Attributes attributes;

    protected SimpleRequestBuilder() {
        super(new SimpleRequest());
    }

    public SimpleRequestBuilder setAttributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * 构建请求
     *
     * @return 返回构建的请求
     */
    @Override
    public Request build() {
        NetRequest request = this.request;
        if (request.getProtocol() == 0)
            throw new NullPointerException("protocol is 0");
        if (this.checker != null)
            this.setCheckKey();
        this.request = new SimpleRequest();
        return request;
    }

}
