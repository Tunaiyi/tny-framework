package com.tny.game.boot.transaction;

import com.tny.game.common.context.*;

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
