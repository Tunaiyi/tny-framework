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
