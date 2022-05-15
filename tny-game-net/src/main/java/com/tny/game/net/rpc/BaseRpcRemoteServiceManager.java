package com.tny.game.net.rpc;

import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.listener.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
@EventBusListener
public class BaseRpcRemoteServiceManager implements RpcRemoteServiceManager, EndpointKeeperCreateListener<RpcAccessIdentify> {

    private final Map<RpcServiceType, RpcRemoteServiceSet> servicerMap = new ConcurrentHashMap<>();

    @Override
    public RpcRemoteServiceSet loadOrCreate(RpcServiceType serviceType) {
        return servicerMap.computeIfAbsent(serviceType, (k) -> new RpcRemoteServiceSet(serviceType));
    }

    @Override
    public RpcRemoteServiceSet find(RpcServiceType name) {
        return servicerMap.get(name);
    }

    @Override
    public void onCreate(EndpointKeeper<RpcAccessIdentify, Endpoint<RpcAccessIdentify>> keeper) {
        MessagerType messagerType = keeper.getMessagerType();
        if (messagerType instanceof RpcServiceType) {
            RpcServiceType serviceType = as(messagerType, RpcServiceType.class);
            RpcRemoteServiceSet servicer = loadOrCreate(serviceType);
            keeper.addListener(new EndpointKeeperListener<RpcAccessIdentify>() {

                @Override
                public void onAddEndpoint(EndpointKeeper<RpcAccessIdentify, Endpoint<RpcAccessIdentify>> keeper,
                        Endpoint<RpcAccessIdentify> endpoint) {
                    servicer.addEndpoint(endpoint);
                }

                @Override
                public void onRemoveEndpoint(EndpointKeeper<RpcAccessIdentify, Endpoint<RpcAccessIdentify>> keeper,
                        Endpoint<RpcAccessIdentify> endpoint) {
                    servicer.removeEndpoint(endpoint);
                }
            });
        }
    }

}