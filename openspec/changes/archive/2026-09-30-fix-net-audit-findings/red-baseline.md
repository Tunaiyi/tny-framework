# Red Baseline — fix-net-audit-findings

运行环境：JVM 25 (Azul Zulu 25+36-LTS) / Gradle 8.5，`./gradlew :tny-game-net-netty4:test --tests ...`（toolchain 按 `gradle.properties javaVersion=21`）。

## 组 1｜编解码帧内长度防线（2026-09-29）

`FrameLengthGuardTest`：11 用例 = **9 红（目标行为缺失）+ 2 绿（兼容锚，必须始终为绿）**

| 用例 | 修复前现象 | 红灯原因 |
|---|---|---|
| 体长度声明超过可读 | `IndexOutOfBoundsException`（readBytes 越界） | 先 `heapBuffer(16MB)` 分配后失败；无 `causeDecodeError` |
| 头声明计数多于实际 | `IndexOutOfBoundsException` | 计数循环吞异常继续，把体字节当第二/三个头消费 → 流错位 |
| 单头解码异常 | `IndexOutOfBoundsException`（非 NetCodecException） | `catch(Throwable)` 吞掉后继续循环 |
| 头部对象编解码器长度 | `IndexOutOfBoundsException` | `heapBuffer(1000)` 分配后才 `readBytes` 越界 |
| payloadLength 为负 | `IndexOutOfBoundsException` | 仅上界检查，负值穿透到 payload 消费 |
| 头消耗超 payloadLength（体负） | `IllegalArgumentException`（heapBuffer 负容量） | 负 bodyLength 直接进分配器 |
| 废字节位与配置不对称 | 无守卫（放行继续解码；`maxWasteBitSize=0` 配置下为 `ArithmeticException`） | 只查"配置要求而报文缺位"，反方向放行 |
| 校验位不对称 | 无异常（按猜测的本地 codeLength 啃帧窗口） | 同上 |
| 加密位不对称 | 无异常 | 同上 |
| 兼容锚：旧版合法报文 | **绿** | 往返字节一致、头体完整 |
| 兼容锚：合法帧 | **绿** | 长度自洽帧正常进入消息解码 |

**修复后**：`./gradlew :tny-game-net-netty4:test` 全模块 8 类 65 用例全绿（FrameLengthGuardTest 11/11）。

## 组 1 补充判定（实施期发现，已如实记录）

1. **harness 修正**：初版红基线中三条不对称用例因构造 `payloadLength > 实际字节数` 命中"半包返回 null"而未真正抵达守卫；修正构造后产品级结论不变——修复前三处反向守卫均不存在（代码事实），修复后全部按 `causeDecodeError` 拒绝。
2. **⑥"终态消费自洽"断言缓行（范围裁定）**：无废字节路径下 `bodyLength` 由窗口公式导出，消费恒等于 `payloadLength`（结构重言式，断言无附加价值）；有废字节路径下 reader 交错读取与 writer 空体省略不对称（writer 在 body 为空时不写 partial 字节而 payloadLength 已声明之——**废字节开启 + 空体 + shift>0 属存量线级缺陷**，两旧版之间同样错位 1 字节）。将其纳入本变更需先扩 spec（涉及 waste 编解码互操作行为），按"禁止顺带修改未在规格差量中声明的行为"原则**不在组 1 实施**，登记为后续独立变更候选。

## 移交后续变更的存量发现（组 1 实施期取证）

- `NettyWasteWriter.write`：空体帧不写 partial 字节，但 `NetPacketV1Encoder.writePayload:85` 的 payloadLength 无条件累加 `getTotalWasteByteSize()`（含 partial）→ 空体+废字节帧声明恒多 1 字节，新旧实现在该形态下均流错位。属 waste 子系统存量缺陷，建议与 ⑥ 一并另立差量。

## 组 2｜编码失败零字节与回执失败（2026-09-29）

`NetPacketEncodeHandlerTest`（pipeline().write + 独立 promise，模拟生产 writeAndFlush 回执路径）：**4 红 + 1 绿锚**

| 用例 | 修复前 | 现象（当前 harness 实测） |
|---|---|---|
| WARN 级失败：回执失败 | 红 | promise 成功（吞异常后残帧按成功写出） |
| WARN 级失败：零字节 | 红 | out 内 magic+option 5 字节上线 |
| ERROR 级失败：回执含编码原因 | 红 | 回执成功，无 EncoderException 链 |
| closeOnError=true：回执含原因 | 红 | 同上 |
| 兼容锚：成功帧字节逐位不变 | **绿（修复前后同绿）** | `[magic][option][fixed32 LE][body]` 稳定 |
| writeHeaders 部分失败整帧失败 | 红 | 声明计数 N、实写 M<N，对端错位 |

