package com.tny.game.suite.core;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttributeUtils;
import com.tny.game.suite.login.Account;
import com.tny.game.suite.login.GameTicket;

/**
 * session key 常量
 * Created by Kun Yang on 16/1/27.
 */
public interface SessionKeys {

    AttrKey<String> OPEN_ID_KEY = AttributeUtils.key(SessionKeys.class, "OPEN_ID_KEY");
    AttrKey<String> OPEN_KEY_KEY = AttributeUtils.key(SessionKeys.class, "OPEN_KEY_KEY");
    AttrKey<Boolean> GET_CACHED_RESPONSE = AttributeUtils.key(SessionKeys.class, "GET_CACHED_RESPONSE");
    AttrKey<Account> ACCOUNT_KEY = AttributeUtils.key(SessionKeys.class, "ACCOUNT_KEY");
    AttrKey<GameTicket> TICKET_KEY = AttributeUtils.key(SessionKeys.class, "TICKET_KEY");

    AttrKey<Object> SYSTEM_USER_ID = AttributeUtils.key(SessionKeys.class, "SYSTEM_USER_ID");
    AttrKey<String> SYSTEM_USER_USER_GROUP = AttributeUtils.key(SessionKeys.class, "SYSTEM_USER_USER_GROUP");
    AttrKey<String> SYSTEM_USER_PASSWORD = AttributeUtils.key(SessionKeys.class, "SYSTEM_USER_PASSWORD");

}
