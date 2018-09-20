package com.tny.game.net.telnet.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTelnetCommandHolder implements TelnetCommandHolder {

    protected Map<CommandType, List<TelnetCommand>> commandTypeMap = new HashMap<CommandType, List<TelnetCommand>>();

    protected Map<String, TelnetCommand> commandMap = new HashMap<String, TelnetCommand>();

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

    @Override
    public String execute(String[] commands) {
        TelnetCommand command = getCommand(commands[0]);
        if (command == null)
            return commands[0] + " is not exist!";
        return command.handlerCommand(null, new TelnetArgument(commands));
    }

}
