# Design

## Context

动机见 proposal.md 的 Why；行为契约见 specs/benchmark-harness/spec.md 差量。本文件记录现状事实与实现取向。

现状（均为仓库与运行实证）：

- `build.yml` 的 `bench-routine` 作业（第 126 至 175 行）触发条件为 push、schedule、workflow_dispatch 三种，无任何内容条件；单次全程实测约十八至二十一分钟，其中三十七个参数组合里二十四个来自加密装配矩阵类；结果 JSON 经 rebase 防竞态后回写入库（`4bb98df` 被拒一案例证了 rebase 修复的必要性，已上线）。
- `tny-bench/build.gradle` 的族清单 ext（`benchRoutineClasses`、`benchDevSuiteClasses`、`benchFacilityProbe`、`benchRoutineAlgoArms`）是选择面单一事实；缺省 `jmh` 执行面为常规族全部加探针（`split-bench-suites` 落地态）。
- 曲线现状：gh-pages 仅存 5.7.x 基线数据点约七轮，main 恢复合入后两套基线将混入同一序列组（`split-bench-suites` 把执行通道扩展到双分支 push 时未做分组，属遗留债务，本轮以数据点尚少为窗口低成本补救）。
- 爆炸半径：`bench-routine` 与 `benchRoutineExport` 的引用面 grep 实证只存在于 `.github/workflows/build.yml`、`tny-bench/build.gradle`、`tny-bench/README.md` 三处；codegraph 不索引 Gradle DSL 与 workflow job 标识符（`benchAnchorRun` 检索仅命中无关 Java 符号，`split-bench-suites` 已记录同结论）。本变更零 Java 源码改动。
- 与在途变更的交集核查：`consolidate-ci-it-lanes`（删 e2e）提案明文"bench 线 jobs 不在本变更范围"；`fix-ci-unit-flakes` 只改 unit 作业——两者与本变更在 `build.yml` 上改动段落不相交。

## Goals / Non-Goals

**Goals:**
- 非行为性合入（文档、案卷、流水线脚本）完全不触发计时与回写。
- 框架合入的反馈时长从约二十分钟降到七至九分钟（速览面），完整覆盖仍由夜间与手动保底。
- 曲线序列组按"分支 × 规模"隔离，告警比较不跨组。
- 规模定义作为集中声明进基准构建脚本，CI 只选枚举例，不复制选择面内容。

**Non-Goals:**
- 不改基线测量参数（fork 数、预热与测量迭代）——降低精度换时长属于动摇 D3 纪律，明确不做。
- 不改评审通道、单元与集成测试通道的触发规则；不做草稿 PR 跳过（见 Open Questions）。
- 不改结果回写的 rebase 与 `[skip ci]` 机制。
- 不给两个规模配置各自的告警阈值（先用同一阈值观察分组后的噪声，属运维参数）。

## Decisions

### D1 内容门禁：前置判定作业加放行条件（P13）

`build.yml` 新增秒级作业 `bench-scope-gate`：仅对 push 事件读取本次提交改动的文件清单（`git diff --name-only` 对比事件携带的前后两个 commit），命中以下任一前缀即判定为框架行为改动——任何模块的 `src/`、任何 `build.gradle`、根 `gradle.properties`、`gradle/libs.versions.toml`、`tny-bench/` 全部；其余判定为非行为性改动。schedule 与 workflow_dispatch 事件恒定放行完整规模。`bench-routine` 声明 `needs` 该作业并在 push 场合以其输出为放行条件。判定输入不可达时（如 force push 导致事件携带的前序 commit 在远端不存在）按框架改动放行——宁可多跑一次计时，不可漏掉真实退化。

- **验证回答（P13）**：文档合入后 bench-routine 显示 skipped、分支无新自动提交；框架合入后速览数据点约九分钟内落曲线——两条都是流水线页面可直接观察的断言。
- **被否决备选**：① 第三方 paths-filter 动作——为一个正则判断引入外部供给依赖与版本钉维护，仓库既有实践是内联脚本可覆盖的场景不引第三方（P13 同等可得，成本更低）；② 工作流级 `on.push.paths` 过滤——GitHub 的路径过滤作用于整个工作流，会把 unit 与 integration 也挡掉，评审守门面不能按内容豁免（正确性问题与改动内容无关）；③ 在 bench-routine 内部用第一步自杀式跳过——同作业在 skipped 状态仍占 runner 名额并照常拉取代码，前置判定作业让被挡场合只花几秒。

### D2 两个规模的表达：构建脚本集中声明加枚举例开关（P5、分类契约同源）

`tny-bench/build.gradle` 新增 `benchQuickClasses`（常规族的速览子集声明：全管线、发送队列、RPC 配对三类加设施探针，加密矩阵类不入）；`jmh` 选择面按新枚举例 `benchScope` 取值：`quick` 用速览清单（此时 `benchRoutineAlgoArms` 参数域覆写自然不再命中任何类，无需删除），缺省（本地直接执行与 `full`）维持现状全族——"缺省运行即基线"的既有 Scenario 语义零变化。`benchRoutineExport` 的目标文件名随规模取 `bench-<日期>-quick.json` 或维持 `bench-<日期>-routine.json`，同日期覆盖每规模各一份。CI 的 push 场合传 `-PbenchScope=quick`，定时与手动传 `full` 或不传。

