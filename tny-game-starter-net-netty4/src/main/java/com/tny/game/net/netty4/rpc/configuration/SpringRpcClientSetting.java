package com.tny.game.net.netty4.rpc.configuration;

import com.tny.game.net.rpc.setting.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/10 4:57 下午
 */
public class SpringRpcClientSetting extends RpcClientSetting {

	@Override
	public List<RpcServiceSetting> getServices() {
		return super.getServices();
	}

	@Override
	public RpcClientSetting setServices(List<RpcServiceSetting> services) {
		return super.setServices(services);
	}

}
