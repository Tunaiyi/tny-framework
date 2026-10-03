# 红灯基线（任务 1.5，2026-09-29，daemon=Corretto 21）

| 测试 | 结果 | 定性 |
|---|---|---|
| MessageQueueResizeTest.enableFromDisabledState | **红**（NPE，addAll(null)） | A5 缺陷复现 |
| MessageQueueResizeTest.disableReleasesRetainedMessages | **红**（CircularFifoQueue(0) 容量异常） | resize(0) 语义缺失复现 |
| MessageQueueResizeTest.concurrentResizeAndAccessMustNotThrow | **红**（NPE 连锁捕获） | 并发 resize 崩溃复现 |
| MessageQueueResizeTest.shrinkKeepsMostRecent | 绿 | 现实现碰巧正确（防回归基线） |
| MessageQueueResizeTest.ringEvictionBoundsMemory | 绿 | 环形挤出正确（防回归） |
| SessionResendSafetyTest.loginTimeCacheEnablementMustNotBreakSession | **红**（登录链 NPE） | 真实触发链在会话层复现 |
| SessionResendSafetyTest.resendAfterCloseIsInert | 绿 | 关闭后 no-op 现实现已满足（防回归） |

## 附带发现（超出本变更范围，移交队列）

`tny-game-net-test` 的 **MockNetTunnel 测试桩自身存在缺陷**：close()→disconnect()
无条件回调 session.onUnactivated 且 SUSPEND 态无幂等守护，匿名会话场景触发
close↔onUnactivated 无限递归（StackOverflowError，堆栈：MockNetTunnel:131/196 →
BaseNetSession:508）。真实链路 BaseNetTunnel.close 有双检守护不受影响。
本变更测试改用 BaseNetTunnel 真实子类；MockNetTunnel 修复建议并入
`simplify-tunnel-unbound-rejection` 或独立小变更。
