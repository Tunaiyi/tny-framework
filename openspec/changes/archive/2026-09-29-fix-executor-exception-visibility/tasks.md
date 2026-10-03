# Tasks

## 1. 测试先行（红灯基线）

- [x] 1.1 预研并落定日志断言手段：确认 `:tny-game-net` / `:tny-game-net-netty4` 测试运行时 slf4j 绑定；如可捕获则建测试工具 `LogCapture`（log4j2 内存 Appender 挂目标 logger，@AfterEach 卸载）；不可捕获按 design R3 降级（受影响用例 1.2/1.4 改人工验收清单并在此披露）
- [x] 1.2 `SerialCommandExecutorFailureTest`（net）：mock `RpcCommand.execute(worker)` 返回异常完成的 future → 断言 EXECUTOR logger 出现含命令名的 error 记录；成功 future → 无 error；随后再提交一命令正常执行（规格三场景）。当前无消费者，第一断言应红
- [x] 1.3 `RelayHandlerBehaviorTest` 式复用：`NettyMessageHandlerDiscardTest`（netty4）——`channelRead` 喂消息且 TUNNEL attr 置 null → 断言 warn 留痕（当前零日志，应红）；就绪路径无丢弃日志（绿基线）
- [x] 1.4 `HandlerResultCodeReplyTest`（netty4）：~~断言死分支无应答（应红）~~ **修正为回归基线**：结果码应答经证实为既有行为，测试固化其存在（含 mock 回执链修复）；两例现状绿
- [x] 1.5 `MockNetTunnelCloseTest`（net-test 模块 test 源集，若无则建）：匿名会话 bind → `close()` 两次 + 断言会话进入 CLOSE、`onUnactivated` 至多一次生效、无 StackOverflowError（当前递归应红）
- [x] 1.6 运行新测试，红/绿矩阵记入变更目录 `red-baseline.md`

## 2. 实现修复

- [x] 2.1 `SerialCommandExecutor.executeCommand`：whenComplete 移出 debug 门（D1）——cause error 无条件、value 保留守卫
- [x] 2.2 `NettyMessageHandler.channelRead`：无 tunnel 分支补 warn（通道 remoteAddress + 消息标识），TODO 注释改写为指向观测后续项（D2）
- [x] 2.3 ~~移除恒假判断复活分类链~~ **撤销**（红灯实验推翻前提，见 red-baseline 修正节）
- [x] 2.4 `MockNetTunnel`：close/disconnect 幂等守卫 + 状态前置（D4）

## 3. 回归与收尾

- [x] 3.1 `./gradlew :tny-game-net:test :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全量绿（既有 85+ 与新用例）
- [x] 3.2 `./gradlew :tny-game-net-test:compileJava :tny-game-net-test:compileTestJava -Dorg.gradle.java.home=<corretto-21>`（桩修改的消费方编译回归）
- [x] 3.3 release-note：error 日志存量显形预期（R2）、结果码应答行为（R1）、MockNetTunnel 修复对下游测试的意义
