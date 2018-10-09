package com.tny.game.net.command.plugins;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.Message;

/**
 * 绑定会话插件
 *
 * @param <UID>
 */
public class TunnelBindSessionPlugin<UID> implements VoidControllerPlugin<UID> {

    private SessionKeeperFactory sessionKeeperFactory;

    public TunnelBindSessionPlugin(SessionKeeperFactory sessionKeeperFactory) {
        this.sessionKeeperFactory = sessionKeeperFactory;
    }

    @Override
    public void doExecute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) {
        if (tunnel instanceof NetTunnel) {
            SessionKeeper<UID> sessionKeeper = sessionKeeperFactory.getKeeper(tunnel.getUserType());
            try {
                sessionKeeper.online((NetTunnel<UID>) tunnel);
            } catch (DispatchException e) {
                context.doneAndIntercept(ResultFactory.create(e.getResultCode(), e.getBody()));
            } catch (Throwable e) {
                context.doneAndIntercept(NetResultCode.SESSION_LOSS);
            }
        }
    }
}
