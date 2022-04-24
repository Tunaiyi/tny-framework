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
public class BaseRelayExplorer<T extends NetRelayTunnel<?>> implements RelayExplorer<T> {

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

    private static class TunnelKey {

        private final long instanceId;

        private final long id;

        public static TunnelKey of(RelayTunnel<?> tunnel) {
            return new TunnelKey(tunnel.getInstanceId(), tunnel.getId());
        }

        public static TunnelKey of(long partition, long id) {
            return new TunnelKey(partition, id);
        }

        private TunnelKey(long instanceId, long id) {
            this.instanceId = instanceId;
            this.id = id;
        }

        private long getInstanceId() {
            return instanceId;
        }

        private long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof TunnelKey)) {
                return false;
            }

            TunnelKey tunnelKey = (TunnelKey)o;

            return new EqualsBuilder().append(getInstanceId(), tunnelKey.getInstanceId())
                    .append(getId(), tunnelKey.getId())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(getInstanceId()).append(getId()).toHashCode();
        }

    }

}
