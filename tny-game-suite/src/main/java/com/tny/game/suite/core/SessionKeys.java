package com.tny.game.suite.core;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrUtils;
import com.tny.game.suite.login.Account;
import com.tny.game.suite.login.GameTicket;

/**
 * session key 常量
 * Created by Kun Yang on 16/1/27.
 */
public interface SessionKeys {

    AttrKey<String> OPEN_ID_KEY = AttrUtils.key(SessionKeys.class, "OPEN_ID_KEY");
    AttrKey<String> OPEN_KEY_KEY = AttrUtils.key(SessionKeys.class, "OPEN_KEY_KEY");
    AttrKey<Account> ACCOUNT_KEY = AttrUtils.key(SessionKeys.class, "ACCOUNT_KEY");
    AttrKey<GameTicket> TICKET_KEY = AttrUtils.key(SessionKeys.class, "TICKET_KEY");
    AttrKey<Boolean> GET_CACHED_RESPONSE = AttrUtils.key(SessionKeys.class, "GET_CACHED_RESPONSE");
    AttrKey<Integer> REQUEST_ID_COUNTER = AttrUtils.key(SessionKeys.class, "REQUEST_ID_COUNTER");

    AttrKey<Object> SYSTEM_USER_ID = AttrUtils.key(SessionKeys.class, "SYSTEM_USER_ID");
    AttrKey<String> SYSTEM_USER_USER_GROUP = AttrUtils.key(SessionKeys.class, "SYSTEM_USER_USER_GROUP");
    AttrKey<String> SYSTEM_USER_PASSWORD = AttrUtils.key(SessionKeys.class, "SYSTEM_USER_PASSWORD");

}
