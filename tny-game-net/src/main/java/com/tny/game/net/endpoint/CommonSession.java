package com.tny.game.net.endpoint;

import com.google.common.base.MoreObjects;
import com.tny.game.net.transport.*;
import org.slf4j.*;

/**
 * 抽象Session
 * <p>
 * Created by Kun Yang on 2017/2/17.
 */
public class CommonSession<UID> extends AbstractEndpoint<UID> implements NetSession<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(CommonSession.class);

    public CommonSession(EndpointEventHandler<UID, ? extends NetEndpoint<UID>> eventHandler, int cacheSentMessageSize) {
        super(null, eventHandler, cacheSentMessageSize);
    }

    @Override
    public Certificate<UID> getCertificate() {
        return certificate;
    }

    @Override
    public long getOfflineTime() {
        return offlineTime;
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        if (isOffline())
            return;
        synchronized (this) {
            if (isOffline())
                return;
            Tunnel<UID> currentTunnel = this.currentTunnel();
            if (currentTunnel.isAvailable())
                return;
            setOffline();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("userGroup", this.getUserType())
                          .add("userId", this.getUserId())
                          .add("tunnel", this.currentTunnel())
                          .toString();
    }

}