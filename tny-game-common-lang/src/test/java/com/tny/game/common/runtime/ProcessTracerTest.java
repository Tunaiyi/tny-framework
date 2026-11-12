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