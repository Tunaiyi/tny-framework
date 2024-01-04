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

package com.tny.game.net.netty4.configuration.application;

import com.tny.game.net.telnet.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.*;

public class SpringTelnetCommandHolder extends BaseTelnetCommandHolder implements TelnetCommandHolder, ApplicationContextAware {

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
