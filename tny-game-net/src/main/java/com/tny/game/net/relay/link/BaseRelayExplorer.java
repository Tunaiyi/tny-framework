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
        T tunnel = tunnelMap.get(TunnelKey.of(instanceId, tunnelId));
        if (tunnel != null) {
            tunnel.close();
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
