package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralServerTunnel<UID> extends ServerTunnel<UID, NetSession<UID>, MessageTransporter> {

    public GeneralServerTunnel(long id, MessageTransporter transport, NetworkContext context) {
        super(id, transport, context);
    }

}
