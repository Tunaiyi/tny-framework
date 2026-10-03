# Design

## Context

动机见 proposal.md - Why。现状事实：tny.it-demo-isolation 由 tny-game-integration-test 模块的 plugins{} 引入；integrationTest 任务本体由 tny.integration-test 插件注册（根脚本装配线先行应用），本插件在其 afterEvaluate 里 `tasks.named('integrationTest')` 追加隔离接线；被测目标 `:tny-game-net-demo` 现被点名两处。既有事故注释（DemoPlayerObjectCodecableCodec 串染实证）全部保留在插件内——那是机制来由，属于插件；目标清单是数据，迁到模块。

爆炸半径核查（设计规则要求）：codegraph 索引覆盖 Java 源码，`.gradle`/buildSrc 插件不在符号图内（与既往两变更同结论，如实记录）；真实消费面为三处——IT 用例对 systemProperty `it.demo.isolatedClasspath` 的读取、forkEvery 与 shouldRunAfter 的执行顺序面、`tasks --all` 任务清单面，重构对三者都保持逐字节不变。

方法论两卷检索（M1 条款要求）：模式卷决策表中"把外部体系纳入框架契约"一行（适配器先例 `ScriptExprContext`）与本案形似而神不合——本案不是接入外部体系，是把插件内嵌配置外提为声明面，无框架内类先例可循；据以引用的是原则卷 P2 的形状（机制依赖配置契约而非具体工程）与仓库构建域自身先例：tny.repositories 与 tny.compile-encoding 共享块"机制在插件、成员由根装配线声明"的既有分工（adopt-gradle-official-dsl 终态），本案把同一分工推进到"配置值"粒度。

## Goals / Non-Goals

**Goals:**

- 消除 buildSrc 内最后一个"新增被测项目需改插件"的耦合点（adopt-gradle-official-dsl 复查 B 类唯一项）。
- 配置面类型化、可发现、误用早爆：扩展名、属性名、校验报错都在插件内自述。
- 单目标行为与现状逐字节一致（零差异红线延续）。

**Non-Goals:**

- 不做 marker 插件自动发现（否决理由见 D2）。
- 不动 tny.integration-test 的任务注册与 shouldRunAfter/maxParallelForks 语义。
- 不处理审计 A 类契约谓词（doc-gradle 例外组号、tny-bench 零发布合同等是规格点名的合同，改动反而降低可核对性）。
- 不引入多 demo 的真实用例（无目标可加；语义仅以注释与探针定义）。

## Decisions

**D1 类型化扩展作为唯一配置面。** 插件经 `extensions.create('demoProcessIsolation', DemoProcessIsolationExtension)` 贡献扩展；扩展为 buildSrc 内一个小型 Groovy 类（`src/main/groovy/tny/buildsrc/DemoProcessIsolationExtension.groovy` 或等价位置），仅含一个 `ListProperty<String> targetProjects`。模块侧写 `demoProcessIsolation { targetProjects.add(':tny-game-net-demo') }`。依据：P2 依赖倒置的构建域应用——机制（隔离接线）依赖"目标工程路径清单"这一契约，不再引用具体工程；P13（扩展空值与非法路径都有确定报错，可验证）。被否决备选：a) 裸 `ext.itDemoIsolationProjects = [...]` 字符串属性——否决理由：无类型、无发现性，键名拼错静默失效，正是本次要消灭的形态；b) 在模块 plugins{} 里给插件传参——Groovy DSL 的 plugins{} 不支持实例化配置块，形态不成立。

**D2 接线时机保持 afterEvaluate，读扩展后校验再装配。** 插件在 afterEvaluate 里读取 `targetProjects.get()`：空列表、路径不存在于 `rootProject.allprojects`、目标工程缺 `jar` 任务或 `runtimeClasspath` 配置，三类违例均抛 GradleException 报出插件名、扩展名与违例值（不吞、不降级为跳过）。多目标按声明序串接；单目标串接结果与现状逐字节同构（parts + 各目标 runtimeClasspath.asPath + 各目标 jar 绝对路径，分隔符同 File.pathSeparator）。依据：P4 封装保护不变量（扩展的存在即声明了"必须有至少一个目标"的不变量，构造与每次读取都维持）；adopt-gradle-official-dsl 实测教训（预编译脚本的时序与 apply 形态）已在该变更收敛为 afterEvaluate 追加，沿用不另起。被否决备选：marker 插件（如 tny.demo-app）+ 从 integration 配置自动发现被标记依赖——否决理由：隔离面从"显式清单"变为"推导集合"，串染事故防线的审计对象消失（该清单正是事故后钉死的白名单），可观测性下降、行为差异风险上升，违背零差异红线；事故注释写明"受控隔离 classpath＝IT 自身产物 + demo 发布 jar"，受控二字体现在人工声明。

