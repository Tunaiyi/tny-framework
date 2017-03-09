package com.tny.game.net.kafka;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.AbstractRequestBuilder;

import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class KafkaRequestBuilder extends AbstractRequestBuilder<KafkaRequest> {

    private static Supplier<KafkaRequest> CREATOR = KafkaRequest::new;

    private KafkaTicketSeller ticketSeller;

    private LoginCertificate certificate;
    private KafkaServerInfo remoteServer;


    protected KafkaRequestBuilder() {
        super(CREATOR);
    }

    public KafkaRequestBuilder setTicketSeller(KafkaTicketSeller ticketSeller) {
        this.ticketSeller = ticketSeller;
        return this;
    }

    public KafkaRequestBuilder setCertificate(LoginCertificate certificate) {
        this.certificate = certificate;
        return this;
    }

    public KafkaRequestBuilder setRemoteServer(KafkaServerInfo remoteServer) {
        this.remoteServer = remoteServer;
        return this;
    }

    @Override
    protected void doBuild(KafkaRequest request) {
        request.setTicket(ticketSeller.create(this.remoteServer, certificate, request.getCheckCode()));
    }

}
