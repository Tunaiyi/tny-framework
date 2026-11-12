# Proposal

## Why

tny.publish（凭据与仓地址属性、共享仓发布任务的属性存在性断言）与 tny.publish-gate（分支形态/版本一致/仓库路由/同号黑名单/标签存证断言与 checkPublishPrerequisites 任务）本是同一主题——"错误发布拦截"——分居两文件，且"哪些任务算共享仓发布"的判定谓词在两处逐字重复（D7 时代以"两处注释互指同源"维持一致性，正是主账本"共享配置块禁止逐字复制"点名的形态）。合并顺带修复一个既有漏网：插件模块线（tny-game-doc-gradle）历史上只应用了 tny.publish、未应用门禁，而 release-versioning 的发布资格需求明文"其余任何分支执行发布任务 MUST 被拒绝"，主体不限 java 制品——doc-gradle 的发布任务本就应受门禁约束。

## What Changes

- tny.publish-gate 的谓词、校验闭包、checkPublishPrerequisites 任务与挂接整体迁入 tny.publish；迁入时两处的"共享仓发布任务"判定合并为**单一局部闭包**，属性存在性断言与门禁挂接共用之，逐字重复根除；合并后约 176 行，在约定插件 250 行界线内；tny.publish-gate.gradle 退役（隔离目录法）。
- 引用面收编：根脚本 javaProjects 装配线行与 tny-game-bom 的 plugins{} 删除 tny.publish-gate 行（tny.publish 已在位）；tny.publications 头注释的两处指称改指 tny.publish。
- 预期内的行为收紧（用户已批准，且性质是向既有规格对齐）：tny-game-doc-gradle 获得 checkPublishPrerequisites 任务与其对全部共享仓发布任务的挂接；其 tasks --all 增加相应行，非白名单分支上其发布任务从"可执行"变为"被拒绝"。其余两线（java 发布线、BOM）门禁行为逐字节不变。
- 根工程缓存键 publishTagCheckMemo（远端标签核对记忆化）原样保留（性质是缓存非契约）。

## Capabilities

### New Capabilities / Modified Capabilities

无。门禁覆盖插件模块线是 release-versioning 既有条文（发布任务白名单拒绝）的落实而非新行为；文件合并与谓词去重属 gradle-build-style 既有纪律（禁止逐字复制）的履行。skip_specs。

## Impact

- **受影响文件**：tny.publish.gradle（吸收门禁段）、tny.publish-gate.gradle（退役）、build.gradle（装配线删一行）、tny-game-bom/build.gradle（plugins 删一行）、tny.publications.gradle（注释 2 处）。
- **验收基线**：tasks --all 允许且仅允许 doc-gradle 门禁相关行新增（逐行核对差异清单）；java 线与 BOM 的 checkPublishPrerequisites 实断言、-m 干跑挂接、空凭据双向探针、dev 线三工程实跑全绿文案不变；m2 与两份 POM 零差异；全量构建绿。