- **被否决备选**：① 在 `build.yml` 里直接复制速览正则——把选择面声明复制到流水线脚本，违背「基准二分分类契约」的集中声明单一事实条款，族调整时两处漂移；② 注册两个独立 Gradle 任务（quick 与 full 各一套 includes）——参数域覆写与产物路径逻辑双倍维护，枚举例单开关覆盖同一套任务更省（P5：规模是执行策略的一个取值，不是第二条流水线）。
- **命名（M3）**：`quick` 与既有 `benchAll`、`benchFast` 属性家族并列；产物后缀 `-quick` 直译速览。

### D3 曲线分组：序列组名携带分支与规模（数据窗口期依据）

benchmark-action 的 `name` 输入即 entries 的分组键；配置为 `Routine full (<分支>)` 与 `Routine quick (<分支>)` 两种取值（由 job 的触发上下文变量拼接）。main 与 5.7.x、速览与完整从此四组互不混线，告警比较天然只发生在同组序列内（action 按分组键维护各自的近期均值）。已存在的约七轮未分组数据点不做迁移——曲线刚起步，历史断层成本近零，与 `split-bench-suites` D4"数据面为空窗口即刻搬家"的裁决同型。

- **被否决备选**：按分支拆 gh-pages 目录或独立数据路径——action 无此一等参数，自造目录结构违背其托管约定；分组键拼接已达成同等隔离。

### D4 保持项（明确继承，不在本设计重述理由）

评审通道零计时、人裁决信号语义、D3 基线参数、回写 rebase 与 `[skip ci]`、`jmhSuiteVerify` 双向对账——全部原样。`bench-compile` 不受内容门禁影响（文档提交仍验证基准可编译与族清单齐整，这正是评审通道应有的全触发）。

## Risks / Trade-offs

- 加密矩阵级退化在合入当天不可见，最迟下一次夜间运行显现 → 矩阵属于算法装配层观察项，非合入守门面；速览面保留全管线哨兵（矩阵劣化若影响生产装配吞吐，管线项同样会动）。
- 内容判定的路径前缀清单随仓库结构演化可能过窄（如新增非 `src` 布局的行为性目录）→ 判定逻辑集中在单一 shell 段落，漏判方向是"多计时"安全侧；README 记录判定标准供演化时同步。
- 门禁作业与曲线分组名依赖 GitHub 表达式上下文变量，push 与 schedule 事件的字段差异需实测覆盖 → 任务组验证安排文档合入、框架合入、手动触发三种实况各观察一次。
- 速览与完整两规模的产物同日共存 → 文件名指纹含规模，引用约定（README）要求变更工件注明规模标识，审计口径明确。

## Migration Plan

1. 本差量规格先行落地评审（P12），通过后实施。
2. `tny-bench/build.gradle`：速览清单与 `benchScope` 枚举例、导出任务产物命名；本地以选择面探针核对速览为十三组合、缺省仍为三十七组合。
3. `build.yml`：门禁作业、`bench-routine` 放行条件与规模参数、曲线分组名。
4. `tny-bench/README.md`：两规模口径、产物命名表、实测时长预算。
5. 实况验证顺序：推送一个纯文档提交观察 bench-routine 被跳过；推送一个含源码的提交（或等待自然合入）观察速览数据点约九分钟落曲线；手动触发一次完整规模核对分组键生效与既有未分组历史点不再增长。
6. 回滚策略：单 revert 回到当前"一切 push 跑全套"行为；曲线旧未分组数据点与新分组点并存不冲突（action 只追加）。

## Open Questions

- 草稿 PR 是否整体跳过 CI 作业（进一步省共享 runner 时长）：与基准无关的门禁策略问题，用户尚未表态，不在本变更实施；若未来实施应进 integration-testing 或独立门禁提案。
- 分组后各规模序列组的告警阈值是否值得分别收紧（如完整组 150%、速览组 120%）：待分组积累约两周数据后运维调参，不影响差量与任务分解。

## 爆炸半径摘要（规则要求）

| 引用面 | 文件 | 处置 |
|---|---|---|
| `bench-routine` 作业与触发条件 | `.github/workflows/build.yml` 第 126 至 175 行 | 加 needs 与放行条件、规模参数、分组名（D1/D3） |
| （新增）内容判定作业 | `.github/workflows/build.yml` | 新作业，数秒级，仅 push 事件生效（D1） |
| 族清单 ext 与 `jmh{}` 选择面、`benchRoutineExport` | `tny-bench/build.gradle` | 增 `benchQuickClasses` 与 `benchScope`；导出命名分支（D2） |
| 执行面与产物章节 | `tny-bench/README.md` | 两规模口径改写与预算更新 |
| 曲线历史数据 | gh-pages 分支 entries 分组键 | 新组名起新组，旧组冻结不迁移（D3） |
| unit / integration / bench-compile | `.github/workflows/build.yml` | 不受影响；与两个在途变更的改动段落不相交 |
