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
package com.tny.game.net.session;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2018/8/12.
 */
public class CommonSessionTest extends NetSessionTest<CommonSession> {

    @Override
    protected CommonSession newSession(CommonSessionSetting setting, NetTunnel tunnel) {
        return new CommonSession(Certificates.anonymous(), new SessionContext() {

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
        }, tunnel, setting.getSendMessageCachedSize());
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
    //     public CommonSession(MockSessionEventHandler<? extends NetSession> eventHandler, int cacheSentMessageSize) {
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