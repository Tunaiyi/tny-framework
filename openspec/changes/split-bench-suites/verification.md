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
