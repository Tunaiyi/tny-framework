package com.tny.game.suite.core;

import com.tny.game.protoex.annotations.*;
import com.tny.game.suite.*;
import org.apache.commons.lang3.builder.*;

/**
 * Created by Kun Yang on 2017/2/28.
 */
@ProtoEx(SuiteProtoIDs.CLUSTER_$INET_CONNECTOR)
public class InetConnector {

    @ProtoExField(1)
    private String id;

    @ProtoExField(2)
    private String desc;

    @ProtoExField(3)
    private String host;

    @ProtoExField(4)
    private int port;

    public InetConnector() {
    }

    public InetConnector(String id, String desc, String host, int port) {
        this.id = id;
        this.desc = desc;
        this.host = host;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("desc", desc)
                .append("host", host)
                .append("port", port)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof InetConnector))
            return false;
        InetConnector that = (InetConnector) o;
        return new EqualsBuilder()
                .append(getHost(), that.getHost())
                .append(getPort(), that.getPort())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHost())
                .append(getPort())
                .toHashCode();
    }
}
