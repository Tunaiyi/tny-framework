# Proposal

## Why

rename-module-modes-plugin 把插件改为 `tny.module-setting`、扩展改为 `moduleSetting`，实现类 `tny.convention.ModuleModes` 当时按"模块作者不可见"保留——类名残留旧词族 modes，与插件、扩展两翼不齐。用户裁定收口最后一层：类改名 `ModuleSetting`（单数，与插件 id 和扩展名同词根），三层标识统一为 module-setting 词族。类内枚举 `Mode` 与字段 `modes` 保留——它们说的是"角色种类"本义，准确。纯符号改名：声明语义、零发布合同、蓝本撮合全部不动。

## What Changes

- `buildSrc/src/main/groovy/tny/convention/ModuleModes.groovy` 移名为 `ModuleSetting.groovy`；类声明、构造方法、`findByType(ModuleModes)` 三处符号随改；`enabled()` 诊断报错串前缀 `"ModuleModes.enabled:"` 同步改为 `"ModuleSetting.enabled:"`（前缀指称类型名，类改名后不改即指向不存在的类型）。
- 消费方类型引用随改六行：`tny.module-setting.gradle` 第 3 行头注释"实现类"指称、第 6 行 `import tny.convention.ModuleModes`、第 8 行 `extensions.create('moduleSetting', ModuleModes, project)` 的简名类型参数；`tny.module-checker.gradle` 第 33 行注释指称与第 45 行 `tny.convention.ModuleModes.enabled(…)` 调用（含 `.Mode.UNPUBLISHED` 的类型前缀随改、枚举名 `Mode` 不动）；`tny.integration-test.gradle` 第 117 行调用（含 `.Mode.APP` 前缀随类型名）。
- 历史指称不回改：归档变更名 `consolidate-module-modes-plugin`（连字符形态，与 `ModuleModes` 字面判据不冲突）、rename-module-modes-plugin 案卷与 verification-notes 中的当时决策记录、HANDOFF 记录面。

## Capabilities

### New Capabilities / Modified Capabilities

无。类符号是 buildSrc 内部实现，主规格全账本不钉名；报错前缀换词不改判定。skip_specs。

## Impact

- **受影响文件**：类文件本体（移名 + 四处内部改动）、`tny.module-setting.gradle` 三处、`tny.module-checker.gradle` 两处、`tny.integration-test.gradle` 一处，共十处现行指称、十二个出现点（2026-10-03 现场 grep `ModuleModes` 大小写敏感定界，经三视角核查复定并修正初稿"九处"与 import/简名形态的误记）。
- **顺序关系**：无前置——本变更直接在 rename-module-modes-plugin 落地后的现名现场操作；与在途 consolidate-compile-baseline-blocks（不触碰这三个文件）、rename-bench-to-benchmark（不触碰 buildSrc/convention）均无共同文件。
- **验收基线**：类名漏改的暴露分三档（buildSrc 为纯动态 Groovy 编译，无 @CompileStatic）——构造方法名漏改编译即红；import、findByType 与全限定调用等取值引用在运行到该行时报红（配置期引用 `projects -q` 可探，`tny.integration-test` 第 117 行属 `integrationTest` doFirst 执行期、须 clean build 兜底）；报错串前缀与注释漏改对构建无影响、仅 grep 判据可兜。`tasks --all` 剔噪清单段对自抓基线逐行零差异（符号名不入任务面）；grep `ModuleModes`（区分大小写）现行面零命中；`clean build` 全绿——偶红按登记标准处置留痕。
