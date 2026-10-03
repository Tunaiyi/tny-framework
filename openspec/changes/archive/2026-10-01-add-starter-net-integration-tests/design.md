# Design

## Context

见 `proposal.md` — Why。此处只记录塑造方案的已核实事实与硬约束：

- 装配链构成（已逐类核实）：`@EnableNetApplication` → `ImportNetBootstrapDefinitionRegistrar`（按 `tny.net.bootstrap.*` 注册每个 server/client 的 Guide + ChannelMaker bean）+ `NetAutoConfiguration`（session keeper/dispatcher/body codec/relay 策略 bean）+ `NetApplication` 生命周期驱动 prepareStart 全链；中继双向由 `@EnableRelayServer/ClientApplication` 与 `SpringBootRelayBootstrapProperties` 承接。
- demo 蓝本零外部牵连（grep 全仓引用面为空），其 yml 已含全部必填键样例：`tny.app.*`、`bootstrap.network.server(s)`、`bootstrap.relay.client/server`、`relay.clusters`（**静态节点、`discovery: false`**，无需注册中心）、`router.fixed-message-router`、`session-keeper-settings`。
- 装配属性经 `@ConfigurationProperties` 绑定，`SpringApplication.setDefaultProperties` 可整体覆盖（含 bind-address 字符串与 Map 键）。
- 数据源具备自有开关：`tny.datasource.redisson.enable` / `tny.datasource.mongodb.enable`（`RedissonDataSourceProperties`/`MongodbDataSourceProperties` 均有 enable 字段）——单应用档可免中间件启动。
- 上一变更已交付并可复用：`integrationTest` 通道、双标签门控与 DOCKER_HOST 注入（`gradle/integration-test.gradle`）、`tny-game-integration-test` 模块、`LogCapture`、Redis/Mongo 容器 IT。
- demo 认证面：`@RpcController`/`@AuthenticationRequired(DEFAULT_USER_TYPE, validator=…)`/`@RpcRequest`/`@RpcParam` 与 `DemoAuthenticationValidator`（`AuthenticationValidator` SPI 实现）——登录/未认证两条路径均为公开装配契约。

**codegraph 爆炸半径分析摘要**（design 规则要求）：对 `ImportNetBootstrapDefinitionRegistrar`、`GameServerApp` 的行级 `analyze_impact` 在图中未解析命中（返回 0/unknown，两文件疑未入索引面）——回退文本检索取证：Registrar 的引用全部位于 starter 自身 `@Import` 注解链与上一变更 harness 的既有 import，无业务方直接引用；demo 三个应用类全仓引用为零。**本变更为纯测试侧新增，不修改任何上述符号**，引用面证据用于确认"复用不破坏"。

## Goals / Non-Goals

**Goals:**
- specs 两条新需求的全部 Scenario 获得无 flaky 设计的机器断言（单应用装配链、多应用剧本）
- 装配保真度：被测装配与用户实际使用的 starter 路径逐字节同源（同注解、同属性绑定、同 demo 控制器/校验器）
- 复用既有两级通道与容器基建，零新增构建概念

**Non-Goals:**
- 不修改 starter/boot/basics/demo 任何生产代码；测试暴露装配缺陷另立 fix 变更
- 不覆盖 nacos/etcd 服务发现装配（`discovery: false` 静态互联即满足剧本；发现语义属 `net-boot-integration` 既有账本）
- 不引入注册中心容器（避免与既有 etcd 单测环境依赖叠加变慢）
- 不做 demo 全部应用类逐一覆盖（client 型 app 的 `waitForConsole` 交互式入口不在剧本内）

## Decisions

