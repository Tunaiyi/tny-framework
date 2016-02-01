package com.tny.game.telnet.command;

import java.util.List;

public interface TelnetCommandHolder {

    public List<TelnetCommand> getCommandByType(CommandType commandType);

    public TelnetCommand getCommand(String name);

}
