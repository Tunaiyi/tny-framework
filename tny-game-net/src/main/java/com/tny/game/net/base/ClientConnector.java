package com.tny.game.net.base;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface ClientConnector<UID> {

    /**
     * @return 客户端名字
     */
    String getName();

    boolean isClosed();
    boolean close();
}