**实现**：`NetPacketEncodeHandler`/`RelayPackEncodeHandler` 分级处置后重抛（后者顺带修正 `handleOnDecodeError` 误标）；`RelayPacketV1Encoder` 帧起点回退+重抛；`DefaultNettyMessageCodec.writeHeaders` 单头失败抛 `causeEncodeFailed`。

**修复后**：`:tny-game-net-netty4:test` 70 用例全绿；`:tny-game-net:test` 61 全绿；net-bench PacketCodec 冒烟通过（正式对比留组 29.3）。

**harness 取证（EmbeddedChannel 语义三连坑，记录避免复踩）**：`writeOutbound(...)` 返回 boolean 非 future；`unsafe().write` 绕过 pipeline；编码失败关连路径下 `flushOutbound()` 可抛 ClosedChannelException（需吞之）。

## 组 3｜安全配置启动期完备性（2026-09-29）

`CodecSecurityConfigTest`（tny-game-net，6 用例）+ `NetPacketCodecConfigGuardTest`（netty4，1 用例）：**7/7 红**（缺 `checkSecurityConfig`、除零、负下标、派生缓存不失效、prepareStart 无校验）。
实现：`DataPackCodecOptions.checkSecurityConfig()`（encrypt||verify 且无密钥→IllegalStateException）、两个取键 getter floorMod + 空键明确异常、`setSecurityKeys` 失效派生缓存；`NetPacketV1Codec.prepareStart` 首行挂校验（先于单元解析，报错指向配置本身）。
修复后：`:tny-game-net:test` 67 绿、`:tny-game-net-netty4:test` 71 绿。

## 组 4｜报文标识唯一性与拼位边界（2026-09-29）

`AutoIncrementIdGeneratorTest` 7 用例：**4 红**（n=8 邻分片首值同为 6、n=6 全分片 12000 次出现重复、n=1 退化为纯计数的期望值变化）+ 3 绿锚（单线程严格递增、shard4/6 断言外的单调锚）。
`RpcAccessIdentifyTest` 5 用例：**3 红**（负 index、负 serverId、未注册类型静默构造）+ 2 绿锚（合法拼位与旧公式逐位一致=wire 不变、越上界构造拒绝）。
实现：idgen 位宽 `32-numberOfLeadingZeros(n-1)` + floorMod；`checkIndex` 双侧界、`checkServerId`、`formatId` 全路径校验、`(long)` 构造与 `setId` 严格解析 + 回代自洽校验。
修复后：`:tny-game-net:test` 全绿（含组 3/4 新增 12 用例）。

## 组 5｜凭证比较与顶号顺序（2026-09-29）

`CertificateCompareTest` 4 用例：**2 红**（双已认证定序恒 false、混合状态双向同真）+ 2 绿锚（相等时刻互斥、自比较恒否）。
`CommonSessionKeeperAuthOrderTest` 2 用例：**1 红**（takeover 失败时 `verify(oldSession, never()).close()` → NeverWantedButInvoked，修复前确先杀旧）+ 1 前绿后更强锚（InOrder 接管→终结）。
实现：`Certificate` 两 default 方法取 `other.getAuthenticateAt()`、混合状态单分支互斥、不可比时刻保守 false；`CommonSessionKeeper.doAuth` 改 `session.online(cert)` 成功后再 `oldSession.close()`（close→resetSession 相对顺序不变）。
test 编译修复：tny-game-net test 类路径缺 common-lang（受检异常层级解析），补 `testImplementation project(":tny-game-common-lang")`。
验证：`:tny-game-net:test` `:tny-game-net-netty4:test` 全绿。

## 组 6｜下线幂等与单次派发（2026-09-29）

`SessionOfflineOnceTest` 3 用例：**3/3 红**（回调内 close 递归；断开+关闭双派发；正常序列 offline 双计数——该"锚"实为暴露缺陷的红灯）。
实现：`BaseNetSession.setOffline` 加 `OFFLINE/CLOSE` 状态短路（INIT→OFFLINE 不受影响）；`offline()` 结构不动（幂等收敛于 setOffline 单点，符合"状态跃迁集中判定"设计）。`onOffline` 入队去重不单加——双入队根因是双派发，单派发后不存在；避免 O(n) 队列扫描的伪修复。
验证：`:tny-game-net:test`、`:tny-game-net-netty4:test` 全绿。

