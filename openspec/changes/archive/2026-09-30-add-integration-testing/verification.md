# 验证记录

## 组 1（P0 测试依赖版本治理）— 2026-09-29

### 验收口径改判（用户批准）

原任务 1.3/1.4 验收为"全仓 `test` 全绿"。实测发现全仓存在**与本变更无关的既有红灯/环境依赖**，用户改判口径为：**相对 HEAD 基线零新增失败，且不得以禁用/豁免/回退钉版本方式绕过**。specs R2 Scenario 1 的 THEN 措辞已同步修订。

### 证据链

1. **版本接管生效**（1.1/1.2）：`./gradlew :tny-game-net:dependencies --configuration testRuntimeClasspath` 解析结果
   - `org.junit.jupiter:junit-jupiter(-api/-engine/-params) -> 5.10.1`（Boot 3.2.1 BOM）
   - `org.mockito:mockito-core / mockito-junit-jupiter -> 5.7.0`；`net.bytebuddy -> 1.14.10`（支持 Java 21 字节码）
2. **基线对照**（1.3）：`git stash push -- gradle/dependency.gradle build.gradle gradle.properties` 回到 HEAD 后执行
   `./gradlew :tny-game-net-netty4:test --tests "*FrameLengthGuardTest*" --rerun-tasks` → **11 tests, 9 failed**；
   恢复改动后全仓 `./gradlew test --continue` → 唯一失败类仍是 `FrameLengthGuardTest` 同样 9 项。**结论：升级前后失败集合相同——版本升级零新增失败，未修改/禁用任何存量测试。**
3. **红灯归属（勘误后的结论）**：`FrameLengthGuardTest` 等为**未跟踪的在途工作文件**，属另一变更 `fix-net-audit-findings`（其任务 1.1 明写"运行记录红灯"的 TDD 红基线）。**本工作区存在并发修改**：会话开始时 git 快照即含 M/?? 在途文件（`PacketAssemblerTest` 等），且 20:21 仍有新文件写入——另一会话/用户正在实时实施 fix-net-audit-findings。
   ⚠️ 本记录早期版本曾把 9 红误判为"增量态 flaky、clean 后转绿"，实际是并发会话在两次运行之间实现了帧长守卫（主源码 M）使红灯转绿；"既有提交红灯"表述亦不准确（文件未跟踪）。1.3 的核心结论（升级零新增失败）不受影响。
4. **etcd 既有环境依赖**：`tny-game-namnspace-etcd` 的 `EtcdNamespaceExplorerTest` 静态连接 `127.0.0.1:2379` 且对 futures 无超时 `.get()`——本机无 etcd 时全仓 `test` 无限挂起（HEAD 既有行为，非升级引入）。本次验证以临时容器解阻：
   `docker run -d --name it-p0-etcd -p 2379:2379 gcr.io/etcd-development/etcd:v3.5.11 etcd --listen-client-urls http://0.0.0.0:2379 --advertise-client-urls http://127.0.0.1:2379`
   **注意**：组 7 最终验证（7.3）与后续任何全仓 `test` 前需保证该容器在跑（`docker start it-p0-etcd`），收尾执行 `docker rm -f it-p0-etcd`。
5. **clean 全量回归 + pom 抽查**（1.4）：`./gradlew clean test --continue` 全仓从零重跑 +
   `generatePomFileForMavenJavaPublication`（结果见下节，完成后填写）。

### 1.4 结果（2026-09-29 补记）

- ✅ `./gradlew clean test --continue` **BUILD SUCCESSFUL，零失败**（14 个 test 任务实际执行；netty4 71 用例 0 失败，etcd 在容器就绪下通过）——注：这是 ~18:30 工作区快照（含并发会话当时已完成的守卫实现）下的结果，非稳定仓库态；20:21 后并发新写入的在途测试文件曾致 `compileTestJava` 失败（缺 checked 异常声明），与本变更无关
- ✅ pom 抽查：`tny-game-net-test` 发布 pom 中 `junit-jupiter-api 5.10.1`、`mockito-junit-jupiter 5.7.0`，与 Boot BOM 一致，无其它版本漂移

## 暂停点状态（2026-09-29 20:2x，用户决定：等并发会话完成再继续）

工作区存在活跃并发实施（fix-net-audit-findings），恢复 apply 前先确认其收束（`git status` 不再新增/变动其范围文件）。

**已完成并落盘**：组 1 全部（1.1-1.4 ✅）；2.1 约定脚本 `gradle/integration-test.gradle` 已建、根 build.gradle 已 apply 且 `integrationTest` 任务注册验证通过；2.2 的 `test { excludeTags }` 与哨兵 `tny-game-net/src/integrationTest/.../ChannelSentinelIT.java` 已写，**双向隔离验证未完成**（被对方在途文件的编译错误阻断）。

