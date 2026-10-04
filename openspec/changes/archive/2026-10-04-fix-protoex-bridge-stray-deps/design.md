# Design

## Context

实施前事实核对（2026-10-04 现树复测，立项前完成）：两桥构建文件逐字节相同（diff 空，14 行全文含 `apply plugin: 'maven-publish'` + 四组 api 语句）；`grep -r "jprotobuf\|JProtobuf\|com.baidu" tny-game-net-netty4-codec-protoex/src/` 全 src（主与测试）零命中；protoex 桥主代码 `ProtoExMessageBodyCodec.java` 引用 `com.tny.game.protoex.*` 与 netty4 框架面（真实依赖出处）；jprotobuf 桥 import `com.tny.game.protoex.*`（其 protoex 依赖真实，方向不对称）。仓内 jprotobuf 消费方全清单（grep `codec-jprotobuf|libs.jprotobuf` 于各 build.gradle）：codec-jprotobuf 自身、两桥、net-netty4、starter-codec、net-demo、integration-test、starter-net-netty4——除 protoex 桥这两行外全部是自己代码所需或自身构建，各真实消费者均自行显式声明，无借道。catalog 现状 jprotobuf=2.4.23（并行版本治理推进），本册删除引用行与版本号无涉。

## Goals / Non-Goals

Goals：protoex 桥发布 POM 的编译级依赖恰好收缩为 `tny-game-net-netty4` + `tny-game-protoex` 两条框架面；桥的发布行为（坐标、版本、其余 POM 内容、SPI）零扰动。Non-Goals：不动 jprotobuf 桥文件（其声明与引用相符）；不动两桥的任何 Java 源码与测试源码；不消解"两文件不再同文"之外的任何构建形态问题（例如 `apply plugin: 'maven-publish'` 行与装配线重复面不属本册）；不代并行版本治理工作推进任何依赖版本。

## Decisions

**D1 契约落点新建能力 `codec-bridge-assembly`，条款语言沿用引擎装配先例（P2 依赖契约、装配自声明原则的编解码面应用）。** 原则先例已由 `remove-engine-transitive-assembly` 入册为 `expression-engine-assembly` 规格 Purpose："任何构件的发布依赖面不得再静默携带特定实现"——本册把同一原则应用到第二个契约面（编解码桥），非另起炉灶（模式卷 M1：沿用既有框架内解法）。备选"把桥面条款并入 expression-engine-assembly 能力文本"否决：该能力的 Purpose 与需求整体围绕表达式引擎求值与自注册，塞入编解码桥会把两个互不相干的契约面搅进一个能力单元，能力账本按契约面切分的现行组织被破坏。**备选"仅改构建文件、零规格入册（skip_specs）"否决：发布 POM 依赖清单是外部可观察的发布面行为，收缩即行为变更，按 specs 规则应入册为契约而非藏进实现。**
**D2 第一判据取 POM 文本的精确移除形态。** before/after 各自 `generatePomFileForMavenJavaPublication` 产物逐行比对：差异恰为 `tny-game-codec-jprotobuf` 与 `jprotobuf` 两个 `<dependency>` 块（含子树）消失，其余逐行零差异。POM 是本变更唯一的外部可观察面，判据直接打在它上面；依赖解析集比对（`dependencies --configuration apiElements/runtimeElements`）作第二层旁证。
**D3 消费面验证优先于推断。** 仓内借道可能性已用 grep 清单排除，但仍以编译实跑兜底：`:tny-game-starter-net-netty4:build`（同时装配两桥的官方出口）、`:tny-game-net-demo:build`、`:tny-game-integration-test:compileIntegrationJava`（P3 档引用两桥实现）三点全绿才算"仓内零波及"从静态证据升级为运行证据。
**D4 无代码符号变更，爆炸半径以构建文件消费清单替代 codegraph。** 本册删除的是两个依赖声明行，Java 符号零改动，codegraph impact/callers 无适用对象；替代取证为上文消费者 grep 全清单（Context 段在案），符合规则要旨（分析关键变更面的波及范围），差异形态在此注明。

## Risks / Trade-offs

- 风险一：仓外消费者借道史不可考——BREAKING 已在 proposal 声明，迁移义务与路径写入规格兼容场景（自声明即恢复逐值一致）；发布走下一版号，随 release notes 公告。
- 风险二：protoex 桥的 `jprotobuf` 排除项子树在 POM 中的实际形态（带不带 exclusions 块）影响 D2 diff 的行数——before 件生成后按实形态核判据，判据定义（恰两块移除）不变。
- 权衡：两桥文件"不再同文"后，任何未来对桥构建文件的改动天然需要逐桥思考——复制粘贴复发窗口关闭，值。

## Compatibility Impact

protoex 桥发布 POM 的 compile 级依赖恰好减少 `com.tnydev.game:tny-game-codec-jprotobuf` 与 `com.baidu:jprotobuf` 两条（BREAKING 面）：仓内全部消费方已显式自声明（grep 清单 + D3 三点编译实跑兜底），零波及；仓外消费者若曾借这两条传递获得 jprotobuf 栈，需在自身依赖面显式声明后行为逐值恢复（规格 codec-bridge-assembly 第三场景）。协议格式、桥类 API、SPI 注册、BOM constraints 面零变化。

## Migration Plan

第一步，按权威口径抓 before（先运行 POM 生成任务再转存；解析集、两桥源码引用复测记录、行数形态）。第二步，删 protoex 桥两行。第三步，after 全套比对 + D3 消费面三点 + `clean build`，结论记 verification-notes.md。回滚单文件两行还原。

## 实施时点补注（2026-10-04）

D2 的 apiElements 旁证条款失效，如实修正：apiElements 控制台报告在本仓形态下不逐条列出 api 坐标面（before 件对两条目标坐标本就零命中，输出形态为配置说明行加 "No dependencies"）——该旁证对本变更无鉴别力，"仅差对应两条"的预期不成立。第一判据 POM 逐行比对（恰两完整块 −32 行、after 零 jprotobuf、依赖块计数 6→4）与消费面三点编译实跑承担全部证据职能，判据效力不受影响。另披露：2.1 除删两行外按本运动惯例补了三行来由注释（纯注释，零 POM/解析影响）；POM diff 的连续删除段以"前一 `<dependency>` 开标签保留、末尾开标签并入删除"的等价锚定呈现，净效果与"恰好移除两个完整块"一致（行数与内容集合均可对账）。
