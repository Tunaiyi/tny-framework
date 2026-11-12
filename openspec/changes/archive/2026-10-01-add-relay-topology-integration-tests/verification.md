# Verification — add-relay-topology-integration-tests

## 等待窗口记录（用户批准选项1：待并行 codec 改造落定后续跑）— 2026-10-01

- 阻塞定性：并行会话 security-generations 改造（NetPacketCodecSetting 默认 verifier 切换 + SipHash24/Crc32/XorTile 新类 + CRC64 修改）处于半态——子进程单元表 checkUnit 两类失败（sipHash24 未注册→显式点名 crc64 后单元表整体空），任何运行结果不可归因。
- 恢复判据（三者齐）：①`./gradlew :tny-game-net:test :tny-game-net-netty4:test` 绿；②并行变更（fix-common-*/optimize-legacy-codec-paths/security 系）提交或工作区 codec 文件静默；③demo runtimeClasspath 所解析 net/netty4 jar 与源码同步（必要时 publishToMavenLocal）。
- 恢复后首命令：`./gradlew :tny-game-integration-test:integrationTest -PincludeDocker --tests "*RelayLoginScenarioIT*" --tests "*RelayOneHopIndependentIT*" --rerun-tasks`（T4 回归锚 + T2 双分支），绿则按 tasks 顺序推进 2.2 验证→2.3 移交→3.x F 族。
- 已就绪资产：五拓扑 yml（codec 显式点名抗漂移，两族共用）、RelayNodeApp、ScenarioSupport 双分支基座、T2 双分支 IT、specs/design/proposal 终形。

## R 族结案（2.2/2.3）— 2026-10-01

- T2 双分支验收达成：三进程装配 ✓、两段链路建立 ✓（relay 日志 `Tunnel(20) 连接接受`）、业务调用**可定位失败** ✓（relay 日志 `controller [10001] not exist` = 中间跳本地派发 miss，客户端有界收到失败码非悬挂）、全资源回收 ✓；×3 连跑稳定
- codec 代次演进适配记录：verifier 单元名实为 `cRC64CodecVerifier`（lowerCamelName 仅首字母小写），crypto=`xOrCodecCrypto`——矩阵五 yml 全部显式点名 legacy 逃生舱，抗 default-to-mac-generation 漂移
- 移交 `add-relay-transit-hop`（框架增强提案要点）：
  * 缺口：`RelayPacketServerProcessor.onTunnelRelay` 无条件本地终止；三节点中转模型（LocalAccessTunnel/RemoteRelayTunnel）仅存 javadoc
  * 路线：经 ServerRelayExplorer SPI 缝（`UnitLoader.getLoader` + RelayServerAutoConfiguration:28-30）注册支持二次转发的 explorer/隧道实现；涉及跨跳 TunnelConnected/Disconnect 语义、tunnelId 键空间、协议无改动需论证
  * 完成判据：本矩阵 T2/T3 连通分支自动激活（IT 双分支断言已内置连通路径，框架落地即绿，无需改测试）

## T3 级联补记（矩阵二审审计 CRITICAL → 用户裁决"补齐"）— 2026-10-01

- 原 tasks 2.2 子项"T3 级联 yml（relay-2 段）"勾选无实体（三路独立审计同判 CRITICAL 账实不符），经用户裁决补齐：`it-t3-{access,relay1,relay2,game2}.yml` + `RelayTwoHopCascadeIT`（design D5"级联=每级同形应用、yml 参数不同"至此才有行为证据）
- T3 首轮实证（两跑修正日志锚点分层后绿）：三段链路全部可观测——接入隧道层 `Tunnel(N) 连接接受`（relay-1 对 access）、节点间 serve 链路层 `RelayLink(SERVER)`（relay-2 接受 relay-1、game-2 接受 relay-2）；**断链固定于首跳 relay-1**（`controller [10001] not exist`）且 relay-2 派发面干净（流量未达第二跳）——D6"中间跳本地终止"缺席语义在级联下**照传**，级联装配本身无缺陷；四进程有界退出终态断言绿
- 教训固化（已回写 design D5 终形补记）：①服务名前缀约定不可实施（枚举词汇表锁死），隔离由进程组/容器/端口/mongo 库名承担；②`rpc.cluster` 条目加 serve-name 会静默翻转 discovery（`RpcServiceSetting.isDiscovery()`），D5 约定仅对 relay.clusters 成立——三份 F 族 yml 头注释已锁警示

