# Proposal

## Why

对齐前变更（align-message-queue-lock，已归档）选择 synchronized 时遗漏了虚拟线程维度：JDK 21 上虚拟线程在 synchronized 内竞争阻塞会 pin 住载体线程（JEP 491 于 JDK 24 才根治），而 ReentrantLock 的 park 路径可正常让出载体。MessageQueue.addMessage 位于发送热路径（服务器间会话为多生产者并发发送，见该归档变更评审中的修正讨论），是唯一每消息必经的 synchronized 点，应改 ReentrantLock。本变更是同日前一变更结论的即时修正（代码尚未提交）。

## What Changes

- `MessageQueue.sentMessageLock` 由 `Object` 监视器改为 `ReentrantLock`；四个方法 `synchronized(...)` → `lock()/unlock()`（try/finally）。可重入、虚拟线程友好、行为不变，声明 skip_specs。
- 模式卷选型行同步修正：热路径锁默认 ReentrantLock（虚拟线程兼容），synchronized 仅限"冷路径+短临界区"。

## Capabilities

### New Capabilities

（无——行为不变重构。）

### Modified Capabilities

（无。）

## Impact

- 仅 `MessageQueue.java`（锁原语层替换，约 8 行）。全项目其余 10 处 synchronized 均为冷路径（guide 启动 DCL、codec 惰性初始化、重连监控），登记不改（虚拟线程若进入这些路径将均为低频阻塞点，JDK 24 升级或性能变更时统一处理）。
- 防回归：`MessageQueueResizeTest` 6 用例（含并发 smoke）现成覆盖。
