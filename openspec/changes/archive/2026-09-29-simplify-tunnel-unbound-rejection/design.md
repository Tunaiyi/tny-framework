# Design

## Context

现状（读证）：`doReceive:144-156` 无 null 防护 + `while(true)` 死枝（`BaseNetSession.receive` 路径恒 true/throw，false 重试不可达）；`channelRead`（handler 层）已有 warn（executor-visibility 入账条款）；生产可达性受 EventLoop 串行保护（proposal 已如实标注"理论难触发"）。`AbstractSessionKeeper.send2AllOnline:120-123` 遍历全部会话不判在线。

## Goals / Non-Goals

**Goals:** 通道层合同补全；接收结构线性化；send2AllOnline 决策材料。
**Non-Goals:** 不实施 send2AllOnline 行为变更（材料交后续变更决策）；不动 handler 层既有 warn 逻辑（分层互补）。

## Decisions

**D1：守卫置于快照读取后、上下文创建前**——`session == null → warn + return false`，先于 `RpcTransactionContext.createEnter`（避免为注定拒绝的消息构造上下文与 monitor 计数——拒绝不产生统计污染）。

**D2：while(true) 线性化**：`if (session.isClosed()) return false; return session.receive(rpcContext);`——执行序与现状等价（false 重试路径不可达已被读码证实），结构消除"看似可能自旋"的误读风险。TunnelSessionSnapshotTest 与既有 receive 行为测试背书。

**D3：send2AllOnline 决策材料（不实施，写入变更目录 `send2AllOnline-decision.md`）**，三选一各附影响：
- A 按 `isOnline()` 过滤（名实一致；行为 BREAKING：现在"发给离线"的调用方若依赖其连接恢复后收到——实际不可能收到，write 到关闭通道即失败/异常，故 A 的真实语义是"从不可控失败变为静默跳过"）；
- B 改名 `send2All`（无行为变更但公共 API BREAKING——抽象模块 public 面，违反 P11 成本高于 A）；
- C 维持+文档（零风险但把名实债留给后人）。
建议倾向 A（行为从"不可控"变"可预测"，规格可固化），最终由后续变更提案决策。

## Risks / Trade-offs

- **R1**：D2 线性化若我对 `receive 恒 true` 的判断有漏（某自定义 session 实现返回 false 期望重试？）——全仓 `implements NetSession/receive` 实现清单 apply 时复核（内置 session 均已核实恒 true/throw）；外部实现返回 false 时新旧行为差异=重试与否，旧语义"同 context 无限自旋"本就病态，线性化后单次拒绝更安全。

## Open Questions

无。
