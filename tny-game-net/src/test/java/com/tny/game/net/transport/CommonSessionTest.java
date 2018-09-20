package com.tny.game.net.transport;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.utils.ExeAide;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.CommonSessionTest.TestCommonSession;
import org.junit.*;
import test.*;

import java.util.concurrent.atomic.LongAdder;

import static org.junit.Assert.assertEquals;


/**
 * Created by Kun Yang on 2018/8/12.
 */
public class CommonSessionTest extends NetSessionTest<TestCommonSession> {

    @Override
    protected TestCommonSession newSession(Certificate<Long> certificate) {
        return new TestCommonSession(certificate, 30);
    }

    @Test
    @Override
    public void acceptTunnel() {

        // 正常登录
        NetTunnel<Long> loginTunnel;

        loginTunnel = mockTunnel(createLoginCert());
        NetSession<Long> sessionAcceptOk = newSession(createLoginCert());
        assertAcceptTunnelOk(sessionAcceptOk, loginTunnel);
        // 重复登录 相同 tunnel
        assertAcceptTunnelOk(sessionAcceptOk, loginTunnel);
        // 重复登录 相同授权 tunnel
        loginTunnel = mockTunnel(createLoginCert());
        assertAcceptTunnelOk(sessionAcceptOk, loginTunnel);
        // 重复登录 其他 tunnel
        NetTunnel<Long> otherTunnel = mockTunnel(createLoginCert(certificateId + 1, uid));
        assertAcceptTunnelException(sessionAcceptOk, otherTunnel);

        // 用未登录授权登录
        NetTunnel<Long> unloginTunnel = mockTunnel(createUnLoginCert());
        NetSession<Long> sessionAcceptUnloginTunnel = newSession(createLoginCert());
        assertAcceptTunnelException(sessionAcceptUnloginTunnel, unloginTunnel);

        // session 离线 重登 session
        NetSession<Long> sessionOffline = createSession();
        sessionOffline.offline();
        loginTunnel = mockTunnel(createLoginCert());
        assertAcceptTunnelOk(sessionOffline, loginTunnel);

        // session 关闭登录
        NetSession<Long> sessionClose = createSession();
        sessionClose.close();
        loginTunnel = mockTunnel(createLoginCert());
        assertAcceptTunnelException(sessionClose, loginTunnel);

    }

    @Test
    public void parallelOfflineIfCurrent() {
        NetTunnel<Long> loginTunnel = mockTunnel(createLoginCert());
        TestCommonSession session = createSession(loginTunnel);
        int taskNum = 100;
        session.resetAcceptTime();
        TestAide.runParallel("offlineIfCurrentParallel", taskNum, () -> {
            while (true) {
                if (session.offlineIf(session.getTunnel()))
                    break;
            }
            session.acceptTunnel(mockTunnel(createLoginCert(certificateId, uid)));
        });
        assertEquals(taskNum, session.getOfflineTimes());
        assertEquals(taskNum, session.getAcceptTime());
    }

    @Test
    public void closeParallel() {
        NetTunnel<Long> loginTunnel = mockTunnel(createLoginCert());
        TestCommonSession session = createSession(loginTunnel);
        TestAide.parallelTask(
                TestTask.runnableTask("closeParallelChangeStatue", 30, () -> {
                    session.offlineIf(session.getTunnel());
                    try {
                        session.acceptTunnel(mockTunnel(createLoginCert(certificateId, uid)));
                    } catch (ValidatorFailException ignored) {
                    }
                }),
                TestTask.runnableTask("closeParallel", 3, () -> {
                    Thread.sleep(3);
                    session.close();
                }));
        assertEquals(1, session.getCloseTimes());
    }

    protected static class TestCommonSession extends CommonSession<Long> {

        private LongAdder acceptTime = new LongAdder();
        private LongAdder offlineTimes = new LongAdder();
        private LongAdder closeTimes = new LongAdder();

        public TestCommonSession(Certificate<Long> certificate, int cacheMessageSize) {
            super(certificate, cacheMessageSize);
        }


        // public TestCommonSession(NetTunnel<Long> tunnel, Long unloginLong, MessageInputEventHandler<Long, NetTunnel<Long>> inputEventHandler, MessageOutputEventHandler<Long, NetTunnel<Long>> outputEventHandler, int cacheMessageSize) {
        //     super(tunnel, unloginLong, inputEventHandler, outputEventHandler, cacheMessageSize);
        // }

        @Override
        NetTunnel<Long> doAcceptTunnel(Certificate<Long> newCertificate, NetTunnel<Long> newTunnel) {
            acceptTime.increment();
            return super.doAcceptTunnel(newCertificate, newTunnel);
        }

        @Override
        void doOffline(Tunnel<Long> tunnel) {
            offlineTimes.increment();
            super.doOffline(tunnel);
        }

        @Override
        StageableFuture<Void> doClose(int state) {
            closeTimes.increment();
            return super.doClose(state);
        }

        @Override
        void onTryClose() {
            ExeAide.runUnchecked(() -> Thread.sleep(10));
            super.onTryClose();
        }

        public int getAcceptTime() {
            return acceptTime.intValue();
        }

        public int getOfflineTimes() {
            return offlineTimes.intValue();
        }

        public int getCloseTimes() {
            return closeTimes.intValue();
        }

        public void resetAcceptTime() {
            this.acceptTime.reset();
        }
    }

}