# Tasks

## 1. 基线抓样

- [x] 1.1 抓基线存本变更目录 baseline/：`./gradlew tasks --all --console=plain | grep -v -e '^> Task :' -e 'actionable tasks:' -e '^BUILD SUCCESSFUL in '` 的有序任务清单段；`./gradlew projects -q --console=plain` 评估输出记录为绿。

## 2. 改名实施

- [x] 2.1 `tny.project-checks.gradle` 以 git mv 移名 `tny.module-checker.gradle`（插件 id 由文件名派生随之更换）；根 `build.gradle` 第 42 行应用行改 `apply plugin: 'tny.module-checker'`（第 41 行引入注释不含插件 id，不随改）；文件头按 D2 补一行命名来由（"工程标识名 tny.module-checker 指向本文件内容重心——构件模块两条硬合同的配置期对账"），既有职责、边界句与历史路径指称"前身为 gradle/project-checks.gradle"不动；本文件第 4、46 行对 tny.module-modes 的指称按 D3 不触碰（实施时若该两处已因 rename-module-modes-plugin 落地而更新，按现场保留其成果）。验证：`./gradlew projects -q` 通过。

## 3. 零差异验收

- [x] 3.1 零差异核对：`tasks --all` 剔噪清单段对 1.1 基线逐行零差异（该插件无任务注册，预期构造性零差异，出现任何差异即停回用户处）；`grep -rn "tny\.project-checks" build.gradle settings.gradle gradle buildSrc/src tny-*/build.gradle docs .github openspec/config.yaml` 活代码零命中（历史路径"gradle/project-checks.gradle"字样按 D2 保留、不属该 grep 形态）；`./gradlew clean build` 全绿。全部结论记 verification-notes.md。
