# 验收记录：rename-bench-suite-class

## 结论

验收通过。约定插件支撑类 `tny.convention.BenchSuite` 更名为 `tny.convention.BenchmarkSuite` 后，任务清单、基准枚举、族属对账与全量构建四条证据链对改名前基线全部零差异，现行面不存在旧类名残留。

## 改名范围

本次改名触碰三处：

1. 类文件本体以 `git mv` 移名，`buildSrc/src/main/groovy/tny/convention/BenchSuite.groovy` 更名为 `BenchmarkSuite.groovy`；
2. 类声明行改为 `abstract class BenchmarkSuite`（现文件第 9 行）；
3. 插件 `buildSrc/src/main/groovy/tny.benchmark-module.gradle` 两行随改——第 7 行括注 `（tny.convention.BenchmarkSuite）`、第 16 行 `extensions.create('benchmarkSuite', tny.convention.BenchmarkSuite)`。

扩展名 `benchmarkSuite`、五个属性名、模块构建文件与根脚本的引用形态全部零触碰；类头注释与历史变更名指称零触碰（历史册归档件内的旧名提法按前批口径不在判据内）。

## 证据链（按时序）

1. **基线首抓（改名前，HEAD 上）**：`tasks --all` 剔噪清单段 3457 行与 `projects -q` 评估绿记录在案；`jmhList` 枚举抓样落空——该任务的输出文件要先跑任务才生成，首抓只做了文件转存而未先运行，写成空文件。
2. **改名实施**：三处改动落地，`buildSrc/src` 内旧类名零残留。
3. **枚举与族属基线补抓**：以 `git stash` 回滚改名状态，先运行 `./gradlew :tny-benchmark:jmhList -PbenchAll` 再转存 listFile，得到 31 个基准键的改名前枚举；同时运行 `jmhSuiteVerify` 留族属对账行；随后 `git stash pop` 恢复改名状态。
4. **改名后复跑**：`projects -q` 评估通过；`jmhList -PbenchAll` 枚举与 `jmhSuiteVerify` 对账对基线逐行零差异；大小写敏感、词边界口径的旧类名残留检索在现行面（根脚本、settings、gradle 目录、buildSrc 源码、各模块构建文件、docs、.github、openspec 现行规格）零命中。
5. **任务清单比对的口径修正**：改名后全量任务清单与首抓基线逐行 diff 出现差异，逐块核查后确认差异全部是编码渲染噪声——首抓时刻的 daemon 在钉不住 UTF-8 locale 的环境里输出，中文描述行被打成问号；佐证是任务名列（3397 项，纯 ASCII）两侧零差异，且 `nmcpPublishAllPublicationsToCentralPortalSnapshots` 等"疑似新增"行在首抓件内实际存在 52 处。处置：再次 stash 回滚，与改名后同一 daemon、同一捕获口径重抓改名前清单（`baseline/tasks-all-before-rerecorded.txt`），恢复改名后重抓 after，3457 行全文逐行零差异；重抓件替换首抓件归档。
6. **全量构建**：`./gradlew clean build` 全绿（51 秒，无失败任务、无偶红，登记在册的 `CollectionLockTest` 抖动本轮未出现，无需走 fix-ci-unit-flakes 处置）。

## 留痕：基线抓取两条口径教训

- 转存型任务的基线必须先运行任务再转存输出文件，否则抓到的是空文件而非状态；
- 逐行零差异判据要求 before 与 after 在同一 daemon、同一 locale 环境下抓取，否则中文描述行的编码渲染会让判据失真——任务名列的 ASCII 子集比对可作噪声甄别手段。

## 判据结果汇总

| 判据 | 结果 |
|------|------|
| `projects -q` 配置评估 | 通过（改名前后各一次） |
| `tasks --all` 剔噪清单（同环境重抓口径） | 3457 行逐行零差异 |
| `jmhList -PbenchAll` 枚举 | 31 键逐项零差异 |
| `jmhSuiteVerify` 族属对账 | 逐行零差异 |
| 旧类名词边界现行面检索 | 零命中 |
| `clean build` | 全绿 |
