package com.tny.game.net.base;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
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

    protected NetIdGenerator idGenerator;

    private NetworkContext context;

    private final NetAppContext appContext;

    public NetBootstrap(NetAppContext appContext, S setting) {
        this.appContext = appContext;
        this.setting = setting;
    }

    public <T> NetworkContext getContext() {
        return as(this.context);
    }

    @Override
    public void prepareStart() throws Exception {
        MessageFactory messageFactory = UnitLoader.getLoader(MessageFactory.class).checkUnit(this.setting.getMessageFactory());
        MessagerFactory messagerFactory = UnitLoader.getLoader(MessagerFactory.class).checkUnit(this.setting.getMessagerFactory());
        CertificateFactory<Object> certificateFactory =
                UnitLoader.getLoader(CertificateFactory.class).checkUnit(this.setting.getCertificateFactory());
        MessageDispatcher messageDispatcher = UnitLoader.getLoader(MessageDispatcher.class).checkUnit(this.setting.getMessageDispatcher());
        CommandTaskBoxProcessor commandTaskProcessor =
                UnitLoader.getLoader(CommandTaskBoxProcessor.class).checkUnit(this.setting.getCommandTaskProcessor());
        RpcForwarder rpcForwarder = UnitLoader.getLoader(RpcForwarder.class).checkUnit(this.setting.getRpcForwarder());
        this.context = new NetBootstrapContext(appContext, this.setting,
                messageDispatcher, commandTaskProcessor, messageFactory, messagerFactory, certificateFactory, rpcForwarder);
        this.idGenerator = UnitLoader.getLoader(NetIdGenerator.class).checkUnit(this.setting.getTunnelIdGenerator());
        this.onLoadUnit(this.setting);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
    }

    public S getSetting() {
        return setting;
    }

    protected void onLoadUnit(S setting) {
    }

}
