package com.tny.game.suite.login;

import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER_AUTH, GAME})
public class ServeTicketMaker implements TicketMaker<ServeTicket> {

    @Override
    public String make(ServeTicket ticket) {
        String serverType = ticket.getServerType();
        String truePWD = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
        return LoginAide.ticket2MD5(ticket, truePWD);
    }

}
