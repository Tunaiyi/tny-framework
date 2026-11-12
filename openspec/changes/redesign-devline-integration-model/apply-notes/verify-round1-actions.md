# verify 第一轮处置记录（2026-10-06）

对核验报告四项 WARNING 与两项 SUGGESTION 的逐条处置：

| 核验发现 | 处置 | 证据位置 |
|---|---|---|
| W1 main 无平台强制（只追加不改写无外部执法） | 已配置：main 经典分支保护（禁 force push、禁删除，管理员可绕过）；`refs/heads/dev/**` ruleset 禁强推（id 24532851）；`refs/heads/release/**` ruleset 禁强推、禁删除、禁非快进，管理员旁路保留为退役出口（id 24532831） | gh api 回读三者在位 |
| W2 退役检查无生产实现 | tny.integrate 新增 `retireGuard` 任务：枚举远端 main..维护分支的未集成提交并拒绝，同时要求线谱系登记表已有该线行（状态处置成文）才放行 | buildSrc/src/main/groovy/tny.integrate.gradle 尾部任务 |
| W3 维护分支拒特性无机器守卫 | build.yml 新增 `release-line-patch-only` 作业：目标为 release/** 的 PR 含 feat 首行提交即拒绝 | .github/workflows/build.yml |
| W4 例外"随下一版发出"执法表述虚标 | 规格措辞改为诚实执法点：releaseTag 输出逐条列出本次发版携带的 propagated-as 提交（可见可审计，不虚标"强制拦截"）；实现随附首发无标签边界防护 | specs/branch-integration-gates/spec.md 例外需求段 + tny.release.gradle |
| S1 同号双防线短路 | 不改产品代码（下一补丁号校验天然先于远端标签存在检查短路，双层都是拒绝，行为正确）；记录于此防止未来误判为缺陷 | 本文件 |
| S2 动画"祖父线"措辞 | 已统一为"祖父登记线" | docs/branch-flow-animation.html |

回归：端到端演练第十三轮 18/18 全绿（含新守卫未破坏任何既有路径）；`:tasks --group release` 五任务在位编译通过；`openspec validate` 通过。

遗留（不变）：5.4 与 6.4 等自然运行窗口（今晚镜像定时、下一个真实 PR 顺带确认两个新 CI 作业）；8.1/8.2 等真实发布周期；6 项任务勾齐后 verify→archive。
