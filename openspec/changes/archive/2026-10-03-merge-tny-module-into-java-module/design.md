# Design

## Context

动机见 proposal.md - Why。实施前事实核对（2026-10-03 现场，另经四视角核查复核并以隔离探针实测）：tny.tny-module 的活代码指称三处（根 build.gradle 第 66 行应用、tny.java-module 第 6 行兄弟插件清单、第 36 行指向该插件的注释——该注释迁入后按 D4 改写才成为本文件自指），插件文件 36 行（wc 口径；末行无行尾换行符，编辑器口径 37 行）、内容两块声明体，另有一个 `plugins { id 'maven-publish' }` 头块按冗余不迁入。其应用面与 tny.java-module 同为 configure(javaProjects) 成员：两条应用语句分别位于根 build.gradle 第 61 行与第 66 行，同在一个装配块内、无条件守卫；gradleProjects 线与 BOM 线均不应用该插件，全仓无第二处应用（tny.plugin-module 头注释亦明言插件线不走 tny.java-module 的发布元数据链）。mavenJava 发布的 DSL 承载现状：在根脚本第 65 行应用的 tny.publications 其第 23 行，与 tny.tny-module 第 25 行，同用 `publications { mavenJava(MavenPublication) { ... } }` 的 create-or-configure 形态——先声明者创建、后声明者配置，两文件并存共作是当前构建的实证（核查并以 /tmp 隔离探针实测两种先后顺序配置均全绿、发布任务清单逐行一致）。tny.java-module 现 118 行，其头部 plugins 块已含 maven-publish。爆炸半径核查（openspec/config.yaml 的 design 规则要求）：codegraph 符号图不覆盖构建脚本；真实消费面经全仓引用扫描定界为上述三处，CI、docs 与主规格目录零命中，本变更不触碰任何 Java 源码与产物面。

## Goals / Non-Goals

Goals：java 装配线的插件数与应用行各减一；jar 清单与构件接线配置迁移后发布物逐字节不变；迁入版面符合主账本区块顺序需求。Non-Goals：不借合并调整清单属性、POM 结构或 versionMapping 语义；不动 tny.publications 与 tny.central（消费面按发布物名与类别语，与插件 id 无关）；不代 merge-publish-gate-into-publish 变更收编其 tny.publish-gate 指称面；不回改历史归档。

## Decisions

**D1 落点 tny.java-module，实施序在 merge-dependency-sources-into-java-module 变更之后。** 同线成员一致使零差异由落点构造成立（与 merge-publish-gate-into-publish、merge-dependency-sources-into-java-module 两个整合变更的 D1 同型论证）；排在其后是因为两变更同编该文件头注释兄弟清单行与根装配块，先后落地让本变更工件书写的现场文本稳定。否决备选"并入 tny.publications"：构件接线（from components.java）与发布元数据（POM、签名）虽相邻，但 tny.publications 现 118 行、按文件头边界自述专司发布元数据与签名，除 java 线外还服务 BOM 线（tny-game-bom 第 7 行直接应用；插件模块线的 tny-game-doc-gradle 并不应用它，tny.central 头注释亦写明两条线均不应用），把仅 java 线的 jar 清单四属性并入会使它变成第二个 java 装配插件、破坏各条装配线各自的插件边界语义；java-module 才是 java 线专属装配的归属。
**D2 mavenJava 创建归属前移，语义等价性由现状形态、隔离探针实测与发布物零差异三重论证。** 迁移后 publishing 块在 tny.java-module（第 61 行应用）执行、成为 mavenJava 的先创建者，tny.publications 块转为配置方——两个声明本就是同一 create-or-configure DSL 形态（当前现场即"先建后配"共作，核查实测反转先后亦全绿，发布、签名、generatePom 任务集两种顺序完全一致），先后互换不引入新形态；发布名不变故全部派生任务名不变；versionMapping 的注册发生在配置阶段，其消费（runtimeClasspath 的解析与 POM 版本替换）发生在 generatePomFileForMavenJavaPublication 的执行期，afterEvaluate 只是 maven-publish 定案发布并创建任务的时点——两个候选注册时点都严格早于任何消费时点，前移不改变"注册先于消费"的关系。块内次序约束随整块迁移保持：versionMapping 声明须位于 from(components.java) 之后（Gradle 文档语义，现状即如此）。
**D3 jar 清单四属性并入接收文件既有的 jar 配置块。** java-module 已有 `tasks.named('jar').configure`（duplicatesStrategy 与打包排除两项）；清单四属性与那两项属性互相独立，合为一个块不改变任务终态。主账本需求七以三十秒扫读为验收，要求读者不跟控制流、不翻别处即可看全每项配置——同一文件对同一任务留两段声明，读者要看全 jar 配置必须扫到第二处，与该验收精神相悖（类比引申，非条文原话），故合并为单块，避免的碎片化形态即"同任务两段声明"。属性行与其来由注释（Automatic-Module-Name 的点号替换说明）逐字随行。
**D4 publishing 块落位文件末尾；头注释三点收编；退役用隔离目录法。** 区块顺序需求（"应用插件、身份、编译配置、依赖管理、依赖声明、注册任务，最后是发布与签名"）决定 publishing 块置于接收文件 idea 块之后作全文件末段——落位不改变 D2 的时序论证（同一文件体在第 61 行整体求值）。头注释三点收编：伞句职责列举扩入"jar 清单四属性与 mavenJava 构件接线"；共享块类别枚举删"与构件接线"并去掉括号清单中的 tny.tny-module；第 36 行"均经 tny.tny-module 的 from components.java 自动挂入发布"改写为本文件 publishing 段自指。插件文件移入 /tmp/gradle-retired-quarantine/ 隔离目录（不直接删除，与 merge-demo-isolation-into-integration-test 等归档先例同型）；活代码 grep 以实施时现场为准（若彼时兄弟清单已因 merge-publish-gate-into-publish 变更移除 tny.publish-gate 一项，本变更仅删 tny.tny-module 一项，判据不变）。

