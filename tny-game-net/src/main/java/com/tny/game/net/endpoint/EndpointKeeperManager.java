/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.endpoint;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;
import com.tny.game.net.rpc.*;

import java.util.Optional;

/**
 * 会话管理者
 * <p>
 */
@UnitInterface
public interface EndpointKeeperManager {

    <K extends EndpointKeeper<? extends Endpoint>> K loadEndpoint(ContactType userType, NetAccessMode accessMode);

    <K extends EndpointKeeper<? extends Endpoint>> Optional<K> getKeeper(ContactType userType);

    <K extends SessionKeeper> Optional<K> getSessionKeeper(ContactType userType);

    <K extends TerminalKeeper> Optional<K> getTerminalKeeper(ContactType userType);

}
