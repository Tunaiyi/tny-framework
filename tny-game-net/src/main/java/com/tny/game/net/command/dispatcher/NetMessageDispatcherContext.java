package com.tny.game.net.command.dispatcher;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.plugins.*;

import java.util.Collection;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-30 18:03
 */
@UnitInterface
public interface NetMessageDispatcherContext extends MessageDispatcherContext {

	void addControllerPlugin(CommandPlugin<?, ?> plugin);

	void addControllerPlugin(Collection<? extends CommandPlugin<?, ?>> plugins);

	void addAuthProvider(AuthenticateValidator<?> provider);

	void addAuthProvider(Collection<? extends AuthenticateValidator<?>> providers);

}
