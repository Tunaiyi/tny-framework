# Tasks

## 1. P0 测试依赖版本治理（design D4）

- [x] 1.1 `gradle/dependency.gradle` 删除 junit 系（`vers.junit` 相关 5 条）与 `mockito_junit_jupiter` 的硬写版本，仅保留坐标交 Boot BOM 接管；同步清理 `vers.junit`/`vers.mockito` 残留引用。验证：`./gradlew :tny-game-net:dependencies --configuration testRuntimeClasspath | grep -E "junit-jupiter|mockito"` 解析结果为 5.10.x/5.7.x
- [ ] 1.2 引入测试域新依赖坐标与 BOM：`assertj-core`、`awaitility`（Boot BOM 管理）加入 libs 表；根 `build.gradle` dependencyManagement 增加 `mavenBom("org.testcontainers:testcontainers-bom:1.20.4")`，libs 表加入 testcontainers `junit-jupiter`/`mongodb` 坐标。验证：`./gradlew :tny-game-net:testDependencies` 或任一模块 `dependencies --configuration testRuntimeClasspath` 无解析错误
- [x] 1.3 存量测试兼容性回归并修复（只改测试代码，不豁免用例）：`./gradlew test` 全仓执行，逐一修复 Mockito 3→5 与 JUnit 5.8→5.10 引发的失败（如 `Matchers`→`ArgumentMatchers`）。验证：零新增失败、零禁用/豁免。`FrameLengthGuardTest` 9 红经 git stash 基线对照确认与版本升级无因果——系并发会话实施 fix-net-audit-findings 的在途红灯（后由其自行转绿）；etcd 模块既有本地环境依赖（无服务时挂起）以临时容器解阻。详见 verification.md
- [x] 1.4 组验证：`./gradlew clean test --continue` 全仓 **BUILD SUCCESSFUL、零失败**（14 个 test 任务，netty4 71 用例全绿）；pom 抽查 `tny-game-net-test`/`tny-game-tester`：junit-jupiter-api=5.10.1、mockito-junit-jupiter=5.7.0（BOM 接管生效），无其它版本漂移

## 2. P1 两级验证通道骨架（design D1，specs R1）

- [ ] 2.1 新建 `gradle/integration-test.gradle`：`integrationTest` source set（classpath 含 main+test 输出）、`integrationTest` Test 任务（`includeTags 'integration'`，`docker` 标签经 Gradle 属性 `-PincludeDocker` 并入）、`maxParallelForks=2`、失败日志 full；根 `build.gradle` 的 `configure(javaProjects)` 末尾 apply。验证：`./gradlew :tny-game-net:tasks --group verification` 列出 `integrationTest`
- [ ] 2.2 根 `build.gradle` `test` 任务改为 `useJUnitPlatform { excludeTags 'integration' }`。验证：在 `tny-game-net/src/integrationTest` 临时放入一个 `@Tag("integration")` 空跑样例类，`./gradlew :tny-game-net:test` 不执行它、`./gradlew :tny-game-net:integrationTest` 执行它（对应 specs R1 前两 Scenario），随后保留该样例作为通道哨兵用例
- [ ] 2.3 容器用例跳过语义：`gradle/integration-test.gradle` 中当 `-PincludeDocker` 未开启时 `excludeTags 'docker'`；开启时无容器环境的执行以 JUnit 前置条件整体判为 skip（哨兵样例补一个 `@Tag("docker")` 变体）。验证：本机无/有 Docker 两种条件下分别执行 `./gradlew :tny-game-net:integrationTest -PincludeDocker`，结果符合 specs R1"容器不可用时的降级语义"（skip 不算失败）
- [ ] 2.4 组验证：`./gradlew :tny-game-net:test :tny-game-net:integrationTest` 通过且两报告独立；`./gradlew check` 行为与变更前一致（不触发 integrationTest、不拉容器）

## 3. P2 集成模块与网络 harness（design D2/D3，specs R3）

- [ ] 3.1 `settings.gradle` include `tny-game-integration-test`；新建模块目录与 `build.gradle`：依赖 `tny-game-net`、`tny-game-net-netty4`、`tny-game-codec-jackson`、`tny-game-codec-jprotobuf`、`tny-game-codec-protoex`、`tny-game-net-test`（仅复用其既有 mock，不加新发布依赖）；本模块不 apply 发布脚本。验证：`./gradlew :tny-game-integration-test:build` 通过，`publishing` 任务不存在
- [ ] 3.2 harness 骨架 `com.tny.game.it.harness.NetIntegrationHarness`（具体类，`AutoCloseable` + JUnit 5 Extension）：照抄 `NettyServerTunnelTest` 最小装配先例手工组装 `NetBootstrapContext`，经 `NettyServerGuide(appContext, setting)` 绑定 `127.0.0.1:0`，暴露 `serverAddress()`、`connectClient()`、`MessageRecorder`；关闭顺序 client→server，`close()` 内断言 EventLoopGroup 终止。验证：一条哨兵 IT `HarnessSmokeIT`（标 `@Tag("integration")`，放本模块 `src/integrationTest`）——起停一次、连接建立即成功；`./gradlew :tny-game-integration-test:integrationTest` 通过。此任务为 harness 行为的测试先行件：先写 `HarnessSmokeIT`（应红）再实现（转绿）
- [ ] 3.3 连接-认证-往返端到端 `TcpTunnelRoundTripIT`（specs R3 Scenario 1）。验证：用例通过，回执内容字节级断言（AssertJ），等待经 Awaitility 显式超时
- [ ] 3.4 对端消失失败路径 `TcpPeerVanishIT`（specs R3 Scenario 4：服务端通道强关后客户端在有限时间内观察到断开事件）。验证：用例通过且无挂起（超时会使其失败，即验证有界性）
- [ ] 3.5 组验证：`./gradlew :tny-game-integration-test:integrationTest` 全绿；连续 5 次执行无假红（flaky 探测）

