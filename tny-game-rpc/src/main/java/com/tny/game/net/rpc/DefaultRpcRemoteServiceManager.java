package com.tny.game.net.rpc;

import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.rpc.setting.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
@EventBusListener
public class DefaultRpcRemoteServiceManager extends BaseRpcRemoteServiceManager {

    public DefaultRpcRemoteServiceManager(RpcClientSetting setting) {
        setting.getServices()
                .stream()
                .map(RpcServiceSetting::serviceName)
                .map(s -> (RpcServiceType)RpcServiceTypes.ofService(s))
                .forEach(this::loadOrCreate);
    }

}