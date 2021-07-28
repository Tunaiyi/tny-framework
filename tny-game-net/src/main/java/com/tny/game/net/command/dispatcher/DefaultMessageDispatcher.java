package com.tny.game.net.command.dispatcher;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.endpoint.*;

@Unit
public class DefaultMessageDispatcher extends AbstractMessageDispatcher implements AppPrepareStart {

    public DefaultMessageDispatcher(NetAppContext appContext, EndpointKeeperManager endpointKeeperManager) {
        super(appContext, endpointKeeperManager);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_9);
    }

    @Override
    public void prepareStart() {
        this.context.addAuthProvider(UnitLoader.getLoader(AuthenticateValidator.class).getAllUnits());
        this.context.addControllerPlugin(UnitLoader.getLoader(CommandPlugin.class).getAllUnits());
        this.context.addCommandListener(UnitLoader.getLoader(MessageCommandListener.class).getAllUnits());
    }

}
