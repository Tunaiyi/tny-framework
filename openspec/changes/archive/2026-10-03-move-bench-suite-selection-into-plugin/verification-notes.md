# 验收记录：move-bench-suite-selection-into-plugin

## 结论

本册自身判据全部闭合：速览实跑键集逐键一致、`-PbenchAll` 全量枚举逐行一致、定向快档探针选中集与产物一致、`tasks --all` 剔噪清单零差异、`projects -q` 评估绿、模块 84 行降到 58 行（需求七界线内）、插件 169 行（250 界线内）。`clean build` 全绿一项被外部改动阻塞——并行会话的 commons-io 2.14 升级批在 `LocalWordsFilterTest` 留有未适配的失败用例，与本册改动零关（详见归因段）；经用户裁决按前册 trim-it-module-dependencies-block 先例**带痕收口**，补跑条款见末段。

## 实施形态

- `tny.benchmark-module.gradle`：在既有 benchParams afterEvaluate 段之前新增规模选择 afterEvaluate 段（D1 注册顺序＝执行顺序，`-PbenchParams` 覆写优先级承前居末）；`familyInclude` 拼接、三分支与缺省档六臂参数域、`benchGc`、`benchFast` 逐字迁入，来由注释随段（D3 不压缩）；头注释边界句改为"jmh 静态参数与依赖声明在模块构建文件，执行面规模选择在插件 afterEvaluate"。
- `tny-benchmark/build.gradle`：jmh 块只留八行静态基线参数与两条说明注释（缺省执行语义句保留并补指回插件位置），控制流区段整体移除。

两处机械等价改写（语义零变化，如实记录）：扩展访问由模块 DSL 的动态名 `benchmarkSuite.` 改为插件体内既有局部变量 `suite.`（`extensions.create` 返回的同一实例，插件第 16 行）；DSL 块内裸赋值 `includes = ...` 改为插件体内 `jmh.includes = ...`（同一扩展属性，benchParams 段 `jmh.benchmarkParameters` 先例同型）。

## 判据与证据（baseline/ 在档）

| 判据 | 基线件 | 复跑件 | 结果 |
|---|---|---|---|
| 速览键集（benchScope=quick） | routine-export-quick-before.txt | routine-export-quick-after.txt | 13 键逐键一致 |
| 全量枚举（-PbenchAll） | jmhenum-before.txt（先运行 jmhList 再转存，权威口径） | jmhenum-after.txt | 31 键逐行零差异 |
| 定向快档（benchInclude=Smoke + benchFast） | jmh-smoke-fast-before.txt | jmh-smoke-fast-after.txt | 选中集（1 基准）零差异、产物行在位 |
| tasks --all 剔噪清单 | tasks-all-before.txt | tasks-all-after.txt | 3457 行逐行零差异 |
| 配置评估 | — | projects -q | 绿（含模块无控制流残留的 grep 佐证） |

比对方法一次自纠（如实记录）：速览键集首轮比对用 `sed 's/ *[-+].*//'` 剥数值列失败（JMH 输出的 ± 号非 ASCII 加减号），产生 13 行对的伪差异；肉眼逐行核对该伪差异输出中键名列已逐项一致后，改用 `awk` 取前五行（键名、参数三列、模式列、不含性能值）复比，判据干净成立。转存型样件全部按权威口径先运行任务再转存。

## clean build 阻塞归因（探针取证在案）

`./gradlew clean build` 于 `:tny-game-common-io:test` 失败：`LocalWordsFilterTest.unloadedFilterPassesThrough` 报 `java.io.UncheckedIOException: 模拟文件瞬时不可读`。归因链：并行升级会话把 commons-io 2.8.0→2.14.0 后，同一 `LocalWordsFilter` 类先编译红（前册 trim-it-module-dependencies-block 归档件反证在案）、源码适配后测试仍红——commons-io 2.14 的文件读取行为变化未适配完；本册改动仅触 `tny-benchmark/build.gradle` 与 `tny.benchmark-module.gradle`，与 `tny-game-common-io` 的单元表面无依赖边。用户裁决带痕收口（2026-10-03）。

## 遗留清理与补跑条款

- `tny-benchmark/results/bench-20261003-quick.json` 为基线抓样的速览导出覆写了既有 bot 写回件（git 跟踪、M 状态）：数据件可再生，恢复原样或交由下次 CI 写回覆盖均可，待用户处置指令（不擅动 checkout）。
- 补跑条款：待并行会话完成 common-io 测试适配、全仓 `clean build` 恢复绿后，本册补跑一次全绿并把结果记入本归档件追加段（先例：trim 册同款条款、bench 链回归补记）。

## 归档后补跑记（2026-10-03，兑现本册补跑条款）

commons-io 2.14 升级引入的 `LocalWordsFilterTest` 测试红已由变更 `adapt-commons-io-214-word-filter` 修复（守卫恢复 + JDK 逐行读取，其验收件记录全仓 `./gradlew clean build` 恢复全绿，1 分 30 秒）。本册归档时立下的"外部适配完成后补跑全绿记入归档件"条款就此兑现；补跑证据归属与全文见 `openspec/changes/adapt-commons-io-214-word-filter/verification-notes.md`。
