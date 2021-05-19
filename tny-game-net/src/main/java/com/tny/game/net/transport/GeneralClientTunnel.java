package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import org.slf4j.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralClientTunnel<UID, E extends NetTerminal<UID>> extends GeneralTunnel<UID, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralClientTunnel.class);

    public GeneralClientTunnel(NetBootstrapContext<UID> bootstrapContext) {
        super(null, TunnelMode.CLIENT, bootstrapContext);
    }

    @Override
    protected boolean onOpen() {
        if (!this.isAvailable()) {
            try {
                this.status = TunnelStatus.INIT;
                NetTransport<UID> transport = this.endpoint.connect();
                if (transport != null) {
                    this.transport = transport;
                    this.transport.bind(this);
                    this.status = TunnelStatus.ACTIVATED;
                    this.endpoint.onConnected(this);
                    return true;
                }
            } catch (Exception e) {
                this.disconnect();
                throw new TunnelException(e, "{} failed to connect to server", this);
            }
        }
        LOGGER.warn("{} is available", this);
        return false;
    }

    @Override
    protected void onDisconnect() {
        this.disconnectChannel();
    }

    @Override
    protected void onWriteUnavailable() {
        this.endpoint.reconnect();
    }

    @Override
    public String toString() {
        return "NettyClientTunnel{" + "channel=" + this.transport + '}';
    }

    @Override
    protected boolean replayEndpoint(NetEndpoint<UID> endpoint) {
        return this.endpoint == endpoint;
    }

}
