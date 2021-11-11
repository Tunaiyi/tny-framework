package com.tny.game.net.rpc;

import com.tny.game.common.url.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.rpc.auth.*;
import com.tny.game.net.rpc.setting.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/10 2:22 下午
 */
public class RpcClientFactory {

	private ClientGuide clientGuide;

	private NetAppContext appContext;

	private RpcServiceSetting setting;

	public RpcClientFactory() {
	}

	public RpcClientFactory(NetAppContext appContext, RpcServiceSetting setting, ClientGuide clientGuide) {
		this.clientGuide = clientGuide;
		this.appContext = appContext;
		this.setting = setting;
	}

	public RpcServiceSetting getSetting() {
		return setting;
	}

	public String getServiceName() {
		return setting.getServiceName();
	}

	public boolean isDiscovery() {
		return setting.isDiscovery();
	}

	private <ID> PostConnect<ID> postConnect(long id) {
		return (c) -> {
			RequestContext context = RpcAuthMessageContexts
					.authRequest(appContext.getAppType(), appContext.getServerId(), id, setting.getPassword())
					.willResponseFuture(3000L);
			c.send(context);
			context.getRespondFuture().get(12000, TimeUnit.MILLISECONDS);
			return true;
		};
	}

	public <UID> Client<UID> create(long id, URL url) {
		return clientGuide.client(url, postConnect(id));
	}

	public RpcClientFactory setClientGuide(ClientGuide clientGuide) {
		this.clientGuide = clientGuide;
		return this;
	}

	public RpcClientFactory setAppContext(NetAppContext appContext) {
		this.appContext = appContext;
		return this;
	}

	public RpcClientFactory setSetting(RpcServiceSetting setting) {
		this.setting = setting;
		return this;
	}

}