**恢复第一步**：`./gradlew :tny-game-net:test :tny-game-net:integrationTest`（JAVA_HOME=Corretto 21），确认 test 不含哨兵、integrationTest 含哨兵，然后勾选 2.1/2.2 并继续 2.3。

**遗留清理**：临时 etcd 容器 `it-p0-etcd` 保持运行（全仓 test 需要）；本变更全部完成后 `docker rm -f it-p0-etcd`。

## 组 2（P1 两级通道骨架）— 2026-09-30

- ✅ 2.1 `integrationTest` source set + 任务注册（`:tny-game-net:tasks --group verification` 列出）
- ✅ 2.2 双向隔离：`ChannelSentinelIT`（@Tag integration）在 `:test` 结果 0 命中、在 `:integrationTest` 命中执行
- ✅ 2.3 三态：默认 docker 哨兵不入执行集；`-PincludeDocker`+可用容器→执行通过；`-PincludeDocker`+不可达（`DOCKER_HOST=tcp://127.0.0.1:1`）→ `DockerChannelSentinelIT.xml` 记 `skipped="1"` 且 exit=0（对应 specs R1"skip 不算失败"）
- ✅ 2.4 `check` dry-run 任务图仅 `:testClasses/:test/:check`，不含集成；`build/test-results/{test,integrationTest}` 与 `build/reports/tests/{test,integrationTest}` 两目录独立
- 附注：docker 哨兵复用本机既有 `gcr.io/etcd-development/etcd:v3.5.11` 镜像，无额外拉取成本；`@Container` 与 `org.testcontainers.containers.Container` 简单名冲突，改显式 import 解决（符合"禁止内联全限定名"规范）

## 组 3（P2 集成模块与网络 harness）— 2026-09-30

- ✅ 3.1 `tny-game-integration-test` 模块建立、从 javaProjects/BOM/发布三重排除（BOM pom grep 计 0），构建通过
- ✅ 3.2 `NetIntegrationHarness`：去 Spring 复刻 `registerChannelMaker` 装配配方（真实 `NettyServerGuide`/`NettyClientGuide` + `DefaultDatagramChannelMaker` + 手工驱动 encoder/decoder/dispatcher 的 prepareStart），单元以 `it` 前缀专属名注册避免同 JVM 全局表冲突；`HarnessSmokeIT` 真实 bind+connect+clean close 先红后绿
- ✅ 3.3 `TcpTunnelRoundTripIT`：客户端 REQUEST → 服务端通道层 echo RESPOND → 客户端经框架真实响应关联（`RequestContent.willRespondFuture` → `session.receive`→`pollFuture`→`RespondFutureTask`）收取回执，body 字节一致
- ✅ 3.4 `TcpPeerVanishIT`：`shutdownServer()` 触发 FIN，客户端隧道 5s 内经 Awaitility 观察到 `!isActive()||isClosed()`
- ✅ 3.5 连续 5 次 `integrationTest`（--rerun-tasks）全绿，无 flaky

**实施发现（harness 关键约束，已据此定型观测机制）**：
1. `NettyClientGuide.getBootstrap()` 硬编码 `new NettyMessageHandler(context)`（`NettyClientGuide:72`），**不走** `nettyMessageHandlerFactory` 单元——故集成侧不能在客户端 pipeline 塞自定义录制 handler；客户端入站观测改走框架的响应 future（`MessageSent.respond()`）。服务端则经工厂装配，可用 `InboundHandler` 录制 `serverInbound`。
2. `MessageContents.request(...)` 默认不激活响应 future，须链式 `.willRespondFuture(timeout)`；`createMessage` 才对 `RequestContent` 登记 `putFuture`（`BaseNetSession:347`）。
3. 服务端 `InboundHandler` echo 后 `return`（不 super），避免 `BaseMessageDispatcher` 因无控制器对 REQUEST 追加"SERVER_NO_SUCH_PROTOCOL"二次回执污染客户端观测；控制器派发栈不在本 harness 范围（IT 类 javadoc 已披露）。

## 组 4（P2 网络链路集成矩阵）— 2026-09-30

