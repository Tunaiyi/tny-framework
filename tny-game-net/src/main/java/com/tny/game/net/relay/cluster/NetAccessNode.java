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

package com.tny.game.net.relay.cluster;

import com.tny.game.common.collection.map.access.*;
import com.tny.game.common.url.*;

import java.util.Map;

/**
 * 集群节点信息
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:21 下午
 */
public interface NetAccessNode {

    /**
     * @return 集群服务实例 id
     */
    long getId();

    /**
     * @return 网络协议 http/https/tcp
     */
    String getScheme();

    /**
     * @return host
     */
    String getHost();

    /**
     * @return 端口
     */
    int getPort();

    /**
     * @return 是否监控
     */
    boolean isHealthy();

    /**
     * @return 获取 url
     */
    default String getUrl() {
        return url().toString();
    }

    /**
     * @return 获取地址
     */
    default URL url() {
        return new URL(this.getScheme(), this.getHost(), this.getPort());
    }

    /**
     * @return 获取元数据
     */
    MapAccessor metadata();

    /**
     * @return 获取元数据
     */
    Map<String, Object> getMetadata();

}
