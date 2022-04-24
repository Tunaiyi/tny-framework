package com.tny.game.net.rpc.setting;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 5:02 下午
 */
public class RpcClientSetting {

    private List<RpcServiceSetting> services = new ArrayList<>();

    public List<RpcServiceSetting> getServices() {
        return services;
    }

    public RpcClientSetting setServices(List<RpcServiceSetting> services) {
        this.services = services;
        return this;
    }

}
