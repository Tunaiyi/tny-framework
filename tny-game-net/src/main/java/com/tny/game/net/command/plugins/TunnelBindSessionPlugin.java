package com.tny.game.net.command.plugins;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.InvokeContext;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.message.Message;

/**
 * 绑定会话插件
 *
 * @param <UID>
 */
public class TunnelBindSessionPlugin<UID> implements VoidControllerPlugin<UID> {

    private SessionKeeperMannager sessionKeeperMannager;

    public TunnelBindSessionPlugin(SessionKeeperMannager sessionKeeperMannager) {
        this.sessionKeeperMannager = sessionKeeperMannager;
    }

    @Override
    public void doExecute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) {
        if (tunnel instanceof NetTunnel) {
            SessionKeeper<UID> sessionKeeper = sessionKeeperMannager.getKeeper(tunnel.getUserType());
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
