package com.tny.game.suite.login;

import com.tny.game.common.utils.json.JSONAide;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.message.Message;
import com.tny.game.net.session.LoginCertificate;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.suite.core.AppType;
import com.tny.game.suite.core.AttributesKeys;
import com.tny.game.suite.utils.Configs;
import org.springframework.beans.factory.annotation.Autowired;

public class ServeAuthProvider extends GameAuthProvider<Integer> {

    @Autowired
    private ServeTicketMaker maker;

    @Override
    public LoginCertificate<Integer> validate(Tunnel<Integer> tunnel, Message<Integer> message) throws DispatchException {
        String ticketWord = message.getBody(String.class);
        ServeTicket ticket = JSONAide.toObject(ticketWord, ServeTicket.class);
        AppType serverType = ticket.asScopeType().getAppType();
        if (this.maker.make(ticket).equals(ticket.getSecret())) {
            LoginCertificate<Integer> info = LoginCertificate.createLogin(ticket.getServerID(), ticket.getServerID(), serverType.getName(), false);
            String truePWD = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
            tunnel.attributes().setAttribute(AttributesKeys.SYSTEM_USER_ID, ticket.getServerID());
            tunnel.attributes().setAttribute(AttributesKeys.SYSTEM_USER_USER_GROUP, serverType.getName());
            tunnel.attributes().setAttribute(AttributesKeys.SYSTEM_USER_PASSWORD, truePWD);
            return info;
        }
        return LoginCertificate.createUnLogin(-1);
    }
}
