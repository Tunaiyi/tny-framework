# Design

## Context

见 proposal.md 的 Why。已实测的事实底座：wrapper 8.14.5 下 `./gradlew build --warning-mode all` 稳定报五条"配置锁定后被改写"（父配置集中在 starter-basics 与 starter-net-netty4 的 api/compileOnly/testImplementation，告警时刻在子配置 beforeResolve；本轮复跑父名在 compileClasspath 等形态间浮动，属同一违例的归因口径差异）与一条 `LenientConfiguration.getArtifacts(Spec)`；另有两条 `:tny-benchmark:detachedConfiguration` 非上下文解析告警。治理册两轮的归因实验已证伪三个嫌疑（第七组契约边收缩、java 扩展块改写、对账守卫——单变量回退后告警计数不变），并以共享克隆对照坐实"纯净 HEAD 代码在 8.14.5 报同样六条"。剩余嫌疑集中于 `tny.java-module.gradle` 的两处既有形态：`:60-61` 的 `configurations.configureEach { exclude module: ... }` 排除块（其规则广播经 DefaultConfiguration 在锁定/物化时刻对已存在依赖施加——堆栈形态 `beforeResolve → AbstractModuleDependency.exclude → validateMutation` 与之吻合）与 `downloadDependencySources` 任务对独立配置的 `copy { from }` 解析。爆炸半径核对记录：codegraph 索引对 buildSrc 预编译脚本仅部分覆盖（治理册已核实），本册两触点的影响面以 grep 实证——`exclude module` 全仓书写点唯一（java-module），`downloadDependencySources` 无任务图消费方（不挂 assemble/check，仅手动执行）。

## Goals / Non-Goals

**Goals:**
- 升级追认入库并有验收判据；dependency-management 升至 1.1.7。
- 六条仓内告警全部清零或按新规格完成书面移交（benchmark 两条指名移交）。
- 发布 POM 与模块元数据对 8.5 时代基线零漂移，证明基线切换对发布面无损。

**Non-Goals:**
- 不做 Gradle 9 前瞻改造（只处置 8.14.5 输出中带 9.0/10.0 移除标记的项）；不评估关闭 `org.gradle.configureondemand`（incubating 提示非告警，配置缓存启用属另册决策）。
- 不触碰 `tny-benchmark/` 与并行会话在途文件；环境层 JVM 输出（native-access、CDS sharing）不入库内整改。

## Decisions

**D1 升级顺序：先 dependency-management 1.1.0→1.1.7，单变量复跑告警集，再决定仓内改造面。（P13：每步决定都有"哪条命令证明它"；M1：治理册已把 1.1.7 登记为本升级前置，不另造顺序）**
排除插件本身即"向配置施加规则"的插件，其 1.1.5 至 1.1.7 修复的正是新 Gradle 下的兼容假设——六条告警中可能有条目在插件升级后消失或改形。实验不过则回到原计划（java-module 两处改造）。备选否决：先改 java-module 再升插件——两变量叠在一起时告警集变化无法归因，违反单变量原则。（原则卷 P1、P2 不涉及；本条为构建面决定。）

**D2 五条 Mutating 的根因定位协议：最小实验优先，改造形态其次，不可保义即停。（P13、P12）**
定位两步：其一，在 starter-basics 上临时注释 java-module 的 `configurations.configureEach` 排除块复跑——告警消失即锁定该块与物化时序的交互为根因；其二，若未消失，按剩余嫌疑（tester 动态挂载 `add('testImplementation', it)` 的物化时序）同法单变量试验。修复候选形态（按侵入度升序）：把排除声明从 configureEach 全量广播改为按配置显式声明且早于任何依赖添加；或保持 configureEach 形态但把 java-module 的插件应用时点前移使规则先于依赖物化登记。两者都 MUST 保持"全配置排除 spring-boot-starter-logging 与 log4j-to-slf4j"的既有语义（回归判据：受影响发布 POM 的 exclusions 条目集合零漂移，纳入 D4 基线对照）。停条款：若两形态皆不能在不改变排除语义或插件应用顺序的前提下消警，本册按新规格走"书面移交另立册"回用户裁决，不带伤合入。

**D3 getArtifacts 告警改 artifactView。（P13）**
`downloadDependencySources` 中 `copy { from sourcesCfg }` 改为对独立配置取 `incoming.artifactView { }.files` 后拷贝——官方指引的替代 API；缺源码包的失败路径由 `catch (Exception)` 承接，missing/downloaded 计数语义不变（回归判据：对既有可解析样例复跑该任务，前后输出的两个计数逐值一致）。备选否决：整体删除该任务——它服务 IDE 源码包下载的日常用途，删除超出告警处置所需。

**D4 零漂移基线取件方式：新建 8.5 时代的对照取件点。（按 openspec/config.yaml 零差异验收基线抓样口径执行）**
在共享对象克隆（HEAD 即 8.14.5 提交前状态，wrapper 行改回 8.5 发行版 URL 后运行，克隆目录独立于主工作区避免 daemon 串扰）对 `tny-game-net`、`tny-game-basics`、`tny-game-starter-basics`、`tny-game-starter-net-netty4` 四个发布构件执行 `publishToMavenLocal` 到克隆独立 Gradle 用户目录，转存 POM 与模块元数据为 before 件；主工作区同四构件（先运行后转存）为 after 件，逐条目比对。daemon、locale、JDK 钉住按口径执行。

