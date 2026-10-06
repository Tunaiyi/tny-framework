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
package com.tny.game.net;

import org.junit.jupiter.api.*;

/**
 * 两级验证通道哨兵（add-integration-testing design D1，specs R1）。
 * <p>
 * 永久保留，验证通道隔离契约：本类标 {@code @Tag("integration")}，默认 {@code test} 任务不得执行、
 * {@code integrationTest} 任务必须执行。若通道接线回退，本哨兵会立即暴露。
 */
@Tag("integration")
class ChannelSentinelIT {

    @Test
    void integrationChannelIsReachable() {
        Assertions.assertTrue(true, "integrationTest 通道可达");
    }

}
