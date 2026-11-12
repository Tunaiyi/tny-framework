# Design

## Context

动机见 proposal.md - Why。现状事实（已核实）：

- `MessageQueue`（transport 包）：`CircularFifoQueue` + `StampedLock`；构造时 size≤0 则内部队列为 null（禁用）；`resize` 无条件 `new CircularFifoQueue<>(messageSize)` 后 `addAll(old)` —— old 为 null 即 NPE，messageSize=0 即容量非法异常。
- 触发链：`CommonSessionFactory.create` 固定 cache=0 → `CommonSessionKeeper.doAuth:71` 登录时 `setSendMessageCachedSize(setting)` → setting>0 必炸。
- 读路径缺口：`getMessages/addMessage` 在锁外做 `sentMessageQueue == null` 检查，字段非 volatile，resize 换引用存在发布可见性问题。
- `resend` 三重载仓内零调用（codegraph），纯下游 API；`BaseNetSession.resend` 已有 `isClosed()` 早退。
- `BaseNetSession` 构造 `if (size>0) new MessageQueue(size) else new MessageQueue(0)` 两分支等价（MessageQueue 构造自行处理 ≤0）。

## Goals / Non-Goals

**Goals:** 修复 resize 三态（0→N、N→0、M→N）；固化环形窗口与重发契约；补齐字段可见性。
**Non-Goals:** 不改重发的网络路径（`tunnel.write`）；不改 `SentMessageHistory` 接口签名；不做缓存持久化/跨会话迁移。

## Decisions

**D1：resize(0) 语义 = 禁用并释放**（队列置 null，与构造语义一致；`getMessages` 既有 null→空集合路径自然承接）。
[P4 不变量：MessageQueue 的不变量是"size>0 ⟺ 队列非空"，修复后所有入口维持它。]
- 否决：resize(0) 抛 IllegalArgumentException——被否决，配置面（`CommonSessionSetting` 默认 0）合法允许 0，抛异常会把配置错误升级为登录失败。

**D2：resize 迁移用 `addAll(old)` 天然满足"保留最近 N"**——`CircularFifoQueue` 满时挤出最旧元素，扩容缩容语义自动正确，无需手写迁移。[模式卷先例：不引入新结构。]

**D3：`sentMessageQueue` 字段补 volatile**——锁外 null 检查要看到 resize 后的新引用。[P13；与前变更 D3/D4 同型论证；否决"全部读入锁"：热路径发送每条消息加读锁不值得，volatile 检查 + 锁内操作已足够（检查后入锁重读）。]
实施注意：`addMessage/getMessages` 锁内使用字段处改为锁内重读局部变量，避免检查与使用之间引用被换。

**D4：BaseNetSession 构造冗余分支合并为 `new MessageQueue(sendMessageCachedSize)`**（等价化简，行为不变，P10 反向：删死代码不加新结构）。

## Risks / Trade-offs

- **R1**：resize 并发（登录线程调整 × IO 线程发送）——两操作均持写锁，volatile 修复后锁外检查也正确；测试含并发 smoke。
- **R2**：环形挤出使"重发全部历史"不可能（窗口上限 N）——这是内存有界的必然代价，规格已明示"最近 N 条"，下游按窗口语义使用。

## Open Questions

无。
