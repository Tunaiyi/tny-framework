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
