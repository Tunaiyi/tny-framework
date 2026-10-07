# 红灯基线（任务 1.2，2026-09-29，daemon=Corretto 21）

| 测试 | 结果 | 定性 |
|---|---|---|
| channelActivePropagatesActiveEvent | **红** | 误广播 registered + 传播被日志条件吞 |
| writePassesThroughUnknownObject | **红** | 非包对象零调用（promise 悬挂） |
| verificationRejectionReleasesPacket | **红** | 校验拒绝分支无 release（mock 零交互实证） |
| unknownExceptionReleasesPacket | 绿 | Throwable 分支现状正确（防回归） |

## 过程教训（记录在案）

首版 ③④ 曾出现"假绿"：链路在 `relayMonitor.onReadPacket`（mock link 的 `getService()` 返回
null → `ofRelay(null)` NPE）就已抛错落入 Throwable 分支，release 碰巧被计到。
处置：为 ③④ 各加"路径证明"断言（`verify(processor).onTunnelRelay(...)`——异常必须来自
处理器消费点）+ stub `getService()` 非 null。此后矩阵才是可信的。
**教训：对协作链路的 mock 断言，必须同时验证"路径确实经过预期节点"。**

## R1 审计（任务 2.5 预跑结论见实现后补记）

## R1 审计结论（任务 2.5）

`checkLink`（BaseRelayPacketProcessor:86）与 explorer 分配失败（BaseClientRelayExplorer:127）
等结果码类异常的抛出点均位于包体消费之前（处理器各方法首行校验）；处理器无"消费后再抛
结果码异常"路径 → handler 终结释放纪律成立。既有 Throwable 分支的转发中途失败双释放风险
为历史遗留（非本变更引入），登记至 executor/relay 后续变更评估。
