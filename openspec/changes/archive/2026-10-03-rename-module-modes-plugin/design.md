# Design

## Context

动机见 proposal.md - Why。实施前事实核对（2026-10-03 现场 grep，模式 `tny\.module-modes` 与 `moduleModes`，排除归档与记录面）：改名对象合计十四处。插件 id 字面 `tny.module-modes` 九处：三个模块的 plugins 应用行（`tny-bench/build.gradle:7`、`tny-game-integration-test/build.gradle:11`、`tny-game-net-demo/build.gradle:3`）、`settings.gradle:50` 命名约定权威注释、`tny.project-checks.gradle:4` 头注释与 `:46` 零发布合同报错文案、`tny.integration-test.gradle:101` 头注释、`tny/convention/ModuleModes.groovy:1` 与 `:47` 类头注释。扩展名字面 `moduleModes` 五处：主体文件内部两处（头注释"经 moduleModes 扩展方法声明角色"与 `extensions.create('moduleModes', …)`）、三个模块各一处 `moduleModes { … }` 调用块。主规格 `gradle-build-style` 需求九以"统一标记插件的扩展方法"类别语描述该机制，全文不钉名。

## Goals / Non-Goals

Goals：模块脚本读作"应用 tny.module-setting、声明 `moduleSetting { enableXxx() }`"，插件 id、扩展名、全部现行指称一层改齐。Non-Goals：不改实现类 `tny.convention.ModuleModes` 与其报错文案"ModuleModes.enabled:"（类符号属 buildSrc 内部，消费方 import 面为 project-checks 与 integration-test 两处代码，模块作者不可见）；不改角色语义、判定逻辑与合同行为；不改历史变更名 `consolidate-module-modes-plugin` 的一切指称。

## Decisions

**D1 范围定为"插件 id 加扩展名"，实现类保留（用户在立项问答中裁决命名层级后的对应选择）。** 备选"只改 id"否决——调用块 moduleModes 与插件新名脱节，正是"名字必须说出主体内容"原则要消除的半截身份；备选"全套含类名"否决——类名不出现在任何模块脚本，改动只扩大 import 与报错文案面而无读者收益。扩展名取 `moduleSetting`（单数，与插件名同词根），实现类继续叫 ModuleModes 并在类头注释说明二者关系。
**D2 `settings.gradle:50` 权威文本随改。** 需求九明文"约定的权威定义记录在 settings.gradle 的模块清单注释中"——该处写着"须应用 tny.module-modes"，机制改名而权威文本不改即账本自相矛盾。
**D3 报错文案随改名换指称、不加探针触发。** `tny.project-checks.gradle:46` 的"声明处：tny.module-modes enableUnpublished"是用户可见文案的指称更新，判定逻辑与文案结构不动。不构造违规触发探针的来由：两个声明 enableUnpublished 的工程（tny-bench、tny-game-integration-test）都位于依赖图的叶子端，人为添加反向依赖边必然引入循环使评估先于合同报红，探针构造不出干净的violation——指称一致性由 grep 清零静态证明兜底。
**D4 验收口径沿用改名类先例**：自抓 `tasks --all` 基线（剔进度行、汇总行、耗时尾行）零差异——插件 id 与扩展名不出现在任务名；`projects -q`、`clean build`、grep 双模式清零。

## Risks / Trade-offs

- 风险：扩展名改漏任一调用点，评估当场报"扩展 moduleModes 不存在"。缓解：这是常驻探测器而非静默漂移——改名批次内 `./gradlew projects -q` 立即暴露。
- 风险：本变更与 `rename-project-checks-plugin` 共编 `tny.project-checks.gradle`、与 `rename-bench-to-benchmark` 共编 `tny-bench/build.gradle`，先后组合下工件书写的行号与文本会漂移。缓解：实施第一步现场 grep 重定界，两处交叉行只换自己名下指称。
- 风险：类名 ModuleModes 与新扩展名 moduleSetting 并存，读者可能疑惑。缓解：类头注释第一行写明"应用 tny.module-setting 的工程经本类实例（扩展名 moduleSetting）声明角色"，见名疑义有注释兜底。

## Migration Plan

第一步，现场 grep 定界全部指称存 baseline/。第二步，git mv 主体文件，改主体内部两处、三个模块应用的 id 行与调用块、settings 权威行、五处消费方注释与文案指称。第三步，验收（评估、零差异、双模式 grep 清零、全量构建输出为绿），结论记 verification-notes.md。回滚为文件名与全部指称行还原。
