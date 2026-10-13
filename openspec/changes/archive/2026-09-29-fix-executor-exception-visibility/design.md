# Design

## Context

动机见 proposal.md - Why。关键事实（本会话核实）：

- `SerialCommandExecutor.executeCommand:55-67`：`whenComplete` 整体在 `LOG_NET.isDebugEnabled()` 内——debug 关闭时 future 无消费者，失败静默。
- `NettyMessageHandler.channelRead:114-118`：`tunnel == null` 分支只有 `// TODO rpcMonitor 处理无 tunnel 情况`，零日志。
- 两个 handler 的 `exceptionCaught` 判断 `this == ctx.pipeline().last()`：~~前期判为恒假死代码~~ **红灯实验推翻**——netty `DefaultChannelPipeline.last()` 返回业务尾 handler（head/tail 哨兵不计入），单业务 handler 管线判断为真、分类链与结果码应答工作正常。本项撤销，范围收缩。
- `MockNetTunnel.close():195-198`：`disconnect()`（内部无条件 `session.onUnactivated`）先于 `state=CLOSED` 赋值——匿名会话 close→onUnactivated→close 递归无守卫（前变更实测 StackOverflow）。

## Goals / Non-Goals

**Goals:** 三处失败可见性修复（executor 失败留痕 / 丢弃留痕 / 桩幂等）+ 一项行为回归固化（结果码应答）。
**Non-Goals:** 不改命令执行模型（串行性/队列结构）；不改异常传播终点（通道仍关闭）；无 tunnel 的"拒绝语义升级"（complete exceptionally 等）留给队列中 `simplify-tunnel-unbound-rejection`——本变更只保证**可观察丢弃**；不新增 rpcMonitor 对无 tunnel 的埋点（TODO 的完整实现属观测系统主题）。

## Decisions

**D1：`executeCommand` 的 whenComplete 无条件挂载**，回调内 `cause != null → LOG_NET.error(命令名, cause)`；成功值 `isDebugEnabled` 守卫。
[P13：失败路径开销只在真失败时发生，静默丢失的运维成本远超一条 error 日志。] 否决：整体保留 debug 门内——被否，等于维持"生产零可见性"。

**D2：无 tunnel 丢弃日志用 warn 且不限流。**
[丢弃窗口仅存在于 initChannel 绑定失败至通道关闭之间（毫秒~秒级）；持续高频意味着攻击或绑定缺陷，日志本身正是需要暴露的证据。] 否决：限流/采样——被否，P10，窗口性事件不适用稳态假设。

**D3（撤销）：** 原计划移除恒假判断复活分类链——实验证明判断并非常假、应答已在线，无修复必要。转化为回归测试：`HandlerResultCodeReplyTest` 固化"业务尾 handler 场景下结果码应答存在 + 普通异常无应答"。已知脆弱性登记：若未来管线在 handler 之后再挂业务 handler，应答将静默失效——移交观测主题评估（不改判断式本身，避免无需求支撑的重构）。

**D4：`MockNetTunnel` 对齐真实隧道关序**：`close()` 先判 CLOSED 幂等返回 → `state=CLOSED` 前置 → 再 `disconnect()`；`disconnect()` 对 SUSPEND/CLOSED 早退。[与 `BaseNetTunnel.close/disconnect` 的"先置态后回调 + 锁外通知"模式同构（模式卷先例：状态机双检）。]

## Risks / Trade-offs

- **R1（撤销）**：结果码应答经证实为既有行为，无对端可见性变化；登记的"非业务尾则静默失效"脆弱性移交观测主题。
- **R2**：error 日志暴露此前静默失败存量，上线初期错误日志量可能上升——属真实现形，不回退。
- **R3**：日志断言测试依赖测试运行时的 slf4j 绑定可捕获性——任务 1.1 预研定案，不可捕获则相应两例降级为"人工验收 + 代码评审"并如实披露（前例：guide E2E 降级）。

## Open Questions

无（R3 有明确 fallback 路径）。
