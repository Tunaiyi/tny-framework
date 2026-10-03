# Proposal

## Why

用户定名两部分同步改名：基准模块 `tny-bench` 改为 `tny-benchmark`，包根 `com.tny.game.bench` 改为 `com.tny.game.benchmark`。改名理由有二：其一，`bench` 是缩写，能力规格与 README 对模块的自称都是"基准（benchmark）设施"，模块标识与包名对齐既有 capability 名 `benchmark-harness` 后见名知义；其二，主规格对基准模块全部用名称无关的类别语描述（六条需求与十六个场景正文经现场 grep 无一处出现旧模块名或旧包名），改名不触碰任何规格条文，属纯实现标识变更，实施窗口成本最低。两部分必须同批改：族属清单的四条正则按包名段匹配基准类全限定名，只改一侧会使评审通道的族属对账判红。

## What Changes

- 目录 `tny-bench/` 整体移名为 `tny-benchmark/`（git mv 保历史），`settings.gradle` 的 include 行随改。目录内四类历史面原样随行、名称与内容不回改（历史记录原则）：`results/` 各 JSON；模块根散放的十五份取证 JSON（含带日期与不带日期命名，内容以旧包名作基准键——核查补列，立项清单曾漏）；日期命名取证 markdown（三份中仅 crypto-bench-2026-09-30.md 首行含旧模块路径指称，三份均不含旧包名——口径经核查修正）；两个按旧全限定类名命名的 profile 存档目录。
- `tny-game-net/src/main/java/com/tny/game/net/codec/security-generations_readme.md` 第 84 行以旧模块路径 `tny-bench/crypto-bench-2026-09-30.md` 引用取证文件（核查发现的第九处现行指称，发布模块源码树内的现行文档）：仅把该行路径段改指新目录，所指历史文档本体按上条保留。
- 包根改名：`src/jmh/java/com/tny/game/bench/` 下 12 个 JMH 源文件的 package 声明、相互 import 与目录位置同步换为 `com.tny.game.benchmark`；`benchSuite` 清单中含包名段的四条族正则（routineFamily、devtestFamily、facilityProbes、quickRunExcludes）随改，臂属枚举清单 `routineAlgoArms` 的取值不含包名段、不随改。
- CI 工作流 `build.yml` 随改七处：bench-compile 的三个任务路径、内容门禁的 `^tny-bench/` 触发正则、benchRoutineExport 两个任务路径、产物上传路径、结果回写的 git add 路径、曲线存储步骤的 output-file-path。
- `tny.bench-suite.gradle` 头注释两处 `:tny-bench` 归属指称随改；该行"CI 调用路径不变"的说法因本次工程路径改名而不再成立，按现状改写（任务名历次迁移不变，工程路径的模块段因本次改名替换）。
- 现行文档指称随改：README 标题与正文的全部旧名形态（含第 56 行 `-PbenchInclude` 示例正则的包段、第 121 行斜杠路径、包名目录树），且 README 第 7 行"根 `build.gradle` 另有配置期守卫"一句按实况改写——守卫实体在 tny.project-checks 约定插件的零发布合同块（根脚本一行引入），机制是自报角色沿依赖边核对而非点名禁止，该指称失实经核查确认，触碰即改；`THIRD-PARTY-LICENSES.md` 表行的"（tny-bench 模块）"；`docs/site/index.html` 的模块标签（同文件第 94、184 行的 `./dev/bench/` 趋势页链接由 benchmark-action 按基准类型派生、与模块名无关，不随改）。模块 `build.gradle` 第 45 行与 README 第 130 行两处历史变更名 `promote-netbench-to-tny-bench` 指称按历史记录原则不动，一切归档与 verification-notes 中的旧名指称亦不回改。
- 已知一次性后果（design D2 裁决记录）：JMH 的基准项键由类全限定名派生，包名换代使新结果文件与历史结果、历史曲线条目的基准键整体换代；同名比较只在换代后的新键之间进行，缺失键不构成告警（告警本是人裁决信号非门禁），历史数据全部保留。

## Capabilities

### New Capabilities / Modified Capabilities

无。`benchmark-harness` 六条需求与 `gradle-build-style` 九条需求均为名称无关陈述，任务名、两族分类、零发布合同、内容门禁语义、人裁决纪律全部不变；命名约定类别谓词（刻意不带 `tny-game-` 前缀者为不发布设施）对改名后的模块判定结果不变。skip_specs。

## Impact

- **受影响文件**：`settings.gradle`（include 一行）、`.github/workflows/build.yml`（七行九处路径与正则）、`buildSrc/src/main/groovy/tny.bench-suite.gradle`（头注释两处）、`tny-benchmark/build.gradle`（族正则四条）、12 个 JMH 源文件、`tny-benchmark/README.md`、`THIRD-PARTY-LICENSES.md`、`docs/site/index.html`、`tny-game-net` 源码树内 security-generations_readme.md 的路径引用一行，以及模块目录本身。
- **前置约束**：`refine-bench-routine-triggering` 变更（尚余实况核验与收口两任务）实施归档之后本变更才可实施——两变更共同编辑 `build.yml`、模块 `build.gradle` 与 README 三处，而该变更的收口任务要以这三处的现行形态对其案卷做差量核对，其手动完整档实况也要在归档时点的形态下观察记录；中途改名会使该变更的取证与账本对账时序断裂。与在途的 rename-subprojects-baseline-plugin、merge-publish-gate-into-publish、merge-dependency-sources-into-java-module 三变更无共同文件，互无顺序约束。
- **验收防线与盲区（经核查修正的精确口径）**：族正则中 routineFamily、devtestFamily、facilityProbes 三条是 `jmhSuiteVerify` 的对账输入，其单侧漂移由"未归族、多族、空面、探针失配"判红当场暴露（对账的"目录一侧"实为 JMH 运行时枚举出的类包段而非文件树，物理移动目录而 package 声明未改不会被判红——目录与包的一致由本变更原子同批保证）；quickRunExcludes 不在对账输入内，其漏改表现为速览档静默扩容而非判红，由验收 3.1 的速览键集逐键换代比对兜住；另有 `jmhListVerify` 空枚举判红与产物上传 `if-no-files-found: error` 两道常驻防线。全量构建、三道验收 grep（tasks 2.2，已修正为可达成且覆盖全部现行面）为验收项。
