package com.tny.game.net.kafka;

import com.tny.game.net.message.MessageBuilderFactory;
import com.tny.game.net.dispatcher.RequestBuilder;
import com.tny.game.net.dispatcher.ResponseBuilder;
import com.tny.game.net.session.Session;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class KafkaMessageBuilderFactory implements MessageBuilderFactory {

    private KafkaTicketSeller ticketCreator;

    public KafkaMessageBuilderFactory(KafkaTicketSeller ticketCreator) {
        this.ticketCreator = ticketCreator;
    }

    @Override
    public RequestBuilder newRequestBuilder(Session session) {
        KafkaSession kafkaSession = (KafkaSession) session;
        return new KafkaRequestBuilder()
                .setRemoteServer(kafkaSession.getServerInfo())
                .setCertificate(kafkaSession.getCertificate())
                .setTicketSeller(ticketCreator)
                .setSession(session);
    }

    @Override
    public ResponseBuilder newResponseBuilder(Session session) {
        KafkaSession kafkaSession = (KafkaSession) session;
        return new KafkaResponseBuilder()
                .setLocalServer(kafkaSession.getServerInfo());
    }

}
