package com.tny.game.net.endpoint;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/17 7:32 下午
 */
public interface EndpointContext {

	MessageDispatcher getMessageDispatcher();

	CommandTaskBoxProcessor getCommandTaskProcessor();

}
