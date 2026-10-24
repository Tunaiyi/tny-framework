# Design

## Context

动机见 proposal.md - Why。实施前事实核对（2026-10-03 现场 grep，`ModuleModes` 大小写敏感；经三视角核查复定，修正初稿对 `tny.module-setting.gradle` 形态的误记）：现行指称十行、共十二个出现点。类文件 `buildSrc/src/main/groovy/tny/convention/ModuleModes.groovy` 内部四行——第 9 行类声明、第 21 行构造方法、第 50 行 `enabled()` 诊断报错串前缀、第 52 行 `findByType(ModuleModes)`；消费方六行——`tny.module-setting.gradle` 第 3 行头注释"实现类 tny.convention.ModuleModes"、第 6 行 `import tny.convention.ModuleModes`、第 8 行 `extensions.create('moduleSetting', ModuleModes, project)`（经 import 后的简名类型参数），`tny.module-checker.gradle` 第 33 行注释与第 45 行调用（第 45 行含全限定调用与 `.Mode.UNPUBLISHED` 两个出现点），`tny.integration-test.gradle` 第 117 行调用（含 `.Mode.APP` 前缀在内两个出现点）。类头注释第 1 行与插件头注释中的 `consolidate-module-modes-plugin` 为历史变更名（连字符形态，实测与大小写敏感判据零重叠），按记录原则不动。主规格账本零钉名（前批变更 grep 已证）。

## Goals / Non-Goals

Goals：插件 id、扩展名、实现类三层标识统一 module-setting 词族；行为逐字节不变。Non-Goals：不改枚举 `Mode`、字段 `modes`、方法 `has(mode)` 与 `enableApp` / `enableUnpublished` 方法名；不改 rename-module-modes-plugin 案卷与 verification-notes 中"类名保留"的当时决策记录（历史记录，本变更是其后续裁决不是其否定）；不回改 HANDOFF 与归档。

## Decisions

**D1 单数 ModuleSetting（用户裁决）。** 与 `tny.module-setting`、`moduleSetting` 完全对齐；复数 ModuleSettings 被否——三层单复数不齐会造新歧义。
**D2 报错串前缀随类名改。** "ModuleModes.enabled:" 是对类型名的自指称，类改名后保留即诊断指向不存在的类型；仅换前缀词，消息结构、触发条件、其余文案一字不动。
**D3 枚举与字段保留原名的边界说清。** `Mode`/`modes` 语义是"角色种类集合"，与旧插件名 modes 词同形不同义；改名类里保留它们不是遗漏，本决定记录在案防后人"顺手清理"造成无谓改动面。

## Risks / Trade-offs

- 风险：类名漏改的暴露时机分三档，并非全部"编译即红"（buildSrc 无 @CompileStatic，纯动态 Groovy 编译，实测 Gradle 8.5 配 Groovy 3.0.17）。构造方法名漏改属 Groovy 语法错、buildSrc 编译即红；`import`、`findByType` 与两处全限定静态调用按动态解析在**运行到该行时**才报红——`tny.module-setting` 的引用在配置期（三工程应用即触发，`projects -q` 可探）、`tny.module-checker` 第 45 行在配置期对账闭包内（projects 可探）、`tny.integration-test` 第 117 行在 `integrationTest` 任务 doFirst 内（**projects -q 探不到，须 clean build 实跑或集成测试触达**）；报错串前缀与注释漏改对构建零影响，只能由 grep 判据兜底。缓解：三层探测器互补——编译器管语法档，`projects -q` 管配置期档，3.1 的 clean build 管执行期档，grep 清零管静默档。
- 风险：诊断报错串改动影响依赖该文案的自动化。缓解：全仓 grep "ModuleModes.enabled" 确认无消费方（CI 与测试均按异常存在与否判定，不匹配文案）。
- 权衡：本变更使 rename-module-modes-plugin 的 D1"类保留"决策成为两幕剧——账本以两桩独立变更记录完整呈现裁决演进，避免单变更反复改口径。

## Migration Plan

第一步，抓基线（剔噪 `tasks --all` 清单段、projects 绿记录）。第二步，git mv 移名类文件，类内四处与消费方六行随改（含 `tny.module-setting.gradle` 第 6 行 import 与第 8 行简名调用、第 45/117 行调用的类型前缀随行、`.Mode.*` 枚举名不动）。第三步，验收（grep 大小写敏感清零、清单零差异、clean build 绿兜住执行期引用档——CollectionLockTest 偶红按 fix-ci-unit-flakes 登记标准处置留痕），记 verification-notes.md。回滚为文件回名与十处还原。
