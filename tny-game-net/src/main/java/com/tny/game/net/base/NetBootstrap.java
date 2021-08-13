package com.tny.game.net.base;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.*;
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

	private NetBootstrapContext<Object> context;

	public NetBootstrap(S setting) {
		this.setting = setting;
	}

	public <T> NetBootstrapContext<T> getContext() {
		return as(this.context);
	}

	@Override
	public void prepareStart() throws Exception {
		MessageFactory messageFactory = as(UnitLoader.getLoader(MessageFactory.class).getUnitAnCheck(this.setting.getMessageFactory()));
		CertificateFactory<Object> certificateFactory = as(
				UnitLoader.getLoader(CertificateFactory.class).getUnitAnCheck(this.setting.getCertificateFactory()));
		MessageDispatcher messageDispatcher = as(
				UnitLoader.getLoader(MessageDispatcher.class).getUnitAnCheck(this.setting.getMessageDispatcher()));
		CommandTaskProcessor commandTaskProcessor = as(
				UnitLoader.getLoader(CommandTaskProcessor.class).getUnitAnCheck(this.setting.getCommandTaskProcessor()));
		this.context = new NetBootstrapContext<>(messageDispatcher, commandTaskProcessor, messageFactory, certificateFactory);
		this.idGenerator = as(UnitLoader.getLoader(NetIdGenerator.class).getUnitAnCheck(this.setting.getTunnelIdGenerator()));
		this.postPrepared(this.setting);
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(this.getClass(), SYSTEM_LEVEL_10);
	}

	protected void postPrepared(S setting) {
	}

}
