# Design

## Context

见 proposal.md 的 Why。当前状态的关键事实（均经 2026-10-03 六维审计与构件元数据实测坐实）：`buildSrc/src/main/groovy/tny.dependency-management.gradle:20-27` 的八条 BOM 导入语句按书写顺序合并，后导入者覆盖先导入者、块内显式托管声明覆盖全部 BOM；log4j-bom（第 20 行）排在 spring-boot-dependencies（第 22 行）之前，导致声明的 log4j2 2.22.1 被 Boot 的 2.21.1 覆盖，已发布的 Gradle 构件元数据实测烤入 2.21.1。slf4j-parent 导入自身不管理任何 slf4j 坐标，生效靠其父链恰为官方 slf4j-bom。20 项集中托管把 jetcd-core 传递要求的 guava 33.0.0-jre 强制压回目录声明值。`tny.module-checker.gradle` 是仓内既有的配置期跨工程对账插件，现承担组号对账与零发布合同两项校验，其头注释自述"负责且仅负责"这两项。引擎边使用面实测：tny-game-net 主源码在两处方法体内实例化 `GroovyExprHolderFactory`（CommandPluginHolder.java:62、DefaultMessageDispatcher.java:32）；tny-game-basics 主源码十余个文件使用 `ExprHolder`/`ExprHolderFactory` 契约类型（全部来自 tny-game-expr 接口包）；tny-game-doc 的 mvel 使用收敛在包私有类 MVELExporter（MVELExporter.java:34 无 public 修饰）；tny-game-redisson 对 tny-game-boot 的引用为零处（含测试源码）。codegraph 索引对 buildSrc 的 Groovy 脚本函数覆盖有限（`ModuleSetting.enableUnpublished` 入边冲击仅一处、风险级低），模块边的爆炸半径以已发布 POM 的 compile 域与源码 grep 为准，两条证据链在审计中已互相印证。

约束：全部 .gradle 形态受 `gradle-build-style` 规格约束（程序性行为只放任务动作块或约定插件脚本）；基线抓样遵守 config 的零差异口径唯一权威文本；`tny.java-module.gradle`、`tny.benchmark-module.gradle` 与 `tny-game-integration-test/build.gradle` 上有工作区未提交改动和在途变更（move-bench-suite-selection-into-plugin、trim-it-module-dependencies-block），本册对它们的触碰必须先处理与在途改动的先后关系。

## Goals / Non-Goals

**Goals:**
- 让"事实源声明什么，解析就生效什么"成为配置期机器强制的合同（规格差量 gradle-build-style 的"托管版本面在配置期与声明事实源对账"需求）。
- 修复三处已实证的名实失守与脆弱形态（log4j 次序、slf4j 父链巧合、guava 强制降级），并终结 grpc 无托管层状态。
- 把"发布线构件依赖为发布级形态"变成带豁免登记的机器门禁（规格差量 release-versioning）。
- 清除死键、死声明、死边、悬空引入与注释失实，使账实相符。

**Non-Goals:**
- 不升任何第三方版本数值（tools-template、quartz 的转正、jackson 等 CVE 升版批、alibaba 线对齐、slf4j 与 log4j2 升修复线全部归后续版本升级册）。唯一例外经用户裁决记录在案：guava 声明值升钉为 jetcd-core 要求的最低满足值（见 D3 勘误），因其本质是解除强制降级所迫、且不改变 jetcd 之外的任何请求语义。
- 不动 tny-game-mongodb 对 boot 的真实耦合（需接口下沉，另册）。
- 不修复已发布 POM 中 epoll 分类件缺版本元素（POM 生成形态问题，另册处置）。
- 不触碰 tny-benchmark 与 tny-game-integration-test 的构建文件（在途变更的编辑区）。

## Decisions

