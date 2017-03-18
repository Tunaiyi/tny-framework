package com.tny.game.net.kafka;

import com.tny.game.net.message.protoex.ProtoExRequest;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExField;

/**
 * Created by Kun Yang on 16/8/10.
 */
@ProtoEx(KafkaMessage.REQUEST_ID)
public class KafkaRequest extends ProtoExRequest implements KafkaMessage {

    public static KafkaRequest BLANK = new KafkaRequest();

    @ProtoExField(6)
    private KafkaTicket ticket;

    public KafkaTicket getTicket() {
        return ticket;
    }

    KafkaRequest setTicket(KafkaTicket ticket) {
        this.ticket = ticket;
        return this;
    }

}
