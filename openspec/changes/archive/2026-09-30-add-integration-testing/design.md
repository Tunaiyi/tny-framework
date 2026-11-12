# Design

## Context

见 `proposal.md` — Why。此处只记录影响方案设计的已核实代码现状与硬约束：

- 存量测试为进程内 mock 层：`NettyServerTunnelTest` 手工拼装 `NetBootstrapContext`（绕过全装配链的先例已存在且可运行）；`GuideLifecycleTest` 注释明示 E2E 因 `NetBootstrap.prepareStart` 全 unit 装配链成本被移交 demo 手动验收。
- `NettyServerGuide`/`NettyClientGuide` 均有 `ChannelMaker<Channel>` 注入构造器（`NettyServerGuide.java:71`、`NettyClientGuide.java:54`），transport 装配可参数化而无需改动生产代码。
- `NettyChannelConnection.getRemoteAddress()/getLocalAddress()`（`:45`/`:50`）把 Netty 地址强转 `InetSocketAddress`——LocalChannel 的 `LocalAddress` 会触发 CCE，且该地址形状属于已发布连接契约面。
- 数据源客户端由框架自有装配链创建：`ImportRedisDataSourceBeanDefinitionRegistrar` 读 `tny.datasource.redisson.setting.*`、`tny-game-mongodb` 读 `tny.datasource.mongodb.setting.*`，均不经 Spring Boot 自动配置的 ConnectionDetails 机制。
- 依赖管理采用 `io.spring.dependency-management`，显式声明的版本会强制覆盖 BOM（这正是 Mockito 3.11.2/JUnit 5.8.2 压住 Boot 3.2.1 BOM 的原因）。
- `tny-game-net-test`、`tny-game-tester` 是发布到 Nexus 的测试 fixture 库，其依赖会传染到下游 pom。

**codegraph 爆炸半径分析摘要**（design 规则要求）：
- `NettyServerGuide`（两个构造器 :54/:70 分别分析）：`modify` 影响 direct=1，risk_level=low——Guide 仅被装配边界引用，harness 以既有构造器**使用**（不修改）它，零波及。
- `NettyChannelConnection.getRemoteAddress`（graph 行解析未命中，回退文本检索）：强转点 2 处（`:45`/`:50`），同名符号另有测试替身与 bench 工具各 1 处实现——若为 LocalChannel 放宽地址类型，将触碰已发布契约面（P11），这是否决该方案的关键证据之一。

## Goals / Non-Goals

**Goals:**
- 单元/集成两级通道：默认构建不混入集成用例；容器用例可整体降级为跳过（specs R1）
- 核心网络链路（含全部编解码实现）在真实本机连接下钉入自动化（specs R3/R4）
- 数据访问验证在干净机器可复现（specs R5）；多应用全栈剧本（原 R6/D6）经用户批准移出，转后续变更 add-relay-scenario-it
- 测试域版本治理一次到位，消除 JDK 21 不兼容钉子（specs R2）

**Non-Goals:**
- 不修改任何生产代码的公共接口、装配逻辑与报文格式（测试暴露的缺陷另立变更）
- 不建性能/压力基准（`tools/net-bench` 域）；不覆盖 etcd 真实容器测试（无质量可信的先例模块，留下期）
- 不重构存量单元测试，仅做版本接管后的最小兼容修复
- 不引入新的抽象接口（本变更全部产物为测试代码与构建脚本）

## Decisions

### D1 两级通道 = 独立 `integrationTest` source set + 双标签门控
每个 Java 模块经约定脚本（`gradle/integration-test.gradle`，由根 `build.gradle` 对 javaProjects 统一 apply）获得 `integrationTest` 任务；`test` 任务 `excludeTags 'integration'`，`integrationTest` `includeTags 'integration'`；容器用例叠加第二标签 `docker`，由 Gradle 属性开关并入 include 表达式。默认 `check` 不依赖 `integrationTest`，本期保持 CI 显式分档调用。
**依据**：P13（每个规格 Scenario 都有对应执行入口可证明）；变更历史先例——归档变更将验证任务归档为 `verification.md`，同为"验证独立于构建主通道"的形状。
**否决**：(a) 单 `test` 任务 + `-PincludeTags`（外部建议原案）——否决理由：Testcontainers 等重依赖将常驻普通 test classpath，50+ 模块全量污染，且 IDE 无法目视分层；(b) 只建聚合模块、不动各模块 source set——否决理由：模块自身将失去"就近补集成债"的通道，违反 config 现状条款"变更涉及的 Scenario 必须补对应测试"。
**偏离声明**：tasks 规则"新增测试放入 src/test/java、类名以 Test 结尾"按单元先例书写；集成用例类名以 `IT` 结尾、落 `src/integrationTest/java`，**P12 例外，理由**：本变更正是在建立两级通道这一新行为契约（specs R1），命名与目录是其可观察组成。

