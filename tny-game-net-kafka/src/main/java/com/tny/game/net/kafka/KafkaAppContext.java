package com.tny.game.net.kafka;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.base.simple.AbstractAppContext;
import com.tny.game.net.checker.MessageCheckGenerator;

import java.util.List;

/**
 * Created by Kun Yang on 16/8/10.
 */
public class KafkaAppContext extends AbstractAppContext {

    private String scopeType;

    private int localServerID;

    private long pollTimeout = 3600;

    private List<KafkaServerInfo> servers = ImmutableList.of();

    private KafkaTicketTaker ticketTaker;

    private MessageCheckGenerator verifier;

    private KafkaMessageBuilderFactory messageBuilderFactory;

    public KafkaAppContext(String scopeType) {
        this.scopeType = scopeType;
    }

    @Override
    public String getScopeType() {
        return scopeType;
    }

    public int getLocalServerID() {
        return localServerID;
    }

    public long getPollTimeout() {
        return pollTimeout;
    }

    public List<KafkaServerInfo> getServers() {
        return servers;
    }

    public KafkaTicketTaker getTicketTaker() {
        return ticketTaker;
    }

    public KafkaMessageBuilderFactory getMessageBuilderFactory() {
        return messageBuilderFactory;
    }

    public MessageCheckGenerator getVerifier() {
        return verifier;
    }

    public KafkaAppContext setLocalServerID(int localServerID) {
        this.localServerID = localServerID;
        return this;
    }

    public KafkaAppContext setPollTimeout(long pollTimeout) {
        this.pollTimeout = pollTimeout;
        return this;
    }

    public KafkaAppContext setServers(List<KafkaServerInfo> servers) {
        this.servers = servers;
        return this;
    }

    public KafkaAppContext setServers(KafkaServerInfo... servers) {
        this.servers = ImmutableList.copyOf(servers);
        return this;
    }

    public KafkaAppContext setVerifier(MessageCheckGenerator verifier) {
        this.verifier = verifier;
        return this;
    }

    public KafkaAppContext setTicketTaker(KafkaTicketTaker ticketTaker) {
        this.ticketTaker = ticketTaker;
        return this;
    }

    public KafkaAppContext setMessageBuilderFactory(KafkaMessageBuilderFactory messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
        return this;
    }


}