**D1 对账守卫的形态与时机：配置期读取托管合并模型，落点为 module-checker 的第三项校验。（P12、P13；模式卷 M1 先例优先——仓内"配置期跨工程对账"已有 gradle.projectsEvaluated 形态的现成载体，不另立新插件）**
守卫在 `gradle.projectsEvaluated` 时机对各工程读取依赖托管插件的公开合并模型（托管生效版本映射），逐一核对 `gradle.properties` 具名声明的版本族所对应的关键坐标（每族取版本目录中该族的具名别名所映射的坐标），生效值与声明值不符即抛错并逐项列出坐标、声明值、生效值。核对范围限于八条 BOM 导入所辖的族；20 项显式托管条目因插件语义保证其本身即最终生效值，不列入对账。备选一并否决：注册执行期对账任务跑依赖树比对——只有执行任务才暴露问题，违背"任何构建都受守卫"的规格意图，且依赖树报表本是供人评审的证据而非机器门禁。备选二否决：CI 流水线加脚本比对——绕过了构建系统本身，本地构建不受保护，违反 P13 的可验证性落点。前置风险见 Risks 第一条：托管模型公开 API 的读取形态须先行探针实测，探针不成立时回到本差量改规格而不是静默降级。

**D2 log4j 修复用导入句移位，不用 Boot 属性覆写。（P13；M1：netty、jackson、testcontainers 三族已在"显式导入排在 Boot 之后"的先例形态下工作）**
把 log4j-bom 导入语句移到 spring-boot-dependencies 之后，并在 imports 块头部注释成文"后导入者覆盖先导入者；凡需压过 Boot 管理面的显式大头版本一律排在 spring-boot-dependencies 之后"。备选否决：在 `gradle.properties` 写 Boot 的属性覆盖名（log4j2.version）——那会让同一版本在两个键名下双写（现有 log4j2Version 键与 Boot 属性名），事实源分裂恰是本册要消灭的形态。

**D3 guava 强制降级的修复 = 升版本目录声明值到 33.0.0-jre，保留显式托管条目。（P13：原机制被实测证伪后依证据修订；路线变更经用户 2026-10-03 裁决"选项一"）**
初版方案拟"把 guava 摘出集中托管、交回 Gradle 原生最高版本裁决"。实施验证（任务 5.1 前置试验）证伪了该机制：摘除托管条目后，etcd 工程解析中 jetcd-core 的 33.0.0-jre 请求仍被压回 32.0.1-jre，dependencyInsight 标注 "selected by rule"；DEBUG 日志定位根因为依赖管理插件内部的隐式托管收集器——凡带具体版本号的直连依赖（`api libs.guava` 的目录内联版本即属此类）都会被自动登记为该工程全配置范围的强制托管版本，因此直连声明本身就是施压源，摘除显式条目不改变结果；对照试验（同版本组合、不装该插件的最小工程）解析确实取得 33.0.0-jre，证明原生裁决无问题、问题在插件语义。结论：在该插件框架内，唯一能让全工程 guava 满足 jetcd 要求的修法是把钉值本身升上去。实施：版本目录 guava 声明 32.0.1-jre 升为 33.0.0-jre（jetcd-core 0.7.7 POM 的 compile 声明值，即最低满足值），托管条目保留。备选一并否决（初版方案）：摘除托管条目交回原生裁决——实测无效，本条即其勘误记录。备选二否决：直连声明去版本号并以 constraints 表达下限——无版本直连在该插件语义下无托管源即解析失败，且破坏"带版本条目内联即事实源"的目录分工。影响面记录：全图 guava 由 32.0.1-jre 统一上移至 33.0.0-jre（七处直连模块与其传递消费方），是本册唯一版本数值变更，其兼容性由 5.2 的七模块测试与报表对照背书。

