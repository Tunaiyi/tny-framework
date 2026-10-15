# 红灯基线（任务 2.2，2026-09-29）

6 用例矩阵与提案预期完全一致：

| 用例 | 预期 | 实测 | 定性 |
|---|---|---|---|
| ① disconnectedLinkIsUnregistered | 红 | **红** | 断线条目滞留复现 |
| ② closedLinkIsUnregisteredAndRebuildIsClean | 红 | **红** | 关闭条目滞留复现 |
| ③ tunnelReconnectReplacesAndClosesOldOnce | 绿 | 绿 | re-CONNECT 置换现状正确（固化） |
| ④ linkDisconnectDoesNotEvictTunnelEntries | 绿 | 绿 | 迁移保留现状正确（路障） |
| ⑤ explicitTunnelCloseRemovesEntry | 红 | **红** | 隧道只关不摘复现 |
| ⑥ repeatedTunnelCloseIsInert | 红 | **红** | 重复关闭二次动作复现 |

任务 1.0 核查结论：`linkMap` 全部查询点（acceptConnectTunnel/switchTunnelLink）的键均为
新链路自身 id，且 linkKey 每次连接新生成——无任何代码依赖"僵尸条目可查"，摘除安全。

## Post-archive 强化轮（同日，用户要求增加验证用例）

追加 4 用例（⑦复合键作用域 ⑧并发唯一胜者 ⑨断开→同键重建→旧链路迟到事件 ⑩保留→显式关闭全序列），
**⑨ 抓到原实现的真实缺陷并修复**：

- 缺陷：原摘除用 `linkMap.remove(id, stale)`——CHM 双参 remove 以 equals 比较值，而
  `BaseRelayLink.equals` 基于标识字段（service-instance-key），同键重建窗口内旧链路的
  迟到 onDisconnect 会**误摘新链路**（"值 CAS 防误摘"的假设被实测证伪——全程第 5 次假设修正）。
- 修复：`computeIfPresent` + 引用相等（`==`）判定，不受 equals 语义影响；注释固化教训。
- 十用例全绿（10/10）。原六用例测不出该序列——"红灯证明当时想到的，增强用例抓住没想到的"。
