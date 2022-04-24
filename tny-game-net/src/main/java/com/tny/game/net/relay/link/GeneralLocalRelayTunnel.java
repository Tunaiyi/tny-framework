package com.tny.game.net.relay.link;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class GeneralLocalRelayTunnel<UID> extends ServerTunnel<UID, NetSession<UID>, MessageTransporter> implements LocalRelayTunnel<UID> {

    private final long instanceId;

    private final InetSocketAddress remoteAddress;

    private final LocalRelayMessageTransporter transporter;

    public GeneralLocalRelayTunnel(long instanceId, long id, LocalRelayMessageTransporter transporter,
            InetSocketAddress remoteAddress, NetworkContext context) {
        super(id, transporter, context);
        this.transporter = transporter;
        this.instanceId = instanceId;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public long getInstanceId() {
        return instanceId;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public MessageWriteAwaiter relay(Message message, boolean needPromise) {
        MessageWriteAwaiter promise = needPromise ? new MessageWriteAwaiter() : null;
        this.write(message, promise);
        return promise;
    }

    @Override
    public boolean switchLink(LocalRelayLink link) {
        return this.transporter.switchLink(link);
    }

    //	@Override
    //	public void onLinkDisconnect(NetRelayLink link) {
    //		this.close();
    //	}
    //
    //	@Override
    //	public void disconnectOnLink(NetRelayLink link) {
    //		this.disconnect();
    //	}

}
