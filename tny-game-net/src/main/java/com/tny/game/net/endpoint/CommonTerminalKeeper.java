package com.tny.game.net.endpoint;

import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;

import java.util.Optional;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class CommonTerminalKeeper<UID> extends AbstractEndpointKeeper<UID, Terminal<UID>, Terminal<UID>> implements TerminalKeeper<UID> {

    protected CommonTerminalKeeper(String userType) {
        super(userType);
    }

    @Override
    public Optional<Terminal<UID>> online(Certificate<UID> certificate, NetTunnel<UID> tunnel) throws ValidatorFailException {
        if (tunnel.getMode() == TunnelMode.CLIENT) {
            NetEndpoint<UID> endpoint = tunnel.getEndpoint();
            if (endpoint instanceof Client) {
                endpoint.online(certificate, tunnel);
                Terminal<UID> client = as(endpoint);
                Terminal<UID> oldClient = this.endpointMap.put(client.getUserId(), client);
                if (oldClient != null && client != oldClient)
                    oldClient.close();
                return Optional.of(client);
            }
        }
        return Optional.empty();
    }

}