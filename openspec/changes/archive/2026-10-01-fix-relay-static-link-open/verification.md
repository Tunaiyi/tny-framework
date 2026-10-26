# Verification — fix-relay-static-link-open

## 组 3.1 取证记录（PROBE 插桩实验，插桩已回滚）

- gateway writePacket PROBE：`TUNNEL_RELAY status=OPEN transportActive=true`（14.182）——请求副本包交出链路
- game writePacket PROBE：TUNNEL_CONNECTED（14.095）之后 **10s 静默**——game 会话/派发从未把响应交至 link 写出？否——深读修正：game 派发 RESPONSE 走 replayTunnel.write → maker 惰性 createPacket + `link.isActive()` false 分支 `packet.getArguments().release()`——**每次重试释放共享 arguments**，计数提前归零后成功写出 → encoder 读死 buffer → `IndexOutOfBoundsException: Index 1 out of bounds for length 1`（gateway 侧 RpcHandleCommand ERROR 日志实测）→ 202 回执
- 竞态源头：`assignRelayer` 3 次重试间 `tunnel.write(packet,null)` 复用同一 packet 引用（`BaseClientRelayExplorer:124` 上下文）

## 组 3.1 三探针定位终稿（探针已全部回滚，修复①保留且单测回归绿）

- gateway：本地 `GatewayLoginController.login`（void）执行成功 → `PROBE complete contentNull=true` → `completeSilently`——**网关不给玩家回执（断点层一**，@RelayTo+void 组合的响应归属真空；specs 需求三正钉此）
- game：转发副本执行成功（`contentNull=false` 有 LoginDTO）→ 但 game 侧 `BaseRelayLink.writePacket` PROBE **全程零命中**——RESPONSE 从未抵达 relay 链路写出层（**断点层二**：`enterContext.complete(content)` → relayTunnel 写出环缺失/未触达，嫌疑集中于 GeneralServerRelayTunnel 写出门或 session 绑定）
- 排除项：packet 双释放假设证伪（assignRelayer 无写出）；键错配假设证伪（demo 形制 serve-name 恒给出）

## 待续（新会话入口清单）

1. 断点层二深挖：`RpcInvocationContext.complete(MessageContent,...)` 实现链（AbstractRpcInvocationContext/CompletableRpcTransactionContext）→ game 的 RESPONSE 到 `tunnel.write` 为何不触达——修复候选：replay 请求的回执以 `replayTunnel` 为目标写回（含 relay=true 判定缺失嫌疑：`RpcInvokeCommand:59` 的 relay 标志取自**本地 controller 注解**，game 侧 ServerLoginController 无 @RelayTo 但 tunnel 是 relay 隧道，应走 `relayTunnel.relay()` 回 gateway 而非本地 respond——本地 respond 的 RESPONSE 发进了 replayTunnel.write 但该类 write 实现可能只支持 relay 转发）
2. 断点层一修复：网关 void+@RelayTo 成功时按 specs 需求三回成功回执（`shouldRespondLocally` 语义修订，注意"仅转发回执"既有场景不回退）
3. 剧本恢复双参形态已就绪（RelayLoginScenarioIT 当前即双参+15s），两断点修复后应全绿
4. 本变更未勾任务：3.2/3.3（上述修复的测试先行）、5.2/5.3；add-starter 变更剩余 3.x/4.x 在其后

## 会话二增量定位（探针已再次全部回滚，修复①保留，单测回归绿 exit=0）

- 插桩复跑证据链：game `PROBE complete contentNull=false` 出现但 `onReturn` 探针**未命中** ⇒ `complete(content,error)` 的 `tryCompleted` 守卫失败——**game 的 enter context 在 handler 执行完成前已被他方抢先终结**（这就是"有执行、无回执、link 层静默"的统一解释；层二定性从"路由缺失"修正为"完成权被抢"）
- 下一会话入口（按优先级）：
  1. 排查抢先 complete 方：game 侧派发链对 **RPC_FORWARD_HEADER 副本请求** 的处理嫌疑最大——`BaseNetSession.receive:196` 的 pollFuture、`@BeforePlugin(SpringBootParamFilterPlugin)` 校验失败路径、以及 `RpcMessageAide.send:38` 揭示的**转发回程机制**（`existHeader(RPC_FORWARD_HEADER)` 门——框架本有"回写 forwarder"设计，先读 RpcForwardHeader 的 writeBack 实现再看是否 game→gateway→player 回程该走它而未接通）
  2. 层一（gateway void+@RelayTo 本地不回执）独立可修：`shouldRespondLocally` 语义修订 + 3.2 测试先行
  3. 剧本现况：双参登录 15s 超时（红，即 3.2/3.3 的目标锚）；`git status` 显示工作区含大量并行会话改动（109 文件），本变更改动面严格限于 tny-game-net 两文件+测试，勿误提交他人文件

## 终局记录（工作流 diagnose-relay-205 仲裁 + 装配修复 + 剧本全绿）— 2026-10-01

- 三路读者+三反驳结论收敛（两 holds=true 带措辞修正、一 holds=false 完成方向性纠偏）：205 产自**网关本地派发**（relay 戳无消费者），非 game 注册面/非转发改写
- 修复＝it-relay-gateway.yml 接线 relayMessageHandlerFactory + 剧本 loadScheme；`RelayLoginScenarioIT` **PASS**（登录→经中继 PLAYER$ADD/GET→异步落库 Mongo 可查→资源释放）
- specs 需求三：行为既有满足（CommandCompletionGuardTest:101-107 + 剧本），delta 保留该需求作为契约钉文档（verify 时说明其验证由既有测试承担）
- 本变更框架净改动=修复①（BaseRelayLink 握手放行 + dropPacket 留痕）+ 新单测 4 用例；demo 网关 yml 的 message-handler-factory 缺失属**生产配置缺口**，登记遗留发现（本变更不触碰 demo，交 add-starter 剧本以测试 yml 承接）

## 5.2 终验与子进程隔离加固（2026-10-01）

- 事故链：forkEvery 缺省时同 fork 复用 JVM——剧本类的 demo 类装载/loadScheme 写 JVM 级静态 codec/单元表，串染后续 RedissonDataAccessIT（DemoPlayerObjectCodecableCodec 幽灵 bean）；且其"曾三连绿"实为登录步早失败短路了静态注册的假绿——全量跑才暴露
- 根治三件：`forkEvery=1`（进程级隔离）＋子进程隔离 classpath（demo runtimeClasspath+其 jar，与测试 JVM 切割）＋配置/日志文件改 `file:` 绝对路径注入（隔离后 classpath 资源不再可见；顺带修 Gradle 8.5 systemProperty 不解包 Provider 的坑——此前 -cp 实为字面量 "provider(?)"）
- 终验：`-PincludeDocker` 全量 **3 连跑 × 17 tests × 0 fail**；`-PincludeDocker` 缺席时 docker 标签正确 skip；`:tny-game-net:test :tny-game-net-netty4:test` 绿


## 5.3 交接确认（2026-10-01）

add-starter 剧本全绿+组4收尾完成，本变更阻塞解除闭环。两变更均 100%（10/10、14/14），待 /opsx:verify → 归档（先本变更后 add-starter）。
