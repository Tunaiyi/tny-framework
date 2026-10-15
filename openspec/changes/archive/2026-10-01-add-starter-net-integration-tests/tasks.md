# Tasks

## 1. 模块接线与依赖收口（design D1/D3 前提）

- [x] 1.1 `tny-game-integration-test/build.gradle` 新增 `integrationTestImplementation project(':tny-game-net-demo')` 与 `project(':tny-game-starter-net-netty4')`；沿用既有 `configurations.all` 排除块防日志绑定回潮。验证：`./gradlew :tny-game-integration-test:compileIntegrationTestJava` 通过；`dependencies --configuration integrationTestRuntimeClasspath | grep -E "logback|slf4j-simple|slf4j-impl[^2]"` 无输出且 `log4j-slf4j2-impl` 恰一处
- [x] 1.2 吸收删除占位变更：`rm -rf openspec/changes/add-relay-scenario-it`（其 R6 全文与侦察事实已并入本变更 proposal/design/specs）。验证：`openspec list` 不再出现该变更且 `add-starter-net-integration-tests` 状态正常

## 2. 装配支撑与单应用骨架（design D2/D6，specs 需求一先红）

<!-- 组 2 实施修订注记：容器档 + 命令行注入（原 defaultProperties/无容器表述已被证伪替换），详见 design D2/D3"实施修订"段 -->
- [x] 2.1 先写失败骨架 `com.tny.game.it.assembly.SpringAssemblyGameServerIT`（`@Tag("integration")`，无 docker）：断言"以 `GameServerApp` 为源、defaultProperties 覆盖（数据源 enable=false、bind-address=探测端口）启动上下文"后 `ServerGuide.isBound()` 为真且 `serverInbound` 式消息可往返。此时支撑类未建，编译或断言应红。验证：`./gradlew :tny-game-integration-test:integrationTest --tests "*SpringAssembly*"` 红
- [x] 2.2 实现 `com.tny.game.it.assembly.DemoApps`：空闲端口探测（ServerSocket(0)+重试）、demo 应用 `SpringApplication` 程序化启动（WebApplicationType.NONE、属性覆盖表构建器）、`ConfigurableApplicationContext` 登记表与 `AutoCloseable` 逆序释放；日志绑定唯一性前置断言。验证：2.1 转绿
- [x] 2.3 补单应用全路径：登录（demo `ServerLoginController` 语义，合法凭证）→ 认证态业务调用（`PlayerController` 一条，`CtrlerIds` 常量）回执正确 → **会话保持已认证**（二次调用免重登）→ 未认证拒绝路径（新连接登录前调用需认证协议 → 错误回执 → 同连接登录后重发成功）。对应 specs 需求一两 Scenario。验证：类内全用例绿，`./gradlew :tny-game-integration-test:integrationTest`（无容器）全量不回归
- [x] 2.4 组验证：单应用装配 IT 连续 3 次 `--rerun-tasks` 无 flaky

## 3. 多应用中继剧本（design D5，specs 需求二）

- [x] 3.1 （实施形态经批准改子进程模型）先写失败骨架 `RelayLoginScenarioIT`（`@Tag("integration") @Tag("docker")` + `@Testcontainers(disabledWithoutDocker)`）：game（`RelayGameServerApp`）+ gateway（`RelayGatewayServerApp`）双上下文起服、中继互联可观测（gateway 侧 relay 注册表含 game 节点）——断言至互联层。验证：编译通过且断言红（DemoApps 尚无 relay 属性面）
- [x] 3.2 （实施形态经批准改子进程模型 + 测试 yml 配置化）实现剧本属性面：`DemoApps`/`DemoAppProcess` relay 端口注入（game `bootstrap.relay.server.bind-address`、gateway `relay.clusters` 静态节点 url 指向 game 实际端口、`router.fixed-message-router`），Redis/Mongo 容器地址按上一变更既定键注入。验证：3.1 互联层转绿
- [x] 3.3 剧本全路径：客户端连 gateway → 登录 → 经中继业务调用（`PlayerController`）→ 回执正确返回 → 持久化结果业务侧可查（Mongo 容器内查询断言）→ 关闭后双上下文与中继资源有序释放。验证：docker 档 `./gradlew :tny-game-integration-test:integrationTest -PincludeDocker` 剧本类绿
- [x] 3.4 `MissingAssemblyIT`（specs 需求二 S2）：缺 `relay.clusters` 关键项启动 gateway，断言装配期抛出可定位错误（异常消息含缺失环节标识）且**非**超时形态（用固定短超时窗口反证）。验证：两用例绿
- [x] 3.5 组验证：docker 档全量（含上变更数据 IT 无回归）+ 无容器档剧本类正确 skip；剧本类连续 3 次无 flaky

## 4. 门禁接入与收尾

- [x] 4.1 复核三档归属：单应用装配 IT 归 PR 档（无容器）、剧本/缺项 IT 归 main+nightly 容器档；如 `.github/workflows/build.yml` 描述需补"starter 装配覆盖"一句则更新。验证：两档本地命令按 README 文照抄可复现（`integrationTest` / `integrationTest -PincludeDocker`）
- [x] 4.2 README 两级通道小节补一行：starter 装配 IT 的所属档与运行命令；`tny-game-integration-test` 模块职责描述更新（网络链路 + 装配验证）。验证：文档命令实跑通过
- [x] 4.3 最终验证：`./gradlew clean test` + `./gradlew integrationTest`（全模块）+ `-PincludeDocker` 三命令相对基线零新增失败（口径沿 `add-integration-testing` verification.md）；执行 `/opsx:verify` 确认两条新需求全 Scenario 有对应 IT 且无 CRITICAL
