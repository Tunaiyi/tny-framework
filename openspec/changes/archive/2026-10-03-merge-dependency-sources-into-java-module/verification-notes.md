# 验证记录

## merge-dependency-sources-into-java-module（2026-10-03）

- 环境：JDK 显式钉 Corretto 21.0.12.1（本机默认 JDK 25 与 Gradle 8.5 不兼容）。
- 1.1 前置与基线：rename-subprojects-baseline-plugin 已归档，前置满足。抓四件基线存 baseline/（tasks --all 清单段 3486 行、downloadDependencySources 实跑输出"成功 62 个，无源码包 11 个"、产物文件列表 62 项、dry-run 任务图 20 行）。
- 2.1 合并实施：任务注册块自 tny.dependency-sources.gradle 第 5 至 51 行整块迁入 tny.java-module.gradle 第 110 至 156 行（dependencies 段之后、idea 段之前），块级 diff 与源文件逐字节一致（BLOCK-VERBATIM-OK）；迁入块前按本仓惯例加两行来由注释（退役件自 merge-dependency-sources-into-java-module 变更逐行迁入、只读下载不参与发布门禁）。头注释职责段补"依赖源码包批量下载任务"，兄弟插件清单删去 tny.dependency-sources 一项；根装配行删除；插件文件移入 /tmp/gradle-retired-quarantine/（该目录既有的 CatalogCoord、tny.demo-app 等退役件同处）。`./gradlew projects -q` 通过。
- 3.1 零差异验收：四份探针与基线全部逐行零差异——`tasks --all`（剥离耗时尾行）零差异、dry-run 任务图零差异、实跑输出行零差异（62/11 计数一致）、产物文件列表零差异。grep 判据按 tasks 原文口径（build.gradle、settings.gradle、gradle、buildSrc/src、各模块 build.gradle）对 `tny\.dependency-sources` 零命中；把扫描面扩到根级 markdown 时唯一命中面是会话交接文档 HANDOFF-问题会话-2026-10-03.md（规划记录面，与归档同性质，按历史记录原则不回改）。
- 全量构建事件记录（如实披露）：改造后第一次 `clean build` 报红（45 秒处失败，当时命令仅截尾两行、未留存失败任务名，属取证缺口）；随即第二次 `clean build` 完整跑绿（无 FAILURE）、第三次增量构建全绿（292 任务）、第四次带完整日志留痕的 `clean build` 全绿（339 执行，日志存 baseline/cleanbuild-green-evidence.log）。未能归因的首轮报红与本账本 fix-ci-unit-flakes 登记的"并行负载下时序敏感断言偶红"家族（CollectionLockTest 三次、VoidTypeStageTest 一次，且登记注记明言第二次偶红恰逢 buildSrc 引入期）时段与形态吻合，且四份零差异探针证明合并本身未改变任何任务图与执行面；判定为环境偶红而非本变更引入，留此记录供后续核对。