## 组 7｜连接超时有界与重连不熄火（2026-09-29）

`ClientConnectOptionsTest` 3 用例：**2 红**（CONNECT_TIMEOUT_MILLIS 从未设置、bootstrap 字段非 volatile）+ 1 锚（非正超时不设选项，修复前后同语义）。
`TunnelConnectorReconnectTest` 4 用例：**3 红**（同步失败竞态链条停在 2 次；断开后重连首败即弃；URL 参数非法打断调度）+ 1 锚绿（maxRetry 上限停止）。
实现：
- `NettyClientGuide`：字段 volatile、getBootstrap 构建完成后 volatile 安全发布并真实设置 `CONNECT_TIMEOUT_MILLIS(>0)`；`connectAsync` 入口 closed 拒绝 + 入组后复查自愈；删除丢弃 timeout 的私有重载。
- `CommonTunnelConnector`：`retryFuture` 改 AtomicReference；`autoReconnect` 先清句柄/调度锁再试（同步失败竞态可续排）；`handleConnectFailed` 统一先释放 autoRetry 再 `scheduleReconnect`（续排与否由 `setting.isAutoReconnect()` 单点决定）；`reconnect()` 走 retry=true 语义；无上限重试每 10 次 INFO 留痕；schedule 提交异常回滚锁；URL `retry_intervals` 解析失败回退 setting 值并告警；`stopReconnect` 死方法移除。
验证：`:tny-game-net:test`、`:tny-game-net-netty4:test` 全绿。

## 组 8｜鉴权校验器注册与生效（2026-09-29）

`AuthValidatorResolutionTest` 5 用例，修复后全绿；红基线复证（stash 5 个主源文件）：修复前**编译期缺失** `resolveValidator`/实例重载（新增接缝），既有 API 路径的 `firstGlobalRegistrationSucceeds`/`secondGlobalRegistrationRejected` 在修复前必抛 NPE（`checkNotNull(...getClass())` 代码事实，组前审计已逐行证实）。
实现：`MessageDispatcherContext.resolveValidator`（default，方法级→协议级→全局单点解析）；`ContactAuthenticator.authenticate(validator)` 实例重载（default 回退类签名，下游兼容）；`ContactAuthenticateService` 双签名共实现（null 校验器安全跳过）；`DefaultMessageDispatcherContext.addAuthProvider` 断言方向修正（checkArgument(==null)）；`RpcInvokeCommand` 鉴权触发条件 `isAuth() && !authenticated`。
验证：`:tny-game-net:test` 全绿。

## 组 9｜一行级修复簇（2026-09-29）

红灯：**net 4 红**（作用域恒放行、PONG null 命令入队、currentSession/currentExecutor 裸 NPE）+ **starter 3 红**（stop 死分支、deregister 空转 ×2 用例）。锚绿：类级委托/未声明放行、PING 入队、start 注册计数。
实现：`MethodControllerHolder.isActiveByScope` 改调对偶方法；`MessageCommandBox.doAddCommand` null 命令短路 + default 分支单条 WARN；`RpcContexts` 无绑定线程 `IllegalStateException`（带信息）；`NetApplicationLifecycle.stop()` 判定先于赋值（重复 stop 幂等）；`NetAutoServiceRegister.register` 登记 registrations。
starter 测试基建：`testImplementation libs.alibaba_cloud_starter_nacos_discovery`（main 为 compileOnly，测试类路径缺 spring-cloud 类型）；`publishEvent` 断言对齐 ApplicationEvent 重载（编译期分派）。
验证：`:tny-game-net:test` `:tny-game-net-netty4:test` `:tny-game-starter-net-netty4:test` 全绿。

## 组 10｜检查链空值放行与注解覆盖校验（2026-09-29）

