package com.tny.game.net.telnet;

import com.tny.game.net.session.Session;

public interface TelnetCommand {

    String command();

    CommandType getCommandType();

    String handlerCommand(Session session, TelnetArgument argument);

}
