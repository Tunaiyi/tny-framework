package com.tny.game.suite.login;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.suite.SuiteProtoIDs;
import com.tny.game.suite.core.AppType;
import com.tny.game.suite.core.AppTypes;
import org.apache.commons.lang3.EnumUtils;

import java.io.Serializable;


@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@ProtoEx(SuiteProtoIDs.AUTH_$SERVE_TICKET)
public class ServerTicket implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty
    @ProtoExField(1)
    private String serverType;

    @JsonProperty
    @ProtoExField(2)
    private int serverID;

    @JsonProperty
    @ProtoExField(3)
    private long time;

    @JsonProperty
    @ProtoExField(4)
    private boolean confirm;

    @JsonProperty
    @ProtoExField(5)
    private String secret;

    public ServerTicket() {
        super();
    }

    public ServerTicket(String appType, int server, TicketMaker<ServerTicket> maker, boolean confirm) {
        super();
        this.serverType = appType;
        this.serverID = server;
        this.time = System.currentTimeMillis();
        if (maker != null)
            this.secret = maker.make(this);
    }

    public ServerTicket(AppType appType, int server, TicketMaker<ServerTicket> maker, boolean confirm) {
        super();
        this.serverType = appType.getName();
        this.serverID = server;
        this.time = System.currentTimeMillis();
        if (maker != null)
            this.secret = maker.make(this);
    }

    public String getServerType() {
        return serverType;
    }

    public int getServerID() {
        return this.serverID;
    }

    public long getTime() {
        return this.time;
    }

    public String getSecret() {
        return this.secret;
    }

    public AppType asServerType() {
        return AppTypes.of(serverType);
    }

    public <E extends Enum<E>> E asServerType(Class<E> enumClass) {
        return EnumUtils.getEnum(enumClass, serverType);
    }

    public boolean isConfirm() {
        return confirm;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerTicket serveTicket = (ServerTicket) o;

        if (serverID != serveTicket.serverID) return false;
        if (time != serveTicket.time) return false;
        return secret.equals(serveTicket.secret);

    }

    @Override
    public int hashCode() {
        int result = serverID;
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + secret.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "APITicket{" +
                "serverID=" + serverID +
                ", time=" + time +
                ", secret='" + secret + '\'' +
                '}';
    }
}
