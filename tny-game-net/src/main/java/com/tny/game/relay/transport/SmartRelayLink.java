package com.tny.game.relay.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.packet.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/21 3:51 上午
 */
public class SmartRelayLink implements NetRelayLink {

	private RelayLinkExplorer<?> explorer;

	private NetRelayLink current;

	@Override
	public int getServeNode() {
		return 0;
	}

	@Override
	public String getServeType() {
		return null;
	}

	@Override
	public int getServeLine() {
		return 0;
	}

	@Override
	public long getCreateTime() {
		return 0;
	}

	@Override
	public RelayLinkStatus getStatus() {
		return null;
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
	public WriteMessagePromise createWritePromise() {
		return null;
	}

	@Override
	public void open() {

	}

	@Override
	public void close() {

	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void receive(RelayPacket<?> packet) {

	}

	@Override
	public WriteMessageFuture write(RelayPacket<?> packet, boolean promise) {
		return null;
	}

	@Override
	public WriteMessageFuture relay(long tunnelId, Message message, WriteMessagePromise promise) {
		return null;
	}

	@Override
	public WriteMessageFuture relay(long tunnelId, MessageAllocator allocator, MessageFactory factory, MessageContext context) {
		return null;
	}

}
