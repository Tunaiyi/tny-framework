# 验证记录

## 基线（任务 1.1）

- 首次 `./gradlew clean build` 在 `:tny-game-actor:test` 出现一处失败：`drama.task.VoidTypeStageTest.testAwaitRun1` 断言超时（AssertionError，VoidTypeStageTest.java:304）。
- 单任务复跑两次 `:tny-game-actor:test --rerun` 全部通过（9 个用例零失败）。判定为并行全量构建下的既有间歇性抖动，与构建脚本形态无关；在途变更 fix-ci-unit-flakes（已完成 9/14）正是跟踪此类偶红。
- 处置：按"新红先取 ci-it-diag 案卷"纪律记录证据（本文件），基线重跑一次取绿作为对照快照；若清扫过程中该用例反复变红且形态变化，将暂停清扫回到用户处。
- 环境备注：本机存在六个一天前遗留的集成测试孤儿 JVM（CPU 0%，launchd 收养），与本变更无关未清理；Gradle daemon 空闲无并发构建。
- 本机构建须显式 JAVA_HOME 指向 Corretto 21（记忆坑：JDK 25 与 Gradle 8.5 不兼容）。

## 任务组 2（全仓文本纪律与注释残留）

- 引号批量转换 438 处 + release.gradle 人工 13 处 + 修正后转换器补 4 处；核查器（注释与 ${} 分词感知）全仓零报告。
- 教训记录：初版转换器不识别 "${...} 内嵌双引号"与三引号串，打坏 tny-module.gradle 一处（已修复并顺带落成 5.7 的 providers 形态）与 release.gradle 两处三引号计划块（已还原）。工具已补齐两种形态识别。
- 注释配置残留删除 76+4 行（settings 9、publish-plugin 9、十六个模块文件），删除 diff 逐文件抽查无误伤；tny-bench 第 24 行 "// include 正则不匹配..."为解释来由的合法注释，未删。
- 分号与无大括号 if 全仓清零（复核 grep 仅命中许可证块注释文本）。
- 验证：`:tny-game-net:compileJava`、`:tny-game-basics:jar`+publishToMavenLocal（MANIFEST Implementation-Version 与 POM version 均保持 5.7.x-SNAPSHOT）、`./gradlew projects` 全量配置评估通过、`clean build` 全量构建 BUILD SUCCESSFUL（339 任务）。

## 任务组 3（根文件重排与单一事实源收敛）

- project-checks 探针：临时给 tny-game-actor 加错误组号，`./gradlew projects` 按既有文案报红并列出违例；恢复后转绿。
- dependencyManagement 十七条版本段入 vers 表后，四模块 dependencies 报表逐节比对与基线全等（唯一差异为耗时与页脚行）。
- `-parameters` 探针：tny-game-net 主产物与方法参数属性保留（javap 检出 MethodParameters，测试编译产物同样生效，D6 覆盖面扩展符合设计）。
- 根文件 60 行（界线 80 内）；`clean build` 全绿；`tasks --all` 与基线 diff 仅耗时行差异。
- 5.7 提前说明：Created-By 的 providers 改写已随组 2 事故修复落成；integration-test.gradle 的 DOCKER_HOST/user.home 部分在组 5 完成。

## 任务组 4（gradle/ 脚本分域拆分与界线达标）

- publish-gate 析出：首次抽取脚本把 publications.gradle 头部重复写入（已检测并重建为 119 行单副本），教训：python 拼接须防前缀重复。
- 门禁验证用 `-m` 干跑与单任务实跑替代 `./gradlew publish` 全量执行：开发线真跑 publish 会把快照外发到 Nexus 与 Central 快照仓，属对外发布动作，未经用户授权不执行。干跑证明发布计划含 checkPublishPrerequisites 挂接，实跑校验断言在开发线全绿。
- 行数达标：dependency.gradle 250（界内）、publications.gradle 119、publish-gate.gradle 152、根文件 60。六个文件头职责注释补齐，新脚本 publish-gate/project-checks/subprojects-baseline/plugin-module/java-module 均带头。
- centralCheck 探针：开发线按既有语义报红（"仅发布分支形态可用"）✓。
- 需求六 dryRun 条款适用性判定（任务 4.4）：central.gradle 的自定义动作仅分支/凭据断言与产物完整性校验（只读），正式版上传由 nmcp 插件自带的 publishAggregationToCentralPortal 任务承担，非本脚本编写的不可逆编排；不可逆 git 写操作集中在 release.gradle 且其 `-PdryRun` 预览通道已存在。判定：无需为 central.gradle 补预览通道。

