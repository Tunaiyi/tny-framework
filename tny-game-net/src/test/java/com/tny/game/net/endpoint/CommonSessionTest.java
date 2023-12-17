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
package com.tny.game.net.endpoint;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public class CommonSessionTest extends NetEndpointTest<CommonSession> {

    @Override
    protected CommonSession newEndpoint(CommonSessionSetting setting) {
        return new CommonSession(setting, Certificates.anonymous(), new EndpointContext() {

            //			@Override
            //			public <T> CertificateFactory<T> getCertificateFactory() {
            //				return new DefaultCertificateFactory<>();
            //			}

            @Override
            public NetAccessMode getAccessMode() {
                return NetAccessMode.SERVER;
            }

            @Override
            public MessageDispatcher getMessageDispatcher() {
                return null;
            }

            @Override
            public CommandExecutorFactory getCommandExecutorFactory() {
                return new DefaultCommandExecutorFactory(new SerialCommandExecutorSetting());
            }
        });
    }

    @Override
    public void receive() {

    }

    @Override
    public void send() {

    }

    @Override
    public void resend() {

    }

    // @Test
    // public void closeParallel() {
    //     NetTunnel loginTunnel = mockTunnel(createLoginCert());
    //     CommonSession session = create(loginTunnel);
    //     TestAide.parallelTask(
    //             TestTask.runnableTask("closeParallelChangeStatue", 30, () -> {
    //                 session.offline();
    //                 try {
    //                     session.online(mockTunnel(createLoginCert(certificateId, uid)));
    //                 } catch (AuthFailedException ignored) {
    //                 }
    //             }),
    //             TestTask.runnableTask("closeParallel", 3, () -> {
    //                 Thread.sleep(1);
    //                 session.close();
    //             }));
    //     assertEquals(1, session.getCloseTimes());
    // }
    //
    // protected static class CommonSession extends CommonSession {
    //
    //     private LongAdder acceptTime = new LongAdder();
    //     private LongAdder offlineTimes = new LongAdder();
    //     private LongAdder closeTimes = new LongAdder();
    //
    //     public CommonSession(MockEndpointEventHandler<? extends NetEndpoint> eventHandler, int cacheSentMessageSize) {
    //         super(eventHandler, eventHandler, cacheSentMessageSize);
    //     }
    //
    //
    //     @Override
    //     protected boolean acceptTunnel(NetTunnel newTunnel) throws AuthFailedException {
    //         acceptTime.increment();
    //         return super.acceptTunnel(newTunnel);
    //     }
    //
    //
    //     @Override
    //     protected void setOffline() {
    //         offlineTimes.increment();
    //         super.setOffline();
    //     }
    //
    //     @Override
    //     protected void setClose() {
    //         closeTimes.increment();
    //         super.setClose();
    //     }
    //
    //     public int getAcceptTime() {
    //         return acceptTime.intValue();
    //     }
    //
    //     public int getOfflineTimes() {
    //         return offlineTimes.intValue();
    //     }
    //
    //     public int getCloseTimes() {
    //         return closeTimes.intValue();
    //     }
    //
    //     public void resetAcceptTime() {
    //         this.acceptTime.reset();
    //     }
    // }

}