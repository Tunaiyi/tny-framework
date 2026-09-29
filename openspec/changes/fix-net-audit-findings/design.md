# Design

## Context

见 `proposal.md - Why`：全量深读确认 2 处 P0、2 处 ABBA 死锁、协议标识撞号/借位、鉴权与完成协议缺口、停机死分支、以及 attributes/header/pending 表等一批跨线程竞态。本设计只解决"怎么修"，行为契约以本变更 `specs/` 各 delta 为准。

当前状态约束（决定修法形状的关键事实）：

1. **框架已有"锁外回调"编排先例**：`BaseNetTunnel.statusLock` 用显式可重入锁的唯一原因就是 close/disconnect 的"锁内改状态、锁外 notify"编排；本变更的两处 ABBA 都出现在该编排被嵌套调用破坏的路径上（`disconnect` 锁内调 `onDisconnected` → 再入 `close` → 持外层锁触达 session 锁；集群侧 `register`(linkLock)→`refreshInstances`(instanceLock) 与 `unregisterInstance`(instanceLock)→`close()`(linkLock) 反向）。
2. **快照发布先例**：clusters/`RpcServiceNodeSet` 一族已是"写侧重建 + volatile 不可变列表读侧零锁"，缺陷仅在建→发两步不原子；`CompletableRpcTransactionContext` 已有 INIT→OPEN→CLOSE 的 CAS 幂等终态机，可作"恰好一次应答"的收敛点。
3. **属性底座被 8 处网络路径调用**（RespondFutureMonitor、RpcTracingHeader、BaseMessageDispatcher、NetLogger、RpcServiceNodeSet、RpcAccessor、CommonSessionKeeperManager 等；`BaseNetTunnel.disconnect` 调用面 11 文件）。codegraph 对接口虚分派的调用方解析不完整（`disconnect`/`computeIfAbsent` 的图内 direct impact ≤1），爆炸半径以 grep 调用点为准；两符号均经接口分发，改动必须保持签名不变。
4. config 约定：热路径（每消息级）互斥用 `ReentrantLock`，禁 `synchronized`（JDK21 虚拟线程 pinning）；冷路径（装配/连接事件）可用简单锁；读侧仅需发布一致性用 volatile 快照（模式卷第二节）。

## Goals / Non-Goals

**Goals:**
- 兑现全部 specs delta 的 Requirement/Scenario，且每条决定可指认验证测试（P13）。
- 协议相关修复在 wire 层做到**逐字节向后兼容**或显式声明成对升级，不留"半兼容"态。
- 修复以既有模式收口（M1 先例优先），不为本变更引入新抽象层。

**Non-Goals:**
- 中继 linkMap/tunnelMap 摘除（由 `fix-relay-registry-leak` 承接，本变更 specs 与 tasks 均不触碰其槽位语义）。
- 双注册表（Spring bean vs `@Unit`/UnitLoader）合流、客户端单 event-loop 扩容、加密层改为真实密码学、静态进程级闸门的多 context 化——均为结构性债务，另立变更。
- P3 级问题仅在顺带触碰同文件时处理，不立任务。

## Decisions

### D1 两处 ABBA 死锁：统一为"锁内改状态、锁外回调"单一锁序，不引入全局锁层级
`BaseNetTunnel.disconnect()` 对齐 `close()` 的既有编排：`onDisconnected()` 移出 `statusLock` 临界区（状态与 session 引用在锁内快照，回调在 unlock 后执行），从而"隧道→会话"的触达永远发生在隧道锁外；集群侧 `BaseRelayServeInstance.register/relieve` 把 `cluster.refreshInstances()` 移出 `linkLock`（linkMap 变更与快照重建原子即可，集群级刷新在解锁后调用），使 `instanceLock→linkLock` 成为唯一存在的方向。
**否决备选**：① 全局锁序号 + tryLock 降级——框架内两处锁序均可用既有"锁外回调"先例消除，新增降级机制违反 M2（模式不是目标）；② StampedLock——决策表明示无乐观读需求时更贵且不可重入是死锁地雷。
**依据**：P4（statusLock 保护的不变量是状态字段，不是回调时序）、P13（锁临界区缩短，热路径成本下降）；先例 `BaseNetTunnel.close`、`MessageQueue`。

