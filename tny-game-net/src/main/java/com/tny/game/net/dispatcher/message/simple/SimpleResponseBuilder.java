package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.net.dispatcher.AbstractResponseBuilder;

import java.util.function.Supplier;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public class SimpleResponseBuilder extends AbstractResponseBuilder<SimpleResponse> {

    private static Supplier<SimpleResponse> CREATOR = SimpleResponse::new;

    /**
     * 创建构建器
     *
     * @return 返沪构建器
     */
    protected SimpleResponseBuilder() {
        super(CREATOR);
    }


    @Override
    protected void doBuild(SimpleResponse request) {
    }
    
}