### D2 传输选 loopback TCP + 端口 0，否决 LocalChannel
harness 起真实服务端绑定 `127.0.0.1:0`，客户端经真实 socket 连接。
**依据**：P13（可验证性最大化——真实 socket 覆盖分片/粘包/RST/半开，这些 EmbeddedChannel 与 LocalChannel 都测不到）；P11（零契约变更——loopback 方案完全复用 `NettyServerGuide`/`NettyClientGuide` 既有 `InetSocketAddress` 形状）。
**否决**：(a) LocalChannel（外部建议原案）——否决理由：需先放宽 `NettyChannelConnection` 地址强转，即触碰已发布契约（P11 三问"旧调用是否仍可编译运行"存疑），且 codegraph 显示地址契约面外有 bench/替身实现牵连；"避免真实端口"的收益用端口 0 已等价取得，本机 loopback 与 LocalChannel 的性能差在测试超时窗口量级下不可分辨。(b) 固定端口段管理——否决理由：并行 fork 下碰撞需自建分配器，端口 0 + 读回实际地址是 OS 级无冲突方案。
**留路**：`ChannelMaker` 构造器保留为未来 LocalChannel/UDS 变体的注入口，本期不使用（P10：等第三次需求出现再抽象）。

### D3 网络 harness 与全部跨模块 IT 收敛于新建非发布模块 `tny-game-integration-test`
harness（进程内 server+client 夹具、消息记录器）与编解码矩阵 IT 全部放该模块 `src/integrationTest`；该模块只被自身使用、不参与 `maven-publish`、不入 `tny-game-bom` 约束面。
**依据**：P1（判据逆向应用：harness 必须依赖 netty4/codec 等实现模块，若放 `tny-game-net-test`——已发布 fixture 库——将把实现依赖传染给下游 pom，破坏"抽象模块删实现仍可编译"的隔离意图）；P11（发布面最小化）；P9（harness 收拢散落协作对象，沿用 `XxxContext` 形状先例，命名 `NetIntegrationHarness`——具体类，非接口）。
**否决**：(a) harness 放 `tny-game-net-test` 并新增 netty4 依赖——否决理由：发布库依赖面扩张即公共合同变更，且 netty4 测试已依赖 net-test，主/测试域交叉依赖给 publish 图埋雷；(b) 网络 IT 放 `tny-game-net-netty4/src/integrationTest`、harness 独立小发布模块——否决理由：为单一当前场景新增第二个已发布坐标，违反 P10 三次法则；(c) 各模块 IT 自行复制装配代码——否决理由：三 codec × 多场景的重复即"第三次出现"，正该抽象（P10 反向成立）。
**归属妥协**：网络 IT 不在被测模块目录而在聚合模块——规格 R3/R4 本就是跨模块链路行为，验证面与归属一致。

### D4 版本治理 = 删显式版本、交 Boot BOM 接管
`gradle/dependency.gradle` 中 junit/mockito 坐标去版本号；新增 assertj/awaitility 坐标（Boot BOM 管理）与 testcontainers BOM import（Boot BOM 不覆盖它）。
**依据**：P3（版本策略变化只落在 dependency.gradle 一处，不逐模块修改）。
**否决**：(a) 手工升版本号——否决理由：与 BOM 双头管理必然再漂移，正是本次要还的债；(b) 升级 Boot 大版本顺带解决——否决理由：范围失控，Boot 3.2→3.4 属独立变更。
**副作用披露**：`tny-game-net-test`/`tny-game-tester` 的已发布 pom 中 junit-api 约束将从 5.8.2 变 5.10.1——JUnit 5 minor 向后兼容，下游无感（P11 三问皆"是"，不标 BREAKING）。

