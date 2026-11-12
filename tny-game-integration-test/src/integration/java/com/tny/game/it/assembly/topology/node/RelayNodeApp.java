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
package com.tny.game.it.assembly.topology.node;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.netty4.relay.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

/**
 * 独立中继节点应用（add-relay-topology-integration-tests design D2）。
 * <p>
 * 专职中继拓扑形态：仅激活中继双端注解——{@code @EnableRelayServerApplication} 接受上游
 * （接入应用或上一级中继）的链路拨入，{@code @EnableRelayClientApplication} 向下游
 * （下一级中继或业务应用的中继端点）拨出；不承载玩家接入监听、不承载业务控制器、
 * 不连业务存储（拓扑 yml 无 datasource 段）。级联（T3）与非独立中继（T4，中继端点
 * 与业务共进程）的唯一差异即本应用形态。
 * <p>
 * 装配面与用户自建独立中继应用等价：只用框架公开的激活注解组合，不触碰内部组件
 * （design D2 否决"demo 加 relay-only 类"与"game 兼职中继"两案）。
 * 拓扑/端口/上下游地址全部声明于 {@code resources/it-topo/} 下 yml，经 {@code file:}
 * additional-location 挂载（design D1）。
 */
// 扫描面含 demo.core.common 是为装载 TestRpcServiceType/TestAppType 服务枚举
// （service 键查表在 boot 静态注册面，中继不载业务控制器/校验器）
@SpringBootApplication(scanBasePackages = {"com.tny.game.it.assembly.topology.node", "com.tny.game.demo.core.common"})
@EnableRelayServerApplication
@EnableRelayClientApplication
public class RelayNodeApp {

    public static void main(String[] args) {
        // 与 demo 入口同形：launcher 静态注册驱动 basePackages 扫描（服务枚举/unit 表在此装载）
        ApplicationLauncherContext.register(RelayNodeApp.class);
        SpringApplication.run(RelayNodeApp.class, args);
    }

}