**D5 benchmark 两条的移交登记。（新规格"移交 MUST 指名承接册"）**
在 verification-notes 落移交记录，承接对象指名在途册 `refine-bench-routine-triggering`（其会话此刻仍在编辑 `tny-benchmark/build.gradle`）；全仓告警清零判据据此剔除该两条，核验时以移交件为准。

## Risks / Trade-offs

- [D1 升级插件后告警集若发生形变（增减），D2 的定位实验需重做一轮] → 接受：这正是 D1 排在前面的原因，单变量代价低。
- [排除声明形态改动可能影响所有 java 线模块的发布 POM exclusions 集合] → D4 的零漂移判据按构件逐条目兜底；starter-basics 与 starter-net-netty4 之外再抽两个不同装配线的发布构件入对照集。
- [并行会话持续编辑 java-module 或 benchmark 文件，实施窗口冲突] → 任务组间设 diff 复核步骤；java-module 若出现新的未提交改动即停回用户协调（治理册先例：改动入库后再触碰）。
- [克隆取 8.5 基线需下载旧发行版] → 一次性成本，网络不可达时判据降级为"与治理册归档卷既有元数据记录对照"，如实注记不伪造零漂移。

## Migration Plan

组序：1 dependency-management 升版单变量复跑（含告警集快照对照）→ 2 Mutating 根因定位两步实验（结果回写 design）→ 3 按定位结果实施修复（D2 候选其一）＋ D3 artifactView 改写 → 4 D4 基线取件与零漂移比对 → 5 全仓 `--warning-mode all` 计数收口（移交件除外）＋ clean build＋守卫门禁回归 → 6 D5 移交记录与本册 verification-notes 落档。回滚：D1 版本行、D2/D3 各自独立成组可 revert；wrapper 行独立提交（追认件先行）。

## Open Questions

- D2 若两个修复候选形态都可行，最终取哪个由实验的 POM 零漂移结果定（两者语义等价时取侵入度低者）——不影响任务分解，实施期收口。

## D1–D5 实施结果回写（2026-10-04，任务 3.3）

- **D1 结果：单变量命中根因。** dependency-management 1.1.0→1.1.7 后 `build --warning-mode all`
  复照：Mutating 五条与 getArtifacts 一条**全部清零**（复照件 verification/warnings-after-dm117-empty.txt，
  计数 0），`projects --no-configure-on-demand` 配置评估含守卫全绿。根因判定：六条告警的施动方是
  旧版插件在新 Gradle 下的 beforeResolve 规则施加路径（堆栈 `beforeResolve → AbstractModuleDependency.exclude
  → validateMutation` 与之一致），而非 java-module 排除块自身的书写形态。
- **D2 裁剪：** 定位实验（3.1/3.2）被 D1 单变量短路，不再执行；排除块与 tester 挂载时序经排除法无罪。
  4.1 无改动对象，其验收判据中"Mutating 计数 0"已由 1.1.7 满足，"exclusions 零漂移"并入 5.2 基线比对兜底。
- **D3 裁剪：** getArtifacts 告警同源于旧插件（本次 build 触发的解析路径由插件承担），downloadDependencySources
  的 `copy { from }` 在 1.1.7 下未再报该弃用；4.2 无改动对象。该任务改造留作未来需要时的指引（原文保留）。
- **D4 修正（before 件取法）：** 原"克隆回 8.5"改为更纯的单变量法——克隆取当前 HEAD（两册已入库态），
  在克隆内把 wrapper 回指 8.5、dependency-management 回指 1.1.0，即"基线切换前"状态；主工作区为切换后状态。
  两侧各 publishToMavenLocal 同六构件，比对即"8.5+1.1.0 对 8.14.5+1.1.7"的纯基线漂移，
  不混入两册已申报的合法元数据变化。克隆使用独立 Gradle 用户目录避免与主工作区缓存串扰。
- **D5 修正（移交范围）：** benchmark 两条 detachedConfiguration 告警在 D5 复照 build 形态下计数为 0
  （其触发路径在基准线自身执行面，本 build 未及）；移交记录照立，措辞改为"本册判据形态下未触发，
  若基准线册执行面复现由 refine-bench-routine-triggering 处置"。

## D4 二次修正（2026-10-04，克隆基线实践受阻）

克隆回指 8.5 的取件法被两个事实否决：其一，HEAD 已入库的册A代码使用 `ProjectDependency.getPath`
（8.6+ 才有的属性，"dependencyProject 弃用"的替代写法在 8.5 上直接配置失败——克隆实测报
"Could not get unknown property 'path'"），即 HEAD 码对 Gradle 的最低要求已被合法抬到 8.6+；
其二，回到册A入库前的提交点又混入两册已申报的元数据变化，不纯。修正判据：**发布元数据零漂移
的对照变量收缩为 dependency-management 1.1.0 对 1.1.7 单变量**（主工作区同码同 wrapper 临时回改
插件行取 before、还原取 after）；wrapper 8.5 对 8.14.5 的发布面影响经由"生成发布元数据的插件链
不随 wrapper 版本改变"与本单变量对照合并推定，判据降档如实入册。附带收获：该失败形态同时证明
本仓 Gradle 基线的可用下限现为 8.6+，wrapper 追认提交（84ffc707）与代码事实一致而非越前。