## F 族结案（3.1–3.4）— 2026-10-01

- 环境归因先行（非行为缺陷）：`--rerun-tasks` + `org.gradle.parallel` 会让 integrationTest 与上游 jar 重建**并发**（实证：common-lang jar mtime 落在测试开跑后 37s，子进程撞窗口即 `NoClassDefFoundError: ThrowableRunnable`/Spring 半态——tasks 3.2 首轮与 3.3 一次"失败"皆此因）。处置：迭代一律任务级 `--rerun`，禁 `--rerun-tasks` 跑 docker 档。04:59 旧"失败"记录作废，F1 复跑即绿
- F1 一跳 RPC 转发链**连通主分支达成**（specs R2-S1）：`RpcForwardOneHopIT#oneHopRpcForwardReachesTerminalAndReturns` 断言端到端成功码 + 终端回执 `"respond fwd-<tag>"` 逐字原路返回（`--tests "*RpcForward*"` 独跑与合跑均绿）
- 3.4 同型歧义隔离对照对（specs R2-S3，行为级证据）：
  * 对照 A（`it-f1-caller-ambiguous.yml`，username=game-service 同型）：请求被 FirstRpcForwarderStrategy 转回 caller 自身——caller 进程日志出现 `controller [20007] not exist`，D7 陷阱实证成立，异型 username 是承重隔离手段
  * 对照 B（异型 game-client、无终端）：caller 派发面从未收到终端协议（doesNotContain 断言），失败落转发节点解析面
- 关键框架行为发现①（**回程咽错**，双分支判据修正依据）：转发链断链时错误回执被逐跳改写咽成"code=100 + 空载荷"**假成功**——连通判别 MUST 以终端载荷在场为准（F1/F2 均按此改造；F1 兜底分支同轮由恒真断言升级为真实归因断言，矩阵审计指出）
- F2 两跳级联以**可定位失败**分支结案（specs R2-S2 双结局合法形态）：断链固定于**跳二本地终止**——`RpcForwardCommand.forward()` 每跳把报文头 `to` 重写为下一跳自身接入点（`setTo(toAccess.getForwardPoint())`），跳二 `tryForward` 判 `to.serviceType==currentType && serverId 相符` → 本地派发 → `controller [20007] not exist`（日志实证），终端从未被触达（game-2 派发面干净断言）；四进程存活 + 有界回收终态断言绿
- 移交 `add-relay-transit-hop` 同族**第二份记录：两跳 RPC 转发语义**（与 relay 中间跳并列，只记录不实施）：
  * 缺口①：`to` 逐跳重写为中间节点自身身份，终端逻辑目标丢失——静态集群下每级转发只能"到达下一跳"，无法"送达终端"；多跳需保留原始 `to` 或以 hop-count/显式终端点路由
  * 缺口②：错误回程跨跳不保持（咽成 100+空载荷假成功）——`RpcOriginalMessageIdHeader`/结果码在多级改写中丢失，客户端无从区分"终端成功但空结果"与"链路中段失败"
  * 影响面：`tny-game-net` 的 `RpcForwardCommand`/`RpcForwardHeader`/`FirstRpcForwarderStrategy`；对下游兼容性=新增语义（现网休眠，无既有行为回退风险，与 F1 一跳连通行为不冲突）
  * 完成判据：本矩阵 F2 连通分支自动激活（载荷判别已内置，框架落地即绿）
