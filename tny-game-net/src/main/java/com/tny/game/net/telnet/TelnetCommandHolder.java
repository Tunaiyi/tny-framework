package com.tny.game.net.telnet;

import java.util.List;

public interface TelnetCommandHolder {

    List<TelnetCommand> getCommandByType(CommandType commandType);

    TelnetCommand getCommand(String name);

    String execute(String[] split);

}
