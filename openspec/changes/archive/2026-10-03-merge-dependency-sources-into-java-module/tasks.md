# Tasks

## 1. 前置确认与基线抓样

- [x] 1.1 前置确认：rename-subprojects-baseline-plugin 变更已实施归档（本变更工件按改名后文本形态书写指称，未归档即停）。抓基线：`./gradlew tasks --all --console=plain | sed '/^BUILD SUCCESSFUL in /d'` 的有序任务清单段（构建耗时尾行每次不同，比对范围限定清单段，核查修正）、`./gradlew :tny-game-net:downloadDependencySources --console=plain` 实跑输出（成功与缺件计数、产物文件列表）存本变更目录 baseline/；`./gradlew :tny-game-net:downloadDependencySources --dry-run` 任务图存 baseline/。

## 2. 合并实施

- [x] 2.1 tny.dependency-sources.gradle 的 downloadDependencySources 任务注册块逐行迁入 tny.java-module.gradle（置于 dependencies 段之后、idea 段之前；任务体、任务名、group、description 与全部内嵌注释一字不改）；接收文件头注释职责段补一句"依赖源码包批量下载任务"说明，兄弟插件清单行删去 tny.dependency-sources 一项（其余项与行内其余插件名指称不动）；tny.dependency-sources.gradle 移入 /tmp/gradle-retired-quarantine/；根 build.gradle 的 configure(javaProjects) 装配块删除 `apply plugin: 'tny.dependency-sources'` 一行。`./gradlew projects -q` 通过。

## 3. 零差异验收

- [x] 3.1 差异核对：重复 1.1 三份探针，`./gradlew tasks --all` 剥离耗时尾行后的任务清单段与基线逐行 diff 零差异；`:tny-game-net:downloadDependencySources` 实跑计数与产物列表与基线一致，`:tny-game-net:downloadDependencySources --dry-run` 任务图与基线零差异；`grep -rn "tny.dependency-sources" build.gradle settings.gradle buildSrc/src tny-*/build.gradle` 活代码零命中；`./gradlew clean build` 全绿。全部结论记 verification-notes.md（含 grep 命中清单原文）。
