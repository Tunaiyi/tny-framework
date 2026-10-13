# 红灯基线（任务 1.5，2026-09-29，daemon=Corretto 21）

5 红 1 绿，全部"应红"确定性命中：

| 测试 | 结果 | 复现缺陷 |
|---|---|---|
| legitimatePacketPassesVerification | **红** | verify 条件反：合法包被"verify failed"击落 |
| tamperedPacketIsRejected | **红** | 篡改包反而放行 |
| oversizeMessageFailsLocallyOnEncode | **红** | 超限仅 warn 照发 |
| decodeWithoutReadyTunnelFailsDiagnostically | **红**（NPE 而非 NetCodecException） | 隧道无防护 |
| encodeExceptionIsNotPropagatedByHandler | **红** | 编码异常当前传播→关闭通道 |
| decodeExceptionStillPropagates | 绿 | 解码异常传播=现状=目标（D3 保持） |

## 规划修正（design 已同步）

原 D2"新异常 extends NetCodecException"实施时不可行——该类构造器 private（等同 final）。
修正为 extends NetException（复用既有 ENCODE_ERROR 码），netty4 实现模块自建类型，
net 抽象模块公共面零改动（P1/P11 优先）。测试断言同步用具体类型。

## 实施期新发现（红灯矩阵修正）

verify 路径存在**第二层缺陷**：`NetPacketV1Encoder:87` 用 `alloc().buffer()`（direct）分配
bodyBuffer，第 93 行 `array()` 直接 `UnsupportedOperationException`——启用校验时编码器自身
即崩（解码器用 heapBuffer 证明为复制疏漏）。任务 2.2 扩为一行：`buffer()`→`heapBuffer()`。
该发现强化"verify 从未端到端工作过、零兼容负担"的判断。
