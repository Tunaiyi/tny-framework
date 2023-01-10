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
import com.tny.game.common.url.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.MessageCommandBox;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Predicate;

/**
 * <p>
 */
public class MockNetEndpoint extends AttributeHolder implements NetEndpoint<Long>, NetSession<Long>, NetTerminal<Long> {

    private Certificate<Long> certificate;

    private final List<MessageContent> writeQueue = new ArrayList<>();

    private final List<MessageContent> sendQueue = new ArrayList<>();

    private final List<Message> receiveQueue = new ArrayList<>();

    private EndpointStatus state;

    private final NetAccessMode accessMode;

    public MockNetEndpoint(Certificate<Long> certificate, NetAccessMode accessMode) {
        this.certificate = certificate;
        if (this.certificate.isAuthenticated()) {
            this.state = EndpointStatus.ONLINE;
        } else {
            this.state = EndpointStatus.OFFLINE;
        }
        this.accessMode = accessMode;
    }

    @Override
    public void online(Certificate<Long> certificate, NetTunnel<Long> tunnel) throws AuthFailedException {
        this.certificate = certificate;
    }

    @Override
    public void onUnactivated(NetTunnel<Long> tunnel) {

    }

    @Override
    public NetTunnel<Long> tunnel() {
        return null;
    }

    @Override
    public NetMessage createMessage(MessageFactory messageFactory, MessageContent content) {
        this.writeQueue.add(content);
        return null;
    }

    @Override
    public boolean receive(RpcProviderContext context) {
        this.receiveQueue.add(context.netMessage());
        return true;
    }

    @Override
    public SendReceipt send(NetTunnel<Long> tunnel, MessageContent content) {
        this.sendQueue.add(content);
        return content;
    }

    @Override
    public void resend(NetTunnel<Long> tunnel, Predicate<Message> filter) {

    }

    @Override
    public void resend(NetTunnel<Long> tunnel, long fromId, FilterBound bound) {

    }

    @Override
    public void resend(NetTunnel<Long> tunnel, long fromId, long toId, FilterBound bound) {

    }

    @Override
    public MessageCommandBox getCommandBox() {
        return null;
    }

    @Override
    public void takeOver(MessageCommandBox commandTaskBox) {

    }

    @Override
    public EndpointContext getContext() {
        return null;
    }

    @Override
    public boolean closeWhen(EndpointStatus status) {
        return true;
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
    public List<Message> getSentMessages(Predicate<Message> filter) {
        return null;
    }

    @Override
    public List<Message> getAllSendMessages() {
        return null;
    }

    @Override
    public EndpointStatus getStatus() {
        return this.state;
    }

    @Override
    public long getOfflineTime() {
        return 0;
    }

    @Override
    public void offline() {
        this.state = EndpointStatus.OFFLINE;
    }

    @Override
    public boolean isOnline() {
        return this.state == EndpointStatus.ONLINE;
    }

    @Override
    public boolean isOffline() {
        return this.state == EndpointStatus.OFFLINE;
    }

    @Override
    public Certificate<Long> getCertificate() {
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
        return this.state == EndpointStatus.ONLINE;
    }

    @Override
    public boolean isClosed() {
        return this.state == EndpointStatus.CLOSE;
    }

    @Override
    public boolean close() {
        this.state = EndpointStatus.CLOSE;
        return true;
    }

    @Override
    public NetAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public SendReceipt send(MessageContent content) {
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
    public URL getUrl() {
        return null;
    }

    @Override
    public long getConnectTimeout() {
        return 0;
    }

    @Override
    public int getConnectRetryTimes() {
        return 0;
    }

    @Override
    public List<Long> getConnectRetryIntervals() {
        return null;
    }

    @Override
    public boolean isAsyncConnect() {
        return false;
    }

    @Override
    public MessageTransporter connect() {
        return null;
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void onConnected(NetTunnel<Long> tunnel) {

    }

    @Override
    public void execute(@Nonnull Runnable command) {

    }

}
