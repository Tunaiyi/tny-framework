package com.tny.game.net.utils;

import com.tny.game.common.context.*;
import com.tny.game.net.command.dispatcher.MessageCommandBox;
import com.tny.game.net.session.Session;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 18:26
 */
public interface NetAttrKeys {

    AttrKey<Integer> SESSION_KEY = AttrKeys.key(NetAttrKeys.class, "SESSION_KEY");

    AttrKey<MessageCommandBox> USER_COMMAND_BOX = AttrKeys.key(Session.class.getName() + "USER_COMMAND_BOX");
}
