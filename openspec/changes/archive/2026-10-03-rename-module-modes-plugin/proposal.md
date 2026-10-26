# Proposal

## Why

用户定名：`tny.module-modes` 改为 `tny.module-setting`（立项时首议缩写 tny.modes，经确认环节用户裁定现名）。"modes"（模式复数）语义含混——该插件承载的是模块对自身横切角色的**声明设置**（`enableApp` 与 `enableUnpublished` 两个方法）；"module-setting"说出"给模块做设置"的动作本体。改名同时把模块脚本里可见的扩展名 `moduleModes` 换为 `moduleSetting`，消除"插件叫 setting、调用块叫 modes"的半截身份；实现类 `tny.convention.ModuleModes` 保留（buildSrc 内部符号，模块作者不可见）。纯标识变更：角色声明语义、零发布合同判定逻辑、集成测试蓝本撮合全部不动。

## What Changes

- 文件 `tny.module-modes.gradle` 移名 `tny.module-setting.gradle`（插件 id 随文件名派生）；其内部 `extensions.create('moduleModes', …)` 改为 `moduleSetting`，头注释两处指称随改。
- 三个模块调用点随改：`tny-bench`、`tny-game-integration-test`、`tny-game-net-demo` 的 plugins 应用行改为 `id 'tny.module-setting'`，调用块 `moduleModes { … }` 改为 `moduleSetting { … }`。
- `settings.gradle` 第 50 行命名约定权威文本（"须应用 tny.module-modes 并声明 enableUnpublished()"）指称随改——需求九明文约定权威定义记录于此，机制改名而权威文本不改即失真。
- 消费方指称随改五处：`tny.project-checks.gradle` 头注释与零发布合同报错文案中的插件名、`tny.integration-test.gradle` 头注释、`tny/convention/ModuleModes.groovy` 类头两处（类名本身与报错串 "ModuleModes.enabled:" 保留不动）。
- 历史指称不回改：归档变更名 `consolidate-module-modes-plugin`、各模块注释中的变更出处引用、归档与 HANDOFF 记录面。

## Capabilities

### New Capabilities / Modified Capabilities

无。主规格账本 grep 实测 `module-modes`、`moduleModes`、`ModuleModes` 零钉名——`gradle-build-style` 需求九以"统一标记插件的扩展方法"类别语描述该机制，`benchmark-harness` 与 `release-versioning` 同理；改名不触碰任何条文。skip_specs。

## Impact

- **受影响文件**：改名主体 1 个、三个模块构建文件、`settings.gradle` 权威注释行、`tny.project-checks.gradle` 两处、`tny.integration-test.gradle` 一处、`tny/convention/ModuleModes.groovy` 类头两处。
- **顺序关系**：`tny.project-checks.gradle` 同时是 `rename-project-checks-plugin` 的改名主体——两变更在此文件交叉（本变更改其第 4 行头注释与第 46 行报错文案指称，彼变更移其文件名），先后落地均可、无强约束，后落地一方按实施时 grep 现场为准（同 merge-dependency-sources-into-java-module D2 手法）。与在途 `rename-bench-to-benchmark` 共编 `tny-bench/build.gradle`（本变更改其第 7 行插件 id 与第 11 行调用块名，彼变更移整个模块目录并改族正则四条——不同行、无内容纠缠，两序皆可实施，后落地一方同样按现场为准）。与其余已归档变更无文件交叉。
- **验收基线**：`tasks --all` 清单段（剔除进度行、汇总行与耗时尾行）对实施前自抓基线逐行零差异（插件 id 与扩展名不出现在任务名，预期零差异）；`./gradlew projects -q` 通过；`./gradlew clean build` 全绿；grep 现行面 `tny\.module-modes` 与调用形态 `moduleModes\s*\{` 零命中（类名 `ModuleModes` 按 D1 保留，不在清零面）。历史归档与记录面不回改。
