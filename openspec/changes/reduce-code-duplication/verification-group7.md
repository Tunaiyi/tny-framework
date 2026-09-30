# Verification 组 7 — net 测试脚手架下沉（tasks 7.1–7.3，design D6）

日期：2026-10-01　分支：5.7.x　范围：仅 `tny-game-net/src/test/java/`，零产品源码触达；`tny-game-net-test` 模块零触碰（禁令遵守）。

## 模块测试命令 + 结果行

重构前钉桩（现状绿确认，rule 4 前置）：

- `./gradlew :tny-game-net:test`（未改动时）→ BUILD SUCCESSFUL（test 任务 UP-TO-DATE，前次运行绿且输入未变）
- `./gradlew :tny-game-net:test --rerun --tests "...RequestResponseClosureTest" --tests "...TunnelEventOrderTest" --tests "...SessionOfflineOnceTest" --tests "...SessionResendSafetyTest" --tests "...TunnelUnboundRejectionTest" --tests "...ResendFilterConsistencyTest" --tests "...SessionBroadcastIntentTest" --tests "...MockNetTunnelCloseTest"` → BUILD SUCCESSFUL（8 类实跑全过）

重构后验证（task 7.3）：

- `./gradlew :tny-game-net:compileTestJava` → BUILD SUCCESSFUL（仅 SessionBroadcastIntentTest 既有 deprecation 提示，来自被测 `send2AllOnline` 桥接，非本次引入）
- `./gradlew :tny-game-net:test` → BUILD SUCCESSFUL
- 报告汇总（test-results/test，51 个结果文件）：TOTAL=167 FAIL=0 ERR=0 SKIP=0
- 8 目标类逐类计数（重构前后完全一致）：RequestResponseClosureTest 2 / TunnelEventOrderTest 2 / SessionOfflineOnceTest 3 / SessionResendSafetyTest 2 / TunnelUnboundRejectionTest 2 / ResendFilterConsistencyTest 1 / SessionBroadcastIntentTest 5 / MockNetTunnelCloseTest 1，全部 failures=0 errors=0 skipped=0
- 确定性说明：改动区无时钟/随机依赖；`TunnelUnboundRejectionTest` 既有的 `System.currentTimeMillis()` 入参属消息 time 字段、未被断言，未触碰。

## 变更文件清单

新增（1 文件，115 行，Mulan PSL v2 头 9 行照抄，体内零内联 FQN）：

- `tny-game-net/src/test/java/com/tny/game/net/transport/TestTunnelFixture.java`
  - 落位理由（D6"照 RpcContextFixture 先例、同 test 源集、不设 java-test-fixtures"）：置于 `com.tny.game.net.transport`（test 源集，与产品同包是既有测试惯例）——硬约束是 `TunnelUnboundRejectionTest` 外部调用 `tunnel.resetSession(...)`（protected 桩），仅同包可编译；其余跨包消费方（session 包 5 文件）经 public 类 + 继承覆写 protected 钩子合法访问，已编译验证。
  - 能力面：resetSession/onOpen/onOpened/onClose/onClosed/onDisconnected/doDisconnect 标准桩 + isActive 按 `ActivePolicy` 三态参数化 + 双地址桩按端口参数 + write 双形态透传默认（恒 null / 原样 promise）。

改造（7 文件，diff：22 insertions / 378 deletions；计入 fixture 后净减 241 行，吻合任务 ~260 行估算）：

