# Design

## Context

动机见 proposal.md - Why。实施前事实核对（2026-10-03 现场，经四视角核查复定并修正两处清点错误）：旧模块名 `tny-bench` 的现行指称共九组——`settings.gradle` include 行、`build.yml` 七行（九处出现：bench-compile 三任务路径、内容门禁正则、benchRoutineExport 两任务路径、产物上传路径、回写 git add 路径、曲线 output-file-path）、`tny.bench-suite.gradle` 头注释两处、模块 `build.gradle` 一处（其内容仅为历史变更名 `promote-netbench-to-tny-bench`，按历史记录原则不动）、README、`THIRD-PARTY-LICENSES.md` 表行、`docs/site/index.html` 标签、模块目录自身，以及立项清单曾漏、核查补入的第九组：`tny-game-net/src/main/java/com/tny/game/net/codec/security-generations_readme.md` 第 84 行以旧路径 `tny-bench/crypto-bench-2026-09-30.md` 引用取证文件——发布模块源码树内的现行叙事文档，目录改名后该路径断裂，须随改（仅改路径段，所指历史文档本体保留）。旧包名 `com.tny.game.bench` 的现行指称三组：12 个 JMH 源文件的 package 与 import、族清单四条正则的包名段、README 的目录树与示例正则与斜杠路径。

对账机理精确化（核查修正）：`jmhSuiteVerify` 双向对账的"目录一侧"实为 JMH `-l` 从编译产物枚举出的类全限定名包段，不是文件树扫描——javac 对被显式传入的源文件不强制目录与包一致，物理移动目录而 package 声明未改的情形对账照常全绿；故"目录与包的一致"由本变更 2.1 的原子同批绑定保证，非常驻判红。对账真正探测的是"运行时枚举面与清单声明面"的镜像一致：routineFamily、devtestFamily、facilityProbes 三条正则的单侧漂移由未归族、多族、族空面、探针失配四种判红当场暴露（段界 `\.bench\.` 与新包段 `benchmark.` 无巧合互配，不存在假绿）；quickRunExcludes 不在对账输入内，其漂移表现为速览档静默扩容，由验收 3.1 逐键换代比对兜住（常驻补强见 Open Questions）。

其余机理事实：`jmhListVerify` 空枚举判红在位；产物上传 `if-no-files-found: error` 在位；模块 `build.gradle` 的 `resultsFile` 取 `build/reports/bench/` 路径段与 gh-pages 的 `dev/bench/` 趋势目录均为任务词根或 action 派生路径、与模块名无关不随改；README 第 7 行"根 build.gradle 配置期守卫"指称不实（守卫实体在 tny.project-checks 的零发布合同块、机制为角色沿依赖边核对），本批随改时按实况改写。主规格 `benchmark-harness` 六条需求十六个场景全文名称无关（grep 实测），"新增域不得迫使模块更名"条文与本改名相容——本次为用户命名澄清，非域扩张所迫。

## Goals / Non-Goals

Goals：模块标识、包根、族清单、CI 接线、现行文档五面一步改齐且互相一致；历史数据与归档记录零改写。Non-Goals：不借改名调整族分类、清单内容或基准代码本身；不为 `jmhSuiteVerify` 增加 quickRunExcludes 命中探针（另立后续小变更，不夹带）；不为新键名预建曲线数据桥或改写 gh-pages 历史条目；不把 `bench` 词根从任务名（jmhList、benchRoutineExport）、作业名（bench-routine）、插件 id（tny.bench-suite）与 `reports/bench/`、`dev/bench/` 路径段中清除——改名请求未涉及，混改扩大面。

## Decisions

