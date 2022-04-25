package com.tny.game.net.rpc;

import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.rpc.setting.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
@EventBusListener
public class DefaultRpcRemoteService implements RpcRemoteService, EndpointKeeperCreateListener<RpcAccessId> {

    private final Map<String, RpcRemoteServicer> servicerMap = new ConcurrentHashMap<>();

    public DefaultRpcRemoteService(RpcClientSetting setting) {
        setting.getServices()
                .stream().map(RpcServiceSetting::serviceName)
                .forEach(this::loadOrCreate);
    }

    @Override
    public RpcRemoteServicer loadOrCreate(String service) {
        return servicerMap.computeIfAbsent(service, RpcRemoteServicer::new);
    }

    @Override
    public RpcRemoteServicer getServicer(String name) {
        return servicerMap.get(name);
    }

    @Override
    public void onCreate(EndpointKeeper<RpcAccessId, Endpoint<RpcAccessId>> keeper) {
        RpcRemoteServicer servicer = getServicer(keeper.getUserType());
        if (servicer != null) {
            keeper.addListener(new EndpointKeeperListener<RpcAccessId>() {

                @Override
                public void onAddEndpoint(EndpointKeeper<RpcAccessId, Endpoint<RpcAccessId>> keeper, Endpoint<RpcAccessId> endpoint) {
                    servicer.addEndpoint(endpoint);
                }

                @Override
                public void onRemoveEndpoint(EndpointKeeper<RpcAccessId, Endpoint<RpcAccessId>> keeper, Endpoint<RpcAccessId> endpoint) {
                    servicer.removeEndpoint(endpoint);
                }
            });
        }
    }

}