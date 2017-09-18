package com.tny.game.suite.login;

import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER_AUTH, SERVER, GAME})
public class ServerTicketMaker implements TicketMaker<ServerTicket> {

    @Override
    public String make(ServerTicket ticket) {
        String serverType = ticket.getServerType();
        String key = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
        return LoginAide.ticket2MD5(ticket, key);
    }

}
