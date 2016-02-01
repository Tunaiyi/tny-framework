package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.dispatcher.AbstactRequestBuilder;
import com.tny.game.net.dispatcher.Request;

/**
 * 客户端请求构建器
 *
 * @author Kun.y
 */
public class SimpleRequestBuilder extends AbstactRequestBuilder {

    private Attributes attributes;

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
        SimpleRequest requset = new SimpleRequest();
        requset.setID(this.id);
        if (this.protocol == 0)
            throw new NullPointerException("protocol is 0");
        requset.setModule(this.protocol / 100);
        requset.setOperation(this.protocol);
        requset.setParamList(this.paramList);
        requset.setTime(System.currentTimeMillis());
        if (this.checker != null)
            requset.setCheckKey(this.checker.generate(requset));
        requset.setAttributes(this.attributes);
        return requset;
    }

}