### D1 直接以 demo 模块为装配蓝本（依赖 `tny-game-net-demo`，零修改复用）
集成测试模块以 implementation 依赖引入 demo，`SpringApplication` 直接以 demo 应用类（`GameServerApp`、`RelayGameServerApp`、`RelayGatewayServerApp`）为 source 启动；断言消息使用 demo 公开常量（`CtrlerIds`）与 DTO（`PlayerDTO`/`LoginDTO`）。
**依据**：M1 先例优先——装配面已有活的参考实现（模式卷门面行：starter 是装配收口，demo 是门面使用者），测试再造一套精简应用等于新增平行装配面（双份维护且"与真实用户路径有差异"恰是其缺陷）；P13（断言直接落在用户会写的代码形态上）。demo 外部引用为零（Context 取证），作为测试蓝本引入无涟漪。
**否决**：(a) 测试源码内仿写精简应用——上轮提问中用户已明确选择 demo 蓝本路线；且仿写复制注解/校验器/DTO 约 300+ 行、与 demo 演进必然漂移；(b) 在 demo 模块内直接写 IT——demo 属示例交付物，测试污染其构建面且其非测试模块无 `integrationTest` 通道语义。
**代价与对策**：demo 演进会牵连 IT——同仓库同 PR 联动修复（CI 全模块 `integrationTest` 即探针）；IT 只依赖 demo 公开类型面（常量/DTO/应用类），不触及其内部工具。

### D2 程序化 `SpringApplication` + `setDefaultProperties` 覆盖全部环境相关键
端口、互联地址、数据源 enable 均以 defaultProperties 覆盖 yml（bind-address 字符串、`relay.clusters[*]` 节点 url 等 Map/List 键整段替换）；WebApplicationType.NONE，多应用按角色起独立 ConfigurableApplicationContext。
**依据**：与上一变更数据源 IT 同源决策（其 D5/D6 实定路线，复用其已验证的绑定排除约定）；`@ConfigurationProperties` 绑定序保证 defaultProperties 覆盖 yml（Context 已核实绑定面）。
**否决**：(a) `@SpringBootTest` + `@DynamicPropertySource`——否决理由：多上下文剧本需要应用间互相注入对方实际监听端口（先启动方地址注入后启动方配置），测试框架单上下文模型无法表达；(b) 修改/新增 demo 的 test-profile yml——否决理由：给生产示例文件掺测试专用内容，违背 D1"demo 零修改"。
**端口事实约束**：引导器无"绑 0 读回"公共面（上一变更 D2 已证 `getBindAddress` 不反映实际端口）→ 沿用其结论：`ServerSocket(0)` 探测空闲端口写入配置，bind 成功以 `ServerGuide.isBound()` 复核，失败换端重试。
**实施修订（用户批准，verify 阶段补记）**：
- 同 JVM 双官方应用经实证**结构不可行**（boot 的 `ApplicationLauncherContext`/单元表为 JVM 级静态：第二上下文连带实例化第一应用实体 bean、生命周期 bean 二次启动失败）→ 多应用剧本改**子进程模型**（`DemoAppProcess`：ProcessBuilder 直跑 demo main，即真生产形态，保真度上调；单应用档维持程序化同 JVM 上下文）
- `defaultProperties` 优先级**低于** classpath yml（demo yml 随依赖必达）→ 环境键改**命令行 `--key=value`** 注入（最高优先级）；集群索引 list 整段替换需全字段声明
- 隔离 classpath 下 classpath 资源不可见 → 测试装配 yml 与日志配置改 **`file:` 绝对路径注入**；并发现 Gradle 8.5 `systemProperty` 不解包 Provider（须 doFirst 求值）

### D3 分层标签：单应用装配 IT 无容器档，多应用剧本 docker 档
单应用档启动前置 `tny.datasource.enable=false` 系（Redisson/Mongo enable 关闭）——利用框架自有开关而非测试 hack；剧本档开容器（Redis/Mongo，复用上一变更 `testcontainers` 与 DOCKER_HOST 基建），断言"持久化结果在业务侧可查"。
**依据**：P13；框架自有配置面承诺的裁剪能力本身就是被验对象之一（装配面越用生产路径越好）。
**否决**：全档都开容器——PR 档反馈时长不可接受（上一变更 D7 同理）。
**实施修订（用户批准，verify 阶段补记）**：单应用"无中间件档"前提被证伪（`GameServerApp` 的 `PlayerController→EntityCacheManager` 为数据子系统硬依赖，框架自有 enable 开关裁剪不了 demo 形制的实体 bean）→ 经批准改**全 demo + 容器**路线，且 GitHub runner 自带 Docker，**`-PincludeDocker` 并入 PR 档**（build.yml integration job），本条"PR 不开容器"的否决理由随之撤销。附带实施产物：`forkEvery=1`（demo 类装载/DTO scheme 装载写 JVM 级静态 codec/单元表会串染同 fork 后续 IT，事故实证）+ `@Isolated`。

