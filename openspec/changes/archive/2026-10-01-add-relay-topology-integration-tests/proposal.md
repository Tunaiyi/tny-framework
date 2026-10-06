# Proposal

## Why

中继部署形态的集成验证目前只有两点：客户端直连业务服（`SpringAssemblyGameServerIT`）与"接入侧+业务侧、**中继端点与业务应用共进程**"的剧本（`RelayLoginScenarioIT`）。生产实际部署里中继是独立节点（专职转发进程），且存在多级级联诉求——两种形态下"登录→跨节点业务调用→回执回程"是否可用**从未被验证过**：独立中继 app 形态在示例应用中不存在（中继服务端点始终寄生在业务应用内），级联能力（中继节点自身再作为客户端 dial 下一级中继）只有装配面证据、无行为证据。拓扑矩阵的缺口意味着：一旦用户按真实拓扑部署，装配死路（如上轮实证的四类配置接线问题）只会在生产暴露。

## What Changes

- 集成测试新增**部署拓扑矩阵**（用户裁决"relay+forwarder 都要"；调研工作流 5-agent 反驳核验定形），全部拓扑以 **yml 声明**（测试资源目录，Java 侧仅进程启动与端口注入）：
  - R 族（网络中继）：T2/T3"独立中继作中间跳"经实证为**架构性缺席**（`onTunnelRelay` 仅本地终止，三节点模型只在 javadoc）——交付"三进程装配+两段链路建立可验、业务调用可定位失败（205 断链归因）"，连通验收移交 `add-relay-transit-hop`（框架增强，另行提案）
  - F 族（RPC 转发链）：F1 一跳 `接入(caller proxy)→forwardable 转发应用→game-2 终端`、F2 两跳级联——装配键已全（`bootstrap.rpc.server.forwardable`+`tny.net.rpc.cluster.services` 静态直拨+`@RpcRemoteService(forwardService)`，无需注册中心；**forwardable 需 rpc.server 与 client 双 bootstrap 同开**——核验者补记），全仓零行为证据，本变更推到行为面；含同型身份歧义隔离断言
- T1/T4 既有覆盖纳入同一矩阵命名与断言基座（复用 `RelayLoginScenarioIT` 剧本断言面），不重复建设
- 新增测试装配应用形态：独立中继 app（`@SpringBootConfiguration` + 中继激活注解；级联形态=服务端+客户端双激活）；**不修改 demo 与任何生产代码**
- 两族失败分支均按"连通或失败可定位归因"双结局验收（specs 已按调研终形措辞）；框架侧缺口（relay 中间跳、两跳 to 重写语义）以证据包移交，本变更生产代码零改动红线不破

## Capabilities

### New Capabilities
无。

### Modified Capabilities
- `integration-testing`: 新增三条需求（delta 第二版，按调研终形改写）——"网络中继拓扑矩阵按架构能力如实验收"（含既有两形态回归锚+独立中间跳可定位失败）、"RPC 转发链拓扑端到端可验证或失败分支归因"（一跳/两跳/同型歧义隔离）、"拓扑矩阵间配置与运行互不串扰"；既有"多应用装配验证主干业务场景"需求不动。

## Impact

- **模块**：`tny-game-integration-test`（新拓扑 IT、独立中继测试 app 类、`src/integrationTest/resources/` 拓扑 yml 若干）；`tny-game-net`/`tny-game-net-netty4`/starter 均**零改动**（发现缺口则另立变更）
- **依赖**：全部复用既有接线（demo 蓝本类、子进程载体、容器、forkEvery 隔离、`file:` 配置注入）；新增仅拓扑 yml 与两个测试 app 类
- **CI**：新 IT 归 `-PincludeDocker` 档（依赖真实 Redis/Mongo 的业务侧）；无容器档保持纯网络拓扑子集可选
- **下游兼容性**：无公共 API/报文变更（无 BREAKING）
- **风险面**：T3 若暴露框架级联缺口（如标识/回程路由跨两跳断裂），本变更交付"可定位失败证据+移交"，不吞红也不私修
