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
package com.tny.game.it.assembly.topology.forward;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.netty4.network.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

/**
 * F 族拓扑·独立转发应用（design D7，任务 3.1）：RPC 转发中继的专职进程——
 * {@code @EnableNetApplication} 承载 rpc.server（forwardable 双向，见 it-f1-forward.yml）与
 * rpc.client 静态拨出。
 * <p>
 * 与 {@link com.tny.game.it.assembly.topology.node.RelayNodeApp}（纯中继双端、无 net bootstrap）分开成两个主类：
 * {@code @EnableNetApplication} 与 {@code @EnableRelayClientApplication} 共激活会重复导入
 * SpringBootNetBootstrapProperties（BeanDefinitionOverrideException，实测归因），
 * 该注解组合限制如实登记于 verification.md（装配面发现，非行为缺陷）。
 */
@SpringBootApplication(scanBasePackages = {"com.tny.game.it.assembly.topology.forward", "com.tny.game.demo.core.common"})
@EnableNetApplication
public class ForwardApp {

    public static void main(String[] args) {
        ApplicationLauncherContext.register(ForwardApp.class);
        SpringApplication.run(ForwardApp.class, args);
    }

}