- 装配面发现登记（ForwardApp javadoc 曾指向本档而欠账，现补）：`@EnableNetApplication` 与 `@EnableRelayClientApplication` 共激活会重复导入 SpringBootNetBootstrapProperties（BeanDefinitionOverrideException 实测归因）——"relay+rpc 转发同进程"形态需框架侧装配缝修复才可测，本矩阵两族分进程共置（design D7 互斥结论的另一面）
- tasks 3.1 措辞偏差记录：任务原文"转发 app=RelayNodeApp 扩展 rpc 双 bootstrap"，交付实际为独立 `ForwardApp` 主类（上一条注解冲突即原因），语义不变
- 无存储纯度（矩阵审计 MEDIUM 修复）：中继/接入/调用方/转发四类非存储进程命令行统一排除 redisson+mongo 自动配置族（`ScenarioSupport.NON_STORE_AUTOCONFIG_EXCLUDES`），修复前 relay 子进程实测 eager 创建 MongoClient 与"专职无存储"声明矛盾；修复后进程日志 MongoClient 归零实证

## 矩阵合跑终验（4.1/4.2）— 2026-10-01

- 命令（README 矩阵小节同款，实跑一致）：`./gradlew :tny-game-integration-test:integrationTest -PincludeDocker --tests "*Relay*" --tests "*RpcForward*" --rerun`
- 结果：**8/8 全绿一次通过**（MissingAssemblyIT 装配探针、T4 回归锚、T2 双分支、T3 级联、F1 主用例、F1 同型/异型对照对、F2 两跳归因），互不串扰判据达成
- 中途一次 5 红复盘：归因=并行会话同时跑 `:tny-game-basics:test --rerun-tasks` 强制重建上游 jar 链、子进程撞窗口（NoClassDefFoundError: ByteBufferAllocator / EnumerableCheckLoader 崩 / Spring finishRefresh 半态——与上文环境竞态签名同源）；等待其静默后重跑即全绿。**操作纪律固化：docker 档迭代只用任务级 `--rerun`，且合跑前查 `ps [G]radleWrapperMain` 无并发构建**
- specs R3-S1"矩阵内多拓扑在同一执行环境合并运行，结果与单独运行一致"：各拓扑类此前均有独跑绿记录（T2 含 ×3、T3/F1/F2 独跑绿）+ 本次合跑 8/8，两判据齐

## 三命令基线终验（4.3）— 2026-10-01

| 命令 | 结果 | 备注 |
|---|---|---|
| `./gradlew test --continue` | BUILD SUCCESSFUL（全仓单元零失败） | 中途一次 FAILED 归因并行会话编辑态瞬变（starter-basics/skywalking compile 红→单独重跑即绿），非本变更 |
| `./gradlew integrationTest`（无容器档） | BUILD SUCCESSFUL | docker 类 skip 门控正确（@Tag docker + disabledWithoutDocker 四新类齐） |
| `./gradlew integrationTest -PincludeDocker`（全量容器档） | BUILD SUCCESSFUL | 含本矩阵 8/8 与既有 T1/T4 锚、data/net/starter 各档；本变更零新增失败 |

## 矩阵遗留问题清单（本变更红线内不修，登记待后续处置）— 2026-10-01

| 级别 | 问题 | 归属与处置建议 |
|---|---|---|
| MEDIUM | `DemoAppProcess` 未 `destroyOnExit()`，fork 被强杀时子 JVM 成孤儿占端口 | 前序变更（add-starter-net-integration-tests）资产，本变更禁顺带修改；建议并入 rename-integration-source-set 或独立小 fix |
| MEDIUM | `freePort` 探测与子进程 bind 间秒级窗口可被并发 fork/外部进程抢占；`awaitListening` connect 探测不辨归属（他者占端口即"假就绪"） | 同上属载体既有机制；碰撞频率两轮全矩阵实跑未观测；建议改 SO_REUSEADDR 预留或日志锚点前置 |
| MEDIUM | `ScenarioSupport.topoConfig` 未命中回落到 classpath 根，未来拓扑名撞根目录（it-relay-*.yml）会静默装成 T4 键空间 | 回落分支应收紧为显式 legacy 白名单（测试侧一处小改，留后续） |
| LOW | `@Isolated` 在本工程无运行期效果（JUnit 并行执行未启用，类间隔离实由 forkEvery=1 承担） | 注释级误导，保留无害；如未来启用 junit 并行需重估 |
| LOW | `it-f1-caller-ambiguous.yml` 与主 caller 仅文件名一词之差，泄漏通道=人因（复制粘贴），无装配层锁定 | 主分支有终端载荷逐字断言兜底（假成功免疫），接受残余人因 |
