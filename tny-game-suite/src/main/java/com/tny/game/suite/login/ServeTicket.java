package com.tny.game.suite.login;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoID;
import com.tny.game.suite.core.ScopeType;
import com.tny.game.suite.core.ScopeTypes;
import com.tny.game.suite.core.ServerType;
import com.tny.game.suite.core.ServerTypes;
import org.apache.commons.lang3.EnumUtils;

import java.io.Serializable;


@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@ProtoEx(SuiteProtoID.AUTH_$SERVE_TICKET)
public class ServeTicket implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty
    @ProtoExField(1)
    private String scopeType;

    @JsonProperty
    @ProtoExField(2)
    private String serverType;

    @JsonProperty
    @ProtoExField(3)
    private int serverID;

    @JsonProperty
    @ProtoExField(4)
    private long time;

    @JsonProperty
    @ProtoExField(5)
    private String ticket;

    public ServeTicket() {
        super();
    }

    public ServeTicket(ScopeType scopeType, int server, TicketMaker<ServeTicket> maker) {
        super();
        this.scopeType = scopeType.getName();
        this.serverType = scopeType.getServerType().getName();
        this.serverID = server;
        this.time = System.currentTimeMillis();
        if (maker != null)
            this.ticket = maker.make(this);
    }

    public String getScopeType() {
        return scopeType;
    }

    protected void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getServerType() {
        return serverType;
    }

    public int getServerID() {
        return this.serverID;
    }

    protected void setServerType(String serverType) {
        this.serverType = serverType;
    }

    protected void setServerID(int server) {
        this.serverID = server;
    }

    public long getTime() {
        return this.time;
    }

    protected void setTime(long time) {
        this.time = time;
    }

    public String getTicket() {
        return this.ticket;
    }

    protected void setTicket(String ticket) {
        this.ticket = ticket;
    }


    public ServerType asServerType() {
        return ServerTypes.of(serverType);
    }

    public ScopeType asScopeType() {
        return ScopeTypes.of(scopeType);
    }

    public <E extends Enum<E>> E asScopeType(Class<E> enumClass) {
        return EnumUtils.getEnum(enumClass, scopeType);
    }

    public <E extends Enum<E>> E asServerType(Class<E> enumClass) {
        return EnumUtils.getEnum(enumClass, serverType);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServeTicket serveTicket = (ServeTicket) o;

        if (serverID != serveTicket.serverID) return false;
        if (time != serveTicket.time) return false;
        if (!scopeType.equals(serveTicket.scopeType)) return false;
        return ticket.equals(serveTicket.ticket);

    }

    @Override
    public int hashCode() {
        int result = scopeType.hashCode();
        result = 31 * result + serverID;
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + ticket.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "APITicket{" +
                "scopeType=" + scopeType +
                ", serverID=" + serverID +
                ", time=" + time +
                ", ticket='" + ticket + '\'' +
                '}';
    }
}
