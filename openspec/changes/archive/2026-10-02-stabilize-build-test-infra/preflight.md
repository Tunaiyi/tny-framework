# Preflight — 组 1 前置调查产出(钉因证据与形态锁定)

> 本文件是 tasks 1.1–1.4 的产出记录,组 2/3 的实施形态以本文为准(§4)。

## 1. 结论速览

| 项 | 结论 |
|---|---|
| net-test 并行编译破裂 | **不可复现**(7/7 绿,含规定口径 5 轮并行与全图冷并行)→ 依 design D1/Risks 条款**降级为次选 (b):护栏 + 在册观察**,不做包位移、不加 deprecated 转发 |
| net-test 发布面 | **属发布面**(javaProjects 含 net-test,`gradle/publications.gradle` 生效)→ 若未来走包位移须 deprecated 转发一版;本降级路径下不触发 |
| integration 通道现名 | 源集 `integration` / 任务 `integrationTest`(`rename-integration-source-set` 已于 2026-10-01 归档,取终态) |
| etcd 接入形态 | **形态 b(Testcontainers)**:仓内已有 testcontainers 先例与依赖注入,D2"有仓内用法则优先 b"成立;镜像钉 `gcr.io/etcd-development/etcd:v3.5.11`(与哨兵/CI 同基准) |

## 2. 1.1 复现实验记录

试验命令(任务规定口径):`./gradlew :tny-game-net-test:compileJava :tny-game-net:compileTestJava --max-workers=4 --console=plain`,原始日志 `/tmp/nettest-repro/`。

| 轮次 | 口径 | workers | 结果 |
|---|---|---|---|
| R0 | 双模块 clean 冷启动,串行对照 | 1 | RC=0 |
| R1 | 双模块 clean 冷启动 | 4 | RC=0 |
| R2 | 热增量(仅删两编译单元 compile 输出:net test 类输出 + net-test main 输出) | 4 | RC=0 |
| R3 | 双模块 clean 冷启动 | 4 | RC=0 |
| R4 | 混合增量(删 net main+test 与 net-test main 全部类输出) | 4 | RC=0 |
| R5 | 双模块 clean 冷启动 | 4 | RC=0 |
| R6 | 补测:全仓 `clean` 后 `build -x test`(全图冷并行,含全部消费方 compileTest/jar/javadoc,贴近历史"门禁四轮"口径) | 4 | RC=0 |

**失败签名:零复现。** "串行绿/并行找不到符号"在现树不可复现。

**根因核查佐证(支撑降级判断,非猜测):**
- 两编译单元间**不存在任何非公开成员跨单元引用**(与 proposal 侦察修正一致:历史根因"包私有引用"已随他案公开化消除,如 `TunnelStatus` 等)。
- 编译顺序**已由显式依赖声明钉死**,无缺口可补:`tny-game-net-test/build.gradle` `api project(":tny-game-net")`;`tny-game-net/build.gradle` `testImplementation project(":tny-game-net-test")`。net-test:compileJava 必然后于 net:jar,net:compileTestJava 必然后于 net-test:jar——"找不到符号"所需的图漂移通道在当前声明下不存在。
- split package(`com.tny.game.net.transport` 横跨二模块:net 侧 44 个产品类,net-test 侧 8 个工具类)仍是在册结构隐患,列为**观察项 O1**(§5)。

## 3. 1.2 发布面与依赖方核查

**发布面:** 根 `build.gradle` 的 `javaProjects = moduleProjects - bom - *-gradle - integration-test`,tny-game-net-test 命中(名以 `tny-game-` 开头)→ 被 `apply from: gradle/publications.gradle` + `maven-publish` 纳管;**且 net-test 自身 build.gradle 另有一行显式 `apply plugin: "maven-publish"`**。结论:**net-test 属发布面**。

**net-test 的模块级依赖方(全仓 build.gradle 声明,含 tools/*/starter-* 范围核查,无其他):**

| 模块 | 配置 | 用途 |
|---|---|---|
| `tny-game-net` | `testImplementation` | 单测消费 Mock 脚手架 |
| `tny-game-net-netty4` | `testImplementation` | 单测消费 Mock 脚手架 |
| `tny-bench` | `jmhImplementation` | JMH 基准消费 transport 类 |
| `tny-game-integration-test` | `implementation` | IT 消费 Mock 脚手架 |

**net-test 工具类(`com.tny.game.net.transport` 中 net-test 侧 8 类:MockNetTunnel、MockNetSession、TunnelTestInstance、TestMessages、TestMessagePack、NetTunnelTest、ConnectorTest、TunnelTest)的 import 消费文件清单:**

- `tny-game-net/src/test`(12 文件):command/dispatcher/PluginChainFailClosedTest、command/plugins/MessageSequenceCheckerPluginTest、MessageTimeoutCheckerPluginTest、MessageTimeoutNullAttributeTest、session/MockNetTunnelCloseTest、NetSessionTest、SessionResendSafetyTest、SessionTest(`extends ConnectorTest`)、SessionTestInstance、transport/TunnelSessionSnapshotTest、TunnelUnboundRejectionTest(及经通配 `com.tny.game.net.transport.*` 引用的其余测试)
- `tny-game-net-netty4/src/test`(MockNettyClient(`extends MockNetSession`)、NettyTunnelTest(`extends NetTunnelTest`)、NettyClientTunnelTest、NettyServerTunnelTest、TestGeneralClientTunnel、TestGeneralServerTunnel、HandlerResultCodeReplyTest、TransportWriteRejectionTest、codec/ 下 8 测试等,多为 `import com.tny.game.net.transport.*` 通配)
- `tny-bench/src/jmh`(MessageQueueBenchmark、PacketCodecBenchmark、PipelineCryptoMatrixBenchmark、RespondFutureBenchmark——引用产品类 transport,需按 2.1 清单逐一核对的即上述 import)
- `tny-game-integration-test/src/integration`(HarnessSmokeIT、assembly/ 4 IT、ScenarioSupport、NetIntegrationHarness、net/ 5 IT 等)

> 注:因 split package 同名,`com.tny.game.net.transport` 的 import 无法在文本层面区分产品/net-test 类;按类名(Mock*/TunnelTestInstance/TestMessage*)识别的上表即为消费面全集。

