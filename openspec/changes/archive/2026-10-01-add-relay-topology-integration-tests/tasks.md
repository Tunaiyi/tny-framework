# Tasks

## 1. 矩阵基座（design D1/D2/D3，先建共享件）

- [x] 1.1 测试装配应用类 `com.tny.game.it.assembly.topology.RelayNodeApp`（中继节点：中继服务端+客户端双激活注解；javadoc 注明对应 design D2）与 `RelayBusinessApp`（game-2：若 demo `RelayGameServerApp` 直接可用则不新建，核实后择一并在文件头记录裁决）。验证：`./gradlew :tny-game-integration-test:compileIntegrationTestJava` 通过
- [x] 1.2 提取拓扑共享件：`ScenarioSupport`（进程按依赖序启动器[先起链路 server 端点再起 client dial，规避上轮实证的 dial 竞态]、`it.*` 占位注入表、登录+PLAYER$ADD/GET+Mongo 直查断言助手——从 `RelayLoginScenarioIT` 提取复用，原类改调用零行为变化）。验证：`-PincludeDocker --tests "*RelayLoginScenarioIT*"` 复跑绿（提取回归探针）
- [x] 1.3 拓扑 yml 模板集入 `src/integrationTest/resources/it-topo/`：T2 三份（接入/中继/业务，服务名 t2 前缀、集群 `serve-name` 显式=service 键、业务端口/上下游地址全走 `${it.*}`）；文件头注释声明键空间约定（design D5）。验证：含 clusters 的拓扑 yml 均显式给出 serve-name（game 侧为接受端无集群段，天然豁免——实施时精确化）

## 2. R 族：独立中继中间跳（按架构事实以"装配可验+失败可定位"验收）

- [x] 2.1 IT `RelayOneHopIndependentIT`（三进程装配：接入+独立中继 RelayNodeApp+game-2；已推进至全进程启动+客户端可达）
- [x] 2.2 断言改造为双分支验收（specs R 族 S2）：链路两段建立断言保留；业务调用改断"有界窗口内返回**可定位**失败——中继节点日志含 onTunnelRelay 本地派发/205 证据、玩家侧收到失败回执非超时悬挂、三进程正常回收"；同法改造 T3 级联 yml（relay-2 段——二审审计 CRITICAL：此子项曾勾而无实体，经用户裁决补齐 it-t3-*.yml 四份 + `RelayTwoHopCascadeIT`，见 verification.md"T3 级联补记"）。验证：`-PincludeDocker --tests "*RelayOneHopIndependentIT*" --rerun-tasks` 绿 ×2 连跑；结果与 D6 定性一并写入 verification.md（移交证据包）
- [x] 2.3 移交记录：`add-relay-transit-hop` 框架增强提案要点（ServerRelayExplorer SPI 缝路线、影响面、兼容性）写入本变更 verification.md"移交"节（只记录不实施）

## 3. F 族：RPC 转发链拓扑（连通为主验收）

- [x] 3.1 caller 形态装配（design D7）：测试源 caller app（relay 网关形态+扫描注入 `@RpcRemoteService(value=game-service, forwardService=t1-forward)` 代理的触发控制器，协议 id 用测试域常量避开 CtrlerIds）+ 转发 app（RelayNodeApp 扩展 rpc 双 bootstrap forwardable yml）+ 终端 game-2（复用 SPEAK$SAY_FOR_RPC）；拓扑 yml `it-topo/it-f1-*.yml` 三份（异型 username 防同型歧义，静态 rpc.cluster.services 直拨）
- [x] 3.2 先写 F1 一跳 IT `RpcForwardOneHopIT`（docker 档；客户端调 caller 触发协议→断言终端执行回执原路返回、结果内容含终端标记；失败分支=按日志归因断链跳点结案+移交记录）。验证：绿或归因结案二选一，verification.md 记录
- [x] 3.3 F2 两跳级联 IT `RpcForwardTwoHopIT`（转发应用×2 级联；to 重写语义实证——失败即移交"两跳转发语义"记录）。验证：同 3.2 双分支（实证：跳二本地终止可定位，错误回程被咽成 100+空载荷假成功，移交记录见 verification.md）
- [x] 3.4 specs"同型歧义隔离"断言固化：F1 内加"caller 异型身份配置缺失变体→首跳被转回自身"的对照断言（或注释性负例记录，按实证可行度）（实证：同型变体 caller 日志出现终端协议派发 miss=转回自身成立；异型正例 caller 派发面干净，对照对已固化）

## 4. 矩阵隔离与收尾

- [x] 4.1 跨拓扑合跑：`--tests "*Relay*" --tests "*RpcForward*"` 全矩阵一次绿（互不串扰场景）；服务名/键前缀隔离核对（实证：合跑 7/7 绿；首跑暴露 T2 断言硬编码隧道编号的健壮性缺陷，已改正则匹配——属测试侧修复，specs 行为零变化）
- [x] 4.2 README 矩阵行 + 本变更 verification.md 终稿（两族结论、移交清单）。验证：README 命令实跑（全矩阵 8/8 绿一次通过，命令与 README 记载同款；verification 终稿含 F 族结案/T3 补记/移交两份/遗留问题清单）
- [x] 4.3 最终验证：三命令基线（`test`/`integrationTest`/docker 档）零新增失败（三命令 BUILD SUCCESSFUL，见 verification.md 终验表）；`/opsx:verify` 确认矩阵三需求全 Scenario 覆盖无 CRITICAL
