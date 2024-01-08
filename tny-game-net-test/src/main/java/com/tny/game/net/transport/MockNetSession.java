/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.MessageCommandBox;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Predicate;

/**
 * <p>
 */
public class MockNetSession extends AttributeHolder implements NetSession {

    private Certificate certificate;

    private final List<MessageContent> writeQueue = new ArrayList<>();

    private final List<MessageContent> sendQueue = new ArrayList<>();

    private final List<Message> receiveQueue = new ArrayList<>();

    private SessionStatus state;

    private final SessionEventWatches watches = new SessionEventBuses();

    private final NetAccessMode accessMode;

    public MockNetSession(Certificate certificate, NetAccessMode accessMode) {
        this.certificate = certificate;
        if (this.certificate.isAuthenticated()) {
            this.state = SessionStatus.ONLINE;
        } else {
            this.state = SessionStatus.OFFLINE;
        }
        this.accessMode = accessMode;
    }

    @Override
    public void online(Certificate certificate, NetTunnel tunnel) {
        this.certificate = certificate;
    }

    @Override
    public void online(Certificate certificate) {
        this.certificate = certificate;
    }


    @Override
    public void onUnactivated(NetTunnel tunnel) {

    }

    @Override
    public NetTunnel tunnel() {
        return null;
    }

    @Override
    public NetMessage createMessage(MessageFactory messageFactory, MessageContent content) {
        this.writeQueue.add(content);
        return null;
    }

    @Override
    public boolean receive(RpcEnterContext context) {
        this.receiveQueue.add(context.getMessage());
        return true;
    }

    @Override
    public MessageSent send(NetTunnel tunnel, MessageContent content) {
        this.sendQueue.add(content);
        return content;
    }

    @Override
    public void resend(NetTunnel tunnel, Predicate<Message> filter) {

    }

    @Override
    public void resend(NetTunnel tunnel, long fromId, FilterBound bound) {

    }

    @Override
    public void resend(NetTunnel tunnel, long fromId, long toId, FilterBound bound) {

    }

    @Override
    public MessageCommandBox getCommandBox() {
        return null;
    }

    @Override
    public SessionContext getContext() {
        return null;
    }

    @Override
    public void setSendMessageCachedSize(int messageSize) {

    }

    @Override
    public boolean closeWhen(SessionStatus status) {
        return true;
    }

    @Override
    public SessionEventWatches events() {
        return watches;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public MessageHandleFilter getSendFilter() {
        return null;
    }

    @Override
    public MessageHandleFilter getReceiveFilter() {
        return null;
    }

    @Override
    public void heartbeat() {

    }

    @Override
    public void setSendFilter(MessageHandleFilter filter) {

    }

    @Override
    public void setReceiveFilter(MessageHandleFilter filter) {

    }

    @Override
    public List<Message> getSentMessages(Predicate<Message> filter) {
        return null;
    }

    @Override
    public List<Message> getAllSendMessages() {
        return null;
    }

    @Override
    public SessionStatus getStatus() {
        return this.state;
    }

    @Override
    public long getOfflineTime() {
        return 0;
    }

    @Override
    public void offline() {
        this.state = SessionStatus.OFFLINE;
    }

    @Override
    public boolean isOnline() {
        return this.state == SessionStatus.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == SessionStatus.OFFLINE;
    }

    @Override
    public Certificate getCertificate() {
        return this.certificate;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public boolean isActive() {
        return this.state == SessionStatus.ONLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == SessionStatus.CLOSE;
    }

    @Override
    public boolean close() {
        this.state = SessionStatus.CLOSE;
        return true;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public MessageSent send(MessageContent content) {
        return null;
    }

    public List<MessageContent> getWriteQueue() {
        return this.writeQueue;
    }

    public List<MessageContent> getSendQueue() {
        return this.sendQueue;
    }

    public List<Message> getReceiveQueue() {
        return this.receiveQueue;
    }


    @Override
    public void execute(@Nonnull Runnable command) {

    }

}
