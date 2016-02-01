package com.tny.game.telnet.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTelnetCommandHolder implements TelnetCommandHolder {

    private Map<CommandType, List<TelnetCommand>> commandTypeMap = new HashMap<CommandType, List<TelnetCommand>>();

    private Map<String, TelnetCommand> commandMap = new HashMap<String, TelnetCommand>();

    @Override
    public List<TelnetCommand> getCommandByType(CommandType commandType) {
        List<TelnetCommand> telnetCommands = commandTypeMap.get(commandType);
        if (telnetCommands == null)
            return Collections.emptyList();
        return telnetCommands;
    }

    @Override
    public TelnetCommand getCommand(String name) {
        return commandMap.get(name);
    }

}