`ParamFilterNullPassTest`（3）：**1 红**（null 参数进 filterRange compareTo NPE）+ 2 锚绿；
`MessageTimeoutNullAttributeTest`（3）：**1 红**（attribute null 拆箱 NPE）+ 2 锚绿；
`ParamFilterRegistrationTest`（3）：**3 红**（TextCheck 缺默认注册、单数入口 key 错位不可达、checkCoverage 接缝缺失-反射探测）。
实现：`AbstractParamFilter.filter` null 参数逐项放行（必填缺失由参数装配环节处置）；`MessageTimeoutCheckerPlugin` attribute 判空；`ParamFilterPlugin` 默认注册 `TextCheckFilter`、单数入口 key 改注解类、新增 `checkCoverage`；`TextCheckFilter.wordsFilters` 空表初始化；`BaseMessageDispatcher.checkParamFilterCoverage` + `DefaultMessageDispatcher/SpringBootMessageDispatcher.prepareStart` 尾部接线（控制器注册完成后校验，启动即失败）；`TextFilterAutoConfiguration` 开关前缀统一为 `tny.net.filter.text-filter.enable`（与属性绑定域一致；demo 无旧键使用，零迁移）、`ObjectProvider<WordsFilter>` 收集并真实注入词表（原入参被忽略且零候选炸启动）。
**遗留（登记）**：Spring 侧 `textCheckFilter` bean 与插件默认实例属"双注册表"Non-Goal——词表 bean 注入到插件实际实例的合流另立变更；本组保证长度检查恒可用、内容检查在词表就位时生效、缺检查器场景启动 fail-fast。
验证：三模块测试全绿。

## 组 11｜链路切换包类型修正（Wave-B，2026-09-29）

`SwitchLinkPacketTypeTest` 3 用例：**1 红**（工厂产出切换包类型为 TUNNEL_CONNECT）+ 2 绿锚（正确分派下迁移语义已保留会话；连接包类型不变）。
**实施期设计发现（11.3 半项范围修正）**：切换失败回执改"语义匹配切换结果包"需新增包类型——旧侧解码器对未知选项直接断链，混跑窗口放大故障；**保持现回执（连接类失败包）不变**，已记 release-note 混跑线索。失败回执语义归入后续"relay 协议演进"独立变更候选。
release-note.md 已建：成对升级、发布顺序（先业务服后网关）、回滚约束。
验证：`:tny-game-net:test`、`:tny-game-net-netty4:test` 全绿。

## 组 12｜arguments 参数互换自抵消修复（Wave-B，2026-09-29）

`TunnelConnectedArgumentsWireTest` 3 用例：**1 红**（本地 getter 与形参名颠倒）+ 2 恒绿锚（proto 槽位语义不变=线上字节回归锚；同版本往返不漂移）。
实现（同提交自抵消）：`TunnelConnectedArguments` 构造器改 `super(instanceId, tunnelId)`；`TunnelConnectedArgumentsProto` 写侧改 `instanceId←getTunnelId()`/`tunnelId←getInstanceId()`、读侧 `ofResult(getTunnelId(), getInstanceId())`——**槽位值与旧版逐位一致**，新旧混跑无感；codecor 侧复用同 proto 类，单点收口。
验证：net/netty4 全绿。

## 组 14｜链路分配策略下标越界（Wave-B，2026-09-29）

`RelayAllotStrategyTest` 3 用例：stash 复证修复前 **2 红**（随机策略 1000 次必抛 IndexOutOfBounds；轮询溢出位）+ 1 锚绿（空/单元素）。修复后 3/3 绿。实现：`nextInt(size)` 有界重载 + `Math.floorMod`。

## 组 13｜中继早退路径缓冲终结与丢弃留痕（Wave-B，2026-09-29）

`RelayEarlyExitReleaseTest`（netty4）3 用例，stash 复证修复前 **3/3 红**（已关链路写出：refCnt=1 且回执悬挂；未识别链路收包：通道保持；不可用隧道写出：body 泄漏），修复后 **3/3 绿**。
实现：`BaseRelayLink` relay/write 统一 `writePacket/canForward/dropPacket` 守卫（终结态：release 载荷 + WARN 留痕 + 回执失败完成；maker 路径不进入装配）；`NettyChannelRelayTransport.write` 两重载补 promise 失败释放与 execute 拒绝/异常兜底（release 幂等，与编码器成功释放兼容）；`TransportTunnel.write(message)` 丢弃分支释放 `OctetMessageBody`；`NetLogger.logReceive/logSend` null-link 短路（观测不得顶替协议错误处置）；`NettyRelayLinkConnector` transport 判空 + 日志 cause 本体。
**实施期取证（登记知识）**：① `NetException extends ResultCodeRuntimeException`（unchecked）——中继协议错误本可被 handler 原 catch 正确分级，"仅告警不处置"的真凶是 logReceive NPE 抢跑；② `RelayPacketType.handle` 分派按具体 packet 子类强转，测试桩必须用真实子类；③ checkAvailable 竞态窗口的消息体释放点收敛在 TransportTunnel 层。
验证：net/netty4/starter 三模块全绿。

