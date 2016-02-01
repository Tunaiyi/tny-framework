package com.tny.game.suite.login;

import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"suite.serve_auth", "suite.all"})
public class ServeTicketMaker implements TicketMaker<ServeTicket> {

    @Override
    public String make(ServeTicket ticket) {
        String serverType = ticket.getServerType();
        String truePWD = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
        return LoginUtils.ticket2MD5(ticket, truePWD);
    }

}
