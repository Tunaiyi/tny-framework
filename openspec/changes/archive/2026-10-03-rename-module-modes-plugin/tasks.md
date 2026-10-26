# Tasks

## 1. 前置确认与基线抓样

- [x] 1.1 无强前置确认：本变更与 rename-project-checks-plugin 互不阻塞（交叉处理按各自工件的现场 grep 预案），与 rename-bench-to-benchmark 共编 tny-bench/build.gradle 但不同行。抓基线存本变更目录 baseline/：`./gradlew tasks --all --console=plain | grep -v -e '^> Task :' -e 'actionable tasks:' -e '^BUILD SUCCESSFUL in '` 的有序任务清单段；`./gradlew projects -q --console=plain` 评估输出记录为绿。

## 2. 改名实施

- [x] 2.1 主体与调用点：`tny.module-modes.gradle` 以 git mv 移名 `tny.module-setting.gradle`；主体内部两处随改——头注释"经 moduleModes 扩展方法声明角色"与 `extensions.create('moduleModes', …)` 均改为 `moduleSetting`（实现类 `tny.convention.ModuleModes` 与报错串 "ModuleModes.enabled:" 不动，D1）。三个模块应用行 `id 'tny.module-modes'` 改 `id 'tny.module-setting'`、调用块 `moduleModes { … }` 改 `moduleSetting { … }`（`tny-bench`、`tny-game-integration-test`、`tny-game-net-demo`）。指称随改六处：`settings.gradle:50` 命名约定权威文本（D2）、`tny.project-checks.gradle` 第 4 行头注释与第 46 行报错文案的插件名（D3，文案仅换指称；若彼时该文件已因 rename-project-checks-plugin 移名 tny.module-checker.gradle，按现场文件名操作）、`tny.integration-test.gradle:101` 头注释、`tny/convention/ModuleModes.groovy` 第 1 与 47 行类头注释的插件名。历史变更名 `consolidate-module-modes-plugin` 一切指称不动。验证：`./gradlew projects -q` 通过。

## 3. 零差异验收

- [x] 3.1 防线探针与零差异：探针一（扩展名是活机制的证明）——改名落地后把 `tny-game-net-demo/build.gradle` 调用块临时改回 `moduleModes`，`./gradlew projects -q` 必须报旧扩展名未定义的求值错红（Groovy 动态属性形态，判据为报红本身、不钉死文案措辞），恢复后复验为绿；`tasks --all` 剔噪清单段对 1.1 基线逐行零差异；`grep -rn "tny\.module-modes" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle` 零命中，`grep -rn "moduleModes" build.gradle settings.gradle buildSrc/src/main/groovy tny-*/build.gradle` 零命中（区分大小写，类名 `ModuleModes` 按 D1 保留不在此列）；`./gradlew clean build` 全绿。全部结论记 verification-notes.md（含探针红字原文）。
