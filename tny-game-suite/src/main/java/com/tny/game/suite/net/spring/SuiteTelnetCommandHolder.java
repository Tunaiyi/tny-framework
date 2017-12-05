package com.tny.game.suite.net.spring;

import com.tny.game.telnet.command.BaseTelnetCommandHolder;
import com.tny.game.telnet.command.CommandType;
import com.tny.game.telnet.command.TelnetCommand;
import com.tny.game.telnet.command.TelnetCommandHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SuiteTelnetCommandHolder extends BaseTelnetCommandHolder implements TelnetCommandHolder, ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TelnetCommand> map = applicationContext.getBeansOfType(TelnetCommand.class);
        for (TelnetCommand command : map.values()) {
            CommandType commandType = command.getCommandType();
            List<TelnetCommand> commandList = commandTypeMap.computeIfAbsent(commandType, k -> new ArrayList<TelnetCommand>());
            commandList.add(command);
            commandMap.put(command.command().toLowerCase(), command);
        }
    }

}