- ✅ 4.1 `TcpSessionResendIT`：**登录保活接管方案**（用户选定"harness 造登录协议处理器"的落地形态）——`session.online(已认证凭证)` 触发框架契约"已认证会话断开转 offline 保活而非 close"（`BaseNetSession.onUnactivated:403`），重启服务端新连接后 `session.resend(新tunnel, pred)` 按原序原 id 经真实 socket 补发 + 补发后可用性健康检查。connector 路线经查证不成立：客户端 session 每次连接新建，connector 仅调度重连不维持客户端 session
- ✅ 4.2 `TcpGuideLifecycleIT`：close 释放（isBound 归假）→ 同实例 open 重建再受理 → 新客户端建连成功（兑现 `d989eabc` "重开合同"，替代 GuideLifecycleTest 移交 demo 手动验收的部分）
- ✅ 4.3 `TcpCodecMatrixIT`：`@EnumSource(BodyCodecKind)` 两实现（jprotobuf/protoex）参数化往返全过
- ✅ 4.4 `TcpProtocolCompatIT`：兼容锚=真实编码器现算的最低特性集帧（verify/encrypt/waste 全关即旧版形态，accessId/id/体固定→字节确定）经 raw socket 注入 → 服务端独立解析+echo；损坏帧（坏 magic+超长声明）被拒且健康连接不受影响（无跨连接污染）。帧字节经 harness `encodeClientFrame` 生成（单元名一致性由客户端配置复用保证）
- ✅ 4.5 全套 7 IT 连续 3 次 rerun 全绿无 flaky；spec Scenario ↔ IT 类映射：R3-S1↔RoundTrip/Matrix、R3-S2↔Resend、R3-S3↔GuideLifecycle、R3-S4↔PeerVanish、R4-S1↔Matrix、R4-S2↔Compat.1、R4-S3↔Compat.2（各类 javadoc 已标注追溯）

## 组 5（P3 数据访问容器化集成测试）— 2026-09-30

- ✅ 5.1 `tny-game-integration-test` 接入 testcontainers(junit/mongodb) + starter-redisson/mongodb + data-* 到 `integrationTest` 配置（普通 test/无 Docker 通道零影响）；补 `configurations.all` 排除 logback/slf4j-simple 对齐 log4j2 绑定（本模块自 javaProjects 排除故须自带根约定，否则 Spring Boot 启动期双 slf4j 绑定炸）
- ✅ 5.2 `RedissonDataAccessIT`：`@Testcontainers(disabledWithoutDocker)` + `SpringApplication.run(@Import(RedissonAutoConfiguration))`，`tny.datasource.redisson.setting.{host,port}` 注入容器映射端口，真实 `RedissonClient` map.fastPutIfAbsent/get + RLock tryLock/unlock 全通
- ✅ 5.3 `MongodbDataAccessIT`：`MongoDBContainer("mongo:4.4")`（本地已有镜像，与 legacy mongo-java-driver 3.12 协议兼容；设计原钉 6.0 为驱动兼容上界，4.4 同处下界内，CI 改常量即可升），`tny.datasource.mongodb.setting.{uri,database}` 注入，真实 `MongoClient` insert/find(eq)/delete/count 全通。database 名必填（SimpleMongoClientDatabaseFactory 断言非空）
- ✅ 5.4 `UnreachableDatasourceIT`（仅 integration 不打 docker，closed port 127.0.0.1:6 即时 refused）：Redis 不可达→上下文启动即时失败并定位目标；Mongo 不可达→首次操作抛 MongoException（serverSelectionTimeoutMS=800 保证有界不挂起），不回退
- ✅ 5.5 两态验证：带 `-PincludeDocker` 13 用例全 PASS；不带 docker 时 Redisson/Mongo 容器类 skip（11 PASS 0 FAIL 0 网络用例全过），非容器 IT 不受影响

**harness 关键发现（组 5）**：
- docker 端点在 OrbStack/Docker Desktop 下非 `/var/run/docker.sock`——`gradle/integration-test.gradle` 增加 `DOCKER_HOST` 探测注入（-PdockerHost > 环境变量 > ~/.orbstack|.docker/run/docker.sock），否则 testcontainers 误判"无 Docker"整体 skip（曾致 5.2 静默 SKIP 无输出）
- testcontainers 1.20.4 对 Docker Engine 29 / API 1.54 探测失败（docker-java 过旧）→ 升 1.21.4 解决
- accessor 对象关系层（`RedissonStorageAccessor`）依赖业务 EntityScheme 注解元数据，属实体装配域；5.2/5.3 钉至"框架数据源装配 + 真实服务交互层"（accessor 内部实际使用的 map/client 原语），此为范围披露非缩窄——specs R5 只承诺"面向真实服务实例执行、环境无关可复现"，已满足

## 范围变更（用户批准）— R6 移出本变更 — 2026-09-30

组 6 全栈剧本（specs R6 / design D6）经实测评估：需装配双/三 netty 应用（`@EnableNetApplication`+`@EnableRelayServerApplication`+`@EnableRelayClientApplication`）+ relay 拓扑 + RPC 控制器 + jprotobuf 报文 DTO + Mongo 实体持久化，本质是把 `tny-game-net-demo` 应用簇搬进测试并跑通登录→中继→控制器→落库全链路——是最重且最易 flaky 的独立工程块，与 P0-P2+组5 的价值与风险量级不同。