**D4 grpc 托管层 = 官方 grpc-bom 导入 + 版本目录与托管清单补 vertx-grpc 单条。（P13：纳入 D1 守卫的核对族；P1 无关——纯构建面）**
`gradle.properties` 新增 `grpcVersion` 键（初值取 jetcd-core 0.7.7 当前传递的 1.60.0，等值收编），grpc-bom 按 D2 的次序契约排在 Boot 之后。vertx-grpc 属 io.vertx 组、不在 grpc-bom 管辖，以集中托管清单补一条钉现值 4.5.1。备选否决：导入 vertx-bom——为一个传递构件把整个 vertx 族拉进托管面，扩大了对外约束与对账范围，比例失当（P10 三次法则的同源克制精神：抽象与覆盖面要等证据）。备选二否决：放任传递裸奔——审计已证实多 grpc 来源共存时无统一裁决，正是本册要终结的形态。显式托管清单条目集合因此从 20 变 21，清单注释的"保持 20 项不变"表述同步改为"条目增删须经变更册裁决"。

**D5 slf4j 导入坐标替换为官方 slf4j-bom。（等值替换，P13 的可验证性由 D1 守卫背书）**
`org.slf4j:slf4j-parent` 改为 `org.slf4j:slf4j-bom`，版本键不变，解析结果预期逐坐标等值（父链生效本体即该 BOM）。备选否决：slf4j 族改走显式托管清单——BOM 族走 BOM 导入是 netty 一族的既有正形，把整个族拆成散条目反而丢失随官方发布的构件覆盖。

**D6 发布形态门禁挂在发布任务前置校验，豁免登记为仓内清单文件。（M1：release-versioning 既有门禁（分支白名单、历史版本清单、标签存证）全部是发布执行现场的校验形态，照抄该形状；P12：行为契约已在规格差量先行成文）**
校验对象为发布线构件的 api、implementation、runtimeOnly 配置声明的外部坐标，命中预发布后缀即拒绝发布；豁免清单仿历史版本清单文件先例入版本库，每条须带理由与处置去向，门禁每次执行把当前豁免条目打进输出。备选否决：配置期报红——tools-template 与 quartz 现状就会让每一次日常本地构建失败，开发线寸步难行，门禁语义（只在发布时生效）恰是规格与实现的分工。豁免条目初版两条：com.funs.game:tools-template:0.0.1-SNAPSHOT（上游无正式版，去向：待立的模板件发布册）、org.quartz-scheduler:quartz:2.5.0-rc1（去向：版本升级册）。

**D7 引擎边收缩的形状：实现边降 implementation，契约边补直达 api。（P1 抽象与实现分离、P2 依赖倒置——这两条是本册在模块图上唯一直接命中的命脉原则；P13：每条边改动都有"删实现模块抽象层仍编译"的判据可验）**
tny-game-net 与 tny-game-basics 的 `api project(':tny-game-expr-groovy')` 降为 implementation；两模块各补 `api project(':tny-game-expr')` 直达边（net 的公开构造签名接收 ExprHolderFactory，basics 公开面十余文件使用契约类型，实测于 Context 节；此前两模块的接口编译全靠引擎实现模块传递获得，属 P2 反例形态）。tny-game-doc 的 `api project(':tny-game-expr-mvel')` 降为 implementation（mvel 类型仅出现在包私有类，公开枚举 OutputType 只暴露自身 Exporter 抽象）。tny-game-redisson 的 `api project(':tny-game-boot')` 直接删除（源码零引用，属死边）。备选否决：api 保留、对 POM 做 exclusions 收窄——exclusion 清单要随传递树维护且 Gradle 与 Maven 两发布形态需同写两份，比配置选型更脆。运行面不受损：implementation 仍进 runtimeElements 与 POM 的 runtime 域。
实施期全仓编译扫描另暴露三处仓内连带修正（形状同为"使用者显式自声明"，对外逐配置暴露面与收缩前等值，留档见 verification-notes 第 7 组）：其一，tny-game-starter-basics 的自动配置直接 import mvel 类型（BasicsAutoConfiguration.java:27），自声明 `api project(':tny-game-expr-mvel')`；其二，tny-game-starter-net-netty4 的自动配置直接 import expr 契约与 expr.groovy 类型（NetAutoConfiguration.java:18-19），自声明 `api project(':tny-game-expr-groovy')`。两处取 api 而非 implementation，因收缩前两模块的发布 POM compile 域本就经 doc/net 的 api 传递含对应引擎，声明 api 使对外暴露面零变化。其三，tny-game-net 主源码六个以上文件直接使用 common-lifecycle 类型却从未直连声明（曾靠引擎实现链传递获得可见性），补 `api project(':tny-game-common-lifecycle')`，该边同时恢复下游（如 net-netty4 的 unit 标注引用）经 net 获得 lifecycle 编译可见性。7.2 验证文案中的 P1 判据"删除 expr-groovy 工程后 net 主源码仍编译"经实施证伪——net 直接实例化引擎类属既有 P2 反例代码味，代码重构超出本册边收缩范围；实际判据取"POM compile 域收缩 + 全仓编译通过 + 下游测试通过"三件。

