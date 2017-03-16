package com.tny.game.telnet.command;

import com.tny.game.net.session.Session;

public interface TelnetCommand {

    public String command();

    public CommandType getCommandType();

    public String handlerCommand(Session session, TelnetArgument argument);

}
