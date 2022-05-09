package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.transport.*;

import java.util.Optional;

/**
 * 会话管理者
 * <p>
 */
@UnitInterface
public interface EndpointKeeperManager {

    <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> K loadEndpoint(MessagerType userType, TunnelMode tunnelMode);

    <UID, K extends EndpointKeeper<UID, ? extends Endpoint<UID>>> Optional<K> getKeeper(MessagerType userType);

    <UID, K extends SessionKeeper<UID>> Optional<K> getSessionKeeper(MessagerType userType);

    <UID, K extends TerminalKeeper<UID>> Optional<K> getTerminalKeeper(MessagerType userType);

}
