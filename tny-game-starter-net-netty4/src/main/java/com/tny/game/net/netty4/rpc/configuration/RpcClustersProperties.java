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
@ConfigurationProperties("tny.net.rpc")
public class RpcClustersProperties extends RpcRemoteSetting {

    @Override
    public List<RpcClusterSetting> getClusters() {
        return super.getClusters();
    }

    @Override
    public RpcClustersProperties setClusters(List<RpcClusterSetting> clusters) {
        super.setClusters(clusters);
        return this;
    }
}