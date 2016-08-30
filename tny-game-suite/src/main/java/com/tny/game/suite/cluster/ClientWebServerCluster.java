package com.tny.game.suite.cluster;


import com.tny.game.net.dispatcher.RequestSession;
import com.tny.game.net.kafka.KafkaNetBootstrap;
import com.tny.game.net.kafka.KafkaServerInfo;
import com.tny.game.suite.cluster.game.ServerLaunch;
import com.tny.game.suite.cluster.game.ServerOutline;
import com.tny.game.suite.cluster.game.ServerSetting;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;

public abstract class ClientWebServerCluster extends WebServerCluster {

    @Autowired
    private KafkaNetBootstrap kafkaNetBootstrap;

    public Optional<RequestSession> getClient(int serverID) {
        return kafkaNetBootstrap.getClient(Servers.GAME, serverID);
    }

    public ClientWebServerCluster(String serverType, boolean watchSetting, String... monitorWebTypes) {
        super(serverType, watchSetting, monitorWebTypes);
    }

    public ClientWebServerCluster(String serverType, boolean watchSetting, Collection<String> monitorWebTypes) {
        super(serverType, watchSetting, monitorWebTypes);
    }

    @Override
    protected void postRemoveOutline(ServerNode node) {
    }

    @Override
    protected void postUpdateOutline(ServerNode node, ServerOutline outline, boolean create) {
        kafkaNetBootstrap.getOrCreateClient(new KafkaServerInfo(outline.getServerType(), outline.getServerID()));
    }

    @Override
    protected void postRemoveSetting(ServerNode node) {
    }

    @Override
    protected void postUpdateSetting(ServerNode node, ServerSetting serverSetting, boolean create) {

    }

    @Override
    protected void postRemoveLaunch(ServerNode node) {
    }

    @Override
    protected void postUpdateLaunch(ServerNode node, ServerLaunch launch, boolean create) {

    }

}