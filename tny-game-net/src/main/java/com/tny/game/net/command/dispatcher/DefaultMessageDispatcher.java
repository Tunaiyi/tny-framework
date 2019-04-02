package com.tny.game.net.command.dispatcher;


import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.listener.*;
import com.tny.game.net.command.plugins.*;
import com.tny.game.net.endpoint.*;

@Unit
public class DefaultMessageDispatcher extends AbstractMessageDispatcher implements AppPrepareStart {

    public DefaultMessageDispatcher() {
        super(null);
    }

    public DefaultMessageDispatcher(AppContext context) {
        super(context);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_9);
    }

    @Override
    public void prepareStart() {
        this.addAuthProvider(UnitLoader.getLoader(AuthenticateValidator.class).getAllUnits());
        this.addControllerPlugin(UnitLoader.getLoader(CommandPlugin.class).getAllUnits());
        this.addListener(UnitLoader.getLoader(DispatchCommandListener.class).getAllUnits());
        this.setEndpointKeeperManager(UnitLoader.getLoader(EndpointKeeperManager.class).getOneUnitAnCheck());
    }


    @Override
    public DefaultMessageDispatcher setAppContext(AppContext appContext) {
        super.setAppContext(appContext);
        return this;
    }

}
