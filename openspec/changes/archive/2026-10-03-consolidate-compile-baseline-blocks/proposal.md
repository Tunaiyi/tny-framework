# Proposal

## Why

`tny.cache-policy`（9 行，动态依赖零缓存）与 `tny.compile-encoding`（11 行，编译与 javadoc 编码钉定）是两条装配线共用的编译基线小块——根脚本在 gradleProjects 块（第 52、53 行）与 javaProjects 块（第 59、60 行）各应用一遍，两两成对、单行体量小、主题同属"编译行为与环境解耦"。两插件分立使装配线多两行、头注释互指多处分叉（java-module 两处、plugin-module 一处）。立项时曾议并入 tny.java-module，经核查否决：那会剥掉插件模块线（tny-game-doc-gradle）的这两项基线，或在两插件逐字复制违主账本禁令。用户裁决落点为**两块合为一块、两线继续共用**——合并成单一约定插件 `tny.compile-baseline`，两线各引一行，实现唯一、覆盖面逐字节不变。

## What Changes

- 新建 `buildSrc/src/main/groovy/tny.compile-baseline.gradle`：吸收两插件正文（configurations 零缓存块与 tasks.withType 编码块，逐字保留），头注释合写双职责与边界；顺手修正 cache-policy 头注释的失实指称"仓路由在 tny.repositories"——仓库路由机制已随 centralize-resolution-config 迁至 settings.gradle 的 dependencyResolutionManagement 单点声明，新头注释按现状陈述。
- 根 `build.gradle` 两处：gradleProjects 与 javaProjects 装配块各自的两行应用替换为一行 `apply plugin: 'tny.compile-baseline'`（净减两行）。
- 指称收编三处：`tny.plugin-module.gradle` 第 4 行兄弟清单（tny.cache-policy、tny.compile-encoding 两项并为 tny.compile-baseline 一项）、`tny.java-module.gradle` 第 5 行类别枚举括注与第 74 行"编码由 tny.compile-encoding 共享块承担"注释。
- 两旧文件移入 /tmp/gradle-retired-quarantine/ 隔离目录（整合类变更既有手法）；历史归档、在途变更工件与 HANDOFF 记录面的旧名指称不回改。

## Capabilities

### New Capabilities / Modified Capabilities

无。两配置块的组合与效果（零缓存参数、编码取值来源、覆盖面工程集合）逐字不变，属装配文件内部的实现标识变更。skip_specs。

## Impact

- **受影响文件**：新建 1、根 build.gradle 两装配块、plugin-module 与 java-module 头注释三处指称、退役 2。现场 grep `tny\.cache-policy|tny\.compile-encoding` 现行指称共七处（根脚本四行应用、plugin-module 一处、java-module 两处注释），逐处消化后清零为验收项。
- **顺序关系**：与在途两本改名变更（rename-module-modes-plugin、rename-project-checks-plugin）及 rename-bench-to-benchmark 均无共同文件；与已归档变更零交叉。
- **验收基线**：`tasks --all` 剔噪清单段对实施前自抓基线逐行零差异（两插件均不注册任务，零差异为构造性质）；`:tny-game-net` 生成 POM 与一次 jar 产物对基线逐字节零差异（编码与缓存参数取值不动的直接检验）；`./gradlew clean build` 全绿；grep 两旧 id 活代码零命中。
