# Design

## Context

动机见 proposal.md 的 Why 一节。此处记录决定方案形态的现状与约束。

上一变更（sweep-gradle-build-style）归档后，脚本体系的现状是：根 build.gradle 六十一行（引入面），`gradle/` 目录下十七个 apply-from 脚本加两份数据文件（released-legacy.txt、wrapper/），坐标与版本由 `gradle/dependency.gradle` 的 ext.libs（约一百四十条）与 ext.vers（约三十条）两张 Groovy Map 承载；四十八个模块构建文件以 `libs.xxx` 具名引用消费，根侧消费点集中在 subprojects-baseline 的 dependencyManagement 段（二十五处 GString 拼接）。全仓 `.gradle` 文件已对八条需求存量清零。

约束与事实（迁移前逐一核实）：Gradle 8.5 原生支持版本目录（7.4 起，`gradle/libs.versions.toml` 自动注册为 `libs` 目录，无需改 settings.gradle）与 buildSrc 预编译脚本插件；configure-on-demand、并行构建、无配置缓存的现状全部保留。版本目录的类型安全访问器只注入工程构建脚本，不注入 buildSrc 预编译脚本——约定插件内取坐标须经 `VersionCatalogsExtension` 运行时 API，这是 D3 的直接动因。勘察发现一处既有地雷：tny-game-doc-gradle 有一行裸表达式 `libs.'groovy-all'`（无任何作用，且连字符别名在新机制下解析行为不同），迁移时一并删除。

爆炸半径核查（设计规则要求以 codegraph 分析，结论如实记录）：与上一变更同——`.gradle` 与 TOML 不在 codegraph 的 Java 符号图内，analyze 查询无适用符号节点；本变更同样不触碰任何 Java 类型。真实消费面经引用扫描确定为四类：模块文件的 `libs.` 访问（四十八文件）、根与脚本对 `gradle/` 的 apply 链（二十五处）、CI 工作流按任务名的调用、以及 `gradle.properties` 属性在 plugins 块与 settings 的取用。四类消费面在 D2、D3、D4、D6 下逐一给出保持策略。

方法论两卷检索说明（M1 先例优先条款要求的检索）：模式卷第二节决策表面向 Java 对象运行时模式，无构建脚本载体行；本仓库内构建脚本域的既有先例正是 gradle-build-style 规格自身。本变更不是绕开先例另起炉灶，而是按 P12"行为变更先于代码变更"的程序，以规格差量正式修订先例后再迁结构——M1 条款在此的满足方式即是这份差量本身。抽象时机判据：编码钉定块重复四处、仓库表两处、零缓存两处、logging 排除两处、createProject 两处——P10 三次法则已满足，共享插件化有实证依据。

## Goals / Non-Goals

**Goals:**

- 以版本目录作为第三方坐标的具名事实源，坐标引用获得声明式载体、编辑器补全与构建期校验；以约定插件承载装配与编排，公共配置块单一实现。
- 零差异红线（用户在提案阶段拍板）：任务名集合与任务图执行面、解析版本、构件内容、发布 POM 元数据、CI 调用面对基线逐项 diff 为零。
- 修订后的 gradle-build-style 规格与新形态一致，后续"触碰即改"有单一可对照的载体条文。

**Non-Goals:**

- 不迁移到 Kotlin DSL（Groovy 保持；预编译脚本亦用 Groovy）。
- 不引入配置缓存或关闭 configure-on-demand（性能议题与本变更无关）。
- 不升级 Gradle 或任何第三方插件版本；不改变 BOM 级大头版本的取值。
- 不用平台原生 `platform()` 替换 io.spring.dependency-management 插件（否决理由见 D5）。
- 不动 `gradle.properties` 中的大头版本键、daemon 参数与仓 URL 属性；不动 settings.gradle（默认目录路径自动注册）。
- 不重排模块构建文件内容（访问器名保持，模块面接近零改动是本设计的核心约束之一）。

## Decisions

**D1 官方 DSL 设施双件套作为目标形态。** 版本目录接管 dependency.gradle 两张表；全部 apply-from 脚本迁移为 buildSrc 预编译脚本插件，`gradle/` 目录最终只剩 libs.versions.toml 与数据文件。依据：P12（先改规格——本变更的 specs 差量——再动结构）、P5（插件按变更原因分域）。被否决备选：仅建目录不建插件（否决：动态执行与重复块两大痛点原样留存，规格须长期兼容双载体）；私有扩展 DSL（否决：用户提案阶段已裁决弃用，新人理解私有约定的成本高于官方机制收益，模式卷 M2 精神）。

