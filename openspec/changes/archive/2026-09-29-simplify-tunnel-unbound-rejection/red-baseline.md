# 红灯基线（2026-09-29）

| 用例 | tasks 原预期 | 实测 | 说明 |
|---|---|---|---|
| ① unboundReceiveIsRejectedWithoutCrash | 红 | **红**（NPE） | 缺陷复现 |
| ② channelUnharmedAfterRejection | 绿 | **红**（前置拒绝即 NPE） | tasks 预估口径错——②首行断言即未绑定拒绝，当前必炸；已如实修正（提案期自纠：矩阵按现实而非愿望） |
| ③（对照既有）TunnelSessionSnapshotTest | 绿 | 绿 | 绑定路径回归锚 |

修复后 ①② 转绿、③ 保持（见 3.2 全量记录）。

## 2.3 receive 实现复核结论（design R1）
全仓实现清单：BaseNetSession.receive（恒 true 或抛，读码核实）、MockNetSession（恒 true）、
MockNetTunnel（隧道层非 session）。**无任何返回 false 的实现**——线性化行为等价成立。
