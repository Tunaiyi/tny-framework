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
package com.tny.game.demo.core.server.controller;

import com.tny.game.common.concurrent.*;
import com.tny.game.demo.core.common.*;
import com.tny.game.demo.core.common.dto.*;
import com.tny.game.net.annotation.*;
import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.configuration.command.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;

import java.util.concurrent.ThreadLocalRandom;

import static com.tny.game.net.application.ContactType.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 16:46
 */
@RpcController
@AuthenticationRequired({DEFAULT_USER_TYPE, "game-client"})
@BeforePlugin(SpringBootParamFilterPlugin.class)
public class ServerSpeakController {

    @RpcRequest(CtrlerIds.SPEAK$SAY)
    public SayContentDTO say(Session session, @RpcParam String message) {
        session.send(MessageContents
                .push(Protocols.protocol(CtrlerIds.SPEAK$PUSH), "因为 [" + message + "] 推条信息给你! " + ThreadLocalRandom.current().nextInt(3000)));
        return new SayContentDTO(session.getId(), "respond " + message);
    }

    @RpcRequest(CtrlerIds.SPEAK$SAY_FOR_RPC)
    public SayContentDTO say(@IdentifyToken RpcAccessIdentify id, @RpcParam String message) {
        return new SayContentDTO(id.getContactId(), "respond " + message);
    }

    @RpcRequest(CtrlerIds.SPEAK$SAY_FOR_CONTENT)
    public SayContentDTO sayForContent(@IdentifyToken RpcAccessIdentify id, @RpcParam SayContentDTO content) {
        return new SayContentDTO(id.getContactId(), "respond " + content.getMessage());
    }

    @RpcRequest(CtrlerIds.SPEAK$TEST)
    public SayContentDTO test(Session session,
            @RpcParam byte byteValue,
            @RpcParam short shortValue,
            @RpcParam int intValue,
            @RpcParam long longValue,
            @RpcParam float floatValue,
            @RpcParam double doubleValue,
            @RpcParam boolean booleanValue,
            @RpcParam String message) {
        String content = "\nbyteValue:" + byteValue +
                         "\nshortValue:" + shortValue +
                         "\nintValue:" + intValue +
                         "\nlongValue:" + longValue +
                         "\nfloatValue:" + floatValue +
                         "\ndoubleValue:" + doubleValue +
                         "\nbooleanValue:" + booleanValue +
                         "\nmessage:" + message;
        session.send(MessageContents
                .push(Protocols.protocol(CtrlerIds.SPEAK$PUSH), content));
        return new SayContentDTO(session.getId(), "test result: " + content);
    }

    @RpcRequest(CtrlerIds.SPEAK$DELAY_SAY)
    public Wait<SayContentDTO> delaySay(Session session, @RpcParam String message, @RpcParam long delay) {
        long timeout = System.currentTimeMillis() + delay;
        return new Wait<SayContentDTO>() {

            @Override
            public boolean isDone() {
                return System.currentTimeMillis() - timeout > 0;
            }

            @Override
            public boolean isFailed() {
                return false;
            }

            @Override
            public boolean isSuccess() {
                return isDone();
            }

            @Override
            public Throwable getCause() {
                return null;
            }

            @Override
            public SayContentDTO getResult() {
                System.out.println(message);
                return new SayContentDTO(session.getId(), "delay message : " + message);
            }
        };
    }

}
