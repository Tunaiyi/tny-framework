package com.tny.game.net.relay;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 透传管道发送器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:21 下午
 */
public class TubuleTransporter<UID> implements Transporter<UID> {

	private final RelayTransmitter transmitter;

	private final NetPipe<UID> pipe;

	private final InetSocketAddress remoteAddress;

	private NetTubule<UID> tubule;

	public TubuleTransporter(NetPipe<UID> pipe, InetSocketAddress remoteAddress) {
		this.pipe = pipe;
		this.remoteAddress = remoteAddress;
		this.transmitter = pipe.getTransmitter();
	}

	@Override
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		return this.transmitter.write(new MessagePacket(this.tubule, message), promise);
	}

	@Override
	public WriteMessageFuture write(MessageMaker<UID> maker, MessageContext context) throws NetException {
		WriteMessagePromise promise = as(context.getWriteMessageFuture());
		return this.transmitter.write(() -> new MessagePacket(this.tubule, maker.make(context)), promise);
	}

	@Override
	public void write(MessagesCollector collector) {
		for (Message message : collector.collect()) {
			this.transmitter.write(new MessagePacket(this.tubule, message), null);
		}
	}

	@Override
	public WriteMessagePromise createWritePromise(long sendTimeout) {
		return this.transmitter.createWritePromise(sendTimeout);
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return this.transmitter.getLocalAddress();
	}

	@Override
	public boolean isActive() {
		return this.transmitter.isActive();
	}

	@Override
	public void close() {
		this.pipe.destroyTubule(this.tubule);
	}

	@Override
	public void bind(NetTunnel<UID> tunnel) {
		this.tubule = as(tunnel);
	}

	public void pong() {
		this.transmitter.write(HeartbeatPacket.pong(this.tubule.getId()), null);
	}

	public void ping() {
		this.transmitter.write(HeartbeatPacket.ping(this.tubule.getId()), null);
	}

}
