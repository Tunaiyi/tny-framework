package com.tny.game.net.base;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.unit.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.lifecycle.LifecycleLevel.*;
import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/7 3:38 下午
 */
public class NetBootstrap<S extends NetBootstrapSetting> implements AppPrepareStart {

    protected S setting;

    private NetBootstrapContext<Object> context;

    public NetBootstrap(S setting) {
        this.setting = setting;
    }

    public <T> NetBootstrapContext<T> getContext() {
        return as(this.context);
    }

    @Override
    public void prepareStart() throws Exception {
        MessageFactory<Object> messageFactory = as(
                UnitLoader.getLoader(MessageFactory.class).getUnitAnCheck(this.setting.getMessageFactory()));
        //        EndpointEventsBoxHandler<Object, NetEndpoint<Object>> eventHandler = as(
        //                UnitLoader.getLoader(EndpointEventsBoxHandler.class).getUnitAnCheck(this.setting.getEndpointEventHandler()));
        CertificateFactory<Object> certificateFactory = as(
                UnitLoader.getLoader(CertificateFactory.class).getUnitAnCheck(this.setting.getCertificateFactory()));
        MessageDispatcher messageDispatcher = as(
                UnitLoader.getLoader(MessageDispatcher.class).getUnitAnCheck(this.setting.getMessageDispatcher()));
        CommandTaskProcessor commandTaskProcessor = as(
                UnitLoader.getLoader(CommandTaskProcessor.class).getUnitAnCheck(this.setting.getCommandTaskProcessor()));
        this.context = new NetBootstrapContext<>(messageDispatcher, commandTaskProcessor, messageFactory, certificateFactory);
        this.postPrepared(this.setting);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    protected void postPrepared(S setting) {
    }

}
