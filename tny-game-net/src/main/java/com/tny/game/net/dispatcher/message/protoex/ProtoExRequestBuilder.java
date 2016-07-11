package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.dispatcher.AbstractRequestBuilder;
import com.tny.game.net.dispatcher.NetRequest;
import com.tny.game.net.dispatcher.Request;

public class ProtoExRequestBuilder extends AbstractRequestBuilder {

    protected ProtoExRequestBuilder() {
        super(new ProtoExRequest());
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
        this.request = new ProtoExRequest();
        return request;
    }

}
