# Proposal

## Why

会话"已发送消息缓存"（断线重发能力的数据基础）的运行时启用路径必然崩溃：服务端会话统一以缓存禁用（size=0）创建，登录时按配置调用 `setSendMessageCachedSize`；一旦配置值 >0，`MessageQueue.resize` 对空的旧队列执行 `addAll(null)` 抛 NPE（配置 0 时另有 `CircularFifoQueue` 容量非法异常）。即该特性自诞生起无法启用——重发 API（`resend` 三重载）对下游是公开契约，却建立在一个一触即溃的基础上。

## What Changes

- 修复 `MessageQueue.resize`：旧队列为空时安全初始化；调整为 0 时定义为"禁用缓存"（释放已存消息），与构造语义一致；调整过程不抛异常。
- `MessageQueue.sentMessageQueue` 字段补 volatile（resize 在写锁内换引用，而读方法在锁外做 null 检查——存在发布可见性缺口，属同主题并发正确性）。
- 清理 `BaseNetSession` 构造中等价的冗余 if/else 分支（行为不变）。
- 以规格首次固化重发缓存的行为契约（保留窗口、挤出语义、重发范围、关闭后会话安全）。

## Capabilities

### New Capabilities

- `session-resend`: 会话已发送消息缓存与重发的行为契约——动态启用/禁用、环形保留窗口、重发范围、会话关闭后的安全行为。

### Modified Capabilities

（无——`net-session` 既有合同仅覆盖凭证可见性，与本主题不重叠。）

## Impact

- `tny-game-net/.../transport/MessageQueue.java`（resize 修复 + volatile）、`session/BaseNetSession.java`（构造分支清理，行为不变）。
- `resend` 三重载与 `getSentMessages` 为下游公开 API：仓内无调用方（codegraph 核实），修复后首次真正可用——属"从不可用到可用"，无兼容负担；行为以新规格固化。
- 配置面：`CommonSessionSetting.sendMessageCachedSize`（默认 0=禁用）语义不变；启用路径（登录时 doAuth 调用）由崩溃变为生效。
- 测试：新增 `MessageQueueTest`（resize 三态 + 溢出挤出）与重发安全测试；回归 net 全部既有测试 + netty4。