## 组 15｜属性与 header 容器并发底座（Wave-C，2026-09-29）

`AbstractAttributesConcurrencyTest`（common-lang）：并发首写混用 computeIfAbsent/setIfAbsent/setAttribute **红**（整表覆盖丢写）；原子性/ null 值锚绿。
`HeaderContainerConcurrencyTest`（net）：并发 putHeader 整表丢失 **红**、getAllHeaderMap live 视图 **红**（针对性回退复证 2/2 红）；迭代并发、tracing 拷贝用例绿锚。
**实现范围修正（记录）**：`EmptyImmutableMap` 在 basics 等单线程场景有 null 值用户，**不改全局换 CHM**（避免越范围破坏）；改为 volatile 字段 + getWriter 双检锁唯一换表（首写竞态消除，语义保持）。`AbstractAttributes` 采用**同锁收口**（computeIfAbsent/setIfAbsent 纳入既有 ReentrantLock）而非 CHM——保留 null 值语义；`getMap()` 复制粘贴双检修正。header 容器用既有 creator 注入口切 CHM + `getAllHeaderMap` 返回 LinkedHashMap 快照拷贝。`RpcTracingHeader.copy()` + `putTransitiveHeaders` 拷贝传递，切断请求/响应可变属性别名。
验证：common-lang/net/netty4/basics 测试全绿。

## 组 16｜ABBA 死锁消除（Wave-C，2026-09-29）

`TunnelSessionLockOrderTest`（net/transport）：断言"隧道→会话回调于锁外发生"——修复前 disconnect 嵌套 close 持锁触达 `session.onUnactivated` **红**。
`ClusterInstanceLockOrderTest`（net/clusters）：register/relieve 的 `cluster.refreshInstances()` 与被置换旧链路 `close()` 均断言 linkLock 未持——修复前 **2 红**（探针枚举自注册解决 checkService 前置）。
实现（design D1"锁内改状态、锁外回调"编排统一）：`BaseNetTunnel.disconnect` 的 `onDisconnected()` 移出 statusLock；`BaseRelayServeInstance.doRefreshActiveLinks` 纯快照化、`register/relieve/refreshActiveLinks` 的集群刷新与旧链路终结移至解锁后；`BaseRemoteServeCluster.registerInstance` 纳入 instanceLock。
验证：tny-game-net 123 用例全绿（含组 15/16 新增）。

## 组 17｜通道事件全序单派发（Wave-C，2026-09-29）

`TunnelEventOrderTest` 2 用例：正常关闭序列锚（修复前后同绿，回归护栏）+ 嵌套断开序列 **红**（修复前 close 先于 unactivated、session 双发）。
实现：`BaseNetTunnel` 新增 `unactivatedNotified` 幂等位与 `notifyUnactivated` 单点（会话回调+失活事件捆绑至多一次）；disconnect/close 尾部统一走该点，close 前保证失活先行；bind 换绑成功重置幂等位（新会话重开资格）。
测试桩教训（记录）：TestTunnel 以 `isActive=!isClosed()` 会短路 open() 的早期返回吞掉激活事件——桩必须走真实状态机 `getStatus()==OPEN`。
验证：net/netty4/starter 三模块全绿。

## 组 18｜请求-响应闭环（Wave-C，2026-09-29）

`RequestResponseClosureTest`（net）2 用例 + `TransportWriteRejectionTest`（netty4）1 用例：stash 复证修复前 **3/3 红**（关闭竞态 putFuture 复活 holder→悬挂；多播共享响应等待未拒；event-loop 拒提交→awaiter 悬挂）。
实现：`BaseNetSession` 增 `futureHolderDestroyed` 标志，destroy 后 `respondFutureMonitor()` 恒 null、`putFuture` 命中即以 `SessionClosedException` 失败完成；`AbstractSessionKeeper` 多播守卫（判别=**已装配响应等待**，因 DefaultMessageContent 全量继承 RequestContent，类型判别不可用——实施期取证）；`NettyChannelMessageTransport.write(maker,…)` 内外双层 catch 补 awaiter 失败 + content.cancel；`BaseNetSession.receive` 过滤拒收时取消已 poll future（防悬挂保险）；resend 三态统一走 `sendFilter`。
验证：net/netty4/starter 全绿。