### D2 快照重建原子化：写侧统一入口加锁重建并发布，读侧维持 volatile 零锁
`RpcServiceNodeSet.refreshNodes` 与 `RpcServiceNode.orderAccessPoints` 的重建-发布纳入同一把注册表级 `ReentrantLock`（连接建立/摘除是冷路径，P13 成本可忽略；`registerInstance` 补上 instanceLock，与 D1 的锁序一致）。**不采用** (version, snapshot) CAS 重试发布：重建依赖对底层 CHM 的弱一致遍历，CAS 重试无法保证"包含所有已提交事件"，仍需写侧串行化，不如直接上锁并把闲置的 version 计数器退役删除（避免半生效机制误导后人）。
**否决备选**：读方按 version 判新鲜度——消费点分散（First/Access 两族路由器），改读方协议比改写方扩散面更大。
**依据**：P4（不变量="视图与存活集合最终一致"由单写者锁守护）；先例 clusters 快照族、`BaseRelayServeInstance.activeRelayLinks`。

### D3 属性底座：内部存储换 `ConcurrentHashMap`，废除"锁路径 vs 无锁路径"双轨
`AbstractAttributes` 保留公开签名（P11 合同不变），内部由"RW 锁 + HashMap + 无锁 computeIfAbsent"改为纯 CHM：`setAttribute(key, null)` 语义映射为 remove（CHM 不容 null 值，行为与原 getAttribute 判空等价）；删除 `getMap()` 的复制粘贴双检。同理适用于消息 header 容器（`EmptyImmutableMap` 首写换写时复制或 CHM，`getAllHeaderMap` 返回快照）。
**否决备选**：把 `computeIfAbsent/setIfAbsent` 纳入原有读写锁——两套并发策略并存正是本次事故根因，保留双轨等于留雷；CHM 读侧与读锁读性能同级（无竞争时略优），写侧 compute 原子性直接满足 net-session"属性容器并发安全"契约。
**依据**：P4、P13（每消息 header 拷贝是热路径：拷贝一次快照换确定性的无损坏）；先例 `RespondFutureMonitor` 的 map 选型。

### D4 wire 兼容三策略：自抵消修复 / 逐字节不变 / 成对升级
按修复对线上字节的影响面分三类处理，这是本变更最大的发布约束：
1. **自抵消修复（零 wire 变化）**：`TunnelConnectedArguments` 构造参数互换——构造器改正形参序的**同一提交内**将编码写槽序与解码读槽序对调回来，线上槽位语义与旧版逐字节一致，混跑无感；仅本地 getter 由错变对。同理 `RpcForwardHeader.setFrom/setToForwarder` 自引用 bug 属纯本地字段。
2. **逐字节不变的协议防线**：帧内长度校验、编码零字节、header 计数一致性、idgen 位宽——只收紧本地失败行为，合法报文字节不变；旧客户端报文兼容场景由 `net-protocol` delta 逐条立约并以 golden-wire 样例测试锚定。
3. **成对升级（有 wire 语义变化）**：链路切换包类型修正——发送端从"伪装成连接"改为真实"切换"类型，接收端按类型分派。**否决备选**：接收端启发式识别（空地址的 connect 包视为切换）——把类型真相重新交给数据形状猜测，正是本缺陷的成因模式（P4 不变量再次被破坏）；协议加版本位——单次修正不足以证成新的协议演进机制（P10 例外条款不适用，直接不过）。发布策略：网关与业务服同 release 成对升级；`relay-link` delta 已固化"旧侧未升级时按旧语义处置 + 版本错配告警"，混跑窗口内切换功能降级为现状（可接受：现状本就销毁会话，新侧至少留痕）。
**依据**：P11 合同三问逐条过；P12。

### D5 完成协议：终答守卫收敛到统一终态机，不在各 catch 点分散兜底
"每个请求恰好一个终答"实现为两层：① `RpcHandleCommand` 的 `finally` **无条件**执行守卫——若上下文尚未进入终态则补 `complete(SERVER_ERROR)`（至多一次由 `CompletableRpcTransactionContext` 既有 CAS 保证，至少一次由守卫补全）；② `@RelayTo`/转发失败路径复用同一守卫，禁止 `completeSilently()` 逃逸。响应发出线程不变：完成回调仍投递回会话串行 worker（`whenCompleteAsync(..., executeWorker)`），维持同会话响应有序——观察者回调线程模型按此固化：**完成回调线程=会话 worker，非任意业务线程**。
**否决备选**：每个异常分支手写回包——审计确认缺口正来自"分支各自为政"（M1：框架内已有统一终态机而未用，需修的是接入，不是再造协议）。超时拦截（`MessageTimeoutCheckerPlugin` attribute 缺省）同批改为判空放行，语义归 `message-checking` delta。
**依据**：P5（完成协议是独立不变量，归上下文而非各命令）、P6。

