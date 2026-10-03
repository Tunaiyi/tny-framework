# 验证记录

## rename-subprojects-baseline-plugin（2026-10-03）

- 环境：JDK 为 Corretto 21.0.12.1（显式 JAVA_HOME；本机默认 JDK 25 与 Gradle 8.5 不兼容，buildSrc 的 Groovy 编译会报类文件版本错误，构建须钉 21）。
- 1.1 改名实施：`buildSrc/src/main/groovy/tny.subprojects-baseline.gradle` 以 git mv 移名为 `tny.dependency-management.gradle`（插件 id 由文件名派生，随之更换）；四处活代码指称同步更新——根 `build.gradle` 第 48 行装配引入、改名文件自身头注释（首行"公共基线装配"改"公共依赖管理装配"并补一行命名来由）、`tny.java-module.gradle` 边界行、`tny.plugin-module.gradle` 边界行。文件正文（八个 BOM 导入、二十项具名版本管理、dependencyNotation 桥接、两行组号版本派生）一字未改。`./gradlew projects -q` 通过。
- 2.1 零差异验收：`tasks --all`（剥离 BUILD SUCCESSFUL 耗时尾行）改名前后逐行 diff 零差异（3486 行清单段一致，存 baseline/ 两份）；`grep -rn "tny\.subprojects-baseline"` 于 build.gradle、settings.gradle、gradle/、buildSrc/src、各模块 build.gradle、docs、.github、openspec/config.yaml 与根级 markdown 零命中——改名文件头注释保留的"前身为 gradle/subprojects-baseline.gradle"为历史路径指称，按 design 历史记录原则不回改，亦不在该 grep 判据命中面内；`./gradlew clean build` 全绿（1 分 31 秒，347 个任务，339 执行）。
- 结论：纯标识变更达成，插件 id 不出现在任何任务名与产物面，验收判据与 proposal 预期一致。
