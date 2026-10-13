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

package com.tny.game.net.relay.link;

import org.apache.commons.lang3.builder.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/4 3:39 下午
 */
public class BaseRelayExplorer<T extends NetRelayTunnel> implements RelayExplorer {

    private final Map<TunnelKey, T> tunnelMap = new ConcurrentHashMap<>();

    public <C extends T> C putTunnel(C tunnel) {
        T old = tunnelMap.put(TunnelKey.of(tunnel), tunnel);
        if (old != null && old != tunnel) {
            old.disconnect();
            return tunnel;
        }
        return tunnel;
    }

    @Override
    public T getTunnel(long instanceId, long tunnelId) {
        return tunnelMap.get(TunnelKey.of(instanceId, tunnelId));
    }

    @Override
    public void closeTunnel(long instanceId, long tunnelId) {
        // remove 原子返回唯一胜者：摘除即关闭，重复请求空手而归（天然幂等，relay-link 规格）
        T removed = tunnelMap.remove(TunnelKey.of(instanceId, tunnelId));
        if (removed != null) {
            removed.close();
        }
    }

    private record TunnelKey(long instanceId, long id) {

        public static TunnelKey of(RelayTunnel tunnel) {
            return new TunnelKey(tunnel.getInstanceId(), tunnel.getId());
        }

        public static TunnelKey of(long partition, long id) {
            return new TunnelKey(partition, id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof TunnelKey tunnelKey)) {
                return false;
            }

            return new EqualsBuilder().append(instanceId(), tunnelKey.instanceId())
                    .append(id(), tunnelKey.id())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(instanceId()).append(id()).toHashCode();
        }

    }

}
