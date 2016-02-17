package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.dispatcher.AbstractRequestBuilder;
import com.tny.game.net.dispatcher.Request;

public class ProtoExRequestBuilder extends AbstractRequestBuilder {

    /**
     * 构建请求
     *
     * @return 返回构建的请求
     */
    @Override
    public Request build() {
        ProtoExRequest request = new ProtoExRequest();
        request.setID(this.id);
        if (this.protocol == 0)
            throw new NullPointerException("protocol is 0");
        request.setProtocol(this.protocol);
        request.setParamList(this.paramList);
        request.setTime(System.currentTimeMillis());
        if (this.checker != null)
            request.setCheckKey(this.checker.generate(request));
        return request;
    }

}
