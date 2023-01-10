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
package com.tny.game.net.command.dispatcher;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/14 19:07
 **/
public class NoopRpcCommand extends NoopCommand implements RpcCommand {

    private static final NoopRpcCommand command = new NoopRpcCommand();

    public static NoopRpcCommand command() {
        return command;
    }

    private NoopRpcCommand() {
    }

}
