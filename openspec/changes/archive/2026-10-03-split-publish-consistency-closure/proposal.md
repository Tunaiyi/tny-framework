# Proposal

## Why

`buildSrc/src/main/groovy/tny.publish.gradle` 第 51 至 169 行的 `validatePublishConsistency` 闭包实测 119 行，是可读性治理运动两轴（方法长度、重复代码）全量实测中唯一的程序性超长块：其余长块经逐块查看均为声明性数据段（`tny.git.gradle` 的 ext 常量段、`tny.publications.gradle` 的 publishing 元数据段）或 gradle-build-style 需求一明文指定的任务动作容身处（release 两任务块、`integrationTest`、`jmhSuiteVerify`），均不在治理对象内。该闭包把四类发布校验——分支形态与版本形态核对、仓库路由与快照形态核对、历史后缀同号黑名单、发布标签远端存证核对（内嵌 git 命令包装、远端名推定与 ls-remote 解析）——挤在同一个闭包体内，失败消息把四类来源合流为一条抛出，评审时需要在 119 行里同时装载四类规则。修法按职责切分：同文件内拆为多个辅助闭包，各段来由注释随段迁移，主闭包收敛为收集与合流抛出。行为零变化。

## What Changes

- `tny.publish.gradle` 的 `validatePublishConsistency` 闭包拆为四个同文件辅助闭包（每个控制在四十行以内）：`validateBranchVersionShape`（开发线/发布分支/名单外三分支的版本形态核对）、`validateRepositoryRouting`（快照与正式版本对仓库形态的核对）、`validateLegacyVersionReuse`（历史 -RELEASE 后缀同号黑名单）、`validateReleaseTagAttested`（发布标签远端存证核对；`runGit` 与远端名推定作为该段内部再拆的两个小闭包，根工程记忆化的键结构与缓存语义不动）。
- 主闭包收敛为编排形态：按原顺序调用四段、problems 合流与最终抛出文案逐字保留。
- 文件头双层拦截职责注释补一句门禁内部结构说明；各段原有来由注释随段迁移，零删减。

## Capabilities

### New Capabilities / Modified Capabilities

无。gradle-build-style 条文零改动——不新设块级长度需求条目：本次是单例实测发现，按册修正形态即可；若后续实测再现程序性超长块，届时另立条目讨论通则。skip_specs。

## Impact

- **受影响文件**：仅 `buildSrc/src/main/groovy/tny.publish.gradle`（现 187 行；拆分行与新增注释计入后仍远低于 250 行文件界线）。
- **行为红线**：所有失败消息文案与 problems 收集顺序逐字不变；`publishesToSharedRepository` 谓词、属性 fail-fast 块、门禁挂接零触碰；记忆化键（分支|提交|标签）与"远端不可达按标签缺失拒绝"的 fail-closed 语义零触碰。
- **与活跃册关系**：与 `move-it-fixture-loop-into-plugin`、`move-bench-suite-selection-into-plugin`、`rename-bench-suite-class` 及其余活跃册零文件交集，可并行推进。
- **验收基线与探针**（探针配方沿用归档册 merge-publish-gate-into-publish 的留痕）：java 线代表与 BOM 的 `checkPublishPrerequisites` 实断言绿、`publish -m` 干跑任务图、空凭据双向探针的 fail-fast 消息为常规三件；新增错登形态探针三件——开发线以 `-Pversion` 覆写裸号触发版本形态红、临时发布分支（不推送、探针后即删）触发标签存证红与黑名单红。每件探针 before/after 全文逐字一致；`tasks --all` 剔噪清单同 daemon 口径零差异（前批确立的编码口径教训沿用）；`projects -q` 与 `clean build` 全绿。捕获口径：JDK 钉 Corretto 21、UTF-8 locale、不注入 JAVA_TOOL_OPTIONS、before 与 after 同一 daemon 环境。
