# Proposal

## Why

git 派生事实（分支名、按分支形态推导的项目版本、提交号、时间戳、git 句柄与解析闭包）现在经**根工程 ext 字典**在插件间传递：tny.git 写入约十九个键，tny.release 以 `def git = rootProject.ext` 整包消费，tny.publications、tny.dependency-management（改名后继任）、tny.central 各自裸取 branchName/projectVersion 等键。这是隐式跨插件契约：键名拼错要到执行才炸、"tny.git 必须先于消费者加载"的时序约定只活在注释里、新人读 tny.release 看不出 git.branchName 从哪来。用户定调方向：不并文件（tny.git 105 行 + tny.release 199 行并文件超约定插件 250 行界线），改为**类型化扩展统一调用**。

## What Changes

- tny.git 改为提供根工程扩展 `gitInfo`（实现类 tny.convention.GitInfo，Groovy 动态字段——buildSrc 编译期不依赖 grgit 类，运行时从根工程类路径取，与既有约定插件互不 apply 的形态约定一致）：属性 branchName/branchVersion/projectVersion/commitId/commitTime/buildTime/句柄 grgiter/常量三个/方法 parseBranchVersion、parseProjectVersion、gitTag、gitBranchType 等，与现 ext 键一一对应、推导逻辑逐行迁移不改。
- 四处消费改经扩展调用：tny.release（`git.xxx` → `gitInfo.xxx`，含 `rootProject.extensions.getByType(tny.convention.GitInfo)` 获取句柄）、tny.publications（branchName）、tny.dependency-management（projectVersion 一行）、tny.central（分支守卫的 branchName）。
- tny.git 的根 ext 派生字典整体删除（组号事实 projectGroup/pluginLegacyGroup 与装配线集合 moduleProjects/javaProjects/gradleProjects 属根身份与成员面，保留在根 ext 不动；发布门禁的 publishTagCheckMemo 缓存键与本契约无关，保留）。
- 行为零差异红线：项目版本推导字符串逐字节不变（发布物文件名与 POM version 字段负责证明）、release 两任务 -PdryRun 输出文案不变、centralCheck 与组号对账探针不变、全量构建与任务清单零差异。
- 时序收益：消费者经类型获取扩展，tny.git 未先加载时报"扩展不存在"的明确错误，替代"裸属性取空值再在深处炸"的隐性失败。

## Capabilities

### New Capabilities / Modified Capabilities

无。跨插件协作形态从 ext 字典改为类型化扩展属实现内部，无外部可见行为变化；skip_specs。

## Impact

- **受影响文件**：tny.git.gradle（重写导出面）、新增 tny/convention/GitInfo.groovy、tny.release.gradle（约七处引用点）、tny.publications.gradle（1 处）、tny.dependency-management.gradle（1 处，以改名后文件为准——本变更在 rename-subprojects-baseline-plugin 之后实施）、tny.central.gradle（1 处）。
- **消费面**：CI（读 ext 的没有——发布任务与门禁都走插件内部）、文档、下游——均无感知。
- **依赖关系**：排在 rename-subprojects-baseline-plugin 之后（其文件名为本变更的编辑对象）。
