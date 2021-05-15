package com.tny.game.common.runtime;

import org.junit.jupiter.api.*;

import static com.tny.game.common.runtime.TrackPrintOption.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/10 5:58 下午
 */
class ProcessTracerTest {

    private static ProcessWatcher tracer = ProcessWatcher.of("TracedMotionTest", ALL);

    @Test
    void start() throws InterruptedException {
        Thread.sleep(3000);
        for (int index = 0; index < 20; index++) {
            long time = System.currentTimeMillis();
            ProcessTracer motion = tracer.trace("TEST");
            System.out.println(System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            motion.done();
            System.out.println(System.currentTimeMillis() - time);
            System.out.println("======================================================================");
        }
    }

}