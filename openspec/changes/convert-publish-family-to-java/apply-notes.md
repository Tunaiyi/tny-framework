## 任务 1.2 核对依据留档（由 convert-orchestration-to-java change 的任务 8.2 代记，2026-10-07）

本 change（convert-publish-family-to-java）任务 1.2 的前置已满足，且可机械复核：
convert-orchestration-to-java 已归档为 openspec/changes/archive/2026-10-07-convert-orchestration-to-java/；
buildSrc/src/main/groovy/tny/convention/ 目录下现无 GitFlow 与 GitCli 两个 Groovy 类（二者
现为 tny.convention 包的 Java 类，另含按 gradle-build-style 规格单类 250 行界线从 GitFlow
拆出的两个支撑类 GitRemoteQueries 与 GitLocalRefs）；正则选点判例表的可读位置为该归档目录
的 apply-notes.md 中"正则选点判例表（组 1 起草，组 7 勾验）"一节，发布族移交行在该节末尾，
原文分段计数为 tny.publish.gate.gradle 约七处、tny.central.gradle 两处、tny.publications.gradle
与 tny.publish.gradle 与 tny.github-packages.gradle 合计三处。本 change 执行任务 1.5 时以
归档原文现场转录为准。本条仅留档依据，不代打 1.2 的复选框——本 change 开工执行任务 1.2 时
按其自身判据现场核对后勾选。
