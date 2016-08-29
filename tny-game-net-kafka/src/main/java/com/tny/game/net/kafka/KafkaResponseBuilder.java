package com.tny.game.net.kafka;

import com.tny.game.net.dispatcher.AbstractResponseBuilder;

import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class KafkaResponseBuilder extends AbstractResponseBuilder<KafkaResponse> {

    private static Supplier<KafkaResponse> CREATOR = KafkaResponse::new;

    private KafkaServerInfo localServer;

    protected KafkaResponseBuilder() {
        super(CREATOR);
    }

    @Override
    protected void doBuild(KafkaResponse request) {
        request.setRemoteType(localServer.getServerType())
                .setRemoteID(localServer.getID());
    }

    KafkaResponseBuilder setLocalServer(KafkaServerInfo localServer) {
        this.localServer = localServer;
        return this;
    }
}
