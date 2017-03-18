package com.tny.game.net.kafka;

import com.tny.game.net.message.Message;

/**
 * Created by Kun Yang on 16/8/10.
 */
public interface KafkaMessage extends Message {

    int REQUEST_ID = 10;
    int RESPONSE_ID = 11;
    int KAFKA_TICKET_ID = 12;

}
