/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
