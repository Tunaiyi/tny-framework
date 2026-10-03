# Design

## Context

动机与命名裁决见 proposal.md - Why。实施前事实核对（2026-10-03 现场，前置 rename-bench-to-benchmark 已归档，模块文件位于 `tny-benchmark/`）：旧名出现点十六行——插件文件 `tny.bench-suite.gradle` 六行（第 4 行 `benchSuite` 扩展与 `:tny-benchmark` 归属混排、第 5 行职责段、第 7 行"benchSuite 五清单（tny.convention.BenchSuite）"、第 16 行 `extensions.create('benchSuite', tny.convention.BenchSuite)`、第 18 行同源注释、第 47 行报错文案"检查 benchSuite.routineFamily 与 -PbenchAll"），`tny/convention/BenchSuite.groovy` 两行（第 1、4 行对 `tny.bench-suite` 的指称），`tny-benchmark/build.gradle` 八行（第 4、5、28、29、56、65、70、75 行）。CI、docs、根脚本、主规格对 `tny.bench-suite`/`benchSuite` 零命中（grep 实测；大小写不敏感复核额外暴露的 12 行 `jmhSuiteVerify` 为任务名——按案卷不动、不属两判据形态）。**可见面实测**（前批确立）：扩展名与插件 id 均不入 `tasks --all` 与 help 输出，漏改的暴露依赖评估期读取报错与 grep 判据。

## Goals / Non-Goals

Goals：插件 id、扩展名两层与基准模块新名同词族（benchmark），族清单配置面的读者在模块文件里读作 `benchmarkSuite { routineFamily.set(…) }`；行为逐字节不变。Non-Goals：不改实现类 `tny.convention.BenchSuite` 与五个属性名（routineFamily 等）——类收口与 module-setting 先例同节奏可另批；不改任务名（jmhList/jmhListVerify/jmhSuiteVerify/benchRoutineExport，CI 调用契约与插件头注释"任务名历次迁移不变"注记均继续成立）；不改 `-Pbench*` 属性名与 `reports/bench/`、`dev/bench/` 路径段；不改插件第 3 行历史路径指称；不回改归档与 HANDOFF。

## Decisions

**D1 扩展名取 `benchmarkSuite` 而非 `benchmarkModule`（立项后修订的用户裁决）。** 用户最初给的落点字面是插件名 `tny.benchmark-module`，插件 id 从其字面；扩展名此前经"id 与扩展名同动"裁决拟机械映射为 benchmarkModule，立项核查轮发现——扩展承载的是基准族清单集合，"Suite"在该语境（清单、对账、报错文案）比"Module"准确，且与暂留类名 `BenchSuite` 同词根使"扩展名/类型名"关系与 module-setting 收口前形态同构；插件 id 仍按用户字面取 `tny.benchmark-module`（id 说出"基准模块的约定插件"、扩展说出"族清单集合"，两层语义各得其所）。
**D2 报错文案前缀随扩展名改。** 第 47 行"检查 benchSuite.routineFamily 与 -PbenchAll"改"检查 benchmarkSuite.routineFamily 与 -PbenchAll"——指引对象换名，属性名与触发条件不动；与 module-setting D3 同理。
**D3 `:tny-benchmark` 归属字样归前批。** 插件第 4 行同行含 `benchSuite`（本册改）与 `:tny-benchmark`（rename-bench-to-benchmark 已改，本册实施时已是现名），两词族各有归属互不触碰。
**D4 判据含大小写不敏感复核步骤。** 终态 grep 以两判据（`tny\.bench-suite`、`benchSuite`）零命中为准，另跑一次 `-i` 复核：命中面应只剩历史路径（`gradle/bench-suite.gradle` 第 3 行）、类名（`BenchSuite` 声明与 import）与 `jmhSuiteVerify` 字样（任务名，两处）四类豁免——多出一类即漏改。

## Risks / Trade-offs

- 风险：扩展名读取行漏改，评估或 jmh 段配置期读取即报"扩展 benchSuite 不存在"红。缓解：读取链（第 16 行注册、第 29 行块、第 56/65/70 行 jmh 段、第 47 行报错文案触发）全在配置期求值，`projects -q` 即探；族对账与速览实跑为执行面盖章；grep 双形态清零常驻。
- 风险：插件 id 应用行漏改，评估报"plugin not found"红。缓解：编译器级常驻探测（apply 即解析）。
- 权衡：类名 `BenchSuite` 与扩展 `benchmarkSuite` 词根一致但不完全同名（类无 benchmark 前缀）——类收口批处理；插件 id `benchmark-module` 与扩展 `benchmarkSuite` 词尾不同属 D1 有意分工（id 说对象、扩展说内容），头注释职责段写明二者关系防误读。

## Migration Plan

第一步，抓基线（剔噪 `tasks --all` 清单段、jmhList 全量枚举、族对账与速览绿记录、projects 绿）。第二步，git mv 插件文件；类内六处、类头两行、模块八行随改（属性名、任务名、正则值零触碰）。第三步，验收（三探针、双判据 grep 清零加 `-i` 复核四类豁免、`clean build` 绿——CollectionLockTest 同签名偶红按登记标准处置留痕），记 verification-notes.md。回滚为文件回名与十六行还原。