**D2 目录别名沿用现行下划线键名，模块文件访问器名逐一不变。（实施修正，实测推翻：Gradle 8.5 的版本目录访问器把下划线连同连字符、点号一样用作分组分隔符——`commons_io` 生成 `libs.commons.io` 而非 `libs.commons_io`，且叶子与更深前缀在同路径下互斥（`spring_boot_starter` 与 `spring_boot_starter_test` 冲突）。目录别名因此改为单段 camelCase 键（`commonsIo`、`springBootStarterTest`），对全部引用点（67 个别名、42 个文件）做 1:1 机械改写；"模块引用语义零改写"的目标以改写等价性保住， POM 逐字节一致与依赖解析全等已证明。）** `commons_io`、`jackson_databind` 等键原样写入 TOML（下划线是合法别名字符且不产生嵌套分组），四十八个模块文件的 `api libs.commons_io` 等引用零改写。依据：P13（可验证性：迁移批次以构建成功与依赖报表 diff 直接证明访问器等价）；变更半径最小化。被否决备选：按 Gradle 风格指南改用连字符嵌套别名（`commons-io` → `libs.commons.io`）——否决理由：一次性改写四十八个文件的全部引用点，收益仅是书写风格，风险与噪音不成比例；tny-game-doc-gradle 的裸表达式 `libs.'groovy-all'` 是唯一必须删除的例外（无作用死语句，连字符别名在新机制下反而制造解析歧义）。

**D3 版本目录消费分两类桥接：工程脚本用访问器，约定插件用运行时 API。** 模块与根构建脚本直接以 `libs.xxx` 访问器声明依赖（现行写法不变）；subprojects-baseline 的 dependencyManagement 段迁入约定插件后须经 `project.extensions.getByType(VersionCatalogsExtension).named('libs')` 取依赖对象再转坐标字符串，为此在 buildSrc 提供一个单点辅助闭包（如 `coord('commons_io')` 返回"组:名:版本"字符串），杜绝各处手写取用样板。依据：Gradle 8.5 的访问器生成范围是事实约束；P5（取用方式集中一处）。被否决备选：让 baseline 留在 gradle/ 作 apply-from 脚本以继续吃 ext.libs——否决理由：与 D1 的单一载体相悖，ext 表必须死。核验手段：迁移批次内以 `./gradlew :tny-game-net:dependencies` 报表对基线 diff 证明 coordinate 字符串逐条相等（GString 形态差异允许出现在声明展示面，解析结果必须全等）。

**D4 约定插件三层结构：装配线插件、发布链插件、共享块插件。** 装配线：tny.subprojects-baseline、tny.plugin-module、tny.java-module、tny.git、tny.project-checks、tny.central、tny.release；发布链：tny.publications、tny.publish-gate、tny.publish、tny.publish-plugin、tny.tny-module；模块专属：tny.integration-test、tny.it-demo-isolation、tny.bench-suite、tny.bom-platform、tny.dependency-sources；共享块（被装配线组合）：tny.repositories（镜像与独占路由）、tny.compile-baseline（编码钉定与 -parameters、fork）、tny.cache-policy（动态依赖零缓存）、tny.logging-exclusion（排除 Boot 默认 logging 绑定）。根脚本对三条装配线各保留 `configure(集合) { apply plugin: ... }` 的按序插件引入（声明式集合驱动，见 D6）。（实施修正：预编译脚本插件嵌套应用另一脚本插件在 Gradle 8.5 触发 ClassLoaderScope 缺失——约定插件之间因此互不 apply，各线所需共享块与兄弟插件的引入序统一上收根脚本装配线行；核心 binary 插件仍可写各插件头部 plugins{} 块。实测过程与影响记录见 verification-notes 批次 4-5 节。）依据：P5（按变更原因分层：仓库路由因镜像事故而改、编译基线因编码纪律而改，互不牵连）、P10（重复已实证三次以上）。被否决备选：十七个脚本一比一平移为十七个插件不做共享块——否决理由：提案要解决的"逐字重复"原样保留，D4 的收编正是用户诉求本体。

