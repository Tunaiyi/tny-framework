# Proposal

## Why

`add-integration-testing` 建成的网络集成验证走的是**去 Spring 的手工装配 harness**（绕开全装配链），因此框架对用户承诺的"开箱即用 Starter"装配路径——应用级激活注解、声明式配置引导器注册、控制器派发/认证/会话保活、中继拓扑装配——至今仍是验证盲区；近期变更 `net-boot-integration` 落账的注册装配问题正发生在这条无人看守的链路上。同时，原计划的多应用全栈剧本（R6）在归档时按批准移入占位变更 `add-relay-scenario-it`；本变更以"参考示例应用的真实装配形态"吸收该占位，把装配链验证一次做到位。

## What Changes

- `tny-game-integration-test` 模块新增对 `tny-game-net-netty4`、`tny-game-starter-net-netty4` 与 `tny-game-net-demo`（非发布示例模块）的测试依赖：以 demo 的应用配置类、控制器、认证校验器为装配蓝本，不再在测试源码里手写装配
- 新增**单应用装配集成测试**：以框架官方应用装配形态启动游戏服务端（声明式配置 + 激活注解），真实 TCP 客户端完成登录（经认证校验器）→ 认证态业务调用 → 控制器派发与回执 → 未认证拒绝路径，全程走 starter 装配的引导器/派发器/会话管理链
- 新增**多应用主干业务剧本集成测试**（承接原 R6）：以子进程装配接入侧（含中继客户端）与业务侧（含中继服务端）——实施证实同 JVM 双官方应用受 boot 静态 launcher 结构性不可行，子进程即真生产形态（实施修订记录见 design D2），以属性注入互联地址（无注册中心），剧本"客户端登录 → 经中继业务调用 → 数据持久化（真实 Redis/Mongo 容器）→ 有序释放"；并覆盖"装配缺项以可定位错误暴露"路径
- 装配覆盖统一走容器档（实施修订：demo 应用对数据子系统为硬依赖，"单应用免中间件"不成立）；`docker` 标签档并入 CI PR 通道（runner 自带 Docker，时长代价经用户接受）
- 吸收并删除占位变更 `add-relay-scenario-it`（用户批准）：其 proposal 记录的 R6 需求全文与中继装配侦察事实并入本变更工件
- 不修改任何生产代码公共接口与装配代码（无 BREAKING）；测试暴露装配缺陷另立变更修复

## Capabilities

### New Capabilities

无。

### Modified Capabilities
- `integration-testing`: 新增两条需求——"应用装配链端到端可验证"（官方装配形态启动的服务须被真实消息端到端验证，含正常路径/未认证拒绝路径两场景）；"多应用装配验证主干业务场景"（自 `add-integration-testing` 按批准移出后在此落地的原 R6，含主干剧本一次通过/装配缺项可定位暴露两场景）。既有 5 条需求不变。

## Impact

- **模块（仅测试侧新增，不改主代码）**：`tny-game-integration-test`（新 IT 与装配支撑类）、其构建脚本新增对 `tny-game-net`、`tny-game-net-netty4`、`tny-game-net-test` 既有依赖之外对 `tny-game-starter-net-netty4`、`tny-game-net-demo` 及 demo 传递依赖（log4j2 绑定、示例 DTO）的测试域依赖；`tny-game-net-demo` 作为被测装配蓝本零修改
- **对应 starter/装配面（被测，不修改）**：`tny-game-starter-net-netty4`（应用激活注解、引导器/管道装配注册器、自动配置、中继双向装配）、`tny-game-boot`（应用生命周期与单元装配链）、`tny-game-basics`（控制器解析/认证/派发）
- **与既有 capability 的账本分工**：`net-boot-integration`（服务注册/订阅行为）、`relay-link`/`relay-cluster-view`/`net-rpc-registry`（中继与路由语义）仍由各自单测钉行为条款；本变更钉的是**装配路径本身可被端到端验证**（integration-testing capability 职责），不新增行为要求
- **CI**：装配 IT 与剧本统一归 `-PincludeDocker` 档，且该档已并入 PR 通道（沿用 `add-integration-testing` 建立的通道结构，档位归属按实施修订调整）
- **依赖**：demo 传递依赖引入集成测试 classpath 的绑定冲突风险（log4j2/slf4j 桥先例已知，装配时按既有排除约定处理）
- **下游兼容性**：无公共 API 变更；`tny-game-integration-test` 不发布，新增测试依赖不影响任何发布物 pom