- `session/RequestResponseClosureTest.java` — RacingTunnel → fixture 子类（STATUS_OPEN, 7400/7401）；竞态 latch（inWrite/proceed）与自定义 `write(allocator,content)`（countDown→await→allocate→complete 时序逐字保留）留在子类；原空转 `write(message,promise)` 透传覆写由 fixture 默认吸收（行为同为返回 promise）。
- `transport/TunnelEventOrderTest.java` — 内嵌 TestTunnel 整删（其 write 双形态与 fixture 默认一致）；直接使用 fixture（STATUS_OPEN, 7300/7301）；ClientLikeTunnel → fixture 子类，`onDisconnected→this.close()` 竞态钩子原样保留；`recording` 泛型上界换 fixture。
- `session/SessionOfflineOnceTest.java` — TestTunnel(AtomicInteger) 瘦身为 fixture 子类（NOT_CLOSED, 7100/7101），双写计数体保留。
- `session/SessionResendSafetyTest.java` — TestTunnel(AtomicInteger) → fixture 子类（NOT_CLOSED, 7100/7000），双写计数体保留。
- `transport/TunnelUnboundRejectionTest.java` — 内嵌 TestTunnel 整删，2 个用例构造改 fixture（NOT_CLOSED, 7100/7000，write 透传语义与默认一致）；CountingSession 未动。
- `session/ResendFilterConsistencyTest.java` — CapturingTunnel → fixture 子类（STATUS_OPEN, 7500/7501）；directWrites 计数、factory（id=7）、自定义 `write(allocator,content)`（真实 allocate+complete）逐字保留。
- `session/SessionBroadcastIntentTest.java` — TestTunnel → fixture 子类（NEVER, 7100/7000）；`isActive=false` 的行尾注释迁至构造器注释；`CommonMessageFactory` allocate + isOpen 门控捕获逻辑逐字保留。

未改造：`session/MockNetTunnelCloseTest.java`（见遗留登记 1）。

## 公共签名冻结自查结论

`git status` 复核：本组改动全部位于 `tny-game-net/src/test/java/`，产品 main 源集零文件变更；fixture 与 ActivePolicy 仅在 test 源集，不随任何 jar 发布（未引入 java-test-fixtures），消费者可见类层级零变化。8 文件断言行零改动（diff 内 assert/verify/fail/DisplayName 模式 grep 为空）；被删覆写均有 fixture 默认逐字等价承接，个性化钩子以子类保留。

## 遗留登记（零行为变更铁律下未触碰项）

1. **MockNetTunnelCloseTest 与任务清单现状不符**：该文件并未内嵌同构 TestTunnel 变体——被测隧道是 `tny-game-net-test` 模块（本组禁触）的 `com.tny.game.net.transport.MockNetTunnel`，且测试对象恰是 MockNetTunnel 自身 close 幂等（类注释在册），改引 fixture 会改变被测目标，故文件零改动、仅登记。其内联 StubContext 与 SessionBroadcastIntentTest.StubContext 同型（execute 运行命令），不属 fixture 能力清单（隧道钩子），原样保留。
2. **第 9 处同构内嵌桩**：`transport/TunnelSessionSnapshotTest.java` 亦含 `private static final class TestTunnel extends BaseNetTunnel<NetSession>`，未列入本组 8 文件范围，未改造（fixture 命名 TestTunnelFixture 避开与嵌套类的名称遮蔽歧义）；下轮扩围候选。
3. **SessionContext 同构桩 6 处未下沉**（RequestResponseClosureTest.RacingContext、SessionOfflineOnce/SessionResendSafety 匿名 Fixture.context、ResendFilterConsistency 匿名、SessionBroadcastIntent/MockNetTunnelClose 的 StubContext）：execute 语义分叉两族（4 处空转 vs 2 处 `command.run()`），任务 7.1 能力面清单限定为隧道钩子，故登记为增量去重机会（若下轮下沉须以参数承载两种执行语义，禁止归并）。
4. **桩语义分叉现状保留**（即 D6"mock 语义分叉"成因，归并=行为变更）：
   - isActive 三态经 ActivePolicy 逐字保留：`getStatus()==OPEN` / `!isClosed()` / 恒 false；
   - 本地地址端口分叉：SessionOfflineOnce=7101，SessionResendSafety/TunnelUnboundRejection/SessionBroadcastIntent=7000（经构造参数原样保留；全仓 grep 确认无测试断言这些桩端口值，产品读地址路径仅在 relay link 链路、8 文件用例不经过）。
5. **SessionOfflineOnceTest 的 writes 计数器只写不读**（三个用例均未断言写出次数）——疑似复制自 SessionResendSafetyTest 的死字段，现状保留不删（删除属行为面外的桩结构变更，超本组"仅脚手架引用替换"授权）。
6. 工作区存在他案未跟踪文件 `tny-game-net/src/test/java/com/tny/game/net/relay/link/BaseRelayLinkHandshakeTest.java`（本组开工前已在），前后两轮运行均包含且均绿，本组不拥有、未改动。
