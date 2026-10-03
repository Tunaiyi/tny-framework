# Apply 验证记录

## 组 1：tny-bench/build.gradle 两规模（完成）

- **1.1** `benchQuickRun` 以负向断言表达"常规族减去矩阵类加探针"（族内新增类自动入速览）；`benchScope=quick` 分支不设置 algo 参数域（矩阵类不在选择面）。验证：`jmhList` 缺省输出维持七个基名条目；`-PbenchScope=quick -PbenchFast` 选择面恰好十三个组合（管线 2、队列 6、配对 4、探针 1），矩阵类零条目。
- **1.2** 导出任务按规模命名。验证：quick 导出产出 `results/bench-20261002-quick.json`；完整规模的 `-routine.json` 沿用既入库实况，未在本地重跑覆盖（该文件为 CI 真实数据，本地快跑会污染）。两规模文件名互不覆盖，同日期覆盖限定同规模语义成立。速览导出产物由 benchFast 产生，仅作命名验证，不提交入库。
- **1.3** `jmhCompileGeneratedClasses + jmhList + jmhSuiteVerify` 全链退出零；族属对账输出 routine 4 类 / devtest 5 类 / 探针 1 类，族声明结构未受速览影响。

## 组 2：build.yml 门禁与执行参数（完成）

- **2.1 至 2.4** 门禁作业输出 run 与 scope 两值：push 事件比对前后提交文件清单（源码目录、构建脚本、版本配置、基准模块命中则速览放行；文档、案卷、流水线脚本跳过；前序提交不可达按完整规模安全侧放行）；定时与手动恒完整；PR 一律不放行。`bench-routine` 经 needs 取门禁结果决定起跑与规模；导出命令按 scope 分支传参；曲线分组键为"Routine 规模 (分支)"三元组合。
- **2.5 组验证**：YAML 解析通过；作业拓扑 unit、integration、bench-compile、bench-scope-gate、bench-routine 无环；bench-compile 步骤命令零改动。判定正则八条样例走查：四条行为性路径全部放行、四条非行为性路径全部跳过，含根 build.gradle 与 gradle/libs.versions.toml 边界。

## 组 3：tny-bench/README.md 两规模口径（完成）

- **3.1** 执行面章节改写为"速览与完整"双规模表（成员、组合数、用途、命令），触发规则逐条对应门禁实现；矩阵退化延迟显现写明为既定裁决。表中成员与构建脚本清单三方一致（速览十三组合为组 1 实测）。
- **3.2** 产物约定改为"日期加规模"双指纹命名，同日期覆盖与逐版对比限定同规模；引用示例增加规模标注位。历史注记追加本变更条目。
- **3.3** 全文无"每次合入跑全套"旧口径残留（仅存的历史表述属注记性叙述）。

## 组 4 实况验证（4.1 与 4.2 完成，4.3 待续）

- **4.2 速览实况**（run 36914646306，实现批 9f2a40c8）：门禁判框架改动放行速览；`bench-routine` 全步骤成功，**run 端到端 7 分 29 秒**（19:29:22Z 至 19:36:51Z，快于预估）；bot 回写 `31e0f9f4`，产物 `bench-20261001-quick.json` 经核对为十三条目、fork 2 的 D3 真参数数据（管线 2、队列 6、配对 4、探针 1）。
- **4.1 跳过实况**（run 36914999723，案卷批 3f151906）：门禁判定成功、`bench-routine` 作业状态 **skipped**，零计时零回写；unit/integration/bench-compile 照常全绿。
- **曲线分组实况**（gh-pages data.js）：新组"Routine quick (5.7.x)"含 1 个数据点；旧未分组"Benchmark"冻结在 18 点不再增长（D3 迁移预期成立）。
- **实况暴露缺陷一处**：导出任务日期跟随 runner 时区（UTC）而本地为东八区，同次操作两边日期错位一日（速览首产物落 20261001 而非本地口径 20261002）。修复：`benchRoutineExport` 日期钉 UTC 与回写提交时间戳同基准；本地以 benchFast 验证命名正确后由远端真产物还原。
