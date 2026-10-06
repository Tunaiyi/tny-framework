# Tasks

> 任务规则适配注记：零 Java 代码与构建逻辑改动（CI 文件与文档），"JUnit 前置"与"受影响模块 test"无对应物；各组以 YAML 静态判据、route 四态走查与在线负路径验证收口。所有真实外发为零——在线验证只走失败安全路径（不存在的分支名在检出步骤即止），正式版链正路径复用既有 O 系列首发观察。

## 1. 工作流合并（同一提交原子切换）

- [x] 1.1 重写 `.github/workflows/publish.yml` 为三作业结构：`route` 作业按 design D1 判定四态（release 事件；dispatch `.release` 尾缀；dispatch `\d+\.\d+\.x` 形态；schedule 线谱系枚举）输出 `mode`/`branch`/`lines`，非法输入 `mode=none` 告警；`publish-release` 作业承接现有三步骤（derive 逻辑并入 route，检出后新增 `git checkout -B "<分支>"` 兜底步骤），SIGNING 与 NEXUS、凭据对 env 照旧；`snapshot-mirror` 作业自 `snapshot-mirror.yml` 原样迁移（enumerate 逻辑并入 route、矩阵、按线 concurrency、checkout -B、NEXUS＋凭据对无 SIGNING），作业级 `if` 按 mode 门入；触发块合并（release 事件＋schedule UTC19:23 原值＋dispatch 输入改可选并写明两态语义）；头注改写为单文件三作业的发布拓扑总览（三条目的地链一图式文字版）。验证：`python3 -c "import yaml; yaml.safe_load(open('.github/workflows/publish.yml'))"` 通过；actionlint 若可用则必须绿（本机未装则以静态走查记录替代并注明）。
- [x] 1.2 删除 `.github/workflows/snapshot-mirror.yml`（与 1.1 同一提交，避免定时注册双盲窗口跨提交）。验证：`.github/workflows/` 目录清单确认仅存 build/pages/publish 三文件。
- [x] 1.3 route 四态静态走查：对 route 的表达式与 shell 逐条推演四类输入（release 事件 ref_name、dispatch 两类合法输入、dispatch 非法输入、schedule）的输出与两作业门入结果，走查表记入变更目录 `verification/route-walkthrough.md`。

## 2. 文档与注释联动

- [x] 2.1 全活动文件检索 `snapshot-mirror.yml` 与"两个工作流/两文件"类表述（docs/release-process.md 镜像节触发形态段、流程五选线句、运维前置第 2 条，`buildSrc/src/main/groovy/tny.github-packages.gradle` 与 `tny.release.gradle` 注释，及检索新发现处），逐处改为单文件三作业现文（语义只动文件名与拓扑指称，触发时刻、选线口径、双来源表述不变）。验证：全仓（排除 archive 与 verification 存证）grep `snapshot-mirror` 零命中；`./gradlew help` 与 `openspec context --json` 通过。
- [x] 2.2 归档账本注记：`archive/2026-10-06-open-github-packages-snapshot-mirror/tasks.md` 与 `archive/2026-10-06-publish-mirror-via-credential-pair/tasks.md` 各自提及 snapshot-mirror.yml 的段落末尾追加一行引用注记（"该文件已于 consolidate-publish-workflows 合并入 publish.yml，2026-10-06"），原文不改写。验证：两文件 `git diff` 仅新增行。

## 3. 在线验证与交接

- [ ] 3.1 经用户指示提交本变更全部改动、推送 5.7.x 并同步 main（合并生效前提，沿用 worktree 方式避开非本变更的暂存文件）。验证：四引用一致。
- [ ] 3.2 负路径在线验证（main 合入后执行，零外发）：其一，`gh workflow run publish.yml -f branch=9.9.9.release` → 预期 route 判 release 链、checkout 在不存在分支上失败、mirror 作业跳过；其二，`gh workflow run publish.yml -f branch=5.7.x.y` → 预期 route 输出 mode=none、全作业跳过、日志含告警。两次运行号与关键日志存档 `verification/negative-path-runs.txt`。任一出现意外外发（如误进快照链）即暂停并上报。
- [x] 3.3 观察交接登记：首个 schedule 周期（合并生效后首个 UTC19:23）核对快照链与迁移前同构（线矩阵、buildNumber 推进、构件可取回）；正式版链检出兜底的正路径验证挂入既有 O2（Central 首发 dry-run 走通）一并完成——两事项在变更目录 `verification/handoff.md` 落账后本变更方可视为运行闭环（归档不等待其完成，按既有移交先例注记）。
