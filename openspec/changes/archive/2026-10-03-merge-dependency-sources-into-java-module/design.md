# Design

## Context

动机见 proposal.md - Why。实施前事实核对（2026-10-03 现场）：tny.dependency-sources 在活代码的引用共三处——根 build.gradle 的 configure(javaProjects) 装配块应用行、tny.java-module 文件头兄弟插件清单、插件文件自身；settings.gradle 与各模块构建文件均无 apply 该 id 或点名 downloadDependencySources 任务之处。该插件与 tny.java-module 在根脚本同一装配块先后应用（61、62 两行），工程集合同为 javaProjects。任务注册块共 47 行，其中 doLast 动作块内的动作体 40 行，doLast 之外仅 group 与 description 两条声明式属性赋值——程序性代码全部在任务动作通道内，迁入接收文件不新增配置阶段控制流。tny.java-module 现 118 行，收编后约 167 行，在约定插件 250 行界线内。爆炸半径核查（openspec/config.yaml 的 design 规则要求）：codegraph 符号图不覆盖构建脚本；真实消费面经全仓引用扫描定界——除上述三处指称外，CI 工作流、docs、主规格目录均无该 id 或任务名的引用，本变更不触碰任何 Java 源码与产物面。

## Goals / Non-Goals

Goals：一个单任务插件与其装配行一并消失；任务成员、任务名、任务行为逐工程不变。Non-Goals：不改任务体任何逻辑（逐行迁移）；不动 tny.dependency-management（原 tny.subprojects-baseline）的依赖管理声明段；不把任务扩大到 BOM、插件模块线、集成测试与 bench 等非 java 发布线工程；不回改历史归档中的旧名指称。

## Decisions

**D1 合并落点选 tny.java-module，而非最初请求点名的 tny.subprojects-baseline（用户裁决）。** 立项提问中用户以"合并到 java"裁决落点为 java 线装配插件。两条否决理由：其一，并入 tny.subprojects-baseline（改名后的 tny.dependency-management）会让任务随"全部子工程"的应用面扩大——BOM、插件模块线、集成测试线与 bench 工程各新增一个任务行，属规格与需求都不要求的发布面与任务图扩大，且该插件文件头明文"本插件不触碰任务注册"的既有边界将被打破；其二，若在共享插件内用布尔守卫把任务压回 java 线成员，守卫谓词只能读取根 ext 的 javaProjects 集合，令全工程基线插件耦合 java 线装配判定，违反插件职责分线。落点为 tny.java-module 时成员相等由应用面本身成立，零差异是构造性质而非核对性质。主题契合佐证：本文件 idea 段既有 downloadSources = true 声明（IDE 侧源码下载通道），命令行批量下载任务是同一意图的终端形态，同文件收纳名实相符。
**D2 退役用隔离目录法。** tny.dependency-sources.gradle 移入 /tmp/gradle-retired-quarantine/，手法与账本内多处先例同型（archive/2026-10-03-merge-demo-isolation-into-integration-test 的 tny.demo-isolation.gradle 单文件退役与本次形态完全一致）；活代码 grep 以实施时现场为准——若彼时 tny.publish-gate 已因该变更从兄弟清单消失，本变更头注释编辑对象随现场文本调整，验收 grep 判据不变（tny.dependency-sources 零命中）。
**D3 实施顺序：rename-subprojects-baseline-plugin 归档在先（用户裁决"先改名后合并"）。** 本变更全部工件按改名后的文本形态书写指称；两变更对 tny.java-module 头注释的编辑落在不同行，顺序实施互不冲突。与 merge-publish-gate-into-publish 无共同文件，不构成本变更前置。

## Risks / Trade-offs

- [任务块迁入后 downloadDependencySources 在 java 线各工程的注册时点提前一行（62 行应用变为 61 行插件体内注册）] → 注册均为惰性 tasks.register 且无其他任务依赖它，任务图与执行序不受注册先后影响；dry-run 与 tasks --all 基线核对兜底。
- [兄弟清单行删项后，行内其余插件名的换行排布可能被无意改动] → 编辑仅删除本项并重排该注释行，验收含"头注释其余文字与改名后现场一致"人工核对。
- [实跑探针依赖镜像仓下载源码包，输出计数可能随上游快照漂移] → 基线在迁移前一刻抓取（同一依赖集），前后实跑间隔内依赖版本不变更；若快照漂移致计数差异，以"任务成功执行且逐坐标行为同型"人工判读记录，不静默放宽。

## Migration Plan

前置确认改名变更已归档 → 抓基线（tasks --all、:tny-game-net:downloadDependencySources 实跑） → 任务块逐行迁入 tny.java-module 并收编其头注释 → 根装配行删除、插件文件移入隔离目录 → 验收（评估、零差异、grep 清零、全量构建）。回滚为四文件原位还原（根脚本恢复一行应用、插件文件回位、接收文件撤任务块与注释改动）。
