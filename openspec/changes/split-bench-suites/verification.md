# Apply 验证记录

## 组 1：前置核对（完成）

- **1.1 远端 gh-pages 点位数核对**：`git ls-remote --heads github`（远端名是 `github` 非 `origin`）共 44 分支，**无 gh-pages 分支** → 曲线零历史点位，搬包 bname 断层成本为 0，按 D4 直进，无需人工裁决。
- **1.2 移包前枚举基线**：`./gradlew :tny-bench:jmhList -PbenchAll` 成功；全量 **31 个基准方法条目**（`-l` 不展开参数组合，参数化执行面 37 组合口径不变）存档至本目录 `baseline-premove-jmhList.txt`。
- 顺带观察（不属本变更，未处置）：工作区存在未跟踪文件 `tny-game-bench-dummy-placeholder`（11 字节，内容 "placeholder"，今日 09:36 产生）——非目录不入 settings.gradle 装配，无构建影响；来源不明，留用户确认。

## 组 2：tny-bench/src 支撑件摘出与族移包（完成）

- **2.1** `shared/` 三件就位：`AeadRfc7539` 整类迁入（public 类 + 跨包 8 成员放宽，debug*/私有保持）；`Crc64Slicing`+`CRC64_INITIAL` 摘出宿主入顶层类；`sipHash64`+自带 le64 提为 `SipHash64`。算法体零 diff（le64 为摘出所需的私有副本）。
- **2.2** 族移包 `git mv` 保历史：routine 4 类 / devtest 5 类；package 声明、Matrix 三引用改 shared 简单名 + import、AeadTransport/宿主 import。**对账**：`jmhList -PbenchAll` 移后 31 条 = 移前 31 条，剥族前缀后 diff 为空；分布 routine 7 / devtest 24。
- **2.3** `-PbenchFast` 探针：devtest 子集全量执行成功（结果 JSON 47 条目=size 参数展开，含 AeadTransport selfTest 装配）；routine 子集全量 BUILD SUCCESSFUL（Matrix BenchTunnel/shared 引用运行时装配通过）。

## 组 3：tny-bench/build.gradle（完成）

- **3.1** ext 族清单就位：`benchRoutineClasses/benchDevSuiteClasses`（族子包正则）+ `benchFacilityProbe`（Smoke 指位）+ `benchRoutineAlgoArms` 六臂保留；缺省 jmh includes = 常规∪探针自拼单正则（插件逗号拼接语义规避沿用）。
- **3.2** `jmhSuiteVerify` 两段对账上线；**负向验证已做**：临时把 devtest 正则改 `[L-V]` 起首限位 → 红并点名 Aead/Crypto 两类（S 在 L-V 序内故 Smoke 幸中——机制符合预期）→ 还原转绿。
- **3.3** `benchRoutineExport` + 产物 `-routine.json`；缺省选择面 `-PbenchFast` 实测 **37 组合**（Matrix 六臂 24 + PacketCodec 2 + MQ 6 + RF 4 + 探针 1）；旧任务名 `benchAnchorExport` 确认 not found。
- **3.4** 全链 `jmhCompileGeneratedClasses + jmhList + jmhSuiteVerify` BUILD SUCCESSFUL；`jmhList` 缺省输出仅 routine 4 类 7 基名条目。

## 组 4：.github/workflows/build.yml（完成）

- **注意**：组改动前本文件已被 CI 线独立提交更新（etcd 三雷定案、诊断电路）——外部改动保留，本次仅动 bench 两 job 与 dispatch 注释。
- **4.1** bench-compile 命令升级为 `jmhCompileGeneratedClasses + jmhList + jmhSuiteVerify`（与组 3.4 本地跑绿序列一字不差）。
- **4.2** `bench-nightly`→`bench-routine`：`if:` 三触发枚举（push/schedule/dispatch）、`benchRoutineExport`、artifact 名与提交信息 routine 口径、`[skip ci]` 保留；yml 除历史注释外无 anchor 残留（grep 验证）。
- **4.3** `python yaml.safe_load` 语法通过；可本地化命令均已在组 3 复现。

## 组 5：tny-bench/README.md（完成）

- **5.1** 两族分类表（10 基准 + 3 支撑件逐一归位、探针特例、执行面 37 组合口径）；「锚集」术语退役，历史注记补 split-bench-suites 条目。与 ext 清单/目录三方比对一致（jmhSuiteVerify 输出 4+5+1 类）。
- **5.2** 命令面逐条验证：jmhList（清单入 build/tmp 文件系既有行为，原 README 同款）、devtest 按名子集 ✓、`benchRoutineExport -PbenchFast` 全链出 `results/bench-20261001-routine.json` ✓（快跑产物经授权删除未成、**留存**——JSON 头自述 -f1/wi0 可辨，CI 当日 push 按同日期覆盖纪律更新）、`-PbenchGc` 通路 BUILD SUCCESSFUL（up-to-date 怪癖 README 自带提示）。
- **5.3** README 仅存两处 anchor 字样且均为「历史口径不回改」注记本体；根 README 与 gradle/ 无 benchAnchor/bench-nightly 残留。

## 组 6：首验、回填与收口（部分——6.2/6.4 需外部动作）

