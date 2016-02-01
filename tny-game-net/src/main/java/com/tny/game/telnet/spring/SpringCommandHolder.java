package com.tny.game.telnet.spring;

import com.tny.game.telnet.command.CommandType;
import com.tny.game.telnet.command.TelnetCommand;
import com.tny.game.telnet.command.TelnetCommandHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

public class SpringCommandHolder implements TelnetCommandHolder, ApplicationContextAware {

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TelnetCommand> map = applicationContext.getBeansOfType(TelnetCommand.class);
        for (TelnetCommand command : map.values()) {
            CommandType commandType = command.getCommandType();
            List<TelnetCommand> commandList = commandTypeMap.get(commandType);
            if (commandList == null) {
                commandList = new ArrayList<TelnetCommand>();
                commandTypeMap.put(commandType, commandList);
            }
            commandList.add(command);
            commandMap.put(command.command().toLowerCase(), command);
        }
    }

}