## 任务组 5（模块文件修复与超线拆分）

- D7 双向探针：空凭据注入下 publishToMavenLocal 成功（修复前会被误挂断言）；共享仓 publish 在 doFirst 属性断言处报红、上传未发生（fail-fast 语义保持）。
- 5.1：publishToMavenLocal 全量构件清单与基线零 diff；tny-game-net 与 tny-game-doc 的 main/sources/javadoc 三 jar 条目清单逐文件一致（D5 风险关闭）。
- 5.2：namnspace/doc-gradle 坐标改具名引用后，dependencies 报表解析版本全等（commons-codec 经 BOM 解出 1.15），namnspace 测试 --rerun 全绿。
- 5.3/5.4：tny-bench 72 行、it 模块 79 行、bench-suite.gradle 126 行、it-demo-isolation.gradle 34 行全部入界；CI 等价三连命令 rc=0；integrationTest rc=0。
- 5.5 见 D7 探针；5.6 settings 89 行无连续空行，projects 评估通过；5.7 providers 改造完成（Created-By 随组 2 落成，DOCKER_HOST/user.home 本组改毕）。
- 教训记录：isolation 脚本 apply 顺序必须先于其 tasks.named 依赖的任务注册（integration-test.gradle 先行），首版顺序颠倒已在重建时修正；字符串手术不收敛时改为整文件重建。

## 任务组 6 偏差与裁决（sourcesJar 与 assemble）

- 实测发现：仅保留 java { withSourcesJar() } 时，自建 sourcesJar 接入 assemble，build 执行面从 339 项增至 390 项（+51 恰为各模块 sourcesJar）；构件内容零差异（m2 清单与 sources jar 条目逐文件比对通过）。
- 用户裁决：恢复原任务图——java-module.gradle 先以 tasks.register('sourcesJar', Jar) 惰性注册，java 块 withSourcesJar() 走复用路径（只挂 java 组件不接 assemble）。design D5 已记录实施修正。
- 组 6 终验与 tasks-all diff 在修复后重跑，结论见后。

## 任务组 6 终验结论

- sourcesJar 修复后 `clean build` 执行 339 任务与基线同数且全绿；`tasks --all` 与基线零差异；publishToMavenLocal 构件清单零差异。
- dependencies 四报表逐节比对：唯一差异面为 tny-game-namnspace 的 commons-codec 声明形态（版本字面量改为 BOM 派生 `-> 1.15`），解析版本逐行一致——任务 5.2 验收声明的目标形态，非回归。
- 6.2 逐条需求扫读自检（八条全过）：需求一——配置阶段程序性块已全部迁入 gradle/ 编排脚本（project-checks/publish-gate/bom-platform/bench-suite/it-demo-isolation 均带头），根脚本 61 行；需求二——旧式 task、点名求值、spread 全仓清零（grep 零命中），providers 取代系统属性直读；需求三——版本字面量仅存在于 gradle.properties 与 dependency.gradle 两处事实源（dependency.gradle 恰 250 行入界）；需求四——双引号核查器零报告、分号/无括号 if 零命中；需求五——settings 注释 include 清零、无连续空行、根脚本区块按固定顺序；需求六——gradle/ 全部 15 个脚本文件头职责注释齐备（head 逐一确认），release.gradle 的 -PdryRun 通道在、central.gradle 免补判定已录；需求七——模块文件全部 80 行内、辅助脚本全部 250 行内（wc 复核）；需求八——本变更即独立全仓清扫变更本身。
- 6.3 抽样回归 :tny-game-net/:tny-game-codec/:tny-game-common-lifecycle test 通过（并被终验全量构建覆盖）。
- 扫读三问抽查：tny-game-net 构建文件三十秒可答依赖什么（具名引用+兄弟模块）、用什么编译（根 java-module 线 toolchain 21）、发布到哪里（publications→Nexus/Central 通道），无需跟控制流。
