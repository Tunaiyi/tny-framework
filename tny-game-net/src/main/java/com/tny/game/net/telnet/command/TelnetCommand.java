package com.tny.game.net.telnet.command;

import com.tny.game.net.transport.Session;

public interface TelnetCommand {

    String command();

    CommandType getCommandType();

    String handlerCommand(Session session, TelnetArgument argument);

}
