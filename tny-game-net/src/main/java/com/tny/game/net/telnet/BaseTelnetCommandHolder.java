/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.telnet;

import java.util.*;

public abstract class BaseTelnetCommandHolder implements TelnetCommandHolder {

    protected Map<CommandType, List<TelnetCommand>> commandTypeMap = new HashMap<CommandType, List<TelnetCommand>>();

    protected Map<String, TelnetCommand> commandMap = new HashMap<String, TelnetCommand>();

    @Override
    public List<TelnetCommand> getCommandByType(CommandType commandType) {
        List<TelnetCommand> telnetCommands = commandTypeMap.get(commandType);
        if (telnetCommands == null) {
            return Collections.emptyList();
        }
        return telnetCommands;
    }

    @Override
    public TelnetCommand getCommand(String name) {
        return commandMap.get(name);
    }

    @Override
    public String execute(String[] commands) {
        TelnetCommand command = getCommand(commands[0]);
        if (command == null) {
            return commands[0] + " is not exist!";
        }
        return command.handlerCommand(null, new TelnetArgument(commands));
    }

}
