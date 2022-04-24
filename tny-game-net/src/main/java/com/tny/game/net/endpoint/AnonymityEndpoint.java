package com.tny.game.net.endpoint;

import com.tny.game.net.command.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class AnonymityEndpoint<UID> extends BaseNetEndpoint<UID> implements NetSession<UID> {

    private static final CommonSessionSetting SETTING = new CommonSessionSetting().setSendMessageCachedSize(0);

    public AnonymityEndpoint(CertificateFactory<UID> certificateFactory, EndpointContext endpointContext, NetTunnel<UID> tunnel) {
        super(SETTING, certificateFactory.anonymous(), endpointContext);
        this.tunnel = tunnel;
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        this.close();
    }

}