## 组 19｜派发终答守卫（Wave-C，2026-09-29）

`CommandCompletionGuardTest` 3 用例（守卫路径/正常锚/应答真值表），修复后全绿；stash 复证为**编译缺失红**（`shouldRespondLocally` 新接缝；守卫行为为 finally 条件代码级事实）。
实现：`RpcHandleCommand.execute` finally 无条件终答守卫（`isDone() || cause!=null` 即 onDone(cause)，至多一次交由上下文 CAS）；`RpcInvokeCommand.handleResult` 应答判定抽为静态谓词 `shouldRespondLocally(mode, relay, relaySucceeded, hasBody)`——中继失败（含"非中继隧道"降级）现回错误码而非悬挂/抛穿；移交成功仍静默（响应归中继路径）。
验证：net/netty4 全绿。

## 组 20｜异步命令超时兜底（Wave-C，2026-09-29）

`AsyncCommandTimeoutTest` 2 用例（80ms 兜底触发/时限内完成锚），修复后全绿。红归因：单文件回退 `RpcInvokeContext`（还原 3000 硬编码）后超时用例转红——配置链未通即等价修复前"死字段"行为；全量 stash 因跨组测试编译依赖不可行，已按最小面复证。
实现：`SerialCommandExecutorSetting.commandTimeout`（默认 3000，starter 属性前缀直通）；`CommandExecutorFactory.getCommandTimeoutMillis()` default；`DefaultCommandExecutorFactory` 覆写读 setting；`RpcInvokeContext` 构造解析（链路缺省回落 3000）；`MessageCommandPromise.remainingTimeoutMillis()`；`RpcInvokeCommand` 异步分支 `orTimeout`（同一 CF 终结，回调回会话 worker 推进串行队列）；`resultOfException` 增 CompletionException 解包 + TimeoutException→REQUEST_TIMEOUT。
验证：net/netty4/starter 全绿。

## 组 21｜会话清理任务可终结（Wave-C，2026-09-29）

`SessionKeeperLifecycleTest` 3 用例修复后全绿；红归因：stash 回退为**编译缺失红**（`shutdownScan`/静默轮次包装为新增接缝；修复前句柄即丢弃、无任何取消路径，孤儿 keeper 由"computeIfAbsent 外创建"代码事实确证）。
实现：`AutoCloseableSessionKeeper` 保存 `scanFuture` + `shutdownScan()`（幂等取消）+ `clearInvalidedSessionQuietly` 外层 `catch(Throwable)` 留痕；`CommonSessionKeeperManager` 实现 `AppClosed.onClosed()` 统一取消并清表、`loadKeeper` 创建移入 `computeIfAbsent` 原子域（ON_CREATE 仅胜者触发）。
验证：net/netty4/starter 全绿。

## 组 22｜RPC 注册表原子发布与收缩（Wave-C，2026-09-29）

`RpcRegistryConsistencyTest`（5）+ `RpcForwardHeaderSetterTest`（2）：stash 复证修复前 **5/7 红**（跃迁反向、僵尸驻留、陌生移除 NPE、绑定恒真/NPE、setter 自引用）；并发压力用例修复前侥幸绿（竞态窗口依赖交错），保留为防回归压力锚——如实记录其红面依赖 churn 规模。
实现：`RpcServiceNodeSet` 增 publishLock（重建-赋值原子发布）、removeSession 走 get+空节点 CAS 摘除、缺 token 事件留痕忽略、anyGet 降级 WARN 留痕、退役无消费者 version 计数器；`RpcServiceNode` 跃迁条件修正（空→非空/非空→空）+ `isEmpty()` 写锁内判定；`ContactNodeSet.keeper` volatile + bind 先到先得告警 + `getAccess` 未绑定返回空 + `isActive` 按真实绑定；`BaseRpcServicerManager.findInvokeNodeSet` 改读全量表；`RpcForwardHeader` 双 setter 使用参数（删死队列行）、builder 误接线修正。
**测试环境教训**：跨测试类同名枚举常量（两处探针均名 PROBE）在共享 worker JVM 中注册冲突——探针枚举常量名必须全局唯一。
验证：`net`(142)/netty4/starter 全绿。

## 组 23｜引导器停机·重开·绑定可感知（Wave-C，2026-09-29）