### D6 异步命令超时兜底：激活 promise 既有 deadline 字段，由会话级看门狗兑现
`MessageCommandPromise` 的 3000ms 死字段改为真配置（`tny.net.command.executor.serial.timeout` 级别，缺省值评审定），兜底由**每会话一个**的可取消 deadline（复用会话串行 executor 的调度入口 `offerTask` 风格或单一定时轮）兑现，超时即 `completeExceptionally` → 串行队列 `resumeLoop` 照常前进。**否决备选**：全局 sweeper 仿 `RespondFutureMonitor` 5s 轮扫——超时精度 5-10s 对"队列头阻塞"过粗，且新增全局单例与 D7 终结性诉求矛盾；`CompletableFuture.orTimeout` 直挂——JDK 公共池调度器在虚拟线程/容器受限环境不可控，且完成回调线程需回落会话 worker 保序，仍需一层适配，不如自持。
**依据**：P13（每会话一个定时任务，量级=在线会话数）；先例 `RespondFutureMonitor` 的 holder 注册/注销对称。

### D7 生命周期终结：实现走 `AppClosed` 既有契约链，公共接口零变更
keeper 扫描任务、relay 心跳调度、nacos 订阅句柄的"可终结"统一挂到 common-lifecycle 的 `AppClosed`/`Lifecycle` 职责链先例（关闭方保存 `ScheduledFuture` 句柄并 cancel；`AutoCloseableSessionKeeper` 补 close 语义由 manager 的 `AppClosed` 实现驱动），**不给 `SessionKeeperManager` 接口加方法**——公共接口加方法是下游实现类的编译期破坏（P11 三问第②问不过），而 `AppClosed` 已是框架内停机契约的单一事实源。停机主路径修正（`NetApplicationLifecycle.stop()` 调序 + `running` 判定前置）后，`NetApplication.close()` 由显式 lifecycle 路径执行，Spring 推断销毁仅余兜底。
**否决备选**：接口加 `default close()`——二进制兼容但语义分裂（两个停机真相），M1 已有 `AppClosed` 先例。

### D8 Spring 装配修正：definition 注册与实例化分离 + 条件装配补全
`ImportRpcServiceDefinitionRegistrar` 移除解析期 `getBean`，代理创建移入惰性 FactoryBean/supplier（首次 `getObject` 才解析依赖图，`ObjectProvider` 流在调用时展开以保集合完备）；APM 自动配置补类级 `@ConditionalOnClass`；`defaultCommandExecutorFactory` 双路注册改为 @Bean 侧 `@ConditionalOnMissingBean` + registrar 侧 `containsBeanDefinition` 前置检测；demo 的 `allow-bean-definition-overriding: true` 移除以恢复验证信号。**否决备选**：全量迁移到 `@AutoConfiguration` 重排装配序——5.7.x 维护分支不做装配体系重构（范围控制）。
**依据**：P12（`net-boot-integration` delta 先立约）；先例：boot 的 registrar 家族形状保持。

### D9 鉴权解析收敛单点：`方法级 → 协议级 → 全局` 一次解析、注册断言修正
`RpcInvokeCommand` 的鉴权触发条件改为"声明需鉴权且未认证"，校验器解析下沉到 dispatcher context 的单一方法（该方法已存在死路实现 `getValidator(protocol)`/`defaultValidator`，修通而非新建）；注册断言 `checkNotNull(...getClass())` 改 `assertNull`（消 NPE 与语义反转）。
**否决备选**：为鉴权引入新 Validator 链抽象——三次法则不满足（P10），现有两级表已覆盖场景。
**依据**：P3（扩展点已存在）、P6（`isAuth()` 契约承诺必须可被兑现）。

### D10 死抽象直接删除（BREAKING 集中于本项）
`ClientConnect*`/`ConnectCallback*` 五件套、`ClientConnectorSetting.asyncConnect`、`TunnelEvents.receiveEvent` 死契约、`NetAppContextHolder`、`SessionPushOption`、`NettyMessageBearer`/`ChanelTaskFuture`/空 telnet 壳、`RelayPacketType.handleByTransport` 旁路维持现状（invoker 机制保留、`channelRead` 改走统一分派以兑现规格"事件必然传播"）。全仓 grep 零引用是删除前提（已核）；发布说明列删除清单，若下游报障走"5.8 恢复 + @Deprecated(forRemoval) 一版"的回退路径。**否决备选**：仅标 `@Deprecated` 不删——语义反转的存活 API（`connected()`=异常完成）比删除更毒，留着就是给后人埋雷（P4：不可守约的契约应当终止而非悬挂）。
**依据**：P11（标注 BREAKING + 迁移路径）；P10（删除是收敛抽象面，不是新增）。

