# 验证记录

## rename-bench-to-benchmark（2026-10-03）

- 环境：JDK 钉 Corretto 21.0.12.1；正常 UTF-8 locale；daemon 全停后统一捕获口径（本册实证：长命 daemon 会把清单中文打成 `?????` 编码噪声——判据比对须两侧同 daemon 状态，或以"旧路径全字替换为新路径后与 after 比"消除重命名行后核验残余）。
- 1.1 前置与基线：refine-bench 已归档（run 36923295077 完整档实况在案，见其 verification.md）。四件基线存 baseline/：清单 3457 行、族对账绿、速览 13 键、全量枚举 31 键。
- 2.1 目录与源码（D1 原子批）：`tny-bench/` git mv 为 `tny-benchmark/`（`results/` 4 件、模块根散放取证 JSON 15 件、日期取证 md 3 件、两个旧类名 profile 目录共 20 个跟踪文件原样随行，名称内容零改动，D5）；`settings.gradle` include 随改；包目录 `com/tny/game/bench→benchmark`，12 个源文件 package 与 import 换根；benchSuite 四条族正则 `\.bench\.` 段随改为 `\.benchmark\.`，`routineAlgoArms` 臂属取值不动；模块 build.gradle 第 45 行历史变更名指称不动。`projects -q` 与 `:tny-benchmark:jmhSuiteVerify` 通过（族对账当场全绿即包段与声明互配的机制盖章）。
- 2.2 接线与文档（清单逐项核销）：build.yml 七行九处（bench-compile 三任务路径、门禁 `^tny-benchmark/` 分支并做样例演练——`tny-benchmark/README.md` 命中、`docs/foo.md` 不命中；benchRoutineExport 两任务路径、产物上传 path、回写 git add、曲线 output-file-path；`reports/bench/` 内段与 `dev/bench/` 站路径按 Non-Goals 不动）；`tny.bench-suite.gradle` 两处归属指称改 `:tny-benchmark`、"CI 调用路径不变"半句按 D4 改写现状；README 标题正文、目录树、示例正则（第 56 行 `.*benchmark\.net\.` 新包段）、斜杠路径、守卫句按实况改写（守卫实体 `tny.module-checker`，机制为自报角色沿依赖边核对）、第 130 行历史变更名保留；`THIRD-PARTY-LICENSES.md` 表行；`docs/site/index.html` 标签（两处 `./dev/bench/` 链接不随改）；`tny-game-net/.../security-generations_readme.md` 第 84 行路径段改指新目录（核查补列的第九处现行指称）。
- 验收 grep 三道（命令含修正，与 tasks 记录一致）：词形态 grep -w 加历史变更名排除零命中；全名 grep 零命中；`grep -F 'bench\.'`（字面量 bench 反斜杠句点）对模块构建文件与 README 零命中——三道在终态全部实测达成。修正记录：初稿命令一未排除历史变更名（`-w` 把 `promote-netbench-to-tny-bench` 内嵌旧名判为整词，"零命中"不可达成）且扫描面漏 tny-game-net；初稿命令二对族正则的单段 `\.bench\.` 形态失明——均由规划轮对抗核查发现并落地修正。
- 3.1 探针复跑：速览实跑 13 组合键与基线逐键一一对应（换代映射零缺失）；全量枚举 31 键换代对应；`tasks --all` 剔噪清单段——把 before 的旧工程路径按 D1 全字替换后与 after 逐行比对，**内容行零差异**，同 daemon 复测确认两侧中文描述逐字节一致、差异恰为 34 行预期内工程路径重命名行（一次捕获曾现 daemon 编码噪声，复测排除）；`clean build` 全绿（341 执行，零 FAILURE，本轮未现偶红）。README 基线纪律段换代断点行已落地，但随主体批一同提交——"仅改 README"的门禁鉴别提交未单独成批，3.2 该项观察如实降级为"待后续仅文档类合入自然验证"。
- 3.2 CI 实况：经用户授权推送 5.7.x 后观察——bench-compile 新路径绿、改名批经 src 与 build.gradle 分支放行速览并回写、"仅改 README"识别提交凭改写后的 `^tny-benchmark/` 分支单独放行速览、其后夜间完整档在新产物路径与曲线组落数据点无键缺失误告警。run 37091972404 全作业绿：bench-compile 新任务路径绿、门禁经 src 与 build.gradle 分支放行速览并完成、bot 回写 66e39980 落地新产物路径 tny-benchmark/results/（换代后首个曲线数据点）、unit/integration 全绿。仅改 README 的分支鉴别观察按上条说明待后续自然验证。

- 归档后回归补记：bench 册混批回滚的 `git checkout HEAD -- tny-bench/build.gradle` 曾连带回退插件 id 册已落地的第 7 行引用（tny.module-setting 回退为旧 id），插件册基线复测时以 plugin not found 红暴露；修复经 `clean build` 全绿复验（339 执行、零 FAILURE），并全仓大小写敏感 grep 双旧形态零命中。回归根因是混批事故的连带损害，非两册实现本身；两册验收在各自实施时点均真实达成。
