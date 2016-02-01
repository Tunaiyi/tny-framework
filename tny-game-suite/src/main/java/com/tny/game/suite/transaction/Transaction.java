package com.tny.game.suite.transaction;

import com.tny.game.common.context.Attributes;

public interface Transaction {

    /**
     * 是否打开
     *
     * @return
     */
    boolean isOpen();

    /**
     * 获取事务属性
     *
     * @return
     */
    Attributes attributes();

}