**D3 校验报错文案对齐既有对账风格。** 报红消息结构"谁（tny.it-demo-isolation）+ 什么（demoProcessIsolation.targetProjects）+ 期望（至少一个存在且可隔离的工程路径）+ 实际（违例值列表）"，与 project-checks 的组号对账文案同族，降低读者学习成本。依据：模式卷 M3 命名即文档的构建域等价物；P5。

**D4 事故来由注释留插件、目标数据入模块，注释归属即分层标准。** DemoPlayerObjectCodecableCodec 串染实证、forkEvery 说明等"为什么"留在插件文件头与块内；"是什么目标"由模块声明。依据：gradle-build-style 需求六（注释解释来由）与需求一（模块文件只写声明）的直接推论。

**D5 benchSuite 扩展与速览选择面统一。** tny.bench-suite 贡献 `benchSuite` 类型化扩展，五个属性对应现 ext 五件：routineFamily、devtestFamily、facilityProbes（正则清单）、routineAlgoArms（臂名清单）、quickRunExcludes（速览排除类名正则清单）。数据声明全部移到 tny-bench 模块文件；插件任务（jmhList/jmhSuiteVerify）与模块 jmh{} 块改读扩展。**速览选择语义从"单条负向前瞻正则"改为机制统一**：includes = (routineFamily + facilityProbes).join('|')（与完整规模档共用同一 include 面），excludes = quickRunExcludes（现声明一条 `.*PipelineCryptoMatrixBenchmark.*`）。语义注记：原 quickRun 正则 `(?!routine\.PipelineCryptoMatrixBenchmark)(routine|devtest\.SmokeBenchmark)` 的 include 面允许 `net.routineXyz`（routine 后无点）这类名字，join 形式要求 `routine\..*`——理论上 include 面收窄；实际基准类全部住族子包（jmhSuiteVerify 的目录归族对账在钉死此前提），两面对现存全集等值，以 `-PbenchFast` 选择面输出对基线比对定案（该探针正是"只验证哪些会被选中"的既有设施）。正向语义反而增强：原注释承诺"族新增类自动进速览"，但第二个矩阵类出现时会静默漏进——清单化后"自动进速览"与"显式排除"都成为模块数据的性质，不再有烙进正则的类名。模块文件行数守恒：净增数据声明块的同时，把 jmh{} 里 benchParams 的 Map 解析循环（split/each 中间变量累加，本就是需求一在模块文件的存量违例）移入插件 afterEvaluate 按"条件选择属性值"形态处理，模块回到 80 行界内。依据：P2（机制依赖清单契约）、P5（清单按变更原因归属：族清单随基准代码目录变，机制随 JMH 插件语义变）、P13（选择面探针可验证）。被否决备选：quickRun 整条正则原样入模块数据——否决理由：类名烙进负向前瞻的静默失效陷阱正是本次修复对象，保数据等价而弃机制统一是本末倒置。

## Risks / Trade-offs

- 【afterEvaluate 内 `ListProperty.get()` 对未决值的行为】→ 模块在正文 add（插件 apply 于 plugins{}，其 afterEvaluate 回调注册早于模块正文执行、触发晚于模块正文完成），值在触发时已定；探针：空列表用例必须报红，验证时机假设成立。
- 【多目标拼接与未来某目标 runtimeClasspath 为空】→ 校验已保证目标工程有 runtimeClasspath 配置；空集拼接仅产生多余分隔符的风险由"逐目标段先判非空再拼"消除，并在 IT 单目标回归里以字符串比对确认与现状等值。
- 【扩展类进入 buildSrc classpath 的编译成本】→ 一个 20 行类，增量编译实测记录；预期无感（adopt 变更已证 buildSrc 增量≈0）。
- 【误删插件后模块仍声明扩展】→ 声明语句在扩展不存在时报"unknown property"级别错误，属应用插件缺失的常规 Gradle 失败，可接受。

## Migration Plan

单次原子批次：插件重写 + 模块声明 + 校验类落地 → 定点探针（空列表报红、错路径报红、正常绿）→ integrationTest --rerun → clean build 与 tasks --all 零差异。回滚即两文件 revert。

## Open Questions

无。多目标场景的排序敏感性（哪个 demo jar 在 classpath 前部）在出现第二个真实目标前不产生裁决需求，语义已由 D2 声明序规则覆盖。
