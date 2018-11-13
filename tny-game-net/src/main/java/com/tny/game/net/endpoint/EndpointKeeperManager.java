package com.tny.game.net.endpoint;


import com.tny.game.common.unit.annotation.UnitInterface;

import java.util.Optional;

/**
 * 会话管理者
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-18 15:37
 */
@UnitInterface
public interface EndpointKeeperManager {

    <K extends EndpointKeeper<?, ?>> K loadOcCreate(String userType, EndpointType endpointType);

    <K extends EndpointKeeper<?, ?>> Optional<K> getKeeper(String userType);

    <K extends SessionKeeper<?>> Optional<K> getSessionKeeper(String userType);

    <K extends ClientKeeper<?>> Optional<K> getClientKeeper(String userType);

}
