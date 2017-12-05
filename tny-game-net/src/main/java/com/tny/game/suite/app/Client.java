package com.tny.game.suite.app;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public interface Client<UID> {

    /**
     * @return 客户端名字
     */
    String getName();

    /**
     * 关闭终端
     *
     * @return 是否关闭成功, 成功返回true 失效返回false
     */
    boolean close();

    /**
     * @return 是否关闭终端
     */
    boolean isClosed();

}
