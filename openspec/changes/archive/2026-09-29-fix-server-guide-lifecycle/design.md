# Design

## Context

动机见 proposal.md - Why。现状事实（grep 核实）：

- 四处 static group：`NettyServerGuide:35,37`（boss+child）、`NettyClientGuide:41`、`NettyRelayServerGuide:37(+child)`、`NettyRelayClientGuide:42`；均由各自 `close()` `shutdownGracefully()`。
- `ServerGuide.isBound()`（net 抽象接口 :25）全仓零调用方（下游 API）；server 实现恒 `return false`。
- `NettyServerGuide.bind():133-148`：重绑以 `channel.close().awaitUninterruptibly(30s)` 返回值决定是否 `remove`——`awaitUninterruptibly(timeout)` 返回 boolean，close 正常完成时恒 true，语义近似可用但把"摘除"系于关闭结果；bind 超时/失败（`channelFuture.awaitUninterruptibly(30s)` false 或 future 失败）时 `channels` 不置入但**失败/半开 channel 无人 close**。
- 线程组工厂 `NettyBootstrap.createLoopGroup(epoll, threads, name)` 为 protected 静态工具，可复用。
- 测试基建：`TestGeneralServerTunnel`/`NettyServerTunnelTest` 已能装配出可工作的 server tunnel 栈（guide 构造参数链在测试代码中已有先例）。

## Goals / Non-Goals

**Goals:** group 实例化（四个 guide 全家族）；isBound 真实化；bind 重绑/失败路径无残留。
**Non-Goals:** 不引入共享线程池管理器/引用计数编排；不改 `ServerGuide` 接口签名；不动 relay 的链路重连逻辑（`NettyRelayLinkConnector` 问题在后续变更）；epoll 选择逻辑不变。

## Decisions

**D1：group 由实例在构造期创建、close 时关闭（实例排他），否决共享引用计数方案**
[P4 封装：guide 的不变量"open 时组活着、close 后自己释放"只有自持才能闭环维持；P13：多实例隔离与重启能力可用真实 socket 测试直接验证。] 代价（多实例线程数上升）在 proposal Impact 声明。
- 否决：静态 group + 引用计数 holder——被否：并发计数、"已全关后再获取复活"的竞态需再造一层生命周期，复杂度与它解决的问题（省线程）不成比例（P10）；demo/嵌入式场景实测线程成本可接受。
- 实现细节：`childGroup` 尺寸仍 `processors*2`；`bootstrap()` 的 DCL 保留（懒建不变），但组字段随实例 final 化在构造器创建（避免 DCL 嵌套复杂度；close 幂等由 `shutdownGracefully` 自身保证）。

**D2：isBound 实现为 `!channels.isEmpty()` 且其 channel 未关闭**（channels 已是 `CopyOnWriteMap`，读安全）。
[P6 守约：接口 javadoc 语义"是否已绑定"。] 绑定中的中间态（bind future 未决）不算已绑定——与"开启成功后为真"场景一致。

**D3：bind() 重构为"无条件摘旧 → 关旧（不等待其结果决定摘除）→ 新绑 → 成功才置入"**
[对应规格"耗时或结果 MUST NOT 阻断重绑"。] 失败/超时路径：`channelFuture.channel().close()` 回收半开通道后返回，不置入 map。旧实现的 `awaitUninterruptibly(30s)` 等待保留但仅作"尽量关完"的礼貌等待，返回值不再参与逻辑判断。

## Risks / Trade-offs

- **R1**：依赖"关一个 guide 顺带停掉全部"作为整体停机手段的部署将失效（修复前是巧合行为）。RELEASE NOTE 声明 + 框架正常停机路径走 `AppClosed` 逐个关（核实 starter 停机链是否本就逐 guide close——apply 时确认，若是则此风险实际为零）。
- **R2**：构造器创建 group 会使"创建但从不 open"的 guide 也占线程——客户端建连探测类场景每组仅 1 线程、server boss 1+child N；接受（与 D1 同源的代价）。
- **R3**：多实例真实 socket 测试的端口占用不确定性——使用临时端口（bind port 0 由 OS 分配后读取）规避。

## Open Questions

starter 停机链是否逐 guide 关闭（影响 R1 实际概率）——列为任务 3.1 的核实项，不阻塞设计。
