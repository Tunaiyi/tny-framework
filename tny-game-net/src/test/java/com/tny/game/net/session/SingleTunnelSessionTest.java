package com.tny.game.net.session;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.common.utils.ExeAide;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.session.SingleTunnelSessionTest.TestSingleTunnelSession;
import com.tny.game.net.tunnel.*;
import org.junit.*;
import test.*;

import java.util.concurrent.atomic.LongAdder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static test.MockAide.*;


/**
 * Created by Kun Yang on 2018/8/12.
 */
public class SingleTunnelSessionTest extends NetSessionTest<TestSingleTunnelSession> {

    @Override
    protected TestSingleTunnelSession createUnloginSession(NetTunnel<Long> tunnel) {
        SessionInputEventHandler<Long, NetSession<Long>> inputEventHandler = mockAs(SessionInputEventHandler.class);
        SessionOutputEventHandler<Long, NetSession<Long>> outputEventHandler = mockAs(SessionOutputEventHandler.class);
        TestSingleTunnelSession session = new TestSingleTunnelSession(tunnel, unloginUid, inputEventHandler, outputEventHandler, 30);
        when(tunnel.getSession()).thenReturn(session);
        when(tunnel.getMessageBuilderFactory()).thenReturn(messageBuilderFactory);
        when(tunnel.close()).thenReturn(CommonStageableFuture.createFuture());
        verify(tunnel, times(1)).bind(eq(session));
        return session;
    }

    @Test
    public void parallelOfflineIfCurrent() {
        TestSingleTunnelSession session = createLoginSession();
        int taskNum = 100;
        TestAide.runParallel("offlineIfCurrentParallel", taskNum, () -> {
            while (true) {
                if (session.offlineIfCurrent(session.getCurrentTunnel()))
                    break;
            }
            session.mergeSession(createLoginSession(certificateId, uid));
        });
        assertEquals(taskNum, session.getOfflineTimes());
        assertEquals(taskNum, session.getReloginTimes());
    }

    @Test
    public void closeParallel() {
        TestSingleTunnelSession session = createLoginSession();
        TestAide.parallelTask(
                TestTask.runnableTask("closeParallelChangeStatue", 30, () -> {
                    session.offlineIfCurrent(session.getCurrentTunnel());
                    try {
                        session.mergeSession(createLoginSession(certificateId, uid));
                    } catch (ValidatorFailException ignored) {
                    }
                }),
                TestTask.runnableTask("closeParallel", 3, () -> {
                    Thread.sleep(3);
                    session.close();
                }));
        assertEquals(1, session.getCloseTimes());
    }

    protected static class TestSingleTunnelSession extends SingleTunnelSession<Long> {

        private LongAdder reloginTimes = new LongAdder();
        private LongAdder offlineTimes = new LongAdder();
        private LongAdder closeTimes = new LongAdder();


        public TestSingleTunnelSession(NetTunnel<Long> tunnel, Long unloginLong, SessionInputEventHandler<Long, NetSession<Long>> inputEventHandler, SessionOutputEventHandler<Long, NetSession<Long>> outputEventHandler, int cacheMessageSize) {
            super(tunnel, unloginLong, inputEventHandler, outputEventHandler, cacheMessageSize);
        }

        @Override
        NetTunnel<Long> doMergeSession(NetCertificate<Long> newCertificate, NetSession<Long> newSession) {
            reloginTimes.increment();
            return super.doMergeSession(newCertificate, newSession);
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

        public int getReloginTimes() {
            return reloginTimes.intValue();
        }

        public int getOfflineTimes() {
            return offlineTimes.intValue();
        }

        public int getCloseTimes() {
            return closeTimes.intValue();
        }
    }

}