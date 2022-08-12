/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
