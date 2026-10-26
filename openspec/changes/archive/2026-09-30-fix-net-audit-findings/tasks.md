# Tasks

**组序 = design.md Migration Plan 的三个 Wave 顺序：Wave-A（可独立发布）→ Wave-B（成对升级）→ Wave-C（结构收口 + BREAKING）→ 集成收口**。组内测试任务先于实现任务（config 规则），各组红灯基线运行结果统一记录到本变更目录 `red-baseline.md`（沿用 `fix-relay-registry-leak` 做法），各组末尾附受影响模块测试验证任务。组间相互独立、可分 PR 合入。

## 1. [Wave-A] 编解码帧内长度防线（net-protocol R：有界校验/头计数）

- [x] 1.1 新增 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/network/codec/FrameLengthGuardTest.java`：帧内 varint 声明体长/头长超可读时以解码异常终结且无大分配（分配拦截断言：越界帧触发异常且 JVM 分配不越阈值）；头声明数量与实际不符整帧失败；golden 样例锚定旧版合法报文逐字节可解（compat）。运行记录红灯。
- [x] 1.2 `DefaultNettyMessageCodec.readBody`：分配前校验 `0 <= length <= readableBytes`，越界抛可诊断解码异常；`readBytes` 纳入 try/finally，异常路径释放已分配缓冲。
- [x] 1.3 `DefaultMessageHeaderCodec.decode` 与 `readHeaders` 循环：声明长度/计数上界校验，单头解码失败中断整帧而非 continue 吞掉。
- [x] 1.4 `NetPacketV1Decoder.readPayload`：`bodyLength <= 0` 或小于已耗头+校验长度时走 `causeDecodeError` 规范路径，不再以负数入分配器抛运行时异常；waste 位与配置不对称时显式拒绝。
- [x] 1.5 验证：`./gradlew :tny-game-net-netty4:test` 全绿，1.1 用例转绿。

## 2. [Wave-A] 编码失败零字节与回执失败（net-protocol R：编码失败契约）

- [x] 2.1 新增 `NetPacketEncodeHandlerTest.java`：body 编码中途抛错 → 通道无字节写出、写回执以失败完成并携带原因、WARN 级错误不关连接、ERROR 级按既有 `closeOnError` 处置（三态断言）。运行记录红灯。
- [x] 2.2 `NetPacketEncodeHandler`：编码失败不再"吞异常正常返回"——异常传播给 `MessageToByteEncoder`（由其释放 out 并 tryFailure），保证零字节上线+回执失败；对齐 relay 侧 `RelayPackEncodeHandler` 同型处置。
- [x] 2.3 `NetPacketV1Encoder` / `DefaultNettyMessageCodec.writeHeaders`：任一头/体编码失败整帧失败（头声明计数与实际一致），失败路径 writerIndex 回退至帧起点不残留 magic+option。
- [x] 2.4 `RelayPacketV1Encoder`：`encodeObject` catch 路径回退 out 至帧起点（载荷释放契约由组 13 补齐，本组先保证零字节上线），编码失败仅本次写回执失败。
- [x] 2.5 验证：`./gradlew :tny-game-net-netty4:test` 全绿；`tools/net-bench` 基线跑一轮确认热路径无回退（记录）。

## 3. [Wave-A] 安全配置启动期完备性（net-protocol R：密钥校验）

- [x] 3.1 新增 `tny-game-net/src/test/java/com/tny/game/net/codec/CodecSecurityConfigTest.java`：加密/校验启用而密钥为空时装配校验失败可诊断；合法配置首包往返正常；未启用不受影响。红灯记录。
- [x] 3.2 `DataPackCodecOptions`：`isEncryptEnable && keys.length==0` 启动期 fail-fast（校验点置于 codec 装配单元 `prepareStart` 检查链）；`setSecurityKeys` 同步失效 `securityKeysBytes` DCL 缓存；取键下标用 `Math.floorMod` 防包号负溢出。
- [x] 3.3 验证：`./gradlew :tny-game-net:test` 全绿。

## 4. [Wave-A] 报文标识唯一性与拼位边界（net-protocol R：标识唯一/拼位）

- [x] 4.1 新增 `tny-game-net/src/test/java/com/tny/game/net/command/dispatcher/AutoIncrementIdGeneratorTest.java`：n=CPU 核数及 n∈{2,4,8,6,7} 多线程并发取号无重复（撞号回归：修复前 `(1<<bitCount)|i` 构造性撞号必红）；单线程单调。
- [x] 4.2 `AutoIncrementIdGenerator`：位宽改 `32 - Integer.numberOfLeadingZeros(n - 1)`（index 所需位数），线程取模用 `Math.floorMod`；补类注释声明唯一性域（JVM 内）。
- [x] 4.3 新增/扩展 `tny-game-net/src/test/java/com/tny/game/net/application/RpcAccessIdentifyTest.java`：负 index/超界 serverId 拼装即拒；合法 id 回代校验（parse→format 相等）；旧版合法拼位解析结果逐字段不变（compat）。红灯记录。
- [x] 4.4 `RpcAccessIdentify`：`checkIndex` 补 `index >= 0`，serverId 双侧界校验；`RpcServiceTypes.of` 解析失败路径改 `checkNotNull`/严格 parse；`id` 字段 final 化评估（若有 setId 合法用途则改为校验后写）。
- [x] 4.5 验证：`./gradlew :tny-game-net:test` 全绿。

## 5. [Wave-A] 凭证比较自洽与顶号顺序（net-session R：凭证/顶号）

- [x] 5.1 新增 `tny-game-net/src/test/java/com/tny/game/net/transport/CertificateCompareTest.java`：对全部 Certificate 实现断言"更新/更旧互斥、双已认证按时刻定序、混合状态已认证优先"（当前恒 false/同真，必红）。
- [x] 5.2 `Certificate.isNewerThan/isOlderThan` default 方法：`otherInstant` 取 `other.getAuthenticateAt()`，混合认证两分支方向修正，相等时刻双 false。
- [x] 5.3 新增 `CommonSessionKeeperAuthOrderTest.java`：新会话接管中途被销毁 → 旧会话保持在线、登录返回失败（当前先杀旧必红）；接管成功 → 旧会话终结且下线通知一次。
- [x] 5.4 `CommonSessionKeeper.doAuth` 顺序调整：`session.online(cert)` 成功后再 `oldSession.close()`；`online` 失败路径旧会话不动。
- [x] 5.5 验证：`./gradlew :tny-game-net:test` 全绿。

## 6. [Wave-A] 下线幂等与单次派发（net-session R：下线幂等）

- [x] 6.1 新增 `SessionOfflineOnceTest.java`：下线监听器内调用 `session.close()` → 无递归溢出、下线事件总计一次；通道断开与会话关闭并发 → 下线通知一次、离线队列至多一条记录（当前双派发双入队，必红）。
- [x] 6.2 `BaseNetSession.setOffline` 增加 `status == OFFLINE` 短路；`close()` 内不再无条件 `offline()`；`AutoCloseableSessionKeeper.onOffline` 入队去重。
- [x] 6.3 验证：`./gradlew :tny-game-net:test` 全绿（含既有 `TunnelSessionSnapshotTest` 回归）。

## 7. [Wave-A] 连接超时有界与重连不熄火（net-tunnel R：超时/重连）

- [x] 7.1 新增 `ClientConnectRetryTest.java`：`CONNECT_TIMEOUT_MILLIS` 断言进 bootstrap option（当前丢参必红）；"首败→后成→再断"状态机限时仍持续调度；失败回调与调度登记竞态下 `retryFuture` 由 AtomicReference CAS 摘除（重言式必红场景）；URL 重连间隔参数解析失败回退 setting 值。
- [x] 7.2 `NettyClientGuide.connectAsync` 真实设置连接超时；`CommonTunnelConnector`：`retryFuture/autoRetry` 原子化、重连失败统一按 `setting.isAutoReconnect()` 决定续排、URL 参数解析兜底。
- [x] 7.3 `NettyClientGuide`：`bootstrap` 字段 volatile（对齐 server）；`tunnels` 与 close 竞态——closed 标志先置再快照，`connectAsync` 入口 closed 检查、新增连接入已关组即时关闭。
- [x] 7.4 验证：`./gradlew :tny-game-net-netty4:test` 全绿。

## 8. [Wave-A] 鉴权校验器注册与生效（command-execution R：鉴权维度）

- [x] 8.1 新增 `AuthValidatorResolutionTest.java`：全局兜底校验器首次注册成功（当前 NPE 必红）、第二全局注册明确拒绝；方法未指定 validator 的需登录协议按"方法级→协议级→全局"解析生效（当前死路必红）；三级皆无 → 未登录码；方法级显式指定优先级不变（compat）。红灯记录。
- [x] 8.2 `DefaultMessageDispatcherContext.addAuthProvider` 断言改 `assertNull` 并消除参数求值 NPE。
- [x] 8.3 `RpcInvokeCommand` 鉴权触发条件改 `controller.isAuth() && !authenticated`，接入 `getValidator(protocol)/defaultValidator` 解析链（design D9）。
- [x] 8.4 验证：`./gradlew :tny-game-net:test` 全绿。

## 9. [Wave-A] 一行级修复簇：作用域、PONG 吸收、停机调序、注销登记

本组收纳散落在原发现域分组中的"单行级、可独立发布"修复（design Migration Plan Wave-A 第 5/6 项），结构性同族工作仍留 Wave-C 各组。

- [x] 9.1 新增红灯：`MethodControllerHolderTest` 作用域不匹配按"协议不存在"处置（当前恒放行必红）+ `MessageCommandBoxTest` PONG/未知模式入派发不产生异常与刷屏（当前三层 NPE 必红）。
- [x] 9.2 `MethodControllerHolder.isActiveByScope` 改调 `super.isActiveByScope(scope)`；`DefaultMessageDispatcherContext` 类级 scope/appType 兜底比对同文件核对修正。
- [x] 9.3 `MessageCommandBox.createCommand` null command 入队短路；`RpcContexts` 空上下文访问器判空改抛带信息异常（`RpcEnterInvocationContext` getAccessMode/getSession 判空）。
- [x] 9.4 红灯 `ApplicationStopLifecycleTest`（最小版：stop→监听状态查询归假 + 停止事件恰好发布一次，当前死分支必红）→ `NetApplicationLifecycle.stop()` 判定与赋值调序（close 主体与 bootstrap 缓存的结构性修正仍属组 23）。
- [x] 9.5 红灯 `NetAutoServiceRegisterTest`（最小版：注册→注销计数对称，当前 deregister 空转必红）→ `NetAutoServiceRegister.register` 补 `registrations.add(current)`，异常回滚 running（订阅生命周期全族仍属组 24）。
- [x] 9.6 验证：`./gradlew :tny-game-net:test :tny-game-starter-net-netty4:test` 全绿。

## 10. [Wave-A] 检查链空值放行与注解覆盖校验（message-checking 两条 Requirement）

- [x] 10.1 新增 `ParamFilterNullPassTest.java` + `FilterCoverageTest.java`：空可选参数携带范围/格式/内容注解时放行（当前 NPE→系统错误必红）；超时检查器 attribute 缺省不拦截（当前拆箱 NPE 全量拦截必红）；注解无对应检查器时启动失败可感知（当前 `@TextCheck` 静默失效必红）。
- [x] 10.2 `RangeLimitParamFilter/TextCheckFilter/TextPatternLimitFilter` 空值短路放行；`MessageTimeoutCheckerPlugin` `attribute == null || <= 0` 判空。
- [x] 10.3 `ParamFilterPlugin` 默认注册 `TextCheckFilter`、单数/复数注册入口 key 统一为注解类；启动期注解-检查器覆盖校验 fail-fast。
- [x] 10.4 `TextFilterAutoConfiguration` 开关前缀与属性类统一、`wordsFilters` 注入改 `ObjectProvider` 防零候选炸启动（同键域配置错位顺带修正）。
- [x] 10.5 验证：`./gradlew :tny-game-net:test :tny-game-starter-net-netty4:test` 全绿。

## 11. [Wave-B] 链路切换包类型修正（relay-link R：迁移语义）【成对升级】

- [x] 11.1 新增 `tny-game-net/src/test/java/com/tny/game/net/relay/packet/SwitchLinkPacketTypeTest.java`：经 `RelayPacketType.TUNNEL_SWITCH_LINK.createPacket` 产出的包类型字段为"切换"（当前必红）；服务端对切换包进入迁移分支——既有隧道会话（认证身份/接入号）保留、不新建匿名会话、不触发旧隧道断开级联；空参数不被解释为 0.0.0.0:0。
- [x] 11.2 新增旧侧兼容用例：旧类型（伪装为连接）报文到达新侧时按旧连接语义处置且有版本错配告警（spec compat Scenario）。
- [x] 11.3 `TunnelSwitchLinkPacket` 三参构造器类型常量改 `TUNNEL_SWITCH_LINK`（已实施）。失败回执**保持**连接类包：新增切换结果类型会使旧侧对未知选项断链、放大混跑故障（实施期发现，见 red-baseline 组 11），协议演进另立变更。
- [x] 11.4 变更目录 `release-note.md` 追加：网关/业务服成对升级要求与混跑降级说明（design D4-3）。
- [x] 11.5 验证：`./gradlew :tny-game-net:test :tny-game-net-netty4:test` 全绿。

## 12. [Wave-B] arguments 参数互换自抵消修复（design D4-1）

- [x] 12.1 新增 golden-wire 测试 `TunnelConnectedArgumentsWireTest.java`：修复前后该参数类编码字节逐槽位一致（先固化现网字节为基线，必绿——这是回归锚），本地 `getInstanceId()/getTunnelId()` 修复前互换（必红）。
- [x] 12.2 `TunnelConnectedArguments` 构造器改 `super(instanceId, tunnelId)`，**同一提交内**将其编解码两端的槽位读写序对调保持 wire 不变；`toPacketMessage` 日志值随之正确。
- [x] 12.3 验证：`./gradlew :tny-game-net:test` 全绿且 golden 基线逐字节匹配。

## 13. [Wave-B] 中继早退路径缓冲终结与丢弃留痕（relay-link R：载荷释放/协议错误顶替）

- [x] 13.1 新增 `RelayEarlyExitReleaseTest.java`：向已关闭链路提交中继 → 载荷恰好释放一次（引用计数断言，不依赖 Cleaner）且丢弃告警含链路与消息标识；`channelRead` 丢弃分支（会话未就绪/类型不符）显式 release；未识别链路收 link 型包按协议错误断链而非 `NetLogger` NPE 顶替（当前必红）。
- [x] 13.2 `BaseRelayLink.relay/write` 前置 `isCloseStatus/isActive` 检查，失败路径显式释放包载荷；`TransportTunnel.checkAvailable` 丢弃路径释放消息体；组 2.4 遗留的 relay 编码异常载荷释放并入本组闭环。
- [x] 13.3 `NettyRelayPacketHandler.channelRead`：`relayMonitor.onReadPacket` 前完成链路判空（monitor 空 link 短路），协议错误处置先于观测调用；`NettyRelayLinkConnector` 失败路径 `transport` 判空后再 close、日志取 cause 本体。
- [x] 13.4 验证：`./gradlew :tny-game-net:test :tny-game-net-netty4:test` 全绿；`tools/net-bench` relay 场景断链风暴下缓冲峰值不随丢弃数增长（记录）。

## 14. [Wave-B] 链路分配策略下标越界

- [x] 14.1 新增 `RelayAllotStrategyTest.java`：size>1 时 10k 次随机/轮询分配恒返回合法元素无异常（当前随机策略约半数必红）；轮询计数器模拟溢出后仍非负。
- [x] 14.2 `RandomRelayAllotStrategy` 改 `nextInt(size)`；`PollingRelayAllotStrategy` 改 `getAndIncrement() & Integer.MAX_VALUE` 或 LongAdder 取模。
- [x] 14.3 验证：`./gradlew :tny-game-net:test` 全绿。

## 15. [Wave-C] 属性与 header 容器并发底座（net-session R：属性并发，design D3）

- [x] 15.1 新增 `tny-game-common-lang/src/test/java/com/tny/game/common/context/AbstractAttributesConcurrencyTest.java`：多线程 barrier 确定性交错下并发写全部可读回、读写并发值完整、`setAttribute(key, null)` 等价 remove；新增 `tny-game-net/src/test/java/com/tny/game/net/message/HeaderContainerConcurrencyTest.java`：首写不丢、`getAllHeaderMap` 返回快照（遍历期写不炸）。红灯记录。
- [x] 15.2 `AbstractAttributes`：内部改 `ConcurrentHashMap`（懒创建安全发布），删除复制粘贴双检；`computeIfAbsent/setIfAbsent/setAttribute(null)` 走原子语义；公开签名不变。
- [x] 15.3 消息 header 容器与 `RpcTracingHeader`：首写换容器原子化，transitive header 传递拷贝，杜绝请求/响应别名共享可变 map。
- [x] 15.4 全量编译下游（`./gradlew compileJava`）确认 common-lang 签名零变更；跑 `:tny-game-data:test` 等 attributes 相邻模块冒烟。
- [x] 15.5 验证：`./gradlew :tny-game-common-lang:test :tny-game-net:test` 全绿。

## 16. [Wave-C] ABBA 死锁消除（net-tunnel R / relay-cluster-view R：摘除×注册，design D1）

- [x] 16.1 新增两个限时并发测试：`TunnelSessionLockOrderTest.java`（channelInactive 的 `disconnect` × 登录 `online` barrier 交错，双方限时完成——当前可死锁，设计成可判定的限时断言）；`tny-game-net/src/test/java/com/tny/game/net/clusters/ClusterInstanceLockOrderTest.java`（`unregisterInstance` × `register` 交叠限时完成、调度线程存活）。红灯记录。
- [x] 16.2 `BaseNetTunnel.disconnect`：`onDisconnected()` 移出 `statusLock`（锁内快照 session 引用），与 `close()` 编排一致。
- [x] 16.3 `BaseRelayServeInstance.register/relieve`：`cluster.refreshInstances()` 移出 `linkLock` 临界区；`BaseRemoteServeCluster.registerInstance` 纳入 `instanceLock`。
- [x] 16.4 验证：`./gradlew :tny-game-net:test` 全绿（并发用例连跑 20 次无 flake，记录）。

## 17. [Wave-C] 通道事件全序单派发（net-tunnel R：事件全序）

- [x] 17.1 扩展 `TunnelSessionSnapshotTest` / 新增 `TunnelEventOrderTest.java`：断言"激活→失活→关闭"每类至多一次且有序；断开后紧跟关闭不再出现 close 先于 unactivated、失活双发（当前必红）。
- [x] 17.2 `BaseNetTunnel`：`onUnactivated` 触达去重（同一 session 单次），close 路径事件顺序调整；`session.onUnactivated` 双调用点收敛。
- [x] 17.3 验证：`./gradlew :tny-game-net:test` 全绿。

## 18. [Wave-C] 请求-响应闭环（net-tunnel R：写回执必完成）

- [x] 18.1 新增 `RequestResponseClosureTest.java`：写提交被拒（模拟 event-loop 拒绝）→ 写回执与响应 future 均失败完成、消息不入已发送缓存（当前悬挂必红）；monitor 销毁后请求即失败不复活 holder；接收过滤拒收 RESPONSE → 已 poll 的 future 被取消或回置完成；`sendTo` 多播各接收方独立 future/内容（同 future 双完成必红）。
- [x] 18.2 `NettyChannelMessageTransport.write` 外层 catch 补 `awaiter.completeExceptionally` + `content.cancel`；lambda 内 `writeAndFlush` 抛出同样兜底。
- [x] 18.3 `BaseNetSession`：关闭竞态路径 future holder 终态化（destroy 后 `respondFutureMonitor()` 返回 null 走失败）、`receive` 过滤拒收响应时完成被 poll 的 future。
- [x] 18.4 `AbstractSessionKeeper.sendTo`：RequestContent 多播逐接收方独立内容（或显式拒绝并文档化）；广播前 `isOnline` 检查；resend 统一经 `sendFilter`（重发路径过滤一致性顺带修正）。
- [x] 18.5 验证：`./gradlew :tny-game-net:test :tny-game-net-netty4:test` 全绿。

## 19. [Wave-C] 派发终答守卫（command-execution R：终答恰好一次，design D5）

- [x] 19.1 新增 `CommandCompletionGuardTest.java`：业务在写结果前抛异常 → 请求方收到系统错误应答且完成监听恰好一次（当前永无响应必红）；`@RelayTo` 目标不可用 → 错误码非静默（当前静默悬挂必红）；正常路径单次应答、监听不双触发。
- [x] 19.2 `RpcHandleCommand.finally` 无条件终态守卫：未 complete 则补 `complete(SERVER_ERROR)`，至多一次由既有 CAS 终态机保证；`@RelayTo`/转发失败路径接入同一守卫，杜绝 `completeSilently()` 逃逸。
- [x] 19.3 验证：`./gradlew :tny-game-net:test` 全绿。

## 20. [Wave-C] 异步命令超时兜底（command-execution R：异步兜底，design D6）

- [x] 20.1 新增 `AsyncCommandTimeoutTest.java`：业务返回永不完成的异步结果 → 配置时限到达后请求收错误应答且下一条消息照常执行（当前会话饿死必红）；时限内完成不触发兜底。时限用测试专用小值注入。
- [x] 20.2 `MessageCommandPromise` deadline 激活为配置项（`tny.net.command.executor.serial.timeout`，缺省值评审定，design Open Question），会话级看门狗兑现，完成回调投递回会话串行 worker 保持响应有序。
- [x] 20.3 验证：`./gradlew :tny-game-net:test` 全绿。

## 21. [Wave-C] 会话清理任务可终结（net-session R：清理可终结，design D7）

- [x] 21.1 新增 `SessionKeeperLifecycleTest.java`：manager 关闭后周期任务取消（Future.isCancelled 断言，当前必红）；单轮清理抛 Error 后续轮次仍执行；并发首次 `loadKeeper` 仅一个 keeper 注册任务、落选方无残留。
- [x] 21.2 `AutoCloseableSessionKeeper`：保存 `ScheduledFuture` 句柄，经 `CommonSessionKeeperManager` 的 `AppClosed` 实现驱动 cancel（不给公共 `SessionKeeperManager` 接口加方法）；任务体最外层 `catch (Throwable)` + 错误日志。
- [x] 21.3 `CommonSessionKeeperManager.loadKeeper` 改 `computeIfAbsent` 内创建；孤儿 keeper 的调度登记与创建同原子。
- [x] 21.4 验证：`./gradlew :tny-game-net:test` 全绿。

## 22. [Wave-C] RPC 注册表原子发布与收缩（net-rpc-registry 全部 Requirement，design D2）

- [x] 22.1 新增 `tny-game-net/src/test/java/com/tny/game/net/rpc/RpcRegistryConsistencyTest.java`：并发注册两节点最终视图含双（注入交错必红）；全摘后视图不含、路由服务不可用；陌生节点 remove 不建空壳；激活/失活跃迁各恰好一次（当前反向/不可达必红）；指定接入未命中降级有告警留痕、精确命中无告警；绑定后立即可见、重复绑定告警不覆盖、未绑定查询空（当前 NPE 必红）。
- [x] 22.2 `RpcServiceNodeSet.refreshNodes` 纳入写侧锁统一发布，退役无消费者的 `version` 计数器；`removeSession` 改 `get` 不建、空节点 `compute` 摘除。
- [x] 22.3 `RpcServiceNode` 激活/失活跃迁条件修正（addSession 空→非空触发、removeSession 非空→空触发）；`ContactNodeSet.keeper` volatile + CAS bind + `isActive()` 按真实绑定。
- [x] 22.4 `BaseRpcServicerManager.findInvokeNodeSet` 改读 `invokeNodeSetMap`（消除 load/find 不对称）；`DefaultRpcForwarder.findForwardAccess` 降级回退补 WARN 日志。
- [x] 22.5 `RpcForwardHeader/RpcForwardHeaderBuilder` 转发者 setter 使用参数（消自引用与错目标），删除 `setFrom` 残留死分配。
- [x] 22.6 验证：`./gradlew :tny-game-net:test` 全绿。

## 23. [Wave-C] 引导器停机·重开·绑定可感知（net-guide-lifecycle 两条 Requirement，design D7/D8）

本组承接组 9.4 停机一行调序之外的结构性工作；`net-guide-lifecycle` 既有"关闭后重开恢复"契约由本组兑现。

- [x] 23.1 新增红灯：`GuideReopenTest`（close→re-open 绑定成功且服务可用，当前 bootstrap 缓存绑死旧组必红）；`NettyServerGuideBindTest`（占用地址启动对发起方可感知、无半开通道残留、`isBound()` 与真实通道一致，当前仅 log 必红）。
- [x] 23.2 `NettyServerGuide/NettyClientGuide.close()`：清空 bootstrap 缓存与 channels、幂等 guard、`ServerClosedListener` 单次；重开路径 group/bootstrap 一体重建。
- [x] 23.3 `NettyServerGuide.bind` 失败/超时聚合抛出（或经启动路径可感知错误）；`NetApplication.start` 任一 server 开启失败使启动失败/显式不可用态；`close()` 依赖显式 lifecycle 路径执行，Spring 推断销毁退化为兜底。
- [x] 23.4 `CommonServerBootstrapSetting` 地址解析改显式校验（段数/端口界/IPv6 拒绝带诊断错误，AIOOBE/NPE 消除）。
- [x] 23.5 验证：`./gradlew :tny-game-net-netty4:test :tny-game-starter-net-netty4:test` 全绿。

## 24. [Wave-C] nacos 注册/订阅生命周期与装配条件（net-boot-integration 全部 Requirement，design D8）

本组承接组 9.5 registrations.add 一行修复之外的结构性工作。

- [x] 24.1 新增 `BaseServeNodeClientLifecycleTest.java`（ApplicationContextRunner 驱动）：订阅失败上抛进入重试、终结清条目、二次上下文重新订阅可复活（当前静默失效必红）；无 APM 运行时环境启动成功（类条件缺失必红）；默认配置（不放开 bean 覆盖）启动成功（当前冲突必红）；`discovery: false` 时零订阅。红灯矩阵记录。
- [x] 24.2 `BaseServeNodeClient`：holder map 改实例字段、`onClosed` remove、`subscribe` 命中已存在 holder 时 `start()`；`NacosServeNodeClient.doSubscribe` 异常上抛、LOGGER 归属类修正、stop 异常文案与错误反向调度修正；`NacosEventListener.restart` 去抖。
- [x] 24.3 `ImportRpcServiceDefinitionRegistrar` 移除解析期 getBean，代理创建延迟（FactoryBean/supplier），`ObjectProvider` 消费点流式展开保集合完备；`ImportNetBootstrapDefinitionRegistrar` 构造期 eager 改延迟评估；`"appContext"` 等硬编码 bean 名改类型注入并处理零/多候选。
- [x] 24.4 `NetApmSkywalkingConfiguration` 加类级 `@ConditionalOnClass`；`defaultCommandExecutorFactory` 双路注册收口（@Bean 加 `@ConditionalOnMissingBean` + registrar `containsBeanDefinition` 检测），移除 demo `allow-bean-definition-overriding`；`SpringRelayServeClusterSetting.isDiscovery()` 去掉 `|| serveName 非空`。
- [x] 24.5 `RelayRemoteServeNodeWatchService.prepareStart` 按 serveName 去重、`onClosed` 清列表；`SpringBoot*BootstrapProperties` 装饰条件与 `setServer/setClient` 判空顺带修正。
- [x] 24.6 验证：`./gradlew :tny-game-starter-net-netty4:test :tny-game-net-netty4:test` 全绿。

## 25. [Wave-C] 集群视图一致性与终结（relay-cluster-view R：健康时效/命名键/幂等/可终结）

组 16 的锁序修正先行合入后，本组做视图时效与终结。

- [x] 25.1 新增 `tny-game-net-netty4/src/test/java/com/tny/game/net/netty4/relay/ClusterViewConsistencyTest.java`：健康翻转下一分配周期不再选中（当前无 HEALTHY 分支必红）；服务名≠发现名时新节点仍入视图（当前静默失联必红）；重复注册同 instanceId 仅一套链路、无先启动后丢弃的孤儿连接器（当前必红）；视图关闭后心跳 Future 取消（当前必红）。
- [x] 25.2 `NettyClientRelayExplorer`：clusterMap 键空间统一（双索引或单一命名约定）；`updateInstance` 补健康翻转分支；`addInstance` 先注册判重（返回旧值即拦截）后 start；心跳 `ScheduledFuture` 句柄保存、`onClosed` cancel；`heartbeat` 轮外层 `catch (Throwable)`。
- [x] 25.3 `BaseRelayServeInstance.healthy` 改 volatile；`relayLinkMap` final + 终结改 `clear()`；`BaseServeNode.toString` 的 id 标签取 `getId()`。
- [x] 25.4 验证：`./gradlew :tny-game-net:test :tny-game-net-netty4:test` 全绿。

## 26. [Wave-C] APM span 闭合与标签唯一

- [x] 26.1 新增 `tny-game-starter-net-apm-skywalking/src/test/java/.../SkywalkingSpanClosureTest.java`（mock/agent-core test jar）：异步完成后快照属性清理、EXIT+async 单次结束、`onSuspend` 仅停框架登记 span、StringTag 键位一一对应（当前悬挂/连坐必红）。
- [x] 26.2 `SkywalkingRpcMonitorHandler`：`stopAsyncSpan` 清理 TRACING_SNAPSHOT；EXIT 分支 prepareFor/stopSpan 二选一；`onSuspend` 按登记表精确停 span；tag ID 冲突修正；未用 tag 常量删除。
- [x] 26.3 验证：`./gradlew :tny-game-starter-net-apm-skywalking:test` 全绿。

## 27. [Wave-C] 心跳共享态与 jprotobuf Codec 并发裁决（net-protocol R：心跳；design D11）

- [x] 27.1 新增 `TickMessageIsolationTest.java`：忽略头配置非空时并发心跳处理不触碰共享单例、不抛 UnsupportedOperation（当前 remove 走共享实例必红）；`netty4 NettyMessageHandler` 对 TickMessage 在 ignoreHeaders 前短路。
- [x] 27.2 新增 `tny-game-net-netty4-codec-jprotobuf/src/test/java/.../ConcurrentCodecRoundTripTest.java`：同一类型多线程编解码往返一致（design D11 裁决用例）——通过则结案；失败则登记独立缺陷并在 `red-baseline.md` 标注移交。
- [x] 27.3 protoex Codec 同型用例与错误路径补齐（当前该模块错误路径测试全注释）。
- [x] 27.4 验证：`./gradlew :tny-game-net-netty4:test :tny-game-net-netty4-codec-jprotobuf:test :tny-game-net-netty4-codec-protoex:test` 全绿。

## 28. [Wave-C] 死抽象删除（BREAKING，design D10）

- [x] 28.1 删除前复核：全仓（含 demo/test/下游示例）grep 确认 `ClientConnectFuture/ClientConnectPromise/ConnectCallback/ConnectCallbackStatus`、`ClientConnectorSetting.asyncConnect`、`TunnelEvents.receiveEvent` 链、`NetAppContextHolder`、`SessionPushOption`、`NettyMessageBearer`、`ChanelTaskFuture`、telnet 空壳类、`NetLogger.WatcherAttribute`、空 relay 包类零引用，结果记录本变更目录。
- [x] 28.2 删除上述类型与 `RelayPacketType.handleByTransport` 旁路：`NettyRelayPacketHandler` 的 LINK_OPEN 改走统一分派（同时满足 `relay-link` 既有"激活事件必然传播"契约的回归断言）。
- [x] 28.3 发布说明新增"5.x 删除类型清单与恢复路径（5.8 一次性恢复 + @Deprecated(forRemoval) 过渡）"一节。
- [x] 28.4 验证：`./gradlew clean build` 全绿（删除类 BREAKING 的编译面即验证）。

## 29. 集成回归与发布收口

- [x] 29.1 `red-baseline.md` 终稿：全组红/绿矩阵核对（预期红灯全部转绿，除 27.2 可能的移交项），与本 change specs 的每条 Scenario 建立"Scenario ↔ 测试类"映射表。
- [x] 29.2 demo 冒烟：`tny-game-net-demo` 三进程（网关/业务服/客户端）本地起停+顶号+断线重连+`application-relay-gateway-server.yml` 错误键修正（design Open Question 落定），确认 `allow-bean-definition-overriding` 移除后仍可启动。
- [x] 29.3 `tools/net-bench` 对照基线：编解码与派发热路径（组 1/2/18/19/20 引入的校验与守卫）分配与吞吐无显著回退，结果记录变更目录。
- [x] 29.4 汇总 `release-note.md`：成对升级要求（Wave-B）、配置语义变化清单（connectTimeout/asyncConnect/discovery/顶号变严/错误码可见性新增）、BREAKING 删除清单（组 28）、Wave-A 行为变化（鉴权全局校验器生效、PONG 吸收、作用域真实拦截——此前恒放行的流量开始被拒，属修复方向但需在发布说明显著位置声明）。
- [x] 29.5 验证：`./gradlew clean build` 全绿 + `openspec validate fix-net-audit-findings --strict` 通过。