`BindAddressParseTest`（net）2 红+1 锚；`NetAutoServiceRegisterTest` 新增门槛用例（starter 3 用例 stash 复证全红——含两条既有用例因新桩依赖）；`ClientGuideReopenTest`（netty4）1 红（哨兵法复证；首版测试因未预置 bootstrap 而空洞通过，修正为 `GuideLifecycleTest` 同款哨兵模式后红→绿——教训：缓存失效类断言必须预置脏状态）。
实现：server 侧重绑/清缓存/真实 isBound 已由此前变更落地（核对确认，不重复劳动）；本组补 **client close 清空 bootstrap**、**服务发现注册门槛**（`guide.isBound()` 为假跳过并 ERROR）、**地址解析双侧界校验**（host:port 段数 + 端口域，IPv6 字面量显式拒绝）。
验证：三模块全绿。

## 组 24｜nacos 注册/订阅生命周期与装配条件（Wave-C，2026-09-29）

`BaseServeNodeClientLifecycleTest`（3）+ `BootAssemblySemanticsTest`（3）+ `ApmConditionalBackoffTest`（1）：stash 复证修复前 **4/4 目标用例红**（复活/隔离/覆盖/开关/NPE/类条件），其余为锚。
实现：`BaseServeNodeClient` holder 表实例化、`onClosed` stop+clear、重订阅复活 `start()`、stop 失败不再反向 schedule、LOGGER 归属修正；`NacosServeNodeClient` 订阅异常上抛（不再 printStackTrace 吞）；`NetAutoServiceRegister` 增未监听跳过；`NacosEventListener` restart 去抖（5s 窗口 CAS 胜者）；`ImportRpcServiceDefinitionRegistrar` 解析期 getBean → bean 创建期延迟；executor 双路注册收口（registrar containsBeanDefinition 先到先得 + @Bean @ConditionalOnMissingBean）；`SpringRelayServeClusterSetting.isDiscovery` 开关自洽；watcher 按集群 `putIfAbsent` 去重 + onClosed clear；三个 BootstrapProperties 空值守护；`@ConditionalOnClass(ContextManager)`；demo 移除 `allow-bean-definition-overriding`。
**覆盖缺口（如实记录）**：registrar 惰性化与 watcher 去重为结构改动，单元断言不可达（依赖容器生命周期），由 29.2 demo 三进程冒烟兜底；Apm 条件测试用反射 simple-name 判定规避 compileOnly 类引用。
验证：net/netty4/starter/apm/demo 编译+测试全绿。

## 组 25｜集群视图一致性与终结（Wave-C，2026-09-29）

`ClusterViewConsistencyTest` 2 用例：修复后全绿；stash 复证修复前幂等注册用例**红**（返回新实例）。健康翻转视图用例修复前后同绿——`healthy` volatile 化的收益是多线程可见性（单线程不可测），代码级事实登记。
实现：`BaseRemoteServeCluster.registerInstance` 冲突返回既有实例（幂等契约）；`BaseRelayServeInstance`：`healthy`→volatile、`relayLinkMap`→final+clear（消除 close 重指派可见性洞）、快照化终结；`NettyClientRelayExplorer`：三入口按 `serveName` 统一键空间、`addInstance` 先注册判重后启动连接器（孤儿连接器防线）、心跳 `ScheduledFuture` 句柄收集 + `onClosed` 取消。
**覆盖缺口（登记）**：RENEW_NODE（launchTime 漂移）仍无重建分支（P3 原级，未入本组范围）；双名匹配/心跳取消为 explorer 装配级行为，由 29.2 demo 冒烟兜底。

## 组 26｜APM span 闭合与标签唯一（Wave-C，2026-09-29）

`ApmTagUniquenessTest`（反射采集 AbstractTag id，agent-core 补 testImplementation）：修复前**红**（102/108 冲突），修复后绿。
实现：TARGET→109、FORWARD→110、删除未用 START_TIME/END_TIME；`onBeforeInvoke` EXIT 分支单结束（不再 prepareForAsync+stopSpan 双管）；`stopAsyncSpans` 统一清理 TRACING_SNAPSHOT 属性；`onSuspend` 只停本框架登记 span（RPC/INVOKE 属性句柄，二次停止吞并留痕），不再 `while(isActive)` 连坐无关插件 span。
**覆盖缺口（登记）**：span 生命周期行为依赖 agent 静态上下文（ContextManager），无 mockito-inline 静态桩条件下不做运行时断言，由 29.2 demo（可选挂 agent）兜底。

