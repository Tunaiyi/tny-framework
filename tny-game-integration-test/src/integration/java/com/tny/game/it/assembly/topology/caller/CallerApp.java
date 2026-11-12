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
package com.tny.game.it.assembly.topology.caller;

import com.tny.game.boot.launcher.*;
import com.tny.game.net.netty4.network.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

/**
 * F 族拓扑·调用方应用（design D7，任务 3.1）：玩家接入 + 触发控制器，经
 * {@code @RpcRemoteService(forwardService)} 代理把请求投递到转发应用。
 * <p>
 * 身份防同型歧义（D7 硬约束）：本应用以 game-client 身份入转发节点集，
 * 转发目标 game-service 类型集中只有终端会话，FirstRpcForwarderStrategy
 * 不可能把请求转回调用方自身。扫描面含 demo.core.common 为服务枚举注册。
 */
@SpringBootApplication(scanBasePackages = {"com.tny.game.it.assembly.topology.caller", "com.tny.game.demo.core.common"})
@EnableNetApplication
public class CallerApp {

    public static void main(String[] args) {
        ApplicationLauncherContext.register(CallerApp.class);
        SpringApplication.run(CallerApp.class, args);
    }

}
