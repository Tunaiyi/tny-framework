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
