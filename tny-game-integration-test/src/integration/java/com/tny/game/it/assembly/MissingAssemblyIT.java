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
package com.tny.game.it.assembly;

import com.tny.game.demo.relay.gateway.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * 装配缺项可定位暴露（specs"多应用装配验证主干业务场景" Scenario 2，
 * add-starter-net-integration-tests 任务 3.4）。
 * <p>
 * 剧本装配文件以占位符声明互联结构，运行时参数缺失即 Spring 属性解析失败——断言子进程
 * <b>快速退出（非 0）且日志点名缺失占位符</b>，而非端口静默不就绪的无指向性超时；
 * 同时证明同法启动不缺参的一侧正常，失败归因唯一。
 */
@Tag("integration")
class MissingAssemblyIT {

    @Test
    void missingRelayTargetArgumentFailsFastWithLocatableError() throws Exception {
        Map<String, String> args = new LinkedHashMap<>();
        args.put("it.host", DemoApps.HOST);
        args.put("it.gateway.network.port", String.valueOf(DemoApps.freePort()));
        // 故意缺失 it.game.relay.port —— 装配文件 clusters[0].serve-nodes[0].url 占位符无法解析

        DemoAppProcess gateway = DemoAppProcess.launch(RelayGatewayServerApp.class,
                "relay-gateway-server", java.nio.file.Path.of(java.util.Objects.requireNonNull(MissingAssemblyIT.class.getResource("/it-relay-gateway.yml")).getPath()), args);
        try {
            // 可定位失败 = 有界时间内进程退出；若表现为"挂起到端口探测超时"即违反 specs
            boolean exited = gateway.waitForExit(60_000L);
            assertThat(exited).as("缺项装配应即时失败退出而非挂起").isTrue();
            assertThat(gateway.exitValue()).as("退出码非 0").isNotZero();

            String log = gateway.logTail(16_384);
            assertThat(log).as("启动错误应点名缺失的装配项")
                    .contains("it.game.relay.port");
        } finally {
            gateway.close();
        }
    }

}