**D8 悬空模块与夹具挂载修复：删 settings.gradle:82 的 tny-game-codec-protoex 引入行；tny.java-module.gradle 的夹具挂载谓词追加排除宿主工程。（P13；M1：module-checker 组号对账已有"命中即报红"的先例形态）**
同步为 module-checker 补一条对账：装配线成员工程 MUST 有已跟踪的构建文件，杜绝"include 悬空仍进发布线与 BOM"的复发（审计确认 codec-protoex 在 git 无跟踪文件却产出空 jar 进发布线，属命名谓词零枚举设计的漏洞形态）。本条触碰 `tny.java-module.gradle`——该文件有工作区未提交改动，实施顺序上排最后，且须先与在途变更的提交关系对齐（见 Migration Plan）。

**D9 "托管而无消费"九项逐条裁决：先核对传递落点，无落点同删，有落点注释登记。（gradle-build-style 修改后的"无落点的托管条目被判违例"场景即本条的验收判据；P13）**
commons 族九项（beanutils、collections 3.x、configuration、crypto、dbcp2、lang 2.x、math3、pool、pool2）逐一经解析报表核对是否存在被钉扎的传递坐标：commonsCompress 已实证有落点（testcontainers 传递请求 1.24.0 被钉降至 1.22），保留并登记；commons-lang 2.x 与 commons-collections 3.x 大概率有配置类库的传递落点，以报表为准；确无落点者连同集中托管条目与目录条目一并删除。判据形态用变更目录的依赖报表前后对照（按 config 零差异口径的抓取规范），保证"删除不改变任何解析结果"可证。

**D10 卫生清理一次性完成：删 gradle.properties 四死键；删根脚本两条零应用点插件声明（build.gradle:7、:10）；清 tny-game-doc-gradle 被注释掉的发布配置残留；修版本目录三处失实注释（jprotobuf 的托管表述、coord() 表述、cglib 托管表述）。**
（触碰即改义务按 gradle-build-style 存量违例条款执行；注释修正不改变任何行为，故不列独立需求）。springCloudVersion 死键按"删除"处置，Spring Cloud 版本面的真实需求（nacos 装配线要不要 spring-cloud-dependencies 导入）留给 D6 门禁与版本升级册的后续提案回答——本册不引入新的 BOM 管辖面除非等值收编（D4 的 grpc 属等值）。

## Compatibility Impact

