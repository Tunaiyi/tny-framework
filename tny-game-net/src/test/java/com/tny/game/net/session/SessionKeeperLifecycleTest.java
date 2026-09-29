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
package com.tny.game.net.session;

import com.tny.game.net.application.*;
import com.tny.game.net.rpc.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 组 21（Wave-C）红灯基线：会话清理任务可终结、异常不自杀、并发装载唯一（net-session"清理任务可终结"契约）。
 */
class SessionKeeperLifecycleTest {

    private static Object getField(Object target, Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static SessionKeeperSetting scanSetting() {
        SessionKeeperSetting setting = mock(SessionKeeperSetting.class);
        when(setting.getOfflineCloseDelay()).thenReturn(100L);
        when(setting.getClearInterval()).thenReturn(60_000L);
        when(setting.getOfflineMaxSize()).thenReturn(0);
        return setting;
    }

    @Test
    @DisplayName("shutdownScan 取消周期任务（修复前永不可停）")
    void scanTaskIsCancellable() throws Exception {
        CommonSessionKeeper keeper = new CommonSessionKeeper(DefaultContactType.DEFAULT_USER, scanSetting());
        ScheduledFuture<?> future = (ScheduledFuture<?>) getField(keeper, AutoCloseableSessionKeeper.class, "scanFuture");
        assertNotNull(future, "延迟关闭启用时应登记周期任务");

        keeper.shutdownScan();

        assertTrue(future.isCancelled(), "终结必须取消周期任务（修复前句柄丢弃，本断言应红）");
        keeper.shutdownScan(); // 幂等
    }

    @Test
    @DisplayName("轮内异常不终止后续轮次（scheduleAtFixedRate 静默自杀防线）")
    void roundFailureDoesNotKillScheduler() throws Exception {
        SessionKeeperSetting setting = scanSetting();
        AtomicInteger rounds = new AtomicInteger();
        when(setting.getOfflineMaxSize()).thenAnswer(invocation -> {
            rounds.incrementAndGet();
            throw new IllegalStateException("stub round failure");
        });
        CommonSessionKeeper keeper = new CommonSessionKeeper(DefaultContactType.DEFAULT_USER, setting);

        Method round = AutoCloseableSessionKeeper.class.getDeclaredMethod("clearInvalidedSessionQuietly");
        round.setAccessible(true);
        assertDoesNotThrow(() -> round.invoke(keeper));
        assertDoesNotThrow(() -> round.invoke(keeper));

        assertTrue(rounds.get() >= 2, "两轮均须进入任务体（异常被外层吞住留痕）");
        keeper.shutdownScan();
    }

    @Test
    @DisplayName("并发首次装载仅生效一个 keeper、仅创建一个实例")
    void concurrentLoadKeeperCreatesSingleInstance() throws Exception {
        SessionKeeperSetting setting = scanSetting();
        when(setting.getOfflineCloseDelay()).thenReturn(0L); // 不注册任何周期任务，聚焦装载原子性
        when(setting.getKeeperFactory()).thenReturn("probe-factory");
        AtomicInteger creations = new AtomicInteger();
        SessionKeeperFactory<SessionKeeperSetting> factory = (contactType, config) -> {
            creations.incrementAndGet();
            return new CommonSessionKeeper(contactType, config);
        };

        CommonSessionKeeperManager manager = new CommonSessionKeeperManager(setting, Map.of());
        @SuppressWarnings("unchecked")
        Map<String, SessionKeeperFactory<SessionKeeperSetting>> factoryMap =
                (Map<String, SessionKeeperFactory<SessionKeeperSetting>>) getField(manager, CommonSessionKeeperManager.class, "sessionFactoryMap");
        factoryMap.put("probe-factory", factory);

        int threads = 8;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<SessionKeeper>> results = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            results.add(pool.submit(() -> {
                barrier.await(5, TimeUnit.SECONDS);
                return manager.loadKeeper(DefaultContactType.DEFAULT_USER, NetAccessMode.SERVER);
            }));
        }
        SessionKeeper first = results.get(0).get(10, TimeUnit.SECONDS);
        for (Future<SessionKeeper> r : results) {
            assertSame(first, r.get(10, TimeUnit.SECONDS));
        }
        pool.shutdownNow();

        assertEquals(1, creations.get(), "修复前 computeIfAbsent 外创建：并发落选方仍构造 keeper 并注册周期任务（孤儿泄漏）");
    }

}
