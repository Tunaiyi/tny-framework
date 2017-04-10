package com.tny.game.suite.core;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.suite.login.Account;
import com.tny.game.suite.login.GameTicket;

/**
 * session key 常量
 * Created by Kun Yang on 16/1/27.
 */
public interface AttributesKeys {

    AttrKey<String> OPEN_ID_KEY = AttrKeys.key(AttributesKeys.class, "OPEN_ID_KEY");
    AttrKey<String> OPEN_KEY_KEY = AttrKeys.key(AttributesKeys.class, "OPEN_KEY_KEY");
    AttrKey<Account> ACCOUNT_KEY = AttrKeys.key(AttributesKeys.class, "ACCOUNT_KEY");
    AttrKey<GameTicket> TICKET_KEY = AttrKeys.key(AttributesKeys.class, "TICKET_KEY");
    AttrKey<Boolean> GET_CACHED_RESPONSE = AttrKeys.key(AttributesKeys.class, "GET_CACHED_RESPONSE");
    AttrKey<Integer> REQUEST_ID_COUNTER = AttrKeys.key(AttributesKeys.class, "REQUEST_ID_COUNTER");

    AttrKey<Object> SYSTEM_USER_ID = AttrKeys.key(AttributesKeys.class, "SYSTEM_USER_ID");
    AttrKey<String> SYSTEM_USER_USER_GROUP = AttrKeys.key(AttributesKeys.class, "SYSTEM_USER_USER_GROUP");
    AttrKey<String> SYSTEM_USER_PASSWORD = AttrKeys.key(AttributesKeys.class, "SYSTEM_USER_PASSWORD");

}