# Design

## Context

已实证的框架中继方向模型（上一变更八探针+工作流仲裁的副产物）：中继链路=**客户端角色 dial 服务端角色**（现剧本 gateway=relay client → game=relay server 共进程端点）；链路建立握手、隧道分配（`instanceId+tunnelId` 键）、`relayTunnelFactory` 接入隧道、`controllerRelayStrategy` 解码戳+`relayMessageHandlerFactory` 消费、`fixed-message-router` 按 service 名选集群——每一环都有既有测试锚点。**独立中继进程与级联是全新组合**：中继一收到隧道的转发报文时，若"本地无该 service"能否**二次中继**到其自身集群（中继二）——框架无行为证据，本设计给出语义模型，事实交实证。

拓扑语义模型（服务标识键空间已按缺口②教训显式化）：
- **T2（一跳独立中继）**：game-1(接入，relay client，dial relay-1) → relay-1(独立中继应用：仅 relay server 端点；须同时是 relay client 才能把链路续到 relay-2——T2 里它需要 relay server+client 双角色，client 侧 clusters 指向 game-2 的中继端点) → game-2(业务+relay server，同 T4 的 game 形态)
- **T3（两跳级联）**：同 T2，relay-1 的 client 侧指向 relay-2 的 server 端点，relay-2 的 client 侧指向 game-2 —— 级联要求**每一级中继都是 server+client 双角色应用**，即 app 形态同一、yml 拓扑参数不同（这是可复用的最简装配模型）
- 服务名约定：game-2 的服务名唯一（如 `game-two`）；每级集群的 `serve-name` 显式给出且与查询 service 键一致（键空间教训固化）

## Goals / Non-Goals

**Goals:** T2/T3 拓扑矩阵绿，或失败以可定位形式落档；全部拓扑 yml 声明；矩阵资源隔离可独立复跑。
**Non-Goals:** 不修任何生产代码（框架缺口→移交变更）；不覆盖注册中心发现形态（静态集群即可验拓扑）；不做 >2 跳深链（边际价值低，两跳已含级联语义）。

## Decisions

### D1 拓扑=纯 yml 声明，Java 只有"进程+端口注入"两个动词
每角色一份拓扑 yml（`it-topo/` 目录：`it-t2-relay1.yml` 等），经既有 `file:` additional-location 挂载；运行期仅注入 `${it.*}` 占位（端口/上下游地址），与用户"需要 yml 配置"要求逐字对齐。
**否决**：Java map 拼属性覆盖全部拓扑键——否决理由：拓扑语义沉进代码，矩阵扩展=改代码；违背上轮"配置化"决策路线。

### D2 独立中继 app 类在测试源定义（@SpringBootConfiguration + 双中继激活注解）
`RelayNodeApp`（装配测试类）：主类内声明 `@EnableRelayServerApplication`+`@EnableRelayClientApplication`+扫描注解；T2/T3 各中继实例只是同一主类 + 不同 yml。**不修改 demo**。
**依据**：M1 先例（demo 三个 app 类即此形状的最小复制）；P6（测试装配面与被测装配机制同源）。
**否决**：(a) 给 demo 加 relay-only app——污染示例交付物；(b) 用 game app 兼职中继——中继进程被迫连 Redis/Mongo 并注册业务 bean，"专职中继节点"的验证语义即告失守。

### D3 game-1(接入侧) 复用 demo gateway 形态，业务协议面与服务路由分离
game-1 = `RelayGatewayServerApp`（其登录面已有剧本锚点）；PLAYER 协议经 fixed-message-router 指向 game-2 的服务名。断言基线=T4 剧本同款（登录 ack + PLAYER$ADD/GET ack + Mongo 直查），三拓扑共享断言助手（提取不复制）。
**依据**：P13；复用已实证行为面，新增变量只剩"中继跳数与独立性"。

### D4 T3 失败三级定性流程（不吞红、不私修）
红→复用八点位探针法+工作流仲裁定性：a) 拓扑 yml 参数问题→修到位；b) 测试 app 装配缺项→补接线；c) **框架级联缺口**（如二次中继无消费、隧道的 instanceId 键空间跨跳断裂）→ 把断链跳点证据写入本变更 verification.md，移交独立 fix 变更；本变更以"T3 场景=失败可定位呈现"验收（specs 第二场景已按此措辞——两种结局都算覆盖达成，杜绝为跑绿私改行为）。

### D5 矩阵隔离：单剧本单进程组 + 端口全探测 + 服务名拓扑前缀
每拓扑独立测试类（T2/T3 两类），类内进程按依赖序串行起停（链路 server 端先于 client dial，规避上轮实测的 dial 早于 listen 永久终结竞态）；`serve-name/service` 带拓扑前缀（t2game/t3relay1…），集群键显式给出（缺口②固化）；`forkEvery=1` 既有隔离覆盖 JVM 级静态串扰。
**否决**：一个类跑全矩阵——否决理由：单点失败牵连全部；资源命名共享易撞。

