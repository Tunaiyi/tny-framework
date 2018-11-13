package com.tny.game.net.utils;

import com.tny.game.common.context.*;
import com.tny.game.net.command.dispatcher.MessageCommandBox;
import com.tny.game.net.endpoint.Session;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 18:26
 */
public interface NetAttrKeys {

    AttrKey<MessageCommandBox> USER_COMMAND_BOX = AttrKeys.key(Session.class.getName() + "USER_COMMAND_BOX");

}
