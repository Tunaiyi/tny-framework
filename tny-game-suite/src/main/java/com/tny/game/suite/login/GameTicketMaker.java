package com.tny.game.suite.login;

import com.tny.game.suite.utils.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME, ACCESS})
public class GameTicketMaker implements TicketMaker<GameTicket> {

    @Override
    public String make(GameTicket ticket) {
        String ticketKey = Configs.AUTH_CONFIG.getString(Configs.AUTH_GAMES_TICKET_KEY);
        if (ticketKey == null) {
            throw new NullPointerException("ticketKey 为空");
        }
        return LoginAide.ticket2MD5(ticket, ticketKey);
    }

}