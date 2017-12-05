package com.tny.game.suite.core;

import com.tny.game.common.context.AttrKey;
import com.tny.game.common.context.AttrKeys;
import com.tny.game.suite.login.Account;
import com.tny.game.suite.login.GameTicket;
import com.tny.game.suite.login.ServerTicket;

/**
 * session key 常量
 * Created by Kun Yang on 16/1/27.
 */
public interface AttributesKeys {

    AttrKey<String> OPEN_ID_KEY = AttrKeys.key(AttributesKeys.class, "OPEN_ID_KEY");
    AttrKey<String> OPEN_KEY_KEY = AttrKeys.key(AttributesKeys.class, "OPEN_KEY_KEY");
    AttrKey<Account> ACCOUNT_KEY = AttrKeys.key(AttributesKeys.class, "ACCOUNT_KEY");
    AttrKey<GameTicket> TICKET_KEY = AttrKeys.key(AttributesKeys.class, "TICKET_KEY");

    AttrKey<Object> SYSTEM_USER_ID = AttrKeys.key(AttributesKeys.class, "SYSTEM_USER_ID");
    AttrKey<String> SYSTEM_USER_USER_GROUP = AttrKeys.key(AttributesKeys.class, "SYSTEM_USER_USER_GROUP");
    AttrKey<ServerTicket> SERVER_TICKET = AttrKeys.key(AttributesKeys.class, "SERVER_TICKET");
    AttrKey<ServerTicket> LOCAL_SERVER_TICKET = AttrKeys.key(AttributesKeys.class, "LOCAL_SERVER_TICKET");

}