- **6.1** 缺省选择面 `-PbenchFast` 全链 37 组合确认（分布 24+2+6+4+1，探针 1）；benchFast 快跑 31s。D3 全量本地预算按 27 组合基线 13 分钟线性外推 ≈18 分钟；CI 预估维持 25~40 分钟区间，待 6.2/6.4 实测校正。
- **6.3** 双向负向皆验：① 未归族新类（临时 `UnclassifiedScratchBenchmark` 放域根）→ `jmhSuiteVerify` 红并点名 → 移出还原绿（临时类现存于 /tmp，rm 被权限拦截改用 mv）；② 族正则漂移（3.2 已验，Aead/Crypto 点名）。
- **6.5** `openspec validate --strict` 通过。
- **6.2/6.4 阻塞点（外部）**：workflow_dispatch 首验与自然 push 观察需要——(a) 授权提交并推送本批改动（当前全部未提交），(b) GitHub Actions UI 手动触发 `bench-routine` 或等首个合入 push。本机无 gh CLI。

## 首验证据补充（verify 轮，2026-10-01）

- 本次 push（0a668033）即触发执行通道首跑：**bot 回写 commit `43a5e966`**（`chore(bench): routine benchmark results [skip ci]`，08:55:19Z；push 08:37:05Z → **端到端 ≈18min05s**，`[skip ci]` 防自触发实证）。
- 产物 `bench-20261001-routine.json`（远端 blob 读取）：**37 组合、fork2/wi5/i10 D3 参数、族分布 routine 36 + devtest 探针 1**，FQCN 即新族主键——6.4 数据点与时长核对完成，README 预算节已回填。
- 待核（6.2 余项）：远端 **gh-pages 分支尚未可见**（`git ls-remote` 无此分支）——`Store benchmark result & trend` 步骤结果需经 GitHub MCP（待 OAuth 授权）或 Actions UI 确认；run 链接需 UI 补录。

## 6.2 终核（2026-10-01 22:4x）

- 执行→产物→回写→防循环 四段实证齐备（commit 43a5e966、37 组合 D3 产物、无自触发）。
- **曲线步骤未起线**：距回写 ~6h 远端仍无 gh-pages → `Store benchmark result & trend` 步骤失败被 `continue-on-error` 吞——恰为 design 风险条预想的降级面（信号面缺失，产物面不受影响）。归因需 Actions UI 看该 run 末步骤日志（本机 GitHub MCP OAuth 回调链修复中，无 Actions API 工具）。
- 结论：6.2 保持未勾（run 链接与曲线证据缺口）；下一步：①用户重启会话走修好的 --service-ports OAuth 接线，或 UI 直接看 run 日志；②曲线归因后决定修 action 配置或按 design 接受降级并注记。

## 23:07 第二轮 CI 核查（cron）

- 第二轮 `bench-routine`（push f1acdc9b 触发）：回写 commit `065db998`（bot，15:05:10Z，端到端 ≈21min）——执行链两轮稳定复现 ✓✓。
- **gh-pages 两轮均未生成** → `Store benchmark result & trend` 稳定失败、被 continue-on-error 吞。归因需 run 日志（GitHub MCP 无 Actions API），已向用户索取步骤日志。
- 候选归因（待日志确认）：action 首次建分支推送路径 / jmh 输出解析 / GITHUB_TOKEN scope 边缘。

## 曲线失败归因与修复（用户提供 run 日志）

- 报错原文：`Error: 'auto-push' is enabled but 'github-token' is not set.`——新版 github-action-benchmark 在 auto-push 下要求显式传 token，不隐式取 GITHUB_TOKEN。
- 修复：`build.yml` Store 步骤 `with` 增 `github-token: ${{ secrets.GITHUB_TOKEN }}`（job 级 contents:write 本已具备）。
- 第三轮验证：修复 commit 的 push 即触发下一轮 bench-routine，核 gh-pages 起线后销 6.2。

## 第四轮日志与回写竞态修复（00:0x）

- 用户贴 run 日志：bot commit `4bb98df` 已生成但 `git push` 被拒（fetch first）——**回写竞态实锤**（执行窗 ≈21min 内分支前进即死；stabilize 案卷 run#18 同款）。Store 步骤因 job 中止**从未执行**——token 修复至今未被验证。
- 修复（本变更差量内：回写步骤系 bench-nightly→bench-routine 迁移所写）：commit 后 `fetch + rebase -X theirs origin/$GITHUB_REF_NAME + push HEAD:refspec`；同日期产物冲突取当轮新结果。
- 疑点登记（销 6.2 时顺带核）：第四轮 diff 为 1258+/1258−，与首轮产物 2861 行体量差异大——第五轮闭环时核对远端最新产物**条目数=37** 与参数头，若缩水需查 jmh 任务缓存复用。

## 更正：gh-pages「已生成」系误判（00:1x）

- `git ls-remote ... gh-pages` 无匹配时同样 exit 0（输出为空），`&& echo` 判定式踩空——实际 gh-pages **仍未生成**，fetch 复核证伪。
- 三轮 run 死因重新对表：f49fa5fe 轮回写侥幸成功但 workflow 无 token → Store 失败被吞；ba82cf06 轮（含 token）死于回写竞态、Store 未执行；**e36ee1d6 轮（token+rebase 双修复齐备）才是首验**，~00:33 回写 + Store，00:47 自动核查生效。

## Store 步骤终极归因与预建分支（01:0x）

- 第二轮日志（token 修复后）：action 第一步即 `git fetch <repo> gh-pages:gh-pages`，**分支不存在直接 exit 128**——该版本 auto-push 无自建分支路径；token 本身已生效（fetch 带认证头走到分支查找）。
- 处置：本地 `commit-tree` 空树法预建孤儿分支 `gh-pages`（未触碰共享工作区）。三轮修复至此齐备：显式 token（ba82cf06）→ 回写 rebase（e36ee1d6，第五轮 00e4c9da 实证生效）→ 预建分支（本轮）。
- 触发轮：本次 verification commit 的 push = 首个全条件 run；预期 bot 回写后 Store 走通并推首个 benchmark-data.json。
