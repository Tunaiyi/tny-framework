# Proposal

## Why

现有测试全部停留在进程内 mock 层（EmbeddedChannel + MockNetSession），从编解码→通道→会话→中继→RPC 的核心链路没有任何端到端验证；依赖外部中间件的数据访问模块（redisson/mongodb）测试为零；构建脚本中硬写的旧版测试库（Mockito 3.11.2、JUnit 5.8.2）与 JDK 21 toolchain 不兼容，随集成测试铺开会在执行期爆发。近期多个 fix(net) 变更（guide 生命周期、隧道并发、断线重发）只能靠注释声明"移交 demo 手动验收"，行为无法钉入 CI。需要建立一条快慢分层、可复现的集成测试通道，把这些回归风险收进自动化门禁。

## What Changes

- 测试框架依赖版本治理：删除 `gradle/dependency.gradle` 中硬写的 Mockito/JUnit 显式版本，交由 Spring Boot 3.2.1 BOM 统一管理（Mockito 5.x / JUnit 5.10），并引入流式断言、异步等待、容器化测试支撑库的版本约束
- 建立独立的集成测试构建通道：每个 Java 模块获得可单独执行的集成测试入口，单元与集成用例按标签隔离，依赖容器的用例在无容器环境自动跳过而非失败；默认 `test` 与 `check` 行为不变（不拉起容器、不显著拖慢）
- 网络核心链路真实 TCP 端到端集成测试：服务端与客户端经由真实本机 socket 完成连接、认证、收发、断线重发、中继转发、引导器生命周期的验证，覆盖 TCP 报文体全部编解码实现（jprotobuf/protoex，实施期纠偏：jackson 属 ObjectCodec 层非报文体编解码）；复用既有手工装配先例，绕开"完整装配链成本高"的历史卡点
- 数据访问模块容器化集成测试：Redis/Mongo 以按用例自动起停的真实服务实例执行可复现验证，连接信息注入框架自身的数据源配置体系
- （原计划的多应用全栈场景测试经用户批准移出本变更，转后续变更 add-relay-scenario-it）
- 新增集成测试专用叶子模块 `tny-game-integration-test`（仅测试代码，不发布 API）
- CI 门禁分档：单元测试与无容器集成测试每次执行，容器用例在具备容器条件的环境执行

不修改任何生产代码公共接口、报文协议与已发布 API 行为（无 BREAKING）；若集成测试暴露存量缺陷，属后续独立变更范围。

## Capabilities

### New Capabilities
- `integration-testing`: 构建与测试基础设施对外承诺的行为契约——单元/集成两级验证通道的隔离性、可跳过性、执行入口，以及核心网络链路、数据访问两类集成验证必须可达成的可观察结果

### Modified Capabilities

无。既有 8 个 capability（net-tunnel、net-session、session-resend、relay-link、net-guide-lifecycle、net-protocol、command-execution、message-checking）的需求不变；本变更为其提供真实传输层的验证通道，不改动其规格条款。

## Impact

- **构建脚本**：`gradle/dependency.gradle`（版本治理）、根 `build.gradle`（test 排除标签、apply 新约定脚本）、新增 `gradle/integration-test.gradle`、`settings.gradle`（include 新模块）
- **受影响模块（仅测试侧新增，不改主代码）**：`tny-game-net`、`tny-game-net-netty4`、`tny-game-net-test`（harness 复用层）、`tny-game-net-netty4-codec-jprotobuf`/`tny-game-net-netty4-codec-protoex`（TCP 报文体编解码矩阵经集成测试覆盖；jackson 属 ObjectCodec 层不入 TCP 矩阵——实施期纠偏）、`tny-game-data-redisson`、`tny-game-data-mongodb`、新模块 `tny-game-integration-test`
- **对应 starter 模块**：`tny-game-starter-redisson`、`tny-game-starter-mongodb`——其数据源装配路径由容器化 IT 首次覆盖（`tny-game-starter-net-netty4` 的 relay/全栈装配路径随组 6 移交后续变更）；本变更不修改任何装配代码，若测试暴露装配缺陷另立变更修复
- **依赖**：新增测试域依赖 assertj、awaitility、testcontainers-bom（junit-jupiter/mongodb 模块）；Mockito/JUnit 版本改由 Boot BOM 接管，存量 `*Test.java`（约 65 个文件）需回归通过
- **下游兼容性**：无公共 API 变更；新模块不进入 `tny-game-bom` 发布约束面（约束列表面向下游依赖，测试专用模块是否入 BOM 在 design 决策）
- **CI**：新建立分档流水线配置（仓库当前无任何 CI 配置）
