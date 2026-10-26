# Proposal

## Why

tny.tny-module 是一个 36 行、只有两块内容的约定插件：jar 清单四属性（Implementation-Title、Implementation-Version、Automatic-Module-Name、Created-By）与 mavenJava 发布的构件接线（from components.java 加 versionMapping 解析映射）。它只在根脚本 configure(javaProjects) 装配块应用（第 66 行），与 tny.java-module（第 61 行）同在一个装配块内、工程集合完全相同——而 tny.java-module 本就承载该线的 jar 打包配置与 sources、javadoc 构件通道，发布模块构件的元信息与接线和接收文件属于同一段职责。合并后 java 装配线少一个插件 id 与一行应用语句，tny.java-module 头注释里"构件接线"一项从兄弟共享块归位为自有职责，任务名与发布产物面预期逐字节不变。

## What Changes

- tny.tny-module.gradle 的两块内容迁入 tny.java-module.gradle：jar 清单四属性并入该文件既有的 `tasks.named('jar').configure` 块（同一任务的两段配置合为一个块，属性行与其注释逐字保留，见 design D3）；`publishing` 块整体逐行迁入、按主账本"区块顺序与版面组织统一"需求置于接收文件末尾（design D2 论证 mavenJava 的创建归属随之前移而语义等价）；源文件头部的 `plugins { id 'maven-publish' }` 不迁入，因为接收文件的 plugins 块已应用该插件。
- tny.tny-module.gradle 退役：文件移入 /tmp/gradle-retired-quarantine/ 隔离目录而不直接删除（手法与 archive/2026-10-03-merge-demo-isolation-into-integration-test 等先例一致），账本保留可复核性。
- 根 build.gradle 的 configure(javaProjects) 装配块删除该插件的应用行一行。
- 引用面收编：活代码指称共三处——根脚本应用行、tny.java-module 第 6 行兄弟插件清单、该文件第 36 行指向 tny.tny-module 的注释（迁入后按 design D4 改写为本文件 publishing 段的自指称）。tny.central.gradle 头注释以"具备 mavenJava 发布"的类别语描述消费面、不含插件名，本变更不需要随之修改它。
- 接收文件头注释三点收编（清单经核查补全）：伞句的职责列举扩入 jar 清单四属性与 mavenJava 构件接线；共享块类别枚举删去"与构件接线"并把括号清单中的 tny.tny-module 一项去掉；"边界"段维持发布元数据归 tny.publications 的陈述不变。

## Capabilities

### New Capabilities / Modified Capabilities

无。插件 id 收编与配置迁移属实现标识变更：发布物名称（mavenJava）、jar 清单属性、POM 内容、任务名与任务图全部不变。skip_specs。

## Impact

- **受影响文件**：tny.java-module.gradle（吸收两块内容与头注释三点收编）、tny.tny-module.gradle（退役入隔离目录）、根 build.gradle（装配线删一行）。
- **前置约束**：在 merge-dependency-sources-into-java-module 变更实施归档之后实施——两变更同编 tny.java-module 头注释第 6 行兄弟插件清单（各自删一项）并同删根装配块相邻行，先后落地使本变更实施时的现场文本稳定；两变更内容零纠缠。
- **与 merge-publish-gate-into-publish 变更的相交（经核查修正为两处）**：根 build.gradle 的 configure(javaProjects) 装配块（各删一个不同应用行，现场为第 64 行与第 66 行）与 tny.java-module 头注释第 6 行兄弟清单（对方按其验收判据须清理该行的 tny.publish-gate 指称）。本变更对三种合法先后顺序的操作均不敏感，无顺序约束；头注释收编以实施时现场文本为准（与 merge-dependency-sources-into-java-module 变更 design D2 同一手法）。交叉事实供对方账本自行消化：该变更验收判据"grep 活代码 tny.publish-gate 零命中"的指称面除上述两行外还含 tny.git.gradle 第 6 行、tny.central.gradle 第 65 行与拟退役文件 tny.tny-module.gradle 第 3 行边界注释——本变更不代其收编 publish-gate 引用面，只删自己名下的 tny.tny-module 指称。
- **验收基线**：`tasks --all` 任务清单段（比对前剥离构建耗时尾行）与迁移前基线逐行零差异；`:tny-game-net` 的 jar 清单四属性与 `generatePomFileForMavenJavaPublication` 产物对基线逐字节零差异（比对先核对 daemon JVM 版本与基线记录一致，design D2 语义等价的最终兜底判据）；全量构建绿；grep 活代码 tny.tny-module 零命中。历史归档与 verification-notes 中的旧插件名指称不回改（历史记录原则）。
