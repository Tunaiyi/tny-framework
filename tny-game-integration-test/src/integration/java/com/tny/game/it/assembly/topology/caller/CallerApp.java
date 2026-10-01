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