## 4. P2 网络链路集成矩阵（specs R3/R4）

- [ ] 4.1 断线重发 `TcpSessionResendIT`（specs R3 Scenario 2，追溯 `session-resend` 规格条款并写入类 javadoc）。验证：用例通过——未确认消息重连后按序补发且无重复副作用
- [ ] 4.2 引导器生命周期 `TcpGuideLifecycleIT`（specs R3 Scenario 3：停止释放资源、重启再受理；追溯 `net-guide-lifecycle` 条款）。验证：用例通过，`GuideLifecycleTest` 中"移交 demo 手动验收"的受理行为在集成通道获得自动化钉
- [ ] 4.3 编解码参数化矩阵 `TcpCodecMatrixIT`（specs R4 Scenario 1：JUnit 5 `@ParameterizedTest` 覆盖 jackson/jprotobuf/protoex 三实现的同一往返场景）。验证：3 组参数全通过
- [ ] 4.4 报文兼容与损坏路径 `TcpProtocolCompatIT`（specs R4 Scenario 2 旧报文头应答 + Scenario 3 帧违规拒绝/告警可观察）。验证：两用例通过；构造旧版本报文头的夹具字节序列以常量固化并注释来源版本
- [ ] 4.5 组验证：`./gradlew :tny-game-integration-test:integrationTest` 全绿；specs R3/R4 全部 Scenario 与 IT 类对照表以 javadoc 标注完成（P13 可追溯）

## 5. P3 数据访问容器化集成测试（design D5，specs R5）

- [ ] 5.1 `tny-game-integration-test` 增加 testcontainers 依赖（junit-jupiter/mongodb，经 BOM）；`settings.gradle` 确认 Redis/Mongo 用例所需 `starter-redisson`/`starter-mongodb`/`data-*` 依赖接入。验证：模块编译通过
- [ ] 5.2 `RedissonStorageAccessorIT`（`@Tag("integration") @Tag("docker")`）：Testcontainers 拉起 redis 实例，`@DynamicPropertySource` 注入 `tny.datasource.redisson.setting.*`，断言 Accessor 封装下 map/lock 真实读写；实例随用例回收。验证：`./gradlew :tny-game-integration-test:integrationTest -PincludeDocker` 通过且重复执行结果一致（specs R5 Scenario 1）
- [ ] 5.3 `MongodbStorageAccessorIT`（同标签）：容器钉 `mongo:6.0`，注入 `tny.datasource.mongodb.setting.*`，断言存储读写。验证：同上（specs R5 Scenario 1）
- [ ] 5.4 不可达地址失败路径 `UnreachableDatasourceIT`（specs R5 Scenario 2：数据源指向不可达地址时明确失败并定位配置，不回退不挂起）。验证：两用例通过
- [ ] 5.5 组验证：`./gradlew :tny-game-integration-test:integrationTest -PincludeDocker` 全绿；不带属性执行时 5.2–5.4 全部报告为 skip（specs R1 降级语义在容器用例上复验）

## 6. P3 全栈装配场景（design D6，specs R6）

- [ ] 6.1 测试蓝本配置：以 `tny-game-net-demo` 的接入/业务应用为蓝本新建测试专用装配配置类（同 JVM 双 `SpringApplication.run`，端口 0/随机化，日志隔离），置于 `tny-game-integration-test/src/integrationTest`。验证：`LoginToRelayScenarioIT` 骨架先红（应用可启动、端口可读回）再进入 6.2
- [ ] 6.2 `LoginToRelayScenarioIT`（specs R6 Scenario 1：客户端→接入应用→中继→业务应用控制器调用→数据落 Mongo/Redis→有序释放）。验证：剧本全链路通过，结束断言两上下文与容器资源全部关闭
- [ ] 6.3 装配缺陷暴露路径 `MissingAssemblyIT`（specs R6 Scenario 2：故意缺失一项装配配置启动，断言以可定位装配期错误失败而非超时）。验证：用例通过
- [ ] 6.4 组验证：`./gradlew :tny-game-integration-test:integrationTest -PincludeDocker` 全绿；`./gradlew :tny-game-integration-test:integrationTest`（无 Docker）剧本判 skip

## 7. 门禁与文档收尾（design D7）

- [ ] 7.1 建立 CI 三档流水线配置（PR：`test` + `integrationTest`；main：追加 `-PincludeDocker`；nightly：全量含剧本；运行器编排按 design Open Question 届时载体决定）。验证：三档流水线各成功触发一次，PR 档在无容器运行器上不误红
- [ ] 7.2 README「快速开始/开发」段补一节：两级验证通道的使用命令（`test`/`integrationTest`/`-PincludeDocker`）与 `IT` 命名、双标签约定；`GuideLifecycleTest` 类 javadoc 中"移交 demo 手动验收"的措辞更新为指向自动化钉（仅注释，不改代码行为）。验证：文档命令按文照抄可执行
- [ ] 7.3 最终验证：`./gradlew clean test` + `./gradlew integrationTest`（全模块）+ `./gradlew :tny-game-integration-test:integrationTest -PincludeDocker` 三命令相对基线零新增失败（既有 fix-net-audit-findings 红灯与 etcd 环境依赖除外，见 verification.md 口径）；执行 `/opsx:verify` 确认 specs 全部 Scenario 有对应 IT 且无 CRITICAL
