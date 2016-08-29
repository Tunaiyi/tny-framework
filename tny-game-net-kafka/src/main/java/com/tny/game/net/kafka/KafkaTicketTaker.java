package com.tny.game.net.kafka;

/**
 * Created by Kun Yang on 16/8/10.
 */
public interface KafkaTicketTaker {

    boolean take(KafkaTicket ticket, String checkKey);

}