**D5 保留 io.spring.dependency-management 插件与既有 BOM 导入语义。** dependencyManagement 的 imports/dependencies 块逐条平移（版本串改为从目录取），generatedPomCustomization 关闭等配置原样。依据：零差异红线——该插件参与发布 POM 的 dependencyManagement 段生成，换用 Gradle 原生 `platform()` 会改变 POM 元数据面的结构（下游解析方可见），违反 P11"公共构件元数据是合同"。被否决备选：借 DSL 化之名删掉该插件——否决：那是行为变更，须另行立项并标 BREAKING 评估。

**D6 装配线成员资格仍由根 ext 单点派生，插件不自行判定成员。** javaProjects/gradleProjects/moduleProjects 三集合的派生谓词留在根脚本（现状六行，合规），`central.gradle` 迁移后的 tny.central 仍消费根 ext 集合做 nmcp 聚合，组号对账与零发布合同（tny.project-checks）语义不变。依据：P5（成员资格的变更原因是"装配线划分调整"，与每条线内部配置无关）；spec 差量需求一 Scenario 的根脚本单页形态。被否决备选：五十字节文件各自声明 `plugins { id 'tny.java-module' }` 自报家门——否决理由：成员事实源散落到五十个文件，central 聚合与对账需要反推插件存在性，跨工程校验从声明面退化为探测面，且模块文件全部要动，D2 的"模块零改动"落空。

**D7 版本目录的分组与注释即文档。** TOML 按现行 dependency.gradle 的族分组（slf4j/log4j、jackson、netty、commons、graalvm、spring 家族、测试件、poi/testcontainers 等）保留 `[versions]` 与 `[libraries]` 两段，`[plugins]` 段不启用（插件版本继续在根 plugins 块与 gradle.properties，理由见 D8）；文件头注释写明与 gradle.properties 的事实源分工。多条目共用的版本（redisson、graalvm、jade、icu4j、netty 分类器件）入 `[versions]` 用 version.ref，单件独用版本内联。依据：spec 差量需求六（目录须解释分工）与需求七（目录按族分组）。被否决备选：全部平铺无分组——否决：扫读测试要答"依赖什么"，分组即目录。

**D8 gradle.properties 保留 BOM 级大头版本与插件版本取用。** springBootVersion（根 plugins 块 GString 引用）、log4j2/netty/protobuf/slf4j/jackson/groovy/testcontainers/alibabaCloud/springCloud（BOM 导入句）、javaVersion、encoding、四个仓 URL 属性等键名与取值全部不动。依据：plugins 块与 settings 的插件解析发生在版本目录访问器可用范围之前，强行迁移会引入"版本从哪来"的第二套时序特例，违背简洁目标；spec 差量需求三把这两处的分工写成正式合同。被否决备选：插件版本也进目录 `[plugins]`——否决：根 plugins{} 用 `alias(libs.plugins.x)` 在 apply false 与 spring boot 版本插值混合场景下改造面大，收益仅是形式统一，违反变更半径最小化。

**D9 buildSrc 的构建脚本自身遵守 gradle-build-style。** buildSrc/settings.gradle 仅含仓库声明（默认 portal 可用则整体省略）、buildSrc/build.gradle 只声明 groovy-gradle-plugin 与 gradleApi，无程序性逻辑；预编译脚本不引用第三方插件的类做编译期符号，且第三方插件（grgit、nmcp、io.spring.dependency-management）一律由根构建脚本先行应用、约定插件以头部前置注释声明该约定（实测 Gradle 8.5：buildSrc 作用域无法解析父构建类路径上的插件 id），避免 buildSrc classpath 引入 nmcp/dependency-management 等插件依赖。依据：规格适用域"全部 .gradle 文件"无豁免；P13（编译期依赖越少，失败越早且越少）。被否决备选：在 buildSrc 声明插件坐标以便预编译脚本写 plugins{} 块——否决：把全仓插件栈复制进 buildSrc classpath，双处版本维护正是需求三要消灭的形态。

**D10 迁移批次顺序按依赖倒走：目录先行（原子切换），插件按 git→发布链→装配线→模块专属→根重写收尾。** 目录与 ext 双表不能长期共存（ext.libs 与目录访问器同名解析存在遮蔽风险），故建目录、切全部消费点、删 ext 表在同一个批次内完成并以一次全量构建验收；插件迁移逐域替换，每域替换后旧脚本立即删除，不保留双轨。依据：P13（每批次一个可执行验收点）；上一变更"先机械后结构"的批次经验。被否决备选：先插件后目录——否决：插件内取坐标的辅助闭包（D3）依赖目录存在，倒序会把桥接代码写成临时双轨。

