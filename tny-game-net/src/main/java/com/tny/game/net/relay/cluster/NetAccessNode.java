/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
