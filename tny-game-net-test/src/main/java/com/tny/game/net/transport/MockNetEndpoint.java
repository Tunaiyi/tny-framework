package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * <p>
 */
public class MockNetEndpoint extends AttributesHolder implements NetEndpoint<Long> {

    private Certificate<Long> certificate;

    private List<MessageContext<Long>> writeQueue = new ArrayList<>();

    private List<MessageContext<Long>> sendQueue = new ArrayList<>();

    private List<Message<Long>> receiveQueue = new ArrayList<>();

    private EndpointState state;

    public MockNetEndpoint(Certificate<Long> certificate) {
        this.certificate = certificate;
        if (this.certificate.isAutherized()) {
            this.state = EndpointState.ONLINE;
        } else {
            this.state = EndpointState.OFFLINE;
        }
    }

    @Override
    public void online(Certificate<Long> certificate, NetTunnel<Long> tunnel) throws ValidatorFailException {
        this.certificate = certificate;
    }

    @Override
    public void onUnactivated(NetTunnel<Long> tunnel) {

    }

    @Override
    public EndpointEventsBox<Long> getEventsBox() {
        return null;
    }

    @Override
    public void writeMessage(NetTunnel<Long> tunnel, MessageContext<Long> context) {
        writeQueue.add(context);
    }

    @Override
    public boolean receive(NetTunnel<Long> tunnel, Message<Long> message) {
        receiveQueue.add(message);
        return true;
    }

    @Override
    public SendContext<Long> send(NetTunnel<Long> tunnel, MessageContext<Long> messageContext) {
        sendQueue.add(messageContext);
        return messageContext;
    }

    @Override
    public void resend(NetTunnel<Long> tunnel, Predicate<Message<Long>> filter) {

    }

    @Override
    public RespondFutureHolder getRespondFutureHolder() {
        return null;
    }

    @Override
    public void takeOver(EndpointEventsBox<Long> eventsBox) {

    }

    @Override
    public EndpointEventHandler<Long, NetEndpoint<Long>> getEventHandler() {
        return null;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public MessageHandleFilter<Long> getSendFilter() {
        return null;
    }

    @Override
    public MessageHandleFilter<Long> getReceiveFilter() {
        return null;
    }

    @Override
    public void heartbeat() {

    }

    @Override
    public void setSendFilter(MessageHandleFilter<Long> filter) {

    }

    @Override
    public void setReceiveFilter(MessageHandleFilter<Long> filter) {

    }

    @Override
    public List<Message<Long>> getSendMessages(Predicate<Message<Long>> filter) {
        return null;
    }

    @Override
    public List<Message<Long>> getAllSendMessages() {
        return null;
    }

    @Override
    public EndpointState getState() {
        return state;
    }

    @Override
    public long getOfflineTime() {
        return 0;
    }

    @Override
    public void offline() {
        this.state = EndpointState.OFFLINE;
    }

    @Override
    public boolean isOnline() {
        return state == EndpointState.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return state == EndpointState.OFFLINE;
    }

    @Override
    public Long getUserId() {
        return certificate.getUserId();
    }

    @Override
    public String getUserType() {
        return certificate.getUserType();
    }

    @Override
    public Certificate<Long> getCertificate() {
        return certificate;
    }

    @Override
    public boolean isLogin() {
        return certificate.isAutherized();
    }

    @Override
    public boolean isClosed() {
        return state == EndpointState.CLOSE;
    }

    @Override
    public void close() {
        this.state = EndpointState.CLOSE;
    }

    @Override
    public boolean receive(Message<Long> message) {
        return false;
    }

    @Override
    public SendContext<Long> send(MessageContext<Long> messageContext) {
        return null;
    }

    public List<MessageContext<Long>> getWriteQueue() {
        return writeQueue;
    }

    public List<MessageContext<Long>> getSendQueue() {
        return sendQueue;
    }

    public List<Message<Long>> getReceiveQueue() {
        return receiveQueue;
    }
}
