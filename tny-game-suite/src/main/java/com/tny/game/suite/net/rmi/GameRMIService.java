package com.tny.game.suite.net.rmi;

import com.tny.game.LogUtils;
import com.tny.game.common.utils.json.JSONUtils;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.client.rmi.NetRMIService;
import com.tny.game.net.client.rmi.RMISession;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.ServerSession;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.login.ServeTicket;
import com.tny.game.suite.login.ServeTicketMaker;
import com.tny.game.suite.login.TicketMaker;
import com.tny.game.suite.utils.Configs;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

public class GameRMIService extends NetRMIService implements ServerPreStart {

    private static TicketMaker<ServeTicket> maker = new ServeTicketMaker();

    private String host = null;

    public GameRMIService(AppContext context) {
        super(context);
    }

    @Override
    protected ServerSession createSession(Request request) throws DispatchException {
        try {
            if (this.host == null)
                this.host = RemoteServer.getClientHost();
        } catch (ServerNotActiveException e) {
            throw new IllegalArgumentException(LogUtils.format("请求 协议[{}] 无法找到host", this.host, request.getProtocol()), e);
        }
        String ticketWord = request.getParameter(request.size() - 1, String.class);
        if (ticketWord == null)
            throw new NullPointerException(LogUtils.format("{} 请求 协议[{}] 没有附带验证票据", this.host, request.getProtocol()));
        ServeTicket ticket = JSONUtils.toObject(ticketWord, ServeTicket.class);
        if (ticket == null)
            throw new IllegalArgumentException(LogUtils.format("{} 请求 协议[{}] 没有附带验证票据", this.host, request.getProtocol()));
        String scopeType = ticket.getScopeType();
        String serverType = ticket.getServerType();
        if (maker.make(ticket).equals(ticket.getTicket())) {
            RMISession session = new RMISession(ticket.getServerID(), serverType, this.host);
            String password = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
            session.attributes().setAttribute(SessionKeys.SYSTEM_USER_ID, ticket.getServerID());
            session.attributes().setAttribute(SessionKeys.SYSTEM_USER_USER_GROUP, serverType);
            session.attributes().setAttribute(SessionKeys.SYSTEM_USER_PASSWORD, password);
            return session;
        }
        throw new IllegalArgumentException(LogUtils.format("{} [{}] 请求 协议[{}] 无法通过验证", ticket, this.host, request.getProtocol()));
    }

    @Override
    public InitLevel getInitLevel() {
        return InitLevel.LEVEL_1;
    }

    @Override
    public void initialize() throws Exception {
        this.startService();
    }

}
