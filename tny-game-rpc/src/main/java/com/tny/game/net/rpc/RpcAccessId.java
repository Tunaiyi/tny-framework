package com.tny.game.net.rpc;

import org.apache.commons.lang3.builder.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/3 4:38 下午
 */
public class RpcAccessId implements Comparable<RpcAccessId> {

    private String service;

    private long serverId;

    private long id;

    public RpcAccessId() {
    }

    public RpcAccessId(String service, long serverId, long instanceId) {
        this.service = service;
        this.serverId = serverId;
        this.id = instanceId;
    }

    public String getService() {
        return service;
    }

    public long getServerId() {
        return serverId;
    }

    public long getId() {
        return id;
    }

    private RpcAccessId setService(String service) {
        this.service = service;
        return this;
    }

    protected RpcAccessId setServerId(long serverId) {
        this.serverId = serverId;
        return this;
    }

    protected RpcAccessId setId(long id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RpcAccessId)) {
            return false;
        }

        RpcAccessId id = (RpcAccessId)o;

        return new EqualsBuilder().append(getServerId(), id.getServerId())
                .append(getId(), id.getId())
                .append(getService(), id.getService())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getService()).append(getServerId()).append(getId()).toHashCode();
    }

    @Override
    public int compareTo(RpcAccessId o) {
        int serviceCompare = this.service.compareTo(o.service);
        if (serviceCompare != 0) {
            return serviceCompare;
        }
        long sidCompare = this.serverId - o.serverId;
        if (sidCompare != 0) {
            return sidCompare > 0 ? 1 : -1;
        }
        return (int)(this.id - o.id);
    }

}