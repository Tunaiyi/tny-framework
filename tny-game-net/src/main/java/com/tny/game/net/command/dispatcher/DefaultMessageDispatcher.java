package com.tny.game.net.command.dispatcher;


import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.UnitLoader;
import com.tny.game.common.unit.annotation.Unit;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.command.auth.AuthenticateValidator;
import com.tny.game.net.command.listener.DispatchCommandListener;
import com.tny.game.net.command.plugins.ControllerPlugin;

@Unit
public class DefaultMessageDispatcher extends AbstractMessageDispatcher implements AppPrepareStart {

    public DefaultMessageDispatcher(AppContext appContext) {
        super(appContext);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_9);
    }

    @Override
    public void prepareStart() {
        this.addAuthProvider(UnitLoader.getLoader(AuthenticateValidator.class).getAllUnits());
        this.addControllerPlugin(UnitLoader.getLoader(ControllerPlugin.class).getAllUnits());
        this.addListener(UnitLoader.getLoader(DispatchCommandListener.class).getAllUnits());
    }

}
