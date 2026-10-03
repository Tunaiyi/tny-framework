# Proposal

## Why

`tny.bench-suite` 是基准模块专属装配插件——它只被基准工程一处应用，名字里的 "suite" 说的是族清单集合，但插件实际承担的是"基准模块的整线约定"（清单扩展贡献、枚举对账与落盘任务、参数覆写解析），与刚落成的 `tny.module-checker`、`tny.module-setting` 同属"直陈对象与动作"的命名谱系。用户定名改为 `tny.benchmark-module`：模块名与包根刚由 rename-bench-to-benchmark 从 bench 换到 benchmark，插件 id 若停留 bench-suite 将在同一模块文件里留下新旧词族并存的半截身份。扩展名 `benchSuite` 随插件新名改为 `benchmarkModule`（用户裁决"id 与扩展名同动"），实现类 `tny.convention.BenchSuite` 暂留——与 module-modes 先例同节奏，类名收口可另批。任务名（jmhList、jmhSuiteVerify、benchRoutineExport）、属性名（benchScope、benchAll、benchParams）与 `reports/bench/` 路径段不动：前两类是 CI 调用契约，任务名历次迁移不变的既有注记语义不受本变更影响。

## What Changes

- 文件 `tny.bench-suite.gradle` 移名 `tny.benchmark-module.gradle`（插件 id 随文件名派生）；内部扩展注册 `extensions.create('benchSuite', …)` 改 `benchmarkModule`（类型参数 `tny.convention.BenchSuite` 不动，类保留），头注释四处 benchSuite/bench-suite 指称随改，`jmhListVerify` 报错文案中"检查 benchSuite.routineFamily"的扩展名前缀随改（D 同 module-setting 报错串理）。
- 实现类文件 `tny/convention/BenchSuite.groovy` 头注释两处对 `tny.bench-suite` 的指称随改为新插件名；类名与属性名不动。
- 模块 `build.gradle`（本变更实施时已因 rename-bench-to-benchmark 落地而位于 `tny-benchmark/`）三行应用注与插件名指称、扩展调用块 `benchSuite { … }` 块名与 jmh 段四处 `benchSuite.<属性>` 读取随改为 `benchmarkModule`；族正则、臂属清单等取值一律不动。
- 历史指称不回改：归档案卷、HANDOFF 与本账其他变更工件中的 `tny.bench-suite`/`benchSuite` 记录按记录原则保留。

## Capabilities

### New Capabilities / Modified Capabilities

无。`benchmark-harness` 六需求以"单一顶层模块""集中声明"类别语行文，插件 id 与扩展名不在条文内；主规格 grep 实测零钉名。skip_specs。

## Impact

- **受影响文件**（2026-10-03 现场逐行清点，共十六行）：插件文件 `tny.bench-suite.gradle`（git mv 移名 + 内部六行：第 4、5、7、16、18、47 行的扩展名与插件名指称；第 2 行"前身为 gradle/bench-suite.gradle"历史路径保留）；`tny/convention/BenchSuite.groovy` 类头两行（第 1、4 行对 `tny.bench-suite` 的指称，类名与属性名不动）；`tny-benchmark/build.gradle` 八行（第 4 行应用注、第 5 行应用、第 28 行插件名注、第 29 行扩展块、第 56/65/70 行 jmh 段读取、第 75 行 afterEvaluate 注）。CI、docs、主规格零引用（grep 实测）。
- **前置约束**：`rename-bench-to-benchmark` 实施归档之后（用户裁决两本分工：模块层先落、插件层本承接）。两本共编基准模块构建文件的扩展声明区（该本改族正则四条的值与 `:tny-bench` 归属注，本本改块名、读取行与插件名注），先后落地无静默冲突，后落地一方按实施现场文本为准。与在途 `refine-bench-routine-triggering`（模块改名那本的前置）、`expose-git-info-extension` 等均无共同文件。
- **验收基线**：`tasks --all` 剔噪清单段对实施前自抓基线**逐行零差异，无例外**（任务名全部不动；扩展名经实测不出现在 `tasks --all` 与 `help` 输出，立项初稿曾推测 help 有"扩展可见面"差异，实测证伪后删除该条）；`:tny-benchmark:jmhList -PbenchAll`、`jmhSuiteVerify`、速览 `benchRoutineExport` 三探针实跑兜住扩展改名的配置期与执行期读取链（族正则取值未动，键集与模块改名案的换代对照表直接可比）；grep 现行面 `tny\.bench-suite`（带 tny 前缀形态，历史路径"gradle/bench-suite.gradle"不属判据）与 `benchSuite` 双形态零命中；`clean build` 全绿。
