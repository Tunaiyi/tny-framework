# Proposal

## Why

`BaseNetTunnel.doReceive` 对"会话未绑定的通道收到消息"无任何防护（`session.isClosed()` 直接 NPE），且接收结构遗留 `while(true)` 重试死枝（`session.receive` 实际恒返回 true 或抛异常，false 重试路径不可达）。可达性如实标注：当前生产路径因 netty EventLoop 串行保证（读事件不插入 initChannel 执行中）而难以触发——本变更**不夸大 bug 现势性**，立的是合同完备性：接口面 `receive(NetMessage)` 是 public、客户端中继隧道等演化中该前提随时可能不再成立，防御应当显式而非依赖线程模型的巧合。顺带：首个变更从规格移除的"未绑定拒绝"需求以修正后的语义补回账本；`AbstractSessionKeeper.send2AllOnline` 名实不符的三选一决策材料一并产出（本变更不实施该行为变更）。

## What Changes

- `doReceive`：会话快照为 null 时以可诊断方式拒绝（warn + 返回 false），杜绝 NPE 形态；`while(true)` 死枝重构为线性单路径（行为不变——false 重试本不可达）。
- `net-tunnel` 账本补 ADDED 一条：会话未绑定的通道收到消息必须可诊断拒绝且不崩溃（修正版语义，含"不得静默丢弃"与既有 handler 层条款分层）。
- 决策材料：`send2AllOnline`（现不过滤离线，向关闭通道写出行为不可控）三选一分析（按名过滤/改名/文档化）写入变更目录，实施与否由后续独立变更承接。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `net-tunnel`：ADDED 一条（通道层未绑定拒绝——与已入账的 handler 层"丢弃留痕"条款分层互补）。

## Impact

- 代码：`tny-game-net/.../transport/BaseNetTunnel.java`（doReceive 单文件）。
- 行为变化：理论不可达路径从"NPE 被上层 catch 吞（channelRead 的 catch Throwable）"变为"warn + 显式拒绝"——可观察差异仅在日志形态。
- `while(true)` 重构为纯结构性变更（执行序等价），靠既有 TunnelSessionSnapshotTest + 新用例背书。
- 分层语义在规格中显式：handler 层（TUNNEL attr 未就绪）已条款化；本条覆盖 tunnel 层（session 未绑定）。