**D1 模块名与包根同一变更内原子换批。** 族正则按包名段匹配类全限定名，两侧任何一步改而另一侧不改，`jmhSuiteVerify` 与 `jmhListVerify` 当场判红；而目录移动与包声明的一致又不受对账保护（见 Context 机理修正）——两面夹逼之下唯一安全形态是单批次原子落地，回滚以整提交为单位。
**D2 基准键换代作为一次性断点接受，不回写历史。** JMH 的基准项键就是类全限定名加参数，无旁路可指定旧键名。曾考虑的备选：其一，只改模块名保留包根——制造"工程叫 benchmark、包叫 bench"的双身份，正是用户点名否定的形态；其二，把 `results/` 与各取证 JSON 的历史键批量改写——伪造历史数据，违反历史记录原则，gh-pages 已发布条目更无从改写。取断点方案：同组曲线自换代后只在新键之间同名比较，`github-action-benchmark` 对"历史有而本次无"与"本次新增"的键均不告警，且本通道 `fail-on-alert` 本就是 false、告警仅作信号，人裁决纪律不受扰动；`verification-notes.md` 记新旧键前缀对照表与断点日期，并载明换算规则"历史文件内指向 tny-bench/ 的路径自改名日起一律对应 tny-benchmark/ 同名文件"（含 crypto-bench-2026-09-30.md 首行那条现行承诺式指针——历史面中唯一含未来指向的一处，裁决为不改历史文件、以对照表换算）。
**D3 内容门禁分支 `^tny-bench/` 必须随改，必要性在换代之后而非改名提交本身（核查修正因果）。** 改名提交必然包含 `tny-benchmark/src/` 下的源文件路径与模块 `build.gradle` 路径，门禁的 `(^|/)src/` 与 `(^|/)build\.gradle$` 两分支任一都会单独放行，不存在"换代首跑丢数据点"的风险；该分支若不随改则成为永不命中的死支，此后仅触及模块根面（README、取证 markdown、results 之外的散放文件）的合入将不再被认作基准模块改动，"改动触及基准模块即放行计时"的既定语义残缺。3.2 因此增设鉴别观察：3.1 要补的 README 基线纪律行做成改名落地后的独立提交（仅触及 `tny-benchmark/README.md`），它恰好检验改写后的 `^tny-benchmark/` 分支能否单独放行速览。
**D4 `tny.bench-suite.gradle` 头注释改写按现状原则。** 两处 `:tny-bench` 归属指称改为新工程路径；"CI 调用路径不变"半句改为陈述事实的"任务名历次迁移不变，工程路径的模块段自本变更后为 tny-benchmark"。插件 id 与"前身为 gradle/bench-suite.gradle"等历史指称不动。
**D5 历史随行面四类一律不改名不改内容。** `results/` 各 JSON；模块根散放的十五份取证 JSON（带日期与不带日期命名均有，内容含旧包名基准键——核查补列）；日期命名取证 markdown（三份中仅 crypto-bench-2026-09-30.md 首行含旧模块路径指称，三份均不含旧包名——立项口径经核查修正）；两个按旧全限定类名命名的 profile 存档目录（实名核验：目录名片段无斜杠无连续点号，随 git mv 机械移动安全，全仓现行文档对二者零引用）。验收 grep 以精确点名的现行面为界并排除历史变更名指称（命令原文见 tasks 2.2）。

## Risks / Trade-offs

- 风险：CI 七行中漏改一处，本地探针全绿而工作流夜间跑炸。缓解：任务路径错误在 Gradle 层直接报工程不存在；产物上传 `if-no-files-found: error` 与曲线 output-file-path 缺失各自显式失败，三道常驻防线兜底；再加 3.2 的推送实况观察一次覆盖全链。
- 风险：三条对账输入族正则漏改其一。缓解：`jmhSuiteVerify` 未归族与空面判红当场暴露；改完第一步本地执行该任务。
- 风险：quickRunExcludes 不在对账输入内，漏改使速览覆盖面静默扩容。缓解：3.1 速览实跑键集与基线逐键换代比对（组合数与逐条对应皆判据）；常驻探测器另立后续变更（Open Questions）。
- 风险：换代断点期被误读为性能回归。缓解：D2 对照表记入 verification-notes 与 README 基线纪律行；断点两侧不跨比即无误报面。
- 风险：改名提交自身触发一次速览计时与结果回写，分支多一个 chore 提交。缓解：属门禁按设计放行（src 与 build.gradle 分支命中，D3 修正后口径），且恰留新链实况证据，无需规避。

## Migration Plan

第一步，确认 `refine-bench-routine-triggering` 已归档。第二步，抓四项基线（全量枚举、族对账、速览实跑键集、tasks --all）。第三步，单批次原子改名：目录与包移动、12 源文件包声明与 import、族正则四条、settings include、CI 七行、bench-suite 头注释、README 与其余文档五面及 security-generations_readme.md 路径行。第四步，现行面三道 grep 清零（含历史变更名排除）。第五步，本地全探针复跑与全量构建。第六步，推送后实况观察 bench-compile 与门禁放行，README 基线纪律行独立提交兼作 `^tny-benchmark/` 分支鉴别观察，其后夜间或手动完整档在新路径落数据点。回滚为整提交 git revert（原子性由 D1 保证）。

## Open Questions

- quickRunExcludes 的空命中是否应加常驻判红：现状其漂移只能靠本变更验收探针兜住，与本仓"空面判红"既有纪律（jmhListVerify、族空面检查）不同型。建议另立后续小变更为 `jmhSuiteVerify` 增加"quickRunExcludes 每条正则须在枚举面命中至少一个类"的非空探针；不夹带进本改名变更。
