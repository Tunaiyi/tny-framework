# Proposal

## Why

`add-starter-net-integration-tests` 的多应用主干业务剧本（双官方 demo 应用子进程 + 静态中继集群）逐层暴露出**静态中继+接入网关形态的三个结构性缺口**（剧本红→逐跳修复→下一跳再红的方式取证定位）：

1. **握手首包死锁**（已定位已修）：`onConnected` 直接 `link.auth()` 写出 LINK_OPEN，但 `BaseRelayLink.canForward()`（:218/:175）要求 `status == OPEN`，而客户端 link 的 `open()` 只在收到 LINK_OPENED 后才被调用（`BaseRelayPacketProcessor:41`）——首包永远走 `dropPacket` 早退路径，握手死锁。
2. **（排除项，经实验证伪）** 曾疑"集群注册/查询键错配"：复核确认 demo 形制 `cluster.serve-name` 本就给出（context 键恒非空）；"无可用连接"实为**接入隧道早于中继链路建立**的启动竞态（游戏侧组件在 gateway relay 就绪前连入），且键的填充为惰性问题——ImmutableMap 不接受 null 键，"纯 service 无 serve-name"并不可达。
3. **网关本地回执不回玩家**（已定位）：网关本地执行业务（`GatewayLoginController` 执行与 game 侧认证转发副本均有日志）后，响应未写回玩家接入连接，玩家侧超时；纯转发协议（game 有 controller、gateway 无）可获回执（实测 205 应用码），证实断链精确落在"**本地处理（含 void 方法）+ @RelayTo 共存**"的混合分支。

中继既有单测全部以 mock 直接驱动收发包、绕过"客户端自发首包 / 静态配置装配 / 网关混合回执"三条真实路径，故长期无人发现。

## What Changes

- `tny-game-net`：`BaseRelayLink` 写出路径允许**链路未开（INIT）且底层传输 active** 时发出链路建立握手包（LINK_OPEN）；"未就绪丢弃"与"终结丢弃"的留痕与异常语义分离（终结路径维持 relay-link"中继载荷在早退路径终结释放"契约不变）
- `tny-game-net`：`CommonClientRelayLink.auth()` 的首包发送经上述就绪判定（不绕过载荷释放与回执完成纪律）
- 剧本侧无改动：`add-starter-net-integration-tests` 的 `RelayLoginScenarioIT`（当前红）作为端到端回归锚
- 新增模块本地复现测试：`tny-game-net/src/integrationTest`（既有源集）与 `tny-game-net/src/test` 单元级各钉一层（INIT+active 首包可达 transport；CLOSE/TERMINATED 状态仍走终结释放）

## Capabilities

### New Capabilities
无。

### Modified Capabilities
- `relay-link`: 新增两条需求——"客户端中继链路建立握手不被未开状态吞没"（INIT+传输 active 时握手包必须写出；终结/关闭态维持既有早退终结释放纪律且留痕区分）；"接入网关本地完成的业务处理回执必须送回发起玩家"（void 本地 handler 必须回成功回执；既有"转发且忽略本地结果"语义不吞没、由对端回执）。

## Impact

- **生产代码（行为修复）**：`tny-game-net`（`BaseRelayLink` 写出判定、`CommonClientRelayLink.auth`）；不改 relay 协议线上报文格式（向后兼容，无 BREAKING）
- **下游可见变化**：静态集群（discovery:false）中继链路由"结构性不可用"变为可用——接入侧 `ClientRelayTunnelFactory` 分配失败异常随之消失；注册中心模式若曾以其他路径规避首包（未发现），本修复不影响其语义
- **回归面**：`tny-game-net`/`tny-game-net-netty4` 既有 relay 单测（RelayPacketProcessor/链路终结释放契约等）、`add-starter-net-integration-tests` 的 `RelayLoginScenarioIT`/`MissingAssemblyIT`、`tny-game-net/src/integrationTest` 哨兵不受影响
