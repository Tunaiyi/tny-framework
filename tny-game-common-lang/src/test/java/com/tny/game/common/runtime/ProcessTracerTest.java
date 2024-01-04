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