# Proposal

## Why

`send2AllOnline` 名实不符（实现遍历全部注册会话，不判在线）。经领域所有者裁定：**向离线会话广播是其断线重连恢复链路的组成部分**——离线会话的广播消息进入其重发窗口，恢复连接后业务经 resend 补收，属有意的消息必达设计。因此修复方向是"名随实走 + 意图入账"而非行为改动：改名承认全部投递语义，同时把此前只存在于口述中的设计意图固化为规格——防止后续审计（本链已发生一次）把它当"离线静默失败缺陷"误修。

## What Changes

- `SessionKeeper` 接口新增 `send2All(MessageContent)`；`AbstractSessionKeeper` 实现迁移至该语义命名。
- 旧名 `send2AllOnline` 保留为 `@Deprecated` default 桥接方法（委托新方法）：下游源码与二进制兼容均不破坏，新代码指引至 `send2All`。
- 仓内唯一调用点（`ContactService:254`）切换新方法名。
- **行为零变化**：本变更不含任何投递逻辑修改。

## Capabilities

### New Capabilities

（无。）

### Modified Capabilities

- `net-session`：ADDED 一条——全体广播的投递范围与离线会话窗口收录语义（把领域意图固化为合同，含恢复补收 Scenario）。

## Impact

- `tny-game-net` 抽象模块公共 API：+1 方法、1 方法标记 deprecated（不删除；删除动作留待未来主版本，届时另立变更）。
- 下游影响：零破坏（桥接保留）；IDE 将以 deprecated 提示引导迁移。
- 实现：`AbstractSessionKeeper` 方法重命名 + 接口 default 桥 + `ContactService` 一处切换。
- 登记候选（本单不做）：离线会话的"对断开通道写出必失败+日志噪音"现状——是否演化为"离线只入窗不试写"属行为变更，涉 send/future/重发全链路语义，需真实使用数据支撑，移交后续评估。