### D5 中间件连接注入以属性覆盖写框架自有前缀，否决 @ServiceConnection
容器随机地址以测试装配的属性覆盖（`SpringApplication.setDefaultProperties`，等价于运行时属性优先级最高的注入通道）写入 `tny.datasource.redisson.setting.*` / `tny.datasource.mongodb.setting.*` 前缀。
实现注记：早期工件文字为 `@DynamicPropertySource`（spring-test 上下文机制）；因剧本采用手工 `SpringApplication.run`（D6 移出后仍为真实装配），改用 `setDefaultProperties`，注入目标前缀与"不改生产装配代码"的决定不变（verify 阶段勘误）。
**依据**：M1 先例优先——门面 starter 的装配路径保持生产原样，测试适配配置而非反向改装配代码（P3、P11）。
**否决**：(a) `@ServiceConnection`（外部建议原案）——否决理由：已核实该机制仅对 Boot 自动配置的客户端 bean 生效，框架多数据源 Registrar 不走 ConnectionDetails，"不用改配置类"的前提不成立；(b) 为测试给 Registrar 增加 ConnectionDetails 适配——否决理由：为测试方便改生产装配，本变更 Non-Goal。

### D7 CI 三档：PR（unit + 无容器 IT）→ main（+容器 IT）→ nightly（全量含数据访问容器）
新建流水线配置，档位与 D1 标签门控一一对应；无容器环境跳过语义由 specs R1 保证，CI 不因缺容器误红。
**依据**：P13；**否决**：单档全跑——50+ 模块 × 容器的 PR 反馈时长不可接受。

## Risks / Trade-offs

- [Mockito 3→5 接管后存量测试兼容性爆发面未知] → D4 作为首个任务组先行，全量 `test` 回归即探测网；按 spec R2 只修测试不豁免用例。
- [手工拼装 `NetBootstrapContext` 缺组件导致 harness 卡壳] → 已有 `NettyServerTunnelTest` 最小装配先例照抄；兜底：从 demo `DemoAutoConfiguration` 提取装配配方入 harness 私有 builder；再不可行则收缩首个 IT 场景为 transport 层（不断链，specs R3 的"连接-认证-往返"仍可达）。
- [CI 慢机 loopback 抖动假红] → 断言全部经 Awaitility 显式超时（默认 2s，容器操作 10s）；`maxParallelForks` 从 2 起步。
- [legacy mongo-java-driver 3.12 与新版容器协议不匹配] → 容器钉 `mongo:6.0`；驱动升级登记为独立技术债，不入本变更。
- [harness 资源泄漏（EventLoopGroup 未释放）拖垮后续用例] → harness 实现 `AutoCloseable` 并注册为 JUnit Extension，关闭顺序 client→server 且断言线程组终止（该断言本身即 specs R3"服务端停止与重启"场景）。
- [Trade-off] 网络 IT 集中到聚合模块使"被测模块目录"与"验证代码"分离 → 以包路径按链路分层 + spec 追溯标注（IT 类 javadoc 引 spec 条款）弥补导航性。

## Migration Plan

按 P0→P1→P2→P3 顺序合入，每档独立可回滚：P0/P1 仅动构建脚本与依赖表，回滚 = revert；P2 新增文件全在测试目录，回滚 = 删模块目录；P3 新模块 include 行删除即恢复。生产代码零改动保证任何阶段回滚不影响下游已发布 API。

## Compatibility Impact

本变更不修改任何公共 API 与报文格式。唯一进入发布物的影响为 D4 副作用披露（fixture 库 pom 中 JUnit 约束 5.8.2→5.10.1，minor 兼容，下游无感）。`tny-game-integration-test` 不进 BOM、不发布，对下游依赖面零变化。

## Open Questions

- ~~容器 IT 在 CI 的具体 Runner 镜像/服务容器编排方式~~（已决，apply 阶段）：仓库存在 `github: Tunaiyi/tny-framework` remote，落地为 `.github/workflows/build.yml` 三档（unit/integration/e2e，附 etcd 服务容器解既有 namnspace-etcd 单测依赖）。