本册是发布依赖面收缩，对下游存在源码级破坏（P11 三问的"下游能否无感知升级"答案为否，故 proposal 已标 BREAKING）：
- 消费 tny-game-redisson 的工程（含 starter-redisson、data-redisson、starter-data）：不再从 POM 的 compile 域传递获得 tny-game-boot 及其闭包；boot 类型本就零使用，运行域不受影响。
- 消费 tny-game-net 或 tny-game-basics 的工程：不再传递获得 expr-groovy 实现与 Groovy 运行时（compile 域换成 expr 契约），运行域仍含引擎实现；若下游代码直接 new GroovyExprHolderFactory，升级时须自行声明引擎依赖。
- 消费 tny-game-doc 的工程：不再传递获得 expr-mvel 与 mvel 运行时（compile 与 runtime 域按 implementation 语义调整）。
- 发布元数据的实际解析版本变化：log4j 族 compile/runtime 域版本由 2.21.1 恢复为声明的 2.22.1；guava 族全图解析值由 32.0.1-jre 统一上移至 33.0.0-jre（D3 勘误后经用户裁决的升钉，jetcd-core 的要求使其为必须的最低值；曾按 32.0.1 精确版本消费 guava 的下游需在升级时验证兼容性）。
- 版本目录与托管清单的对外可见面：条目集合变化（grpc 键新增、vertx-grpc 托管条目新增、无落点 commons 条目移除）不改变任何仍在解析图中的坐标的生效版本。

## Risks / Trade-offs

- [D1 的托管合并模型读取形态与插件公开 API 不符（探针失败）] → 实施任务组第一位做最小工程探针实测；探针不成立时停止实现回到规格差量修订"配置阶段"措辞与落点，不带病实现（P12）。
- [log4j 2.21.1 到 2.22.1 的恢复可能触碰集成测试的告警捕获断言与 oplog-log4j 的 appender 装配] → 受影响模块测试与 integration-test 全跑，纳入验证任务。
- [D3 升钉后 guava 全图从 32.0.1-jre 移到 33.0.0-jre，个别传递库可能用到 32→33 之间移除的 API] → 七处直连消费模块测试全覆盖（任务 5.2）加升钉前后解析报表对照（仅 guava 族移动、无其他族漂移）；出现失败即停并回报用户，不带伤合入。
- [D3 原"摘除托管条目"机制已被实测证伪——插件隐式托管收集器使带版本直连声明本身就构成全工程强制] → 勘误已回写本决策与任务清单；后续册若再提"摘条目修降级"须直接引用本条证据。
- [D6 门禁的豁免清单可能成为长期逃逸口] → 规格已要求豁免条目在门禁输出中逐次可见且必须带处置去向，归档检查时可审计。
- [与在途变更冲突：tny.java-module.gradle、tny.benchmark-module.gradle、tny-game-integration-test/build.gradle 有未提交改动或在途册（move-bench-suite-selection-into-plugin、trim-it-module-dependencies-block）编辑] → 本册不触碰后两者；D8 对 java-module 的编辑排在任务组末尾，实施前确认工作区改动已入库或先完成在途册。
- [D9 的传递落点核对若误判，删条目会改变解析结果] → 判据即"删除前后依赖报表零差异"，有落点者一律保留登记，宁保守勿激进。

## Migration Plan

实施顺序按依赖与风险排布：先做 D1 守卫探针与 D2 次序修复（守卫合入前必须已修复失守项，否则守卫落地即全红阻断一切构建）；随后 D5、D4 的等值托管替换与新增；然后 D3 的 guava 升钉与报表对照（机制勘误后经用户裁决的最终路线）；接着 D6、D7 的门禁与边收缩及受影响模块测试；D10 卫生清理穿插在触碰对应文件时完成；D8 与 D9 最后做（D8 待在途改动入库，D9 需先出解析报表基线）。回滚策略：每个决策独立成组可单独 revert；守卫（D1、D6）本身是纯校验增量，revert 不影响其余修复的成立。

## Open Questions

- grpc-bom 导入后实际需要逐一核对的"关键坐标清单"取哪几个成员（api/core/netty/stub 还是全族）——留待实施时按版本目录别名映射生成，两种取法都不改变规格行为。
- 豁免清单文件的落点命名与既有"历史版本清单"文件是否同目录收拢——属形态细节，实施时按 buildSrc 既有对账文件的版式就近决定，不阻塞任务分解。
