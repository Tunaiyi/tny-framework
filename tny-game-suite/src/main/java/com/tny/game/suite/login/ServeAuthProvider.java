package com.tny.game.suite.login;

import com.tny.game.common.utils.json.JSONUtils;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;
import com.tny.game.net.dispatcher.exception.ValidatorFailException;
import com.tny.game.suite.core.ServerType;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.utils.Configs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ServeAuthProvider extends GameAuthProvider {

    @Autowired
    private ServeTicketMaker maker;

    public ServeAuthProvider(List<Integer> authProtocols) {
        super("serve-auth-provider", authProtocols);
    }

    @Override
    public LoginCertificate validate(Request request) throws DispatchException {
        return checkClustersLogin(request);
    }

    protected LoginCertificate checkClustersLogin(Request request) throws ValidatorFailException {
        String ticketWord = request.getParameter(0, String.class);
        ServeTicket ticket = JSONUtils.toObject(ticketWord, ServeTicket.class);
        ServerType serverType = ticket.asScopeType().getServerType();
        if (this.maker.make(ticket).equals(ticket.getTicket())) {
            LoginCertificate info = LoginCertificate.createLogin(ticket.getServerID(), serverType.getName(), false);
            String truePWD = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
            request.getSession().attributes().setAttribute(SessionKeys.SYSTEM_USER_ID, ticket.getServerID());
            request.getSession().attributes().setAttribute(SessionKeys.SYSTEM_USER_USER_GROUP, serverType.getName());
            request.getSession().attributes().setAttribute(SessionKeys.SYSTEM_USER_PASSWORD, truePWD);
            return info;
        }
        return LoginCertificate.createUnLogin();
    }

}
