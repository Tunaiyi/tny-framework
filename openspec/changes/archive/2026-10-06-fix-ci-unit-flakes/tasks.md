# Tasks

> 设施向变更（无公共 API/协议改动）：JUnit5 测试前置条款不适用；本案的"测试先行"体现为 **D2 探针先自证电路、D3 定罪先于手术**。各组末附验证任务；操作纪律：`docker-it-rerun-discipline`（本地）+ `openspec-commit-hygiene`（提交）。

## 1. unit 诊断电路上线（design D1）

- [x] 1.1 `build.yml` unit job：加 `permissions: contents: write`；unit 步改 `continue-on-error` + `tee` 捕获全量输出到文件；新增投递步（`if: failure`）：按 D1 采集清单 (a)docker ps/etcd 容器日志 (b)gradle 输出尾 200KB (c)失败 XML (d)环境自述，`--orphan ci-unit-diag` + `push -f`；新增末尾显式 `exit 1` 步保硬门禁。验证：`python3 -c "import yaml; yaml.safe_load(...)"` 通过 + 逐条对照 integration job 三件套结构同型。
- [x] 1.2 组验证：`./gradlew :tny-game-namnspace-etcd:test --rerun`（本地 OrbStack etcd 在位，经 compose 栈）确认 unit 步改造不影响本地路径；提交仅含 build.yml（暂存面先查），推送。

## 2. 电路自证探针（design D2）

- [x] 2.1 推一次性探针提交：新增独立测试类（不碰存量）必然失败断言，类顶注释与提交信息标 `PROBE fix-ci-unit-flakes 2.1`。验证：push 成功记录 commit sha。
- [x] 2.2 等待该轮 run unit 红后 ≤5 分钟：`git fetch github ci-unit-diag`（443 别名）→ 案卷四件齐（etcd 容器日志/gradle 尾/失败 XML/环境），README 或文件名含 `PROBE`。验证：逐项 `git show FETCH_HEAD:<file>` 存在且非空；缺 (a) 项则按降级语义记录并核查 docker 可见性原因。
- [x] 2.3 回滚探针提交（revert，勿改写历史）。验证：`ci-unit-diag` 保留探针案卷（自然红覆盖前可 fetch 到）；后续轮次 unit 恢复原语义。

## 3. 自然红取证与定罪（design D3）

- [x] 3.1 建立取卷节奏：此后每轮 5.7.x push 后查 unit 结论；红即 fetch 案卷，登记至本 change `diagnosis.md`（新建，格式沿 stabilize it-diagnosis.md 取证时间线表）。验证：`diagnosis.md` 首行含登记规则与 run 账链接。
- [x] 3.2 拿到首个自然红案卷（或连续 5 轮零红 → 记录"就绪竞态假设暂未复现"并跳到 5 组收口、把 §6 unit 登记簿以"观察位"结转）：按 D3 三签名列归因表，定罪到支 a/b/c 之一。验证：`diagnosis.md` 定罪栏填毕，签名证据（栈/日志摘录）+ 判据引用一一对应。

## 4. 根治实施（仅在 3.2 定罪后执行；未定罪则整组跳过并在验证注明）