## Risks / Trade-offs

- [T3 大概率暴露框架缺口（二次中继无既有实现证据）] → D4 定性流程即预案；specs 措辞已把"可定位失败"纳入合法结局，不会为绿改行为
- [独立中继 app 与生产 relay 部署形态仍非 1:1（真实生产可能有独立中继网关产品）] → 测试 app 只用公开注解组合，与用户自建 app 的装配面等价；风险如实：不覆盖任何生产未公开的内部假设
- [拓扑 yml 数量 5+（T2×3、T3×4）维护成本] → 公共段以注释锚定模板文件；矩阵断言助手单一来源
- [docker 档时长↑（每拓扑多 1-2 进程）] → 仅 T2 常驻 docker 档；T3 首轮定性后可转 @Tag("docker") 常驻，若框架缺口移交则 T3 以"失败可定位"最小断言驻留，成本有界

## Migration Plan

纯测试侧新增（新包 `it/assembly/topology/` + 资源 yml），逐拓扑独立可 revert；回滚=删目录+资源文件。无生产触点、无发布物影响。

## Open Questions

- T3 端到端连通性是否有既有实现支撑——设计上无法静态定论（消费代码检索只能给"未发现"级证据），交任务 3.1 实证，D4 处置。

## 调研终形补记（工作流 5-agent+反驳核验，用户裁决"两族都要"）

### D6 relay 中间跳定性升级：架构缺席而非缺口配置
`onTunnelRelay` 服务器端无条件本地终止；全仓无类同时引用双 explorer；RELAY_TRANSPORTER 无二次转发消费；`ControllerRelayStrategy` 的 relay 戳只在玩家 socket 管道被读。ServerRelayExplorer 存在 SPI 装配缝（`UnitLoader.getLoader`），经缝写自定义中转属**新代码**=框架增强范围，移交 `add-relay-transit-hop` 提案（证据包=本变更 T2 双分支断言输出+本段定性）。

### D7 forwarder 拓扑装配基线（F 族 IT 的实现蓝本）
- 调用方：测试源 caller 控制器（RelayNodeApp/caller app 扫描面内）注入 `@RpcRemoteService(value="game-service", forwardService=<转发节点服务名>)` 代理，终端复用 demo `SPEAK$SAY_FOR_RPC`（无存储依赖）
- **forwardable 双 bootstrap 修正（核验者补记，承重）**：转发应用的 rpc.server 与**拨出侧 rpc.client** bootstrap 均需 forwardable=true——终端 RESPONSE 落在其 client bootstrap 会话上，单侧配置回程必丢
- 同型歧义规避（design 级陷阱）：caller 以异型 username（gateway-service/game-server）拨入转发节点，防 FirstRpcForwarderStrategy 首跳转回 caller 自身；F2 两跳的 `to` 重写语义未实证——失败即按双分支断言结案+移交记录
- relay link 与 RPC 转发节点集互斥（relay 会话永不进 RpcForwardNodeSet）——矩阵不存在"forwarder over relay"混连形态，两族仅共存/隔离关系

### D4 更新
T2/T3 的"失败可定位"从兜底分支升级为**主验收分支**（架构缺席已实证）；F1/F2 以连通为主分支、归因结案为兜底。移交清单两份：relay 中间跳增强、两跳 to 重写语义（若 F2 红）。

### D5 终形补记（矩阵二审审计回写，2026-10-01）
- **服务名前缀约定废止、隔离责任移交**：D5 原文"serve-name/service 带拓扑前缀（t2game/t3relay1…）"不可实施——relay 集群路由键受 `TestRpcServiceType` 注册枚举词汇表约束（game-service/game-server/gateway-service/game-client），运行期无法造前缀名。终形隔离由四重承担：进程组（每拓扑独立 JVM + forkEvery=1）、类级独占容器、动态探测端口、mongo 库名（it-t2/it-t3/it-f1/it-f2）。T3 级联实体（it-t3-*.yml + RelayTwoHopCascadeIT）经用户裁决于本轮补齐，级联每级同形（D5 原判）得证。
- **serve-name 语义不对称（框架实证，后续矩阵扩展者必读）**：D5"每集群 serve-name 显式给出"仅对 `relay.clusters` 成立；对 `tny.net.rpc.cluster.services`，`RpcServiceSetting.isDiscovery() = discovery || isNoneBlank(serveName)`——给 rpc 条目补 serve-name 会把静态直拨**静默翻转为发现模式**（`url()` 返回 empty），拨号面断链且报错不在键空间层面、难归因。F 族五份 yml 全部省略 serve-name 为正确形态，勿按 D5 字面"补齐"。
