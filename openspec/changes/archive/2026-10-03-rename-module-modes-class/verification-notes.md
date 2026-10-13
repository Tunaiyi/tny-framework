# 验证记录

## rename-module-modes-class（2026-10-03）

- 环境：JDK 钉 Corretto 21.0.12.1，正常 UTF-8 locale（前批确立的捕获口径，本批首跑即绿无偶红）。
- 1.1 基线：剔噪任务清单 3457 行与 projects 绿记录存 baseline/。
- 2.1 改名实施：类文件 git mv 移名 `convention/ModuleSetting.groovy`，类内以全局 ModuleModes→ModuleSetting 一次改齐四处（类声明、构造方法、第 50 行报错串前缀、第 52 行 findByType，D2 前缀随类型名、消息其余不动；枚举行 `enum Mode`、字段 `modes` 与全部方法名不在替换形态内、逐字保留，D3）；消费方六行随改——`tny.module-setting.gradle` 第 3 行注释、第 6 行 import、第 8 行简名 extensions.create 类型参数，`tny.module-checker.gradle` 第 33 行注释与第 45 行调用（`.Mode.UNPUBLISHED` 随类型前缀、枚举名不动），`tny.integration-test.gradle` 第 117 行调用（`.Mode.APP` 同）。历史变更名 `consolidate-module-modes-plugin`（连字符形态）实测与替换式零重叠、原样保留。`projects -q` 通过。
- 3.1 零差异验收：grep `ModuleModes` 于根脚本、settings、gradle/、buildSrc/src、各模块构建文件、docs、.github、openspec/config.yaml 现行面零命中；`grep "ModuleModes\.enabled"` 于 .github 与模块构建文件零命中，先证报错文案无自动化消费方（D2 风险条兑现）；`tasks --all` 剔噪清单段对基线逐行零差异（3457=3457，符号名不入任务面，构造性零差异如期）；`./gradlew clean build` **一次全绿**（347 任务 339 执行，无偶红）——其中 `tny.integration-test` 第 117 行的执行期引用（`integrationTest` doFirst 内，projects 探不到的那一档）经集成测试实跑覆盖，三档探测器（编译/配置期/执行期）按 design 修正后的表述各得其所。
- 结论：module-setting 词族三层（插件 id、扩展名、实现类）自此齐整，行为零变化，案卷闭环。

- 归档后复验：全仓大小写敏感 `ModuleModes` 零命中复核、`ModuleSetting.groovy` 类内四处（声明/构造/报错前缀/findByType）在位、`jmhList` 枚举与清单比对与前册一致、探针链完整。修复回归后 `projects -q` 与 `clean build` 均绿。
