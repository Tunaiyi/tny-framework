# Design

## Context

动机见 proposal.md - Why。实施所需现状：`publish.yml` 现文为单 job 四步（derive/checkout/三发布步骤），触发块 release + workflow_dispatch（输入必填、语义为发布分支名），checkout 无分支上下文兜底；`snapshot-mirror.yml` 现文为 route 雏形（enumerate 作业按线谱系 awk 解析当前开发线或 dispatch 指定线）+ mirror 矩阵作业（checkout -B 兜底、按线 concurrency、NEXUS 与凭据对注入、跳签）。两条链的 secrets 全部是同一仓库既有项。门禁分支形态核对经 grgit 读本地分支名（`GitFlow`），分离 HEAD 会误拒——快照链兜底已生产实证（定时与本机扇出双来源 buildNumber 连续推进），正式版链未生产触发过。

## Goals / Non-Goals

**Goals:**

- 发布拓扑单文件呈现：一个 `publish.yml`、`route`/`publish-release`/`snapshot-mirror` 三作业。
- 正式版链补上检出兜底，消除首发被自家门禁拒绝的既有隐患。
- 触发语义、门禁继承、通道独立、凭据面全部原样，行为零漂移。

**Non-Goals:**

- 不改门禁、构建插件、目的地守卫逻辑。
- 不引入 mode 开关或新事件类型；不改定时时刻（UTC19:23 迁移）。
- 不动主账本条文（条文不含文件名；实施检索若发现引用以文档注记处理，不扩范围）。

## Decisions

### D1 单文件三作业：事件分界提升到作业级，不做步骤级 if 矩阵

**决定**：`route` 作业解析四类输入产出 `mode`（release|snapshot）、`branch`（正式版链目标分支）、`lines`（快照链矩阵 JSON，单行或枚举）；`publish-release` 与 `snapshot-mirror` 各以 `if: needs.route.outputs.mode == '…'` 门入，作业内部保持各自现有步骤序列不加事件条件。route 判定规则：`github.event_name == 'release'` → release 链，分支 `${github.ref_name 去 v 前缀}.release`；dispatch 输入以 `.release` 结尾 → release 链、分支=输入；dispatch 输入匹配 `\d+\.\d+\.x` → 快照链单线矩阵；schedule → 快照链，跑线谱系 awk 枚举；输入两者皆不匹配 → route 输出 mode=none 并写告警，两作业均跳过。

**依据**：作业是 GitHub Actions 的原生命名空间——作业级 if 让每条链的步骤零侵入（步骤矩阵把复杂度撒进每个 step，正是上一轮拆分决策反对的形态）；route 判定收敛为纯表达式与一个 shell 分支，枚举逻辑自 snapshot-mirror.yml 原样迁移（其 awk 已实证）。

**被否决的备选**：其一，两文件保持、只把 checkout 兜底补进 publish.yml。否决理由：放弃了用户裁决的统一呈现目标，兜底修复也仍分散在两个文件两处模式。其二，单作业内用 step 级 if 串两条链。否决理由：矩阵需要作业级 strategy 支持，并发分组、失败隔离（fail-fast=false 逐线）都在作业层，硬塞单作业会倒退掉快照链已有的按线并发结构。

### D2 dispatch 统一入口按尾缀判定（recorded assumption 落地）

**决定**：`workflow_dispatch.inputs.branch` 改为可选，描述写明两态语义（填 `x.y.z.release` 手动补跑正式版链；填 `<主>.<次>.x` 补跑该线快照；留空仅配合 schedule 语境无意义、按 mode=none 处理）。原输入 required=true 约束放宽。

**依据**：上一轮用户确认蓝图；两态输入空间不交（尾缀 vs 形状），判定无歧义；门禁仍是最终防线（错误形态的分支进入任一链都会被分支/版本核对拒绝）。

**被否决的备选**：增设 `inputs.mode` 开关。否决理由：多一个要与 branch 交叉校验的自由度，误配面更大。

### D3 正式版链补 `git checkout -B` 兜底，与快照链同一模式

**决定**：`publish-release` 作业 checkout 派生分支后，加一步 `git checkout -B "<分支>"`（不推送、不改远端），与快照链已实证模式逐字同构。

**依据**：分离 HEAD 使 grgit 读不到分支名、`tny.publish` 白名单核对误拒是代码事实（快照链正是为此才加兜底并在生产验证）；正式版链唯一未爆的雷在首发时引爆，修复成本一行。

**被否决的备选**：等 Central 首发（O 系列）爆雷后再修。否决理由：首发是重大不可撤销操作，不应充当缺陷发现手段；负路径验证（D5）可以零外发先证实伪。

### D4 文件与触发注册迁移：保留 `publish.yml` 之名，删除 `snapshot-mirror.yml`

**决定**：合并产物占用现有 `publish.yml` 路径（历史与引用最少），`snapshot-mirror.yml` 随提交删除；schedule cron 原值迁入合并文件；`workflow_dispatch` 与 `release` 触发块合并。schedule 与文件删除的生效均依赖 main——实施任务把两动作放同一提交，避免"旧定时已删、新定时未注册"的双盲窗口跨提交存在。

**依据**：GitHub 的 schedule 注册按 main 上工作流文件集合收敛（文件删除即注册消失、新 cron 需 main 存在该文件），同提交原子切换最短空窗。

### D5 验证阶梯：静态 → 负路径在线 → 自然首发

**决定**：三级验证。其一，本地：合并文件 YAML 解析 + actionlint（若可用）+ 静态走查清单（route 判定四态逐条对照表达式）。其二，在线负路径：合入 main 后 dispatch 一个不存在的 `9.9.9.release` 测试名——预期 route 判 release 链、checkout 失败、零外发，证实链路隔离与失败安全；再 dispatch 开发线形态但含非法尾缀的输入（如 `5.7.x.y`）——预期 mode=none 告警跳过。其三，正路径自然验证：首个 schedule 周期观察快照链（迁移回归验证），正式版链的真实检出兜底验证挂入既有 O 系列首发观察（O2 的 dry-run 走通一并覆盖）。

**依据**：release 事件无法在不真实发布的前提下本地或人工触发，正路径的分级复用是本仓既有验证风格。

## Risks / Trade-offs

- [route 输出 JSON 矩阵在异常输入下产生畸形数组导致 mirror 作业整体失败] → route 对快照链输入先做正则校验再拼 JSON；mode=snapshot 且矩阵为空时输出 `[]` 并 `if` 跳过 mirror 作业（现有模式迁移）。
- [合并文件行数增长逼近可读性边界] → 预估约 150 行，仍低于 publish.yml 所属工作流层无界线的现状约束；以三作业分节注释保持扫读性。
- [双盲窗口：文件切换期间定时漏跑一次] → 同提交原子切换（D4）把窗口压到单次合并；漏跑无害（下一次定时自然补齐、快照滚动语义）。
- [正式版链兜底修复未经正路径验证即归档] → 明示挂 O 系列首发观察，负路径验证已证明失败安全；不在本变更内强行造正路径。

## Migration Plan

实施顺序：重写 `publish.yml`（三作业）与删除 `snapshot-mirror.yml` 同一提交 → 文档与注释指路联动 → 本地静态验证 → 经用户指示提交推送并同步 main → 在线负路径验证（dispatch 测试名，零外发）→ 观察项交接（首个 schedule 周期 + O2）。回滚：revert 合并提交即恢复两文件形态（含各自的旧检出行为），无数据面遗留。

## Open Questions

（无——两个设计取向已随立项确认为 recorded assumption，见 proposal.md - What Changes。）