**用户批准**将 R6/D6/组6 移出本变更，另立 `add-relay-scenario-it` 变更承接。本变更 delta specs 删 R6、design 删 D6、tasks 删组6，proposal Impact 相应缩小；`openspec validate --strict` 通过。R6"旧版本报文兼容"属 specs R4（编解码层）仍保留在本变更（`TcpProtocolCompatIT` 已覆盖），仅"多应用全栈业务剧本"移出。

## 组 7.2（文档收尾·部分）— 2026-09-30

- ✅ README「测试：单元与集成两级通道」章节（命令按文可执行实测：`integrationTest` 任务存在、`check` dry-run 不含集成）；`GuideLifecycleTest` javadoc"移交 demo 手动验收"措辞更新为指向 `TcpGuideLifecycleIT`（纯注释，netty4 模块 test 编译通过）
- ✅ 7.1 CI 三档：确认仓库存在 `github: Tunaiyi/tny-framework` remote（GitHub Actions 可运行）→ `.github/workflows/build.yml`（unit/integration/e2e 三 job；unit 与 e2e 附 etcd 服务容器解既有 namnspace-etcd 单测环境依赖；e2e 仅 push/schedule 触发）；YAML 解析验证通过。首次真实流水线触发待 push 后观察（届时若载体策略变化可调整，不影响本地验证结论）
- ✅ 7.3 最终验证三命令全绿：`clean test --continue` 215 tasks 全执行 BUILD SUCCESSFUL（net/netty4/etcd 实跑，etcd 经服务容器）；`integrationTest --continue` 全模块 BUILD SUCCESSFUL；`-PincludeDocker` 档 10 个 IT 类 13 用例全 PASS
- 收尾：临时 etcd 容器 `it-p0-etcd` 已 `docker rm -f` 清理

## Verify 阶段修复（/opsx:verify 报告后，用户批准执行 3 WARNING + 2 SUGGESTION）— 2026-09-30

1. **W1 认证子步入场景**：`RoundTripScenario.connectAndAuthenticate`（`session.online(已认证凭证)`，复用 ResendIT 验证过的原语）成为断言体第一步——R3-S1"连接-认证-往返"现按字面全步覆盖；Matrix/Compat/RoundTrip 全部受益
2. **W2 损坏帧告警可定位断言**：新增 `com.tny.game.it.harness.LogCapture`（log4j2 root 挂 WARN appender）；`TcpProtocolCompatIT` 损坏用例 await 捕获含 `"Tunnel"` + 损坏连接远端端口的解码告警（specs R4"可观察"半句从文字承诺变机器断言）
3. **绑定修复（W2 前提，用户批准引入）**：发现依赖表 `log4j-slf4j-impl` 为 slf4j **1.x** 桥，slf4j-api 2.x 下无 provider 静默退化 NOP（此前 IT 日志实为丢弃）→ 新增 `log4j-slf4j2-impl` 坐标并用于本模块 runtime；此坑对全仓所有用 log4j2 桥的模块同样成立，已登记下方"遗留发现"
4. **W3/S5 工件对现实**：design D5 标题/正文改为"属性覆盖（setDefaultProperties）注入自有前缀"+实现注记；Open Question（CI 载体）标记已决 GitHub Actions；tasks 5.2/5.3 措辞同步；`RedissonDataAccessIT` javadoc 失效"D6"引用删除
5. **verify 自身揪出的实质缺陷**：此前"R6 已从 delta spec 删除"是误报（python replace 因文件尾无空行未命中）——verify 复核发现后真正删除（5 需求/13 场景），strict 校验通过

**修复后复验**：docker 档 13/13 PASS（含认证往返、日志捕获断言）；无容器档全绿；哨兵双向隔离复绿；`openspec validate --strict` 通过。

**遗留发现（不属本变更，建议登记）**：`log4j-slf4j-impl`(1.x) 与 slf4j-api 2.0.10 的组合在多处 runtime 存在 NOP 静默风险，可另立小变更全仓排查替换为 `log4j-slf4j2-impl`。

## 执行环境

- Gradle daemon JDK：Corretto 21.0.12.1（`JAVA_HOME` 显式指定；系统默认 java 为 JDK 25，超出 Gradle 8.5 daemon 支持上限，所有构建命令必须带 `JAVA_HOME` 前缀，见 README 构建环境注记）
- Docker daemon：可用（P3 容器用例前提）
- 分支：5.7.x，基线 HEAD `3f1b05db`
