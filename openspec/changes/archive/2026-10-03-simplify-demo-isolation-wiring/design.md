# Design

## Context

动机见 proposal.md - Why。现状三段协议：demo 贴标记（存在性）→ 隔离插件在全工程评估完后遍历 IT 依赖筛标记 → 配置期逐工程取 jar 任务挂依赖、doFirst 拼串。相关现场事实：工程评估顺序按字母（integration-test 早于 net-demo），这正是上一变更把撮合从 afterEvaluate 推迟到 projectsEvaluated 的原因（marker-declared-isolation-targets design 实施修正注记）；根工程跨工程清单的先例是 tny.publish-gate 第 84 行的 publishTagCheckMemo（远端标签核对结果按分支缓存在 rootProject.ext）。

爆炸半径核查（按规则记录）：codegraph 不覆盖构建脚本，无适用符号；消费面四处——integrationTest 任务图、it.demo.isolatedClasspath 属性及其执行期消费方 DemoAppProcess.java、tasks --all 清单、IT 模块发布禁用。

两卷检索（M1）：本案属"把补丁式时序换成天然正确的数据流"，无对象模式可借；引用构建域自身先例：登记式自报（publishTagCheckMemo 的根清单缓存）与配置携带任务依赖（Gradle 对 configuration 的依赖推断，仓内 processResources/resources 同款机制已在用）。

## Goals / Non-Goals

**Goals:**

- 删除"等全部工程评估完再点名"的时机安排，撮合正确性不再依赖对评估顺序的记忆。
- 插件文件 3→2，单条链路读法收敛到一处。
- 行为零差异（字符串、任务图、防线、外部清单）。

**Non-Goals:**

- 不改 integration-testing 能力的任何外部行为（任务名、标签、docker 语义、CI 档位）。
- 不动 tny.bench-suite、版本目录、settings 解析面等无关件。
- 不重命名 tny.demo-app（名称即"演示应用"，通用词，无发明问题）。

## Decisions

**D1 demo 侧自报登记，替代"别处遍历筛标记"。** tny.demo-app 应用时执行 `rootProject.ext.tnyDemoBlueprints += project`（清单缺失时在 tny.integration-test 应用或 root 初始化处兜底建空表——登记可能先于消费方初始化，取值兜底放读侧）。时序论证：登记发生在 demo 自己评估的瞬间，与任何工程先后无关；消费方读取发生在 integrationTest 的 doFirst（执行期），彼时全部配置期结束，清单必完整。依据：P4（把"何时可见"从调用方纪律变成数据本身的性质）、P2（消费方只依赖"清单里有工程对象"这一契约）。被否决备选：保留 projectsEvaluated+hasPlugin——否决：正确性靠"记得在评估完后跑"，正是本次要消除的隐性知识；保留字符串清单声明——否决：用户已两轮定稿双清单/伸手问题。

**D2 任务依赖改由集成运行时类路径配置携带，隔离串拼装保持在 doFirst。** `dependsOn configurations.integrationRuntimeClasspath` 使 demo jar（作为该配置的构件）的生产任务自动进入依赖，无需跨工程 `tasks.named('jar')`；doFirst 内对登记清单各工程取 `configurations.runtimeClasspath.asPath` 与 jar 产物路径，顺序仍为"IT 产物 + 各蓝图（类路径段、jar 段）按登记序"。等价性：单蓝图现状下 rc 与 jar 值同源同值，字符串逐字节不变；由 dry-run 任务图比对与 IT 端到端复跑双证。被否决备选：仍逐工程挂 jar 任务依赖（执行期挂依赖过晚，且保留跨工程任务查询）。

**D3（已被文首修正注记取代，原案存档于此）隔离接线并入 tny.integration-test。** 合并后 51 个普通模块应用同一插件：登记清单恒空 → 无依赖追加、无 doFirst 附加，行为逐字节不变（探针：任一普通模块 `--dry-run` 任务图与合并前一致 + 全量构建零差异佐证）；集成测试模块额外保留发布任务禁用段（原样迁移）。插件文件净 3→2（demo-app、integration-test 两个留下）。被否决备选：维持三文件——否决：用户诉求本体是减部件，空清单分支无额外风险。

**D4 防线迁移不减：** 集成测试模块在接线装配时校验登记清单非空，为空即配置期报红，文案沿用现版两成因提示（未声明蓝图依赖 / 蓝图未贴标记）。串染事故白名单语义不变：可上隔离面的工程仍须同时满足"被 IT 声明为集成依赖"与"自报为演示应用"。

## Risks / Trade-offs

- 【rootProject.ext 跨工程可变清单】→ 配置期单线程、模式与 publishTagCheckMemo 先例一致；键名定死两处（登记侧、读取侧）由同一属性名注释互指。
- 【dependsOn 配置携带的依赖面比"仅 demo jar"宽（整个集成运行时类路径的生产任务）】→ 集成类路径中的工程构件本就都需要构建后才可跑 IT，实际任务图差异需 dry-run 比对确认；若出现新增任务边，回到用户处选择"保留逐任务挂法+登记清单读时兜底"折中，不静默放宽红线。
- 【doFirst 读 files() 触发即时解析】→ 与现状同（现在也在 doFirst 取 asPath/archiveFile），无新增。
- 【51 模块共享接线代码路径】→ D3 空清单零动作论证 + 探针；若探针发现任何图差异即回退该合并决定（文件保持拆分），登记简化收益保留。

## Migration Plan

单原子批次：基线抓样（integrationTest --dry-run 有序任务清单、若干普通模块 dry-run 任务清单、tasks --all、全量构建绿）→ 三文件改造 → 探针（空登记报红、IT --rerun 绿、任务图比对、全量零差异）→ 记 verification-notes。回滚三文件整体 revert。

## Open Questions

D2 的依赖面宽窄以 dry-run 实测为准；若实测引入新任务边且判为不可接受，备选形态已在风险条写明，届时回用户处裁决后再继续。

## 实施修正注记（写第一行代码前的复核结论）

- D3 撤回：接线只应发生在集成测试模块（其余 51 个模块既无蓝图依赖也无发布禁用诉求），并入 tny.integration-test 就需要给它再造一个"本模块启用隔离接线"的选择加入开关——开关本身是新的跨文件协议，文件数收益抵不过概念数成本。定稿形态：三文件保留；tny.demo-app 增加自报登记三行；tny.it-demo-isolation 删除"全部工程评估完后遍历依赖筛标记"与"逐工程引用 jar 任务"两层协议，任务依赖改由 configurations.integrationRuntimeClasspath 携带（集成测试任务自身的类路径属性本就隐式要求 demo jar 构建，3.2 的 dry-run 比对负责证实任务图零新增边，若有差异按 design 风险条停下回用户处）；多蓝图时的拼接顺序语义定稿为"按集成测试模块的直接依赖声明序"（与原 projectsEvaluated 版的声明序一致，登记序不作为顺序依据）。
- 防线校验时机：配置期（评估完毕后）→ 执行前（integrationTest 的 doFirst，测试启动即验，先于任何 fork）。正确构建无可观察差异；误配置报错从"每次构建必报"变为"跑到集成测试才报"，与 proposal"执行前校验"措辞一致，tasks 3.1 探针命令相应修正。
