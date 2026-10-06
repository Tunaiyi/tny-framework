# Design

## 事实修正（深读全链路后，本提案经历两轮自我修正——全部留痕）

| 断言 | 初稿 | 终版 | 证据 |
|---|---|---|---|
| 摘除时机钩子 | onClosed | **onDisconnect** | 主路径 `NettyChannelRelayTransport.doClose:52 → link.disconnect()` 不进 close；而 `BaseRelayLink.close():311` 内部必经 `doDisconnect()`（293 行 notify onDisconnect）——**onDisconnect 单点覆盖断开+关闭两路径**，onClosed 只覆盖后者 |
| "重连阻断"缺陷 | 存在（putIfAbsent 撞陈旧） | **不存在，撤销** | `NettyRelayLinkConnector.onConnected:80-81`：linkKey 每次连接 `createLinkKey()` 新生成，id=`service-instance-key` 重连必不同，`putIfAbsent` 永不撞陈旧条目；泄漏真实、阻断是误判 |
| 迁移机制 | 仅 switchTunnelLink | **双路径**：re-CONNECT 经 `putTunnel` 同 `(instanceId,tunnelId)` 置换 + `switchTunnelLink` 显式切换 | `acceptConnectTunnel:58-60` putTunnel（旧值 disconnect 关闭恰好一次）；`switchTunnelLink:66+` 按隧道键查表重绑 |
| 隧道滞留=泄漏 | 一并清理 | **保留为设计**（link 条目摘除不影响迁移——迁移定位键是隧道标识，不查询 linkMap） | `getTunnel(instanceId, tunnelId)` 与 `linkMap` 无交叉 |

## Context

动机见 proposal.md。监听 API：`BaseRelayLink.eventWatch():221-224` 返回 `EventWatch<RelayLinkListener>`（common.event，含 addListener 能力——挂接具体方法签名 apply 时以编译定）；`NetRelayLink.isClosed()` public 可用。

## Goals / Non-Goals

**Goals:** link 条目两路径即时摘除；tunnel 协议关闭摘除（幂等）；迁移窗口语义入规格。
**Non-Goals:** 不改 `acceptOpenLink` 冲突分支（阻断证伪后该分支逻辑保持原样——其真实作用是"同 key 异常重复连接的防抖"，与陈旧无关）；不做隧道"放弃未关闭"的 TTL 清理（proposal 已登记边界，需真实流量数据支撑，防误伤迁移窗口）；不追 `getLink` 类查询语义的下游依赖（apply 3.0 检索确认）。

## Decisions

**D1：`acceptOpenLink` 成功路径（`link.open()` 后）经 `eventWatch()` 挂 onDisconnect 摘除监听**
`linkMap.remove(link.getId(), link)`——双参 CAS：同键新链路已置换上来时不误摘（值不等）。链路对象生命周期 ≤ explorer 注册表条目 ≤ 进程，listener 闭包捕获 explorer 强引用无泄漏环（explorer 本就是进程级单例）。
[否决①：在 disconnect()/close() 内反向调 explorer——引入 link→explorer 反向依赖；否决②：定期巡检清理——引入延迟与扫描成本，事件点信息完备无需事后推断。]

**D2：`closeTunnel` 改 `remove` 取回即关**（CHM 原子性给幂等与唯一胜者，规格场景二成立）。

**D3：re-CONNECT 路径核对不改**：`putTunnel` 的 `old.disconnect()`（:32）——注意 disconnect 语义在隧道（BaseNetTunnel.disconnect）即断链通知，session 侧按既有链路走；`getTunnel` 置换竞态由 CHM 保证。**验证责任转给测试**（规格"置换关闭恰好一次"场景）。

## Risks / Trade-offs

- **R1**：摘除后若有代码以 `linkMap` 判断"曾存在的链路"（如审计/重绑辅助）将行为变化——任务 3.0 全仓 grep `getLink/​linkMap` 消费点清单化后再实施。
- **R2**：close 路径 onDisconnect 早于 onClosed/onClosing 后续动作？通知顺序：`CLOSING→onClosing→write→doDisconnect(onDisconnect)→CLOSED→onClosed`（:308-314）——摘除发生在 write(LinkClosePacket) 之后，链路已发出的善后报文不受影响（摘除只出表，不断发送能力）。
- **R3**：隧道"放弃未关闭"残留（登记不修）——规模受"网关放弃重连"这一异常运维事件约束，与 link 泄漏（每次断线必留）不同量级。

## Open Questions

无阻塞。`EventWatch` 的注册方法名（addListener?）以编译即证。
