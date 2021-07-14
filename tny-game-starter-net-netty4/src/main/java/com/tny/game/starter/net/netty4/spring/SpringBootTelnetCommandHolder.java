package com.tny.game.starter.net.netty4.spring;

import com.tny.game.net.telnet.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.*;

public class SpringBootTelnetCommandHolder extends BaseTelnetCommandHolder implements TelnetCommandHolder, ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, TelnetCommand> map = applicationContext.getBeansOfType(TelnetCommand.class);
        for (TelnetCommand command : map.values()) {
            CommandType commandType = command.getCommandType();
            List<TelnetCommand> commandList = this.commandTypeMap.computeIfAbsent(commandType, k -> new ArrayList<>());
            commandList.add(command);
            this.commandMap.put(command.command().toLowerCase(), command);
        }
    }

}
