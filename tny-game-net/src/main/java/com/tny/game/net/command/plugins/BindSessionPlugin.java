package com.tny.game.net.command.plugins;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.message.Message;

/**
 * 绑定会话插件
 *
 * @param <UID>
 */
public class BindSessionPlugin<UID> implements VoidControllerPlugin<UID> {

    private EndpointKeeperManager endpointKeeperManager;

    public BindSessionPlugin(EndpointKeeperManager endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
    }

    @Override
    public void doExecute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) {
        if (tunnel instanceof NetTunnel) {
            SessionKeeper<UID> sessionKeeper = endpointKeeperManager.loadOcCreate(tunnel.getUserType(), EndpointType.SESSION);
            try {
                sessionKeeper.online((NetTunnel<UID>) tunnel);
            } catch (CommandException e) {
                context.doneAndIntercept(ResultFactory.create(e.getResultCode(), e.getBody()));
            } catch (Throwable e) {
                context.doneAndIntercept(NetResultCode.SESSION_LOSS);
            }
        }
    }
}
