# Design

## Context

动机与命名裁决见 proposal.md - Why。实施前事实核对（2026-10-03 现场，大小写敏感 grep `tny\.bench-suite`、`bench-suite`、`benchSuite`）：现行指称十六行——插件文件 `buildSrc/src/main/groovy/tny.bench-suite.gradle` 六行（第 4 行扩展名与工程归属混排指称、第 5 行职责段扩展名、第 7 行配置面行"benchSuite 五清单（tny.convention.BenchSuite）"、第 16 行 `extensions.create('benchSuite', tny.convention.BenchSuite)`、第 18 行同源注释、第 47 行 `jmhListVerify` 报错文案"检查 benchSuite.routineFamily"；第 2 行"前身为 gradle/bench-suite.gradle"历史路径保留），类文件 `tny/convention/BenchSuite.groovy` 头注释两行（第 1、4 行对 tny.bench-suite 的指称），`tny-bench/build.gradle` 八行（第 4 行应用注、第 5 行应用、第 28 行插件名注、第 29 行扩展块 `benchSuite { … }`、第 56/65/70 行 jmh 段读取、第 75 行 afterEvaluate 注）。CI、docs、主规格、根脚本零命中。**扩展名不入 Gradle 可见面的实测**：`:tny-bench:help` 输出 grep benchSuite 零命中、历史各版 `tasks --all` 基线同样零命中——扩展改名在任务清单与 help 面构造性不可见，差异只可能出现在评估错误（漏改）与 grep 面。

## Goals / Non-Goals

Goals：插件 id、扩展名两层与基准模块新名（tny-benchmark/benchmark 包根）同词族收口；任务名、属性名、清单值、对账逻辑、落盘路径全部不动。Non-Goals：不改实现类 `tny.convention.BenchSuite` 及其属性名（routineFamily 等五个）——类名收口若有再议另批，与 module-setting 先例同节奏；不改任务名与 `-Pbench*` 属性名（CI 调用契约，本变更案卷明记不动）；不改插件第 2 行历史路径指称；不回改归档与 HANDOFF。

## Decisions

**D1 扩展名随插件新名取 benchmarkModule（用户裁决"id 与扩展名同动"）。** 模块文件读作 `benchmarkModule { routineFamily.set(…) }` 与插件 `tny.benchmark-module` 严格同词根；备选"只改 id 扩展留 benchSuite"被否——扩展名是模块作者可见面，留旧词即再造半截身份（module-modes 先例的教训原文）。类名 `BenchSuite` 与扩展名的错位是本批有意保留的尾巴（类改动牵动 import 与属性面，另批收口），D3 记录在案。
**D2 报错文案"检查 benchSuite.routineFamily 与 -PbenchAll"的前缀随扩展名改。** 该文案指引用户去读一个将不存在的扩展名；属性名 routineFamily 与属性 -PbenchAll 不动，仅扩展前缀换。与 module-setting D3 同理。
**D3 与 rename-bench-to-benchmark 的分层与时序。** 该本动模块层（目录、包根、族正则值、CI 工程路径、文档五面），本本动插件层（id、扩展名、注释指称）；本本以该本归档为前置——两本共编族清单声明区（该本改第 30-35 行正则值与本本改第 29 行块名同文件不同行），先后落地互不破坏、后落地一方按现场。插件第 4 行"由 :tny-bench 模块声明"归该本收编面，本本实施时该处应已是 tny-benchmark 现名，本本只动同行 benchSuite 字样。
**D4 验收不含 `help` 面判据（实测修正）。** 初稿曾把"help 扩展可见行差异"列为预期差异，实测证伪（扩展名不入 help 与 tasks 清单）——判据改为纯零差异 + grep 双形态清零，不留虚构差异项。

## Risks / Trade-offs

- 风险：扩展名读取行漏改任一处，评估或 `jmh*` 任务实跑即报"扩展 benchSuite 不存在"红。缓解：配置期读取（应用行、块声明）projects 即探，jmh 段读取在配置期求值亦被 projects 覆盖，三探针实跑兜执行面；grep 双形态清零常驻。
- 风险：第 4 行混排指称（benchSuite 与 :tny-bench 同句）在两本先后落地时的编辑冲突。缓解：该行两词族各有归属（模块名指称归 rename-bench-to-benchmark、扩展名指称归本本），D3 写明，后落地只改自己名下字样。
- 权衡：类名 BenchSuite 暂留造成"插件 benchmark-module、类 BenchSuite"的一层错位——类改动牵动 import 与属性契约面，且类注释已写明与扩展的关系，读者无歧义；与 module-setting 类收口先例同批节奏可控。

## Migration Plan

第一步，确认前置归档。第二步，抓基线（剔噪 `tasks --all` 清单段、jmhList 全量枚举、速览实跑键集、projects 绿记录）。第三步，git mv 插件文件，类内六行、类头两行、模块文件八行随改。第四步，验收（三探针、grep 双形态清零、`clean build` 绿——CollectionLockTest 同签名偶红按登记标准处置留痕），记 verification-notes.md。回滚为文件回名与十六行还原。
