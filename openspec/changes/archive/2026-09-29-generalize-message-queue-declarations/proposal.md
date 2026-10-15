# Proposal

## Why

`MessageQueue` 的字段与全部局部变量以具体实现类 `CircularFifoQueue` 声明——该类仅是"环形挤出"构造特性所需的实现（由构造点与 session-resend 规格契约保证），而全部消费操作（add/addAll/stream/iterator）都落在 JDK `Queue`/`Collection` 接口面内。以最低依赖接口声明可：解耦 commons-collections4 类型渗透、为将来实现替换（如无锁队列方案）留出类型层零改动的空间（P7/ISP；模式卷"契约最小化"）。

## What Changes

- `MessageQueue`：字段与 3 个方法内局部变量的声明类型 `CircularFifoQueue<Message>` → `Queue<Message>`；`new` 表达式保持具体实现并注释挤出契约来源。行为零变化，声明 skip_specs。

## Capabilities

### New Capabilities
（无。）
### Modified Capabilities
（无。）

## Impact

- 仅 `MessageQueue.java` 类型声明层（约 5 处），无签名/无行为/无依赖变化（import 仍保留 CircularFifoQueue 于构造点）。
- 防回归：`MessageQueueResizeTest` 6 用例现成。