### D4 认证两路径以 demo 校验器语义为断言基准
S2"未认证被拒且连接可恢复"用 `@AuthenticationRequired` 控制器的调用序（登录前调用→错误回执；登录后同调用→成功）表达；不 mock 校验器（`DemoAuthenticationValidator` 即用户侧实现样例，其判定入参用 demo 自有合法值）。
**依据**：P6 精神——被测装配的失败路径也要真实执行（拒绝逻辑属 basics 派发链，正是要钉的）。
**否决**：注册测试专用恒拒校验器——制造了生产不存在的装配组合，断言价值折半。

### D5 中继互联走静态节点属性注入，无注册中心
gateway 侧 `tny.net.relay.clusters` 按 game 实际中继端口动态构造（`discovery: false` + `serve-nodes[*].url`，yml 已有同款样例），game 侧 `bootstrap.relay.server.bind-address` 走同法探测端口。
**依据**：M1（demo 网关 yml 即此形状的既有先例）；Non-Goal 已排除注册中心。
**否决**：etcd 容器提供真实发现——否决理由：`net-boot-integration` 账本已管发现行为；剧本的验证目标是**装配→互联→调用→持久化**，发现层引入与故障面只会放大 flaky 面。

### D6 测试资产布局与命名沿用上一变更既定约定
新增类全部落 `tny-game-integration-test/src/integrationTest`：`com.tny.game.it.assembly.SpringAssemblyGameServerIT`（单应用）/ `com.tny.game.it.assembly.RelayLoginScenarioIT`（剧本）/ `com.tny.game.it.assembly.MissingAssemblyIT`（装配缺项）；公共启动支撑 `DemoApps`（同包，探测端口/属性装配/上下文登记表，`AutoCloseable` 全量释放）。类名 `*IT`、双标签、Awaitility 有界断言、Mulan 头——全部沿用上变更落地约定。
**否决**：放 demo 模块/独立新模块——上一变更 D3 的循环依赖与发布面论证继续成立。

## Risks / Trade-offs

- [demo 传递依赖与集成 IT classpath 的日志/容器绑定冲突] → 上一变更已建立排除模板（starter-logging/logback/slf4j-simple + log4j2 单一绑定）；`DemoApps` 启动前显式校验绑定唯一。
- [多上下文中继装配面大，失败常表现为深层 bean 错误难定位] → 任务顺序强制"骨架先红再绿"：先断言双上下文起服、端口可读回、中继注册表出现互联（`NettyRelayRegistry` 观测），再接剧本；specs"装配缺项可定位"用同一骨架反证。
- [探测端口 TOCTOU 竞争] → 沿用上变更已实测结论（5 连跑零 flaky 的同一策略），重试包装。
- [剧本多端口/多异步源 flaky] → 全部等待经 Awaitility 有界（连接 10s/消息 5s/持久化 10s）；`maxParallelForks=2` 下剧本类 `@Execution(SAME_THREAD)` 语义（若平台配置了并行）。
- [Trade-off] demo 若未来重构（如改包名）会破坏 IT → 视为特性：IT 是 demo 的回归保护网，重构方在同 PR 内适配。

## Migration Plan

纯测试侧新增，按任务组 P→A→B→C 顺序合入，任一步可独立 revert（删除 `it/assembly` 包与模块 build.gradle 依赖行即回到上变更终态）。不涉数据与发布物。

## Open Questions

- 无。剧本消息选择（登录 `LOGIN$LOGIN` + 业务 `PlayerController` 一条）与持久化断言点（store executor 落 Mongo 后查询）在 D1 复用 demo 既有控制器面后已无待定项。
