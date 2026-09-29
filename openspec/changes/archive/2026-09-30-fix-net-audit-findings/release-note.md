# Release Note — fix-net-audit-findings

（随组推进增量补记；最终汇总见任务 29.4）

## Wave-B：链路切换包类型修正【成对升级要求】

**变更**：`TunnelSwitchLinkPacket` 三参构造器（`RelayPacketType.TUNNEL_SWITCH_LINK` 工厂唯一入口）此前将类型硬编码为 `TUNNEL_CONNECT`——链路上所有"隧道换绑链路"通知被对端解释为"隧道建立"，业务服销毁既有会话（登录态/接入身份清空，玩家掉线重建）。修复后切换包携带正确类型，业务服进入迁移分支，会话保留。

**部署约束**：
- **网关与业务服必须同版本成对发布**。混跑窗口行为：
  - 新网关 → 旧业务服：切换包按 `TUNNEL_SWITCH_LINK`（选项字节 18 段）到达，旧侧解码器对未知类型走 `causeDecodeError` 断链——**旧侧未升级时新网关的链路故障转移会转为断链处置**（相对旧版"静默销毁会话"是可观察差异，均非正常路径，但需在发布顺序上避免混跑：先发业务服、后发网关；同版本窗口内两向均为已知类型）。
  - 旧网关 → 新业务服：旧侧仍发伪装连接包，新侧按连接语义处置（维持旧缺陷行为），业务日志出现 `TunnelConnect` 且 ip/port 为 0.0.0.0:0 的形态可作为混跑识别线索。
- 回滚=两侧成对回滚，禁止单端。

**发布顺序建议**：业务服全量 → 观察 1 个发布周期 → 网关全量。

## 行为收紧声明（Wave-A，客户端/运维可观察）

- 作用域隔离修复后，此前恒放行的跨 scope 协议调用开始按"协议不存在"应答。
- 鉴权全局/协议级校验器开始真实生效；未注册任何校验器的需登录协议仍按 NO_LOGIN 拒绝。
- 顶号判定（凭证新旧）真实生效：重放的旧凭证接管将被拒绝。
- 编码失败、帧内长度越界、参数校验器缺配置等此前"静默/悬挂/崩溃"路径统一变为显式错误应答或启动失败。

## BREAKING：5.7.x 删除的公共类型清单（组 28）

以下类型经全仓（含 demo/test/rpc/starter）零引用复核后物理删除；下游若曾引用，恢复路径＝下个 5.8.x 版本临时恢复并标 `@Deprecated(forRemoval=true)`，随 6.0 正式移除：

- `com.tny.game.net.application.ClientConnectFuture` / `ClientConnectPromise` / `ConnectCallback` / `ConnectCallbackStatus`（死连接回调抽象，`connected()` 语义反转）
- `com.tny.game.net.application.NetAppContextHolder`（恒 null 的死全局上下文）
- `com.tny.game.net.application.ClientConnectorSetting.asyncConnect` 配置项与 `NetConfigs.CONNECT_ASYNC_DEFAULT_VALUE`（无消费点的摆设开关；YAML 残留键将被忽略，不再需要清理）
- `com.tny.game.net.transport.TunnelEvents.receiveEvent` 链 / `TunnelEventWatches.receiveWatch()` / `transport.listener.TunnelReceiveListener`（永不触发的死事件契约——`TunnelEventWatches` 实现者需移除 `receiveWatch()` 覆写）
- `com.tny.game.net.session.SessionPushOption`
- `com.tny.game.net.relay.packet.LinkMessagePacket`；`relay.cluster.watch.ServeInstanceChange` / `ServeNodeOnlineListener` / `ServeNodeOfflineListener`（空壳）
- `com.tny.game.net.netty4.channel.ChanelTaskFuture`（拼写事故 + 零使用）
- `com.tny.game.net.netty4.network.NettyMessageBearer`（异步承载设计从未接通）
- `com.tny.game.net.netty4.network.telnet.{TelnetServer,TelnetHandler,TelnetSession}`（注释空壳）
- `com.tny.game.net.netty4.relay.codec.{DisconnectPacketEncoder,DisconnectedPacketEncoder}`（空类）
- `com.tny.game.net.application.NetLogger` 的 `trace/traceDone(WatcherAttribute,…)` 公开静态方法（不可达入口；`ProcessWatcher` 常量保留）

行为修正型变更（不删类型但改语义，升级需知）：编码失败/帧长越界/写提交被拒从"静默或悬挂"变为"错误回执+断连按分级"；异步命令新增超时兜底（`tny.net.command.executor.serial.command-timeout`，缺省 3000ms）；`EmptyImmutableMap` 首写改双检唯一换表；`NettyRelayPacketHandler` LINK_OPEN 统一分派。
