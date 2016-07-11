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
     * 创建构建器
     *
     * @return 返沪构建器
     */
    protected SimpleResponseBuilder() {
        super(new SimpleResponse());
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
        this.response = new SimpleResponse();
        return response;
    }

}