## Risks / Trade-offs

- 【版本目录访问器对某些别名生成冲突（如键名与目录保留字相撞、`default`/`bundles` 类语义）】→ 构建即暴露；预演批次跑 `./gradlew :tny-game-net:dependencies` 与根配置评估，冲突别名在 TOML 内改注释协商名并同步该键的全部引用点（预计极少）。
- 【D3 的 coord() 辅助与插件取用改变了 dependencyManagement 声明的展示形态】→ 红线只约束解析结果与 POM 字节；验收含 BOM POM（tny-game-bom 的 constraints 清单）与代表性模块 POM 的 dependencyManagement 段对基线逐条比对。
- 【buildSrc 使每次构建多一道编译】→ 实测记入验证记录（预计数秒、增量跳过）；若实测显著（>10 秒）回到用户处重新权衡 D1，不擅自放宽。
- 【预编译脚本与 apply-from 的闭包语义差异（delegate/owner 链）】→ 迁移逐域构建；configure 集合驱动保留在根（D6）使"脚本在哪个工程求值"与原状一致；发现语义漂移即停当批次回退该域。
- 【grgit/nmcp 等插件运行期 apply 字符串形态在无编译期检查】→ 与今日 apply-from 完全同级（今天同样无检查），不新增风险；插件 id 拼错在 apply 即报，构建验证覆盖。
- 【catalog 遮蔽：若删 ext 表前仍有脚本按 map 语义用 libs（如 `libs.'groovy-all'`、`libs.each`）】→ 迁移批次 1 以 grep 清查全部 `libs` 的非访问器用法（已知一处裸表达式即此类），一并处置。

## Migration Plan

1. 基线采集：与上一变更同款三份（tasks --all、四模块 dependencies 报表、publishToMavenLocal 构件清单）另加两份 POM 快照（tny-game-bom 与 tny-game-net 的已发布 POM）。
2. 目录批次：建 `gradle/libs.versions.toml`（D2/D7 分组），切换全部消费点（模块访问器名不变、baseline 的 GString 点、dependency.gradle 内交叉引用），同批删除 dependency.gradle 的 ext 两表与 tny-game-doc-gradle 裸表达式；验收：全量构建绿 + 依赖报表对基线解析全等。
3. buildSrc 批次：骨架（D9）+ 四个共享块插件；验收：共享块单测式探针（对两条装配线的 repositories/compile 行为以 `./gradlew :tny-game-net:jar :tny-game-doc-gradle:compileGroovy` 定点跑）。
4. 装配线批次（按 D10 顺序逐域）：git/project-checks → 发布链五件 → 三条装配线 → integration/bench/bom/dependency-sources；每域替换后旧脚本删除并跑全量构建。
5. 根脚本收尾重写：plugins 块不动，引入面改为插件声明与三行 configure 装配；验收：`wc -l` 界内、`tasks --all` diff 零。
6. 终验：三份基线 + 两份 POM 全 diff 零；对照修订后 gradle-build-style 逐条扫读自检；`centralCheck`、门禁 `-m` 干跑、`releaseCutAndTag -PdryRun`、publishToMavenLocal 凭据探针复跑。

回滚：每批次一个提交边界，问题批次单独回退；`gradle/` 与 buildSrc 不同域双轨，回退即恢复该域旧脚本引用。

## Open Questions

- 无。TOML 别名合法性、coord() 输出形态等实现期才知道的未知数，均属"验证探针当场揭晓、不改变任务拆分"的细节；若 coord() 与 dependencyManagement 的字符串匹配在实测中发现形态不合（如插件对版本范围串的特判），属于 D5 保留插件语义下的局部适配，回到本节记录并当批修正，不影响方案结构。

## Compatibility Impact

本变更不触及公共 Java API 与报文协议（规格规则要求的 Compatibility Impact 小节此处按发布面记录）：发布坐标（group/artifact/version）、POM 元数据结构、构件内容以基线 diff 锁定不变，下游业务工程与 starter 装配无感知；CI 以任务名调用，任务名集合保持；docs/release-process.md 描述的发布任务语义不变。真正的兼容性红线是 D5（保留 dependency-management 插件）所保护的 POM dependencyManagement 段。
