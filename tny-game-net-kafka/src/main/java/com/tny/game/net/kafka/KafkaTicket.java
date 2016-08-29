package com.tny.game.net.kafka;

import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;

/**
 * Created by Kun Yang on 16/8/10.
 */
@ProtoEx(KafkaMessage.KAFKA_TICKET_ID)
public class KafkaTicket {

    @ProtoExField(1)
    private String userGroup;

    @ProtoExField(2)
    private long userID;

    @ProtoExField(3)
    private String remoteType;

    @ProtoExField(4)
    private int remoteID;

    @ProtoExField(5)
    private String secret;

    public KafkaTicket() {
    }

    public KafkaTicket(String userGroup, long userID, String remoteType, int remoteID, String secret) {
        this.userGroup = userGroup;
        this.userID = userID;
        this.remoteType = remoteType;
        this.remoteID = remoteID;
        this.secret = secret;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public long getUID() {
        return userID;
    }

    public String getSecret() {
        return secret;
    }

    public String getRemoteType() {
        return remoteType;
    }

    public int getRemoteID() {
        return remoteID;
    }
}
