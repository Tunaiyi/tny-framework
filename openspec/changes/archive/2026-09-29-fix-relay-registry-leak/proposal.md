# Proposal

## Why

服务端中继注册表存在两处真实泄漏（经全链路深读确认，本版修正了提案初稿的两处误判——详见 design"事实修正"节）：① `BaseRelayExplorer.closeTunnel`（协议显式关闭路径，`onTunnelDisconnect`/`onTunnelClosed` 两处调用）只查表 close 不摘除——隧道 id 全局单调递增永不复用，每条正常关闭的隧道留下永久孤儿条目；② `DefaultServerRelayExplorer.linkMap` 在链路断开或关闭后均不摘除——**主要路径是网关物理断线**（`NettyChannelRelayTransport.doClose → link.disconnect()`，状态停在 DISCONNECT，永不 close、`onClosed` 类钩子永不触发），僵尸链路连同其 transport 引用永久滞留，长服在网关扩缩容/网络抖动下无界增长。

## What Changes

- `closeTunnel`：`remove(key)` 取回即关（CHM remove 原子返回唯一胜者，天然幂等）。
- `DefaultServerRelayExplorer`：链路开通成功时经 `eventWatch()` 挂 `RelayLinkListener`，**onDisconnect 即摘除**（`remove(id, link)` 值相等 CAS）——该事件同时覆盖"物理断线"与"主动 close"两条路径（`BaseRelayLink.close()` 内部必经 `doDisconnect`）；摘除后链路对象连同 transport 可被 GC。
- 隧道条目的断链保留语义**不变**并写入规格：隧道跨网关重连的存活路径是 `putTunnel` 同标识置换（re-CONNECT）与 `switchTunnelLink`（显式切换）双协议，二者均以**隧道条目在断链窗口内仍可查**为前提；link 条目摘除与之无冲突（迁移定位键是 `(instanceId, tunnelId)`，不查询 linkMap）。
- 不改 `acceptOpenLink` 冲突分支（初稿的"重连阻断"经深读证伪：linkKey 每次连接重新生成，`putIfAbsent` 不会与陈旧条目相撞）。

## Capabilities

### New Capabilities

（无新主题。）

### Modified Capabilities

- `relay-link`：ADDED 三条——链路注册条目随断开/关闭摘除（收敛至活链路规模）；隧道显式关闭即摘除（幂等）；断链窗口隧道条目保留（迁移前提，反过度清理条款）。

## Impact

- 代码：`tny-game-net/.../relay/link/BaseRelayExplorer.java`（closeTunnel）、`DefaultServerRelayExplorer.java`（监听器挂接与摘除）。
- 行为变化：断线/关闭后的链路不再可查（`getLink` 面此前返回僵尸——若有调用方依赖"断开链路仍可查"需评估，仓内检索 apply 执行）；被协议关闭的隧道不再占表。
- 已知边界泄漏（**登记不修**，防误判为已全覆盖）：客户端"放弃隧道"而未发协议关闭时，隧道条目仍滞留——其清理属保留策略/超时策略设计问题（TTL 会误伤合法迁移窗口），需专项变更以真实流量数据定策。
- 客户端 explorer（`BaseClientRelayExplorer`）：link 生命周期由 connector 独占管理，任务 3.1 核对其注册表是否有同类摘除缺口。
