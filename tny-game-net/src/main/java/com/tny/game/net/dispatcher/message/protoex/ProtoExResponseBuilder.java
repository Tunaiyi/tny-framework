package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.dispatcher.AbstractResponseBuilder;
import com.tny.game.net.dispatcher.Response;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public class ProtoExResponseBuilder extends AbstractResponseBuilder {

    /**
     * 构建响应
     *
     * @return 返回构建的响应
     */
    @Override
    public Response build() {
        ProtoExResponse response = new ProtoExResponse();
        response.setID(id);
        if (this.protocol == 0)
            throw new NullPointerException("protocol is 0");
        response.setProtocol(protocol);
        response.setResult(result);
        response.setBody(message);
        return response;
    }

}