## 组 27｜心跳共享态与 jprotobuf 并发裁决（Wave-C，2026-09-29）

`TickMessageIsolationTest`（netty4）：stash 复证修复前 **2 红**（PING/PONG 进入 removeAllHeaders 可变面）+ 1 锚绿；`ConcurrentCodecRoundTripTest`（jprotobuf）**裁决通过**——共享 codec 实例 8 线程×300 次并发往返零失败，前期 [疑似] 线程安全项结案；`ProtoExErrorPathTest`：垃圾标签/截断负载双双快速失败（补齐此前全注释模块的错误路径零覆盖）。
实现：`NettyMessageHandler.channelRead` 对 PING/PONG 短路 ignoreHeaders（当前 TickMessageHead 恰为 no-op，属前瞻卫生修复——红面由严格 mock 的 never() 断言坐实）；jprotobuf 探针需 `@TypeProtobuf(9001)` 显式类型码方可装载 scheme（实施期取证）。

## 组 28｜死抽象删除（BREAKING，2026-09-29）

`git rm -f` 删除 18 个零引用文件（删除前逐项复核，仅 `SessionPushOption` 带 IDE 式 final 修饰本地改动、经确认后随删）；9 处引用点清理：`asyncConnect` 字段/常量、`TunnelEvents` receiveEvent 全链、`TunnelEventWatches.receiveWatch`、`NettyMessageHandler` bearer 分支、`NetLogger.WatcherAttribute+trace+traceDone`、两处注释残骸、`NettyRelayPacketHandler` LINK_OPEN 改经 `packetType.handle(processor, transport, packet)` 统一分派（invoker 机制由形同虚设转为真实路径）。
验证方式即编译面：`:tny-game-net/-netty4/starter/starter-apm:test` 全绿 + `demo/net-test/rpc compileJava` 绿。删除清单见 release-note。

## 组 29｜集成收口（2026-09-30）

### 29.1 映射表
`scenarios-mapping.md` 已建立：10 个 capability × 全 Requirement/Scenario ↔ 测试类#方法；无测试覆盖项显式标注"冒烟/代码级"（registrar 惰性化、心跳句柄取消、无上限重试留痕、RENEW_NODE 语义）。

### 29.2 demo 冒烟（部分执行，如实记录）
- 已完成：`application.yml` 移除 `allow-bean-definition-overriding`；`application-relay-gateway-server.yml` 死键 `message-handler` 清除（真实项为 `message-handler-factory`，relay 链路由 relay pack 管线承接，删除不改行为）；`:tny-game-net-demo:compileJava/build` 绿。
- 三进程起停（网关/业务服/客户端真实互通）需 Nacos 注册中心与网络环境，本工作机不可用——移交发布前验证清单（release-note 附）。

### 29.3 net-bench 正式 A/B（同参数 `-f 2 -wi 3 -i 5 -w 500ms -r 1s`，JDK21 toolchain / macOS aarch64）

| 基准 | 基线（勘误后） | 修复后 | Δ | 判定 |
|---|---|---|---|---|
| queue.addMessage cap=0 | 734.6M | 721.3M | -1.8% | 噪声区 |
| queue.addMessage cap=64 | 77.4M | 74.9M | -3.2% | 噪声区（MessageQueue 本体零改动） |
| queue.filteredRead 64 | 35.6M | 44.2M | +24% | 正向噪声 |
| codec.encodeThenDecode verify=false | 2049k | 2203k ±814k | +7.5% | **无回退**（分配前校验两分支成本淹没于噪声） |
| codec.encodeThenDecode verify=true | 969k | 1139k ±47k | +17.6% | 无回退 |
| respond.putAndPoll 1k/10k | 18.36M / ~17.95M | 18.73M / 18.35M | ≈+2% | 无回退 |
| respond.pollMiss 1k/10k | 91.3M / 84.3M | 96.4M / 87.5M | +5% | 无回退 |

**结论**：热路径防线（组 1/2/18/19/20 引入的校验、守卫、兜底）未产生可测量回退；原始数据 `/tmp/net-bench-postfix.log`。

### 29.4 release-note
已成文（成对升级、配置语义变化、BREAKING 删除清单、行为收紧声明、5.8 恢复路径）。

### 29.5 全量构建与校验
`./gradlew clean build`（根级，排除 bench 模块）+ `openspec validate --strict` 结果见执行记录（本组收尾）。
