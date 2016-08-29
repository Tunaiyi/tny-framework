package com.tny.game.net.kafka;

import com.tny.game.net.dispatcher.message.protoex.ProtoExResponse;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;

/**
 * Created by Kun Yang on 16/8/10.
 */
@ProtoEx(KafkaMessage.RESPONSE_ID)
public class KafkaResponse extends ProtoExResponse implements KafkaMessage {

    @ProtoExField(11)
    private int remoteID;

    @ProtoExField(12)
    private String remoteType;

    public int getRemoteID() {
        return remoteID;
    }

    public String getRemoteType() {
        return remoteType;
    }

    KafkaResponse setRemoteID(int remoteID) {
        this.remoteID = remoteID;
        return this;
    }

    KafkaResponse setRemoteType(String remoteType) {
        this.remoteType = remoteType;
        return this;
    }
}
