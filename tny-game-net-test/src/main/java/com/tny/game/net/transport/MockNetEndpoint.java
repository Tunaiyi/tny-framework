package com.tny.game.net.transport;

import com.tny.game.common.context.*;
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.task.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * <p>
 */
public class MockNetEndpoint extends AttributesHolder implements NetEndpoint<Long> {

	private Certificate<Long> certificate;

	private List<MessageContext> writeQueue = new ArrayList<>();

	private List<MessageContext> sendQueue = new ArrayList<>();

	private List<Message> receiveQueue = new ArrayList<>();

	private EndpointStatus state;

	public MockNetEndpoint(Certificate<Long> certificate) {
		this.certificate = certificate;
		if (this.certificate.isAuthenticated()) {
			this.state = EndpointStatus.ONLINE;
		} else {
			this.state = EndpointStatus.OFFLINE;
		}
	}

	@Override
	public void online(Certificate<Long> certificate, NetTunnel<Long> tunnel) throws ValidatorFailException {
		this.certificate = certificate;
	}

	@Override
	public void onUnactivated(NetTunnel<Long> tunnel) {

	}

	//    @Override
	//    public void createMessage(NetTunnel<Long> tunnel, MessageContext<Long> context) {
	//        this.writeQueue.add(context);
	//    }

	//	@Override
	//	public Message createMessage(MessageContext context) {
	//		this.writeQueue.add(context);
	//		return null;
	//	}

	@Override
	public NetMessage allocateMessage(MessageFactory messageFactory, MessageContext context) {
		this.writeQueue.add(context);
		return null;
	}

	@Override
	public boolean receive(NetTunnel<Long> tunnel, Message message) {
		this.receiveQueue.add(message);
		return true;
	}

	@Override
	public SendContext send(NetTunnel<Long> tunnel, MessageContext messageContext) {
		this.sendQueue.add(messageContext);
		return messageContext;
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
	public RespondFutureHolder getRespondFutureHolder() {
		return null;
	}

	@Override
	public CommandTaskBox getCommandTaskBox() {
		return null;
	}

	@Override
	public void takeOver(CommandTaskBox commandTaskBox) {

	}

	@Override
	public EndpointContext getContext() {
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
	public Long getUserId() {
		return this.certificate.getUserId();
	}

	@Override
	public String getUserType() {
		return this.certificate.getUserType();
	}

	@Override
	public Certificate<Long> getCertificate() {
		return this.certificate;
	}

	@Override
	public boolean isLogin() {
		return this.certificate.isAuthenticated();
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
	public boolean receive(Message message) {
		return false;
	}

	@Override
	public SendContext send(MessageContext messageContext) {
		return null;
	}

	public List<MessageContext> getWriteQueue() {
		return this.writeQueue;
	}

	public List<MessageContext> getSendQueue() {
		return this.sendQueue;
	}

	public List<Message> getReceiveQueue() {
		return this.receiveQueue;
	}

}
