package com.tny.game.net.kafka;

import com.tny.game.net.LoginCertificate;

/**
 * Created by Kun Yang on 16/8/10.
 */
public interface KafkaTicketSeller {

    KafkaTicket create(KafkaServerInfo remoteServer, LoginCertificate certificate, String checkKey);

}
