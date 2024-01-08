/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.rpc;

import com.tny.game.common.event.bus.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.rpc.setting.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 6:11 下午
 */
@GlobalEventListener
public class DefaultRpcServicerManager extends BaseRpcServicerManager {

    public DefaultRpcServicerManager(RpcRemoteSetting setting) {
        setting.getClusters()
                .stream()
                .map(RpcServiceSetting::serviceName)
                .map(s -> (RpcServiceType) RpcServiceTypes.ofService(s))
                .forEach(this::loadInvokeNodeSet);
    }

}