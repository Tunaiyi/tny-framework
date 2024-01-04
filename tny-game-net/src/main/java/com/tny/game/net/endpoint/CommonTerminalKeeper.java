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

import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.util.Optional;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class CommonTerminalKeeper extends AbstractEndpointKeeper<Terminal, Terminal> implements TerminalKeeper {

    protected CommonTerminalKeeper(ContactType contactType) {
        super(contactType);
    }

    @Override
    public Optional<Terminal> online(Certificate certificate, NetTunnel tunnel) throws AuthFailedException {
        if (tunnel.getAccessMode() == NetAccessMode.CLIENT) {
            NetEndpoint endpoint = tunnel.getEndpoint();
            if (endpoint instanceof Terminal) {
                endpoint.online(certificate, tunnel);
                Terminal terminal = as(endpoint);
                this.replaceEndpoint(terminal.getIdentify(), terminal);
                return Optional.of(terminal);
            }
        }
        return Optional.empty();
    }

}