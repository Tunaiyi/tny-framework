# Verification

## 1. 实施证据（组 1）

- **删段落地**：e2e job（原 :111-127，含 `services: etcd`）已从 build.yml 消失。实际提交载体=`e942a878`（兄弟会话 fix-ci-unit-flakes 的 unit 电路提交经共享工作树 `git add` 顺带吞入——功能面正确、归属偏差详见 tasks 1.2 勘记，`git diff 0a77b631..e942a878` 含 e2e 删除三处减行为证）。
- **静态断言**：改后 `yaml.safe_load` 通过；job 清单无 `e2e`；integration 的电路步（`Exfil...ci-it-diag`）、硬门禁步（`Fail job...`）、`permissions: contents: write` 完整在位。
- **runner 面直证**：run#33（首个含删段的 run）job 数=4（unit / integration / bench-compile / bench-routine），e2e 不再出现。
- 零 Java/gradle 改动：无模块测试义务（skip_specs 设施变更，循 stabilize 先例以等价证据替代 `:模块:test`）。

## 2. 观察窗账（起点 run#33，口径=IT job 级，循 stabilize 案卷 §6）

| run | head | IT job | 备注 |
|---|---|---|---|
| #33 | e942a878 | ✅ | **全 run success**（含 unit ✅）；runner 面四 job 直证 |
| #34 | 670c3e95 | ✅ | 全 run success |
| #35 | f3bebdcb | ✅ | run 级红主犯=unit `CiCircuitProbeTest` 探针（fix-ci-unit-flakes 线，`ci-unit-diag` 首投卷） |
| #36 | 9979a681 | ✅ | 同上探针红 |
| #37 | edd52dbd | ✅ | |
| #38 | b72d6103 | ✅ | |
| #39 | a1f04f26 | ✅ | 全 run success（门槛在此满 7 ≥ 5） |

- **#40（b26abd89）在途**=第 8 轮加分，不属门槛。
- **卷面终查**：`ci-it-diag` 七轮零新投递（tip 仍=run#17 的 `889e0942`）；电路+硬门禁在合并后照常运转（#35/#36 的 unit 门禁 exit 1 即旁证同类机制有效）。

## 3. 2.1 dispatch 豁免留痕（用户拍板，2026-10-02）

`workflow_dispatch`/`schedule` 仅对**默认分支注册的 workflow** 生效：本仓 main 无 `.github/workflows/`（HEAD=`315f54bf`，陈年暂存分支），GitHub UI 无 Run workflow 按钮，API 在册 build.yml 的 **dispatch total=0、schedule total=0**。强行使之可用需改默认分支结构或设受保护 release branch——均越本变更边界。等价证据：push（非 PR 事件）七连全 job 正常运转已覆盖"超集触发场景"；integration 无 `if:`，事件类型不改变其行为。**结构性发现另案登记：nightly cron 从未生效**（"37 18 * * *" 形同虚设，bench-routine 的 push 触发系歪打正着）——若团队需要真 nightly，须先动默认分支。

## 4. 回退预案状态

未演练（观察期零红）。实际形态注记：单命令 `revert e942a878` 会连带 unit 电路（1.2 勘记的归属偏差后果），仅退删段的手工反向补丁为有效兜底；design D3 已同步修正口径。

## 5. 边界遵守

unit job 段零触碰（其 services/电路/探针归 fix-ci-unit-flakes）；gradle 通道零触碰；未引入任何 GitHub `services:`；`ci-it-diag` 电路与硬门禁原样。