### D11 测试先行与红基线（全变更统一模式）
沿用 `fix-relay-registry-leak` 已确立的做法：每个问题组**先落红灯行为测试**（并发用例用 latch+超时的确定性骨架，不用 sleep 竞速；死锁用例断言"限时完成"而非复现挂死；wire 兼容用 golden 样例字节断言逐字节一致），红/绿矩阵记录于变更目录 `red-baseline.md`，再实现转绿。`jprotobuf` 共享 Codec 的 [疑似] 线程安全以并发往返一致性压测用例作为裁决证据（通过=结案，失败=升级为独立缺陷变更）。
**依据**：P12、P13；先例：本仓库 `RelayExplorerRegistryTest`（该变更任务 1.1）。

## Compatibility Impact

- **BREAKING（公共 API 删除，D10）**：`tny-game-net` 公开类型删除清单见 proposal/发布说明；`tny-game-common-lang.AbstractAttributes`、`ClientGuide/ServerGuide`、各监听接口签名均不变。下游需重新编译，预期改动为零（零引用面）。
- **BREAKING（发布协调，D4-3）**：链路切换包类型修正要求网关与业务服同版本成对部署；混跑窗口内切换能力降级为现状行为（已在新 spec 中固化为告警+旧语义）。
- **非破坏**：wire 字节兼容（D4-1/2）；`isOlderThan/isNewerThan` 为 default 方法修正，实现方无需变更，但**行为变化**——此前恒失效的顶号拒绝将真实生效，依赖旧宽松行为的业务（同凭证重复登录互踢方向反转）需在发布说明中提示；错误码可见性新增（此前静默悬挂的请求开始收到错误应答）属客户端可观察变化，已立约于 command-execution delta。
- **配置语义变化**：`connectTimeout`、`asyncConnect` 删除、`tny.net.relay.cluster.discovery:false` 由"不生效"变"生效"、`tny.apm.skywalking.enable` 缺省含义不变但无 agent 环境不再崩——发布说明逐条列出。

## Risks / Trade-offs

- [D1 锁外回调改变回调时点，观察方可能依赖旧的"锁内同步"假象] → net-tunnel R2 事件全序测试 + `TunnelSessionSnapshotTest` 既有回归兜底；回调本就承诺触发线程即执行线程，时点后移不改变线程。
- [D3 CHM 不接受 null 值的语义映射与个别调用方的"显式存 null"预期冲突] → 全仓检索 attributes 写 null 的调用点（审计未见），并以单测固化 remove 等价语义；发现依赖 null 的调用则回退为锁收口方案并记录例外。
- [D5 无条件终答守卫使部分此前"静默"的流量变成错误应答，客户端可见] → 这正是 spec 承诺（fail-visible 优于悬挂）；错误码取 `SERVER_ERROR`（WARN 级不断连），灰度期用 `tools/net-bench` 与 APM 计数器观察新错误码占比。
- [D4-3 成对升级纪律被违反（网关先升或业务服先升）] → 混跑降级路径已在 spec 固化（按旧语义+告警），最坏等于现状；发布说明标注版本对。
- [范围大、单变更周期长] → tasks 按问题组切分且组间无编译依赖顺序约束（除测试先行规则），可按组提交 PR；P0 组（编解码防线、切换包类型）优先合入。
- [并发测试在 CI 上偶发] → 死锁/竞态用例采用"限时断言 + 确定性交错（CyclicBarrier 注入）"，禁止裸 sleep；红灯基线记录环境。

## Migration Plan

1. **Wave-A（可独立发布）**：net-protocol 防线 + idgen + RpcAccessIdentify 边界 + Certificate + 鉴权注册/作用域/PONG 吸收 + 各复制粘贴单行修正 + starter 一行级（stop 调序、registrations.add）。全部 wire 兼容、无部署协调要求。
2. **Wave-B（同批发版，网关/业务服成对）**：链路切换包类型修正 + arguments 自抵消修复 + relay 缓冲终结释放 + 分配策略越界。
3. **Wave-C（结构收口，最后合入）**：D1/D2/D3/D5/D6/D7/D8；BREAKING 删除（D10）随 Wave-C 版本发布说明集中披露。
4. **回滚**：Wave-A 各项独立可回滚；Wave-B 回滚=旧版本成对回滚（禁止单端）；Wave-C 死锁/锁序修正若引发新时序问题，按组回滚并保留其测试为红（记录已知限制）。

## Open Questions

- 异步命令超时缺省值定多少（现 3000ms 是无人读的死值，评审时结合 `tools/net-bench` 基线定档）——不影响契约与任务拆分。
- `@TextCheck` 检查器接入走 Spring 还是 `@Unit`（双注册表合流属 Non-Goal，此处只定本变更内的临时接线，两法皆可达契约）。
- demo `channel.message-handler` 错误配置键的修正方式（改键名 vs 补别名属性）——属示例修复，不触规格。