**integration 通道现名:** `gradle/integration-test.gradle` 源集 `integration`、任务 `integrationTest`;`tny-game-net/src/integration/` 已存在;`rename-integration-source-set` 变更已归档(2026-10-01),**现名即终态**,本变更按名实施。

## 4. 1.3 Testcontainers etcd 可行性快照 + 形态锁定

- **依赖树:** `gradle/integration-test.gradle` 对全部 javaProjects 注入 `integrationImplementation libs.testcontainers_junit`(别名源自 `gradle/dependency.gradle`,testcontainers-bom 版本统一管控)→ **namnspace-etcd 的 integration 源集零新增依赖即可用 Testcontainers**。
- **先例可照抄:** `DockerChannelSentinelIT` 形态完整(双 Tag + `@Testcontainers(disabledWithoutDocker=true)` + `GenericContainer` etcd);镜像 `gcr.io/etcd-development/etcd:v3.5.11` 同时是 CI unit/e2e 两 job 的 service 镜像——**钉版基准三方一致,design Open Question(镜像 tag 策略)就此落定**。
- **哨兵先例的已知差异(实施须补):** 哨兵只验容器起停、未连客户端;etcd 默认只监听容器内 127.0.0.1,经 Testcontainers 端口映射连接需显式 `--listen-client-urls http://0.0.0.0:2379`(本 IT 补该命令参数,端点取 `getHost():getMappedPort(2379)`)。
- **结论:形态 b**(有环境即真跑,非静默跳过),满足 D2"仓内已有 testcontainers 用法则优先 b"。

## 5. 形态锁定(tasks 组 2/3 按此实施)

**组 2 = D1 次选 (b) 降级路径:**
1. 不做包位移(首选 (a) 不启动);**不加 deprecated 转发**(无包位移,发布面核查结论留档备查,未来若走 (a) 须按其执行)。
2. 护栏落地:`tny-game-net-test/build.gradle` 与 `tny-game-net/build.gradle` 增加契约注释(同名分包在册;顺序由显式依赖声明钉死;禁止新增跨编译单元非公开成员引用;复现试验口径与结果指向本文件)。
3. **观察项 O1(在册):** split package 本身是残余结构隐患;若未来任何构建再现"并行找不到符号"且签名落 `com.tny.game.net.transport`,即重启包位移 `com.tny.game.net.testkit.transport`(含本 §3 消费面清单与 deprecated 转发一版)。
4. 验收口径按现树事实调整:§2 的 5 轮并行绿(R1–R5)+ 串行对照 + 全图冷并行即"≥5 轮并行连续绿且零签名复现";组 2 实施后(仅注释变更,不入编译输入)再补并行轮次与 `:tny-game-net:test :tny-game-net-test:test` 全绿记录于 verification.md。

**组 3 = D2 形态 b:**
1. `EtcdNamespaceExplorerTest` 迁移至 `tny-game-namnspace-etcd/src/integration/java/`,按通道命名约定更名 **`EtcdNamespaceExplorerIT`**;`@Tag("integration")` + `@Tag("docker")` + `@Testcontainers(disabledWithoutDocker=true)`。
2. 环境接线改造(非断言本体):`@Container static GenericContainer` etcd(v3.5.11,监听 0.0.0.0:2379),explorer 改由 `@BeforeAll` 在容器就绪后以映射端点构建;`TestShadingNode` 留原位(integration 编译类路径含 test 输出)。
3. 全部外部交互有界化:`*.get()` → `get(30, TimeUnit.SECONDS)`、`*.await()` → `await(30, TimeUnit.SECONDS)`、`shutdown().join()` → 有界 get;类级 `@Timeout`(SEPARATE_THREAD)兜底。**用例断言逻辑本体零改动**(方法、断言、期望计数全部原样)。
4. CI 不动(依 proposal"不引 CI 改动"):unit job 的 etcd service 与注释在本迁移后成为无害冗余,留待后续变更处置。
