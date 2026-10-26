# 任务组 5/6/7 验证记录（2026-10-06）

## 端到端演练（drill/model4-sim/e2e-gradle.sh，第十二轮）

十二轮迭代后 18/18 断言全绿（含反事实对照），镜像隔离裸仓库运行，真实 gradle 任务全链路：
集成顺序检查拦截、integrateMain（同步→合并→删线）、releaseCut 唯一性、releaseTag 首发与
下一补丁号（含跳号拒、同号拒）、补丁滚动、mergeUpward 成功与 modify/delete 冲突自动回滚、
无门禁合并"历史绿而树丢标记"复现、祖父线 dryRun 判入旧流程。

演练暴露并修复的三个产品缺陷（均有回归断言）：
1. `num` 闭包误用 Matcher.matches() 全串语义，dev/ 前缀名恒 -1，顺序检查与目标枚举静默失效。
2. GitCli.remoteTagPatches 的 slashy 正则含 `${}` 插值永不匹配（Groovy slashy 不做插值），
   下一补丁号枚举恒空。
3. 完整性检查标记检索未排除叙述性路径：演练脚本自含 BUG 编号令检索恒绿（真实仓库的 docs
   同样会自我蒙蔽）；mergeUpward 与门禁统一排除 docs、drill、openspec。
4. 门禁 validateNextPatchNumber 与 shape 校验把 `5.8.x` 当系列名（未去 `.x`），标签枚举与
   期望号双错；矩阵 G3 实锤后统一为 `base.K` 表述。

## 门禁矩阵（4.4，沙箱第十二轮仓库实测）

- main 分支：拒绝，文案列出四种合法形态 ✓
- release/5.8.x 正号 5.8.3：仅剩"发布标签不存在"单条（下一补丁号校验通过）✓
- release/5.8.x 错号：期望号报错并给出应为值 ✓
- 祖父线 5.7.x 快照：全项通过（登记表双层判定生效）✓

## 平台配置核对（5.3，gh api 只读实测 2026-10-06）

- 合并按钮三态：allow_merge_commit=false、allow_squash_merge=false、allow_rebase_merge=true
  ——与"仅 rebase-merge"纪律一致 ✓
- 默认分支 main ✓；**main 无分支保护规则**（"Branch not protected"）：强制推送与删除
  当前只有 buildSrc 守卫（本地侧）一层拦截，平台侧不设防。
- 建议（用户平台操作，不阻塞本变更）：对 main、`dev/*`、`release/*` 加保护——禁 force push
  与分支删除、要求 PR 必经 check；`release/*` 允许维护者直推合并提交（向上合并与补丁
  rebase-merge 走 PR 亦可）。

## 遗留与如实登记

- 3.4 的"补丁影响清单驱动应有目标核对"在任务层落为 `-Pinto` 显式加默认全量更高线，
  清单核对由 PR 模板（6.2）与流程条文承载——git 任务读不到 PR，这一层没有机器强制。
- 6.4 的 CI 侧 commit-lint job 已写入 build.yml，但其运行需一次真实 PR 触发；本地四类
  样本（带编号修复/无编号修复/特性/例外尾注）经 .githooks/commit-msg 实测四态正确。
- 5.1 的 Actions 通配语义：官方 filter-pattern 规则 + 文档示例支持"`*` 不跨斜杠"结论，
  build.yml 已按 `dev/**` 书写；活体探测（probe 分支）随推送执行，结论回填本文件。
- 5.4 的"匹配集合非空断言"：snapshot-mirror 已有空矩阵干净跳过加告警（现成实现）；
  build.yml 侧断言随探测轮一并核验。
- 8.1/8.2 的正式真实周期依赖下一次真实发布窗口，沙箱预演已完成（本文件上节）。
