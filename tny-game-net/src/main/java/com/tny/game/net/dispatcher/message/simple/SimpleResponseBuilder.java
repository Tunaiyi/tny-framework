package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.dispatcher.AbstractResponseBuilder;
import com.tny.game.net.dispatcher.Response;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public class SimpleResponseBuilder extends AbstractResponseBuilder {

    /**
     * 构建响应
     *
     * @return 返回构建的响应
     */
    @Override
    public Response build() {
        SimpleResponse response = new SimpleResponse();
        response.setID(id);
        if (this.protocol == 0)
            throw new NullPointerException("protocol is null");
        response.setModule(this.protocol / 100);
        response.setOperation(this.protocol);
        response.setResult(result);
        response.setBody(message);
        return response;
    }

}
