package com.tny.game.net.message.protoex;

import com.tny.game.net.dispatcher.AbstractResponseBuilder;

import java.util.function.Supplier;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public class ProtoExResponseBuilder extends AbstractResponseBuilder<ProtoExResponse> {

    private static Supplier<ProtoExResponse> CREATOR = ProtoExResponse::new;

    /**
     * 创建构建器
     *
     * @return 返沪构建器s
     */
    protected ProtoExResponseBuilder() {
        super(CREATOR);
    }

    @Override
    protected void doBuild(ProtoExResponse request) {
    }

}
