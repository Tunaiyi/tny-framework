package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.dispatcher.AbstactRequestBuilder;
import com.tny.game.net.dispatcher.Request;

public class ProtoExRequestBuilder extends AbstactRequestBuilder {

    /**
     * 构建请求
     *
     * @return 返回构建的请求
     */
    @Override
    public Request build() {
        ProtoExRequest requset = new ProtoExRequest();
        requset.setID(this.id);
        if (this.protocol == 0)
            throw new NullPointerException("protocol is 0");
        requset.setProtocol(this.protocol);
        requset.setParamList(this.paramList);
        requset.setTime(System.currentTimeMillis());
        if (this.checker != null)
            requset.setCheckKey(this.checker.generate(requset));
        return requset;
    }

}
