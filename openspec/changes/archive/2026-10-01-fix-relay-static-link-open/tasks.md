# Tasks

> design 省略声明：单模块两类状态机修正，不跨模块、无新增外部依赖、不涉安全面与发布物报文格式——符合本仓 design 条件约定，取舍记录于本文件与 verification.md。

## 1. 复现先行（specs"客户端中继链路建立握手不被未开状态吞没"先红）

- [x] 1.1 `tny-game-net/src/test` 新增 `BaseRelayLinkHandshakeTest`（单元，mock `RelayTransport`）：两用例——①status=INIT 且 transport active 时 `auth()` 的 LINK_OPEN 必须抵达 `transport.write`；②status=CLOSED/终结时提交包裹仍丢弃、载荷恰好释放一次、回执以断开异常完成、留痕含链路状态。验证：用例①红（现状被 `canForward()` 吞没）、用例②绿（既有契约基线）
- [x] 1.2 端到端复现锚确认（不新增）：`add-starter-net-integration-tests` 的 `tny-game-integration-test:...RelayLoginScenarioIT` 当前红（"链路已终结，丢弃转发包 [LINK_OPEN]"→玩家"无可用连接"），记录其失败输出到本变更 verification.md 作红基线。验证：该 IT 复跑仍红且错误形态一致

## 2. 修复实现

- [x] 2.1 `BaseRelayLink`：写出判定增加握手定向放行——`packet 类型为 LINK_OPEN && status == INIT && transport 连接就绪` 时直达 `transport.write`（不放宽其它包型在 INIT 的丢弃）；`dropPacket` 留痕文本附实际 `status`，区分"已终结"与"未就绪被误杀"两类历史形态。验证：1.1 两用例全绿
- [x] 2.2 链路状态机闭环确认：LINK_OPEN 发出后收到 LINK_OPENED → `BaseRelayPacketProcessor:41` 的 `link.open()`（INIT→OPEN）与心跳/事件通知既有路径不变；握手在途时链路被关 → 既有终结释放纪律覆盖该窗口（用 1.1 用例②的变体钉住：INIT+已终结竞态下补发的握手仍走 drop）。验证：新增变体用例绿

## 3. 网关回程断链（原假设经三探针+工作流仲裁修正为装配面根因；specs 需求三判定为"既有契约已满足"）

- [x] 3.1 定位取证（终稿，覆盖两段被推翻的中间结论——双释放/完成权假说均伪）：④真根因＝**网关接入通道 `channel.message-handler-factory` 未接线**——decoder 的 controllerRelayStrategy 只打 relay 戳，接力转发依赖 `NettyRelayMessageHandler`（工厂 bean 已存在于 RelayClientAutoConfiguration:94-95），demo yml:29 历史死键误判（`message-handler:` 键名写错被审计删除后注释"无需覆写"）致透明中继面在 demo/所有既有配置中从未被选中：表外协议在网关本地 dispatch 回 205。三读者+反驳工作流（holds 判定+两处措辞修正）定案；次因＝测试 JVM 缺 demo DTO 的 TypeProtobuf scheme 装载（NPE 弃包）。**结论：框架行为无缺陷，需求三的两 Scenario 由既有实现满足**——`shouldRespondLocally` 四分支已被 CommandCompletionGuardTest:101-107 钉住（含"中继成功响应归属中继路径""中继失败本地兜底"），端到端由剧本绿覆盖
- [x] 3.2 测试先行替代验证：以剧本红→绿全程为行为锚（装配修正前 205/超时、修正后全绿）；单元面无新增缺口（101-107 已覆盖需求三语义），不强造重复用例——按 specs 需求文本逐 Scenario 对照现有测试矩阵记录于 verification.md
- [x] 3.3 装配面修复落地：`it-relay-gateway.yml` 网关接入通道补 `message-handler-factory: relayMessageHandlerFactory` + 剧本 static 块 `loadScheme(LoginDTO/PlayerDTO)`。验证：`RelayLoginScenarioIT` 全绿（登录→透明中继 PLAYER$ADD/GET→Mongo 落库可查）

## 5. 回归与联动

- [x] 5.1 `./gradlew :tny-game-net:test :tny-game-net-netty4:test` 全绿（既有 relay 单测：处理器分派、终结释放、链路摘除等契约不回退）；`:tny-game-net:integrationTest` 哨兵不受影响
- [x] 5.2 全 IT 无回归：`-PincludeDocker` 档全模块跑（`MissingAssemblyIT` 及其余 IT）。验证结果记入本变更 verification.md
- [x] 5.3 交接确认：`add-starter-net-integration-tests` 解除阻塞后回到其任务 3.4/3.5 与组 4 收尾（剧本连跑 3 次稳定性由其验证任务承接，不在本变更重复）
