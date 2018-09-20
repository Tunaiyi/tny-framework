package com.tny.game.net.transport.message;

/**
 * 客户端响应构建器
 *
 * @author Kun.y
 */
public interface MessageBuilderFactory<UID> {

    MessageBuilder<UID> newBuilder();

}
