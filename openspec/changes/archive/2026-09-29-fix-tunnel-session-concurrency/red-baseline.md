# 红灯基线记录（任务 1.5）

执行时间：2026-09-29。命令：`./gradlew :tny-game-common-lang:test --tests '*StampedLockAideTest'` 与
`:tny-game-net:test --tests '*TunnelSessionSnapshotTest' --tests '*VisibilityContractTest'`（修复前）。

## 结果

| 测试 | 结果 | 结论 |
|---|---|---|
| StampedLockAideTest.supplyFallsBackAndRerunsUnderWriteRace | **红**（expected 2, was 1） | 精确复现"validate 前置于执行、执行后不复验"缺陷 |
| StampedLockAideTest.callFallsBackAndRerunsUnderWriteRace | **红**（同型） | 同上 |
| StampedLockAideTest.runFallsBackAndRerunsUnderWriteRace | 绿 | 现 run 版实现"悲观一次+无条件再执行"碰巧计数为 2；修复后语义正确仍为 2，断言持续成立 |
| StampedLockAideTest.supplyRunsExactlyOnceWithoutConcurrentWrite | 绿 | 无竞态路径两版一致 |
| VisibilityContractTest（3 字段 volatile 断言） | **全红** | certificate / latelyHeartbeatTime / accessId 均非 volatile，确定性缺陷锁定 |
| TunnelSessionSnapshotTest.inFlightMessageBelongsToSnapshotSessionAcrossSwap | 绿 | 印证 design D1：现实现靠 doReceive 内单次 volatile 快照读"碰巧"满足该语义；本测试价值为防回归（若有人破坏快照读法即红） |

## 与规划产物的差异上报（待用户决策）

1. **net-tunnel 规格第三条"未绑定会话安全拒绝"超出本变更实施范围**：
   现实现（`BaseNetTunnel.doReceive`）对 null 会话直接 NPE；要满足该承诺需在 doReceive
   加空防护，并把现存 `while(true)` 重试结构一并处理（false 会导致忙自旋），
   与 design Non-Goal"不动死循环结构"冲突。tasks.md 无对应实现任务。
   对应占位测试已从 TunnelSessionSnapshotTest 移除。
   → 决策：用户批准方案 (a)，该 Requirement 已从差量删除，"未绑定会话安全拒绝"
     留待后续独立 change（建议名：add-tunnel-unbound-session-rejection）。
2. **1.2/1.3 行为级并发测试降级为声明式断言**：JMM 竞争在单测中修复前后均概率性通过，
   行为测试无法确定性"红"；已改为 volatile 修饰符断言（能确定性防回归）并合并为一个
   VisibilityContractTest 文件。spec 的 net-session/relay-link 行为承诺由声明式断言 +
   代码评审承载，与 spec 文本不冲突（spec 描述行为，测试选择最诚实的可执行形态）。

## 追加：实施中发现并修复的同族缺陷（已披露于会话，非静默吸收）

`StampedLockAide` 的 3 个 `*InWriteLock` 方法（run/supply/call）实现与名字不符：
并非写锁，而是与乐观读方法相同的"前置换位校验 + 悲观读降级"。其中
`supplyInWriteLock` 的唯一调用方是本变更规格核心场景涉及的 `BaseNetTunnel.bind()`
（会话切换写侧）。design D1/R1 明确要求"写侧切换在写锁下保护未来多步实现"，
该前提在此工具下不成立，故修复属于兑现已批准设计，而非新增范围：

- 修复前红灯：`writeLockMethodsProvideMutualExclusion` FAILED（"写锁"临界区两线程可交错，maxConcurrent=2）
- 修复：三方法改为真 `writeLock()`；全仓调用方审计见 audit.md
- tasks.md 2.2 描述已同步更新覆盖全部 7 个方法

