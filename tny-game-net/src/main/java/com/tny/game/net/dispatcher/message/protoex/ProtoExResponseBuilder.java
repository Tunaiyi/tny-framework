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
     * 创建构建器
     *
     * @return 返沪构建器s
     */
    protected ProtoExResponseBuilder() {
        super(new ProtoExResponse());
    }

    /**
     * 构建响应
     *
     * @return 返回构建的响应
     */
    @Override
    public Response build() {
        Response response = this.response;
        if (response.getProtocol() == 0)
            throw new NullPointerException("protocol is 0");
        this.response = new ProtoExResponse();
        return response;
    }

}
