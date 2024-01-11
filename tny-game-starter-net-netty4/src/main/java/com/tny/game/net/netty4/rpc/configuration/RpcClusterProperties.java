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

package com.tny.game.net.netty4.rpc.configuration;

import com.tny.game.net.rpc.*;
import com.tny.game.net.rpc.setting.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/5 4:20 下午
 */
@ConfigurationProperties("tny.net.rpc.cluster")
public class RpcClusterProperties extends RpcRemoteSetting {

    @Override
    public List<RpcClusterSetting> getServices() {
        return super.getServices();
    }

    @Override
    public RpcClusterProperties setServices(List<RpcClusterSetting> clusters) {
        super.setServices(clusters);
        return this;
    }
}