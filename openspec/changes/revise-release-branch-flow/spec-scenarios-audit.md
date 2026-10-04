# 5.2 规格差量对账表（任务 5.2）

对 `openspec/changes/revise-release-branch-flow/specs/release-versioning/spec.md` 新增需求
"发布容器与标签一经创建不可移动"的四个场景逐项登记证据：

| 场景 | 证据位置 | 结论 |
|---|---|---|
| hotfix 合回之后容器与标签原封不动 | 修复轮演练 v2 的 2.2 段（`drill/run-v2-output.txt`：PENDING-2-OK、CONTAINER-IMMUTABLE-OK、TAG-IMMUTABLE-OK、CHERRY-DONE、PROVENANCE-OK；双修复笔经 `git cherry` pending 集重放） | 已验证 |
| 常规发布切支后无新提交时合回不产生任何移动 | 演练 v2 的 2.1 段（SKIP-OK 与 TRIPLE-OK）与 2.3b 段（PENDING-EMPTY-OK：已移植提交重跑报"全部存在于线，跳过"） | 已验证 |
| 重放合回遇到冲突时全体保持原状 | 演练 v2 的 2.3 段（核心用例：第一笔成功、第二笔冲突——CONFLICT-DETECTED 后 abort、`reset --hard` 清回原线头含成功的第一笔、切回容器，RESET-OK 断言三方原状；首轮缺口"只撤销当前一笔"已修复）；人工等价命令文案在 `tny.release.gradle` 的 releaseMergeBack 冲突异常信息中 | 已验证 |
| 事后重新发布同一容器时存证核对仍然成立 | 门禁侧输入恒等性由前三行保证（容器头永不离开标签解引用指向的提交，`checkPublishPrerequisites` 第 5 重校验的比对结果与首发相同）；端到端登记为"首个真实发布（5.7.9 起）时跟随核对"，随发布收口补记 | 演练级验证＋首发布跟随核对 |

既有五项需求逐项复核（不受本变更影响的理由）：

1. 正式版版本串为裸三段号——版本派生插件 `tny.git.gradle` 与 `GitFlow` 类零改动，容器名
   形态与派生规则原样。
2. 快照坐标形态跨发布期保持稳定——线形态与派生不变；内容口径收窄属本变更契约声明
   （坐标字符串与形态判定不受影响，需求原文只约束形态）。
3. 发布资格由分支形态白名单决定——门禁插件 `tny.publish.gradle` 零改动；main 不发布由
   白名单天然覆盖，未新增判定。
4. 历史后缀形态占用的版本号不得复用——`gradle/released-legacy.txt` 读取与判定保留在
   `releaseCut` 与门禁两处，文案不变。
5. 发布标签是正式版发布的前置存证——第 5 重校验原样（CLI ls-remote 定案不变）；标签
   时点后移到 releaseTag 不改变"先于制品、指向构建提交"的判定语义，忘打标签仍被同一条
   拒绝文案拦住。