## Risks / Trade-offs

- 风险：创建者反转触发 publishing 容器行为差异（tny.publications 第 106 行 `sign publishing.publications.mavenJava` 的直接引用在"先建后配"顺序下是否解析同一对象）。缓解：该引用按名称解析容器元素、与创建时点无关；核查已在隔离探针实测两种顺序配置全绿且任务集一致；验收再以生成 POM 逐字节对比与签名任务在册做双重兜底。
- 风险：Created-By 属性取自 Gradle daemon 所在 JVM 的系统属性，基线抓取与复跑之间 daemon 重启换 JDK（核查实测 21.0.12.1 与 21.0.5 拼出的行值不同）会造成与迁移无关的逐字节假红。缓解：基线抓样时显式固定 JAVA_HOME 并把 daemon 的 java.version 值抄入 baseline 记录；复跑比对前先核对 JVM 值一致，不一致先对齐；jar 探针在 clean 之后或以 --rerun-tasks 重跑，防止拿迁移前旧产物充数。
- 风险：`tasks --all` 捕获输出含"BUILD SUCCESSFUL in 耗时"尾行，逐行零差异按字面执行必然假红。缓解：基线与复跑两份捕获均剥离耗时尾行、比对范围限定任务清单段（判据原文见 tasks）。
- 风险：接收文件与 merge-dependency-sources-into-java-module 变更叠加后行数增长——现 118 行，加前一变更归档后新增的 downloadDependencySources 任务注册块共 47 行，再加本变更两块约 26 行（其中清单属性约 8 行并入既有 jar 块、publishing 块 16 行落末尾，净增约 24 行），合计约 189 行。缓解：在 250 行界线内且余量清晰；未来再收编逼近界线时按扫读与长度需求拆分，不预先设计。
- 风险：versionMapping 前移改变 runtimeClasspath 解析快照的注册先后。缓解：解析发生在发布物生成执行期而非注册期（D2），且 POM 依赖版本逐字节零差异为验收红线，任何差异即停回用户处裁决。

## Migration Plan

第一步，确认 merge-dependency-sources-into-java-module 变更已归档。第二步，固定 JAVA_HOME 抓三项基线（剥离尾行的 tasks --all 任务清单段、clean 后重跑的 :tny-game-net jar 之 MANIFEST.MF、generatePomFileForMavenJavaPublication 生成的 POM 全文，并记录 daemon 的 java.version 值）。第三步，双块迁入与头注释三点收编：清单属性并入 jar 块、publishing 块落文件末尾、第 36 行注释改自指。第四步，根装配行删除、插件文件移入隔离目录。第五步，验收：三件探针比对零差异（先核对 JVM）、grep 清零、全量构建绿，结论记 verification-notes.md。回滚为三文件原位还原：根脚本恢复一行应用、插件文件自隔离目录回位、接收文件撤两块与注释改动。
