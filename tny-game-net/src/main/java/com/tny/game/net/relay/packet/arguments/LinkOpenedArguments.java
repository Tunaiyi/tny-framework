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
package com.tny.game.net.relay.packet.arguments;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 3:26 下午
 */
public class LinkOpenedArguments implements LinkPacketArguments {

    public static LinkOpenedArguments success() {
        return new LinkOpenedArguments(true);
    }

    public static LinkOpenedArguments failure() {
        return new LinkOpenedArguments(false);
    }

    public static LinkOpenedArguments of(boolean result) {
        return new LinkOpenedArguments(result);
    }

    private final boolean result;

    private LinkOpenedArguments(boolean success) {
        this.result = success;
    }

    public boolean getResult() {
        return result;
    }

    public boolean isResult() {
        return result;
    }

    public boolean isFailure() {
        return !result;
    }

    public boolean isSuccess() {
        return result;
    }

}
