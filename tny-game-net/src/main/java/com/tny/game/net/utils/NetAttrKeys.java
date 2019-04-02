package com.tny.game.net.utils;

import com.tny.game.common.context.*;
import com.tny.game.net.command.dispatcher.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-11 18:26
 */
public interface NetAttrKeys {

    AttrKey<MessageCommandBox> USER_COMMAND_BOX = AttrKeys.key(NetAttrKeys.class.getName() + "USER_COMMAND_BOX");
    
}