- [x] 4.1a （支 a）`tny-game-namnspace-etcd` 测试面类级有界预检：`@BeforeAll` Awaitility ≤10s 探 etcd health，超时抛显式环境异常。验证：本地 `--rerun` 单测绿 + 故意停 etcd 时报"环境错"而非挂死/断言错。**跳过裁定（2026-10-05 按组头"未定罪则整组跳过并在验证注明"落笔）**：本支定罪前提（services etcd 就绪竞态）至今未被任何案卷证实——定罪栏两个自然红（run#38 与 GitHub Actions run id 37165077819）均判非 etcd，观察账自 run#94 起的多轮观察内该假设未现形。依 design D3"禁止未取证先修"，本项不实施、只记裁定；若日后出现 etcd 模块连接拒绝或超时栈的同签名红，回到任务 3.2 重新定罪，届时再实施本项。
- [x] 4.1b （支 b）定罪用例按 stabilize §5 判例改有界轮询+绝对上限。验证：本地单跑定罪用例绿；改造处注释引用本案卷 run 号。完成注记（2026-10-04）：案 #1 与案 #2 的判例式改造经用户指令当日在移交线实施完毕——actor 线的 FlowTestUnits 驱动循环加十秒绝对上限，TypeStageTest 与 VoidTypeStageTest 全部墙钟窗口判据改为尝试计数判据；common-lang 线的 ObjectLockerTest 改会合形态、CollectionLockTest 的并行驻留观测改为二十秒有界窗口多轮重试；同轮修复 MapperLocker 在获取失败与中断路径"归还引用计数却不销毁条目"的生产残留（有界轮询能够收敛的前提，机制详见 diagnosis.md 案 #2 执行记录）。验证兑现：本地空载与八进程 CPU 烧机负载下，`:tny-game-actor:test --tests 'drama.task.*'` 共 28 个用例全绿、`:tny-game-common-lang:test` 全模块连同消费面 `:tny-game-data:test` 与 `:tny-game-net:test` 全绿；改造处注释引用实况（2026-10-05 复核修正原"逐处"表述）：actor 三文件的改造注释共十四处引用 run#38；ObjectLockerTest 五处与 MapperLocker 一处引用 GitHub Actions run id 37165077819；CollectionLockTest 三处改造注释引用登记簿"本地登记 #2/3/4"条目——该用例的红色案卷线索即此登记条目（本地三次偶红，无 CI run 号），其所属家族见案 #2 执行记录。任务 4.2 的"连续零红 ≥10 次 push"计数自本修复推送轮起另行累计。
- [x] 4.2 组验证（2026-10-06 经用户裁决改写为移交形态后勾选）：根治提交后连续 CI unit 零红 ≥10 次 push 属时间型观察，会话内不可做完——计数登记与持续跟踪移交 `diagnosis.md` 账目（与本变更 47dff9d2 收口提交的账目记忆结转同一形态），变更归档不中止观察；期间任何同签名复发按原文回 3.2 复诊，达标判据与口径（stabilize §6：红主犯署名到 job）不变。

## 5. IT 尾款账目收口（design D4）

- [x] 5.1 读 stabilize 归档体 §6 登记簿推进至 10/10（只记账不施工：每轮查 integration 结论 + `ci-it-diag` 无新投递即计数）。验证：本 change 记录终局行 `10/10 定案`。
- [x] 5.2 门禁阶段二书面定案：在 `handoff-ci-integration-remediation.md` 补"阶段二生效：保持硬门禁/不降级/不重试"终裁行（文档在归档体，允许追加终裁注记，不改历史正文）。验证：注记含日期与 run 号。

## 6. 收口

- [x] 6.1 `release-note.md`（设施向：unit 电路生效声明、根治内容与定罪案卷号、门禁语义零变化声明）。完成注记（2026-10-05）：release-note.md 已撰写，含上述三要素，另如实声明两处账实——实施期追加的 MapperLocker 生产修复（提案修订注记已对齐）与案 #1 轮案卷固化失败（run#38 原件不可复取、签名证据仅存定罪栏摘录）。
- [x] 6.2 账与记忆结转：stabilize `it-diagnosis.md` §3"另案"登记与 memory `docker-it-rerun-discipline` 关联条目更新为已收口/移交到位。验证：`openspec validate fix-ci-unit-flakes` 通过；全仓 `./gradlew test`（本地，OrbStack 在位）绿。完成注记（2026-10-05）：结转三处已落笔——stabilize 归档体 §3 加收口指针行、§6 收口后续账加"unit 另案收口账"追记段（均为追加，历史正文未改写）；memory `docker-it-rerun-discipline` 条目追加 unit 另案收口段并更新索引行。验证两条兑现：`openspec validate fix-ci-unit-flakes` 输出"Change 'fix-ci-unit-flakes' is valid"；全仓 `./gradlew test` 于 2026-10-05 实测（OrbStack etcd 与 redis 端口在位）BUILD SUCCESSFUL，171 份结果文件全量扫描合计 919 用例、失败与错误 0、跳过 0。任务 4.2 的连续零红观察计数不属本条验收，在该行继续滚动。

> 3.2 完成注记：首案卷=run#38（支 c 跨线：tny-game-actor 墙钟时序测试，判决与处方见 diagnosis.md 定罪栏案#1；本案不动刀）。
