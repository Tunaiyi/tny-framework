package com.tny.game.suite.login.event;

import com.tny.game.suite.login.Account;

public interface AccountListener {

    /**
     * 帐号创建
     *
     * @param account 帐号
     */
    default void onCreate(Account account) {
    }

    /**
     * 帐号登录
     *
     * @param account 帐号
     */
    default void onOnline(Account account) {
    }

    /**
     * 帐号下线
     *
     * @param account 帐号
     */
    default void onOffline(Account account) {
    }

    /**
     * 帐号创角
     *
     * @param account 帐号
     */
    default void onCreateRole(Account account) {
    }

}
