# Proposal

## Why

**BREAKING**（发布 POM 的编译级依赖清单收窄，外部消费者若曾借道获得 jprotobuf 将失去传递来源，详见 Impact）。

`tny-game-net-netty4-codec-protoex`（protoex 协议的 Netty4 报文体编解码桥）的构建文件与姊妹模块 `tny-game-net-netty4-codec-jprotobuf` 的构建文件逐字节相同（14 行，diff 为空）——历史复制粘贴后未做差异化。其中两行对 protoex 桥是错登：`api project(':tny-game-codec-jprotobuf')`（jprotobuf 编解码核）与 `api libs.jprotobuf`（jprotobuf 第三方库）。查证已坐实（2026-10-03，用户裁定"疑为错登需先查证"后的只读核查，本轮立项前按现树复测仍然成立）：protoex 桥的主代码与测试代码对 jprotobuf 相关类型零引用（全 src grep 零命中），其真实装配面只有 protoex 协议栈与 netty4 编解码框架。错登已泄到发布面：protoex 桥发布的 POM 以 compile 级携带 `tny-game-codec-jprotobuf` 与 `jprotobuf` 两条依赖，凡引用该桥的下游工程都被无声拖入第二种协议的完整技术栈。这与并行工作刚在引擎面确立并归档的装配原则相悖——`expression-engine-assembly` 规格 Purpose 明文"任何构件的发布依赖面不得再静默携带特定实现"；本册把同一原则落到编解码桥面。姊妹方向不受影响：jprotobuf 桥确实在代码里引用 protoex 类型（实测 import 在案），它声明的 protoex 依赖是真实装配面，文件保持零触碰。

## What Changes

- `tny-game-net-netty4-codec-protoex/build.gradle`：删除 `api project(':tny-game-codec-jprotobuf')` 与 `api libs.jprotobuf` 两行；保留 `api project(':tny-game-net-netty4')` 与 `api project(':tny-game-protoex')`（主代码真实引用面）。
- `tny-game-net-netty4-codec-jprotobuf/build.gradle`：零触碰（其依赖声明与代码引用逐行相符）。
- 两文件不再逐字节相同——此前"同文复制"的重复形态随错登清除自然消解，无需另行治理。
- 外部兼容性：本变更使 protoex 桥发布 POM 的 compile 依赖恰好减少上述两条；仓内消费方（`tny-game-starter-net-netty4`、`tny-game-net-demo`、`tny-game-integration-test`、`tny-game-codec-jprotobuf` 等）全部自行显式声明 jprotobuf 相关依赖（grep 清单在案），无一借道传递，仓内零波及；仓外若有消费者曾依赖这条传递路径取得 jprotobuf，升级本框架版本后需自行显式声明——此为 BREAKING 标注的理由与范围。

## Capabilities

### New Capabilities / Modified Capabilities

- `codec-bridge-assembly`（新增能力规格）：规定 netty4 报文体编解码桥的发布依赖面自声明原则——每座桥对外携带的编译级依赖恰好是它自身代码装配的协议栈，不得静默携带另一协议栈的实现。差量含 1 条需求、3 个场景（正常路径：桥发布依赖面与代码引用面一致；失败形态：错登依赖从 POM 消失的精确形态判据；兼容路径：借道消费者的自声明义务）。

## Impact

- **受影响公共面**：`com.tnydev.game:tny-game-net-netty4-codec-protoex` 发布 POM（compile 依赖 −2）——**BREAKING**（外部传递借道面）。抽象模块接口、报文协议格式、SPI 注册行为零变化：桥的类、注解、编解码语义、`NetPackageCodecSPI` 装载面全不动。
- **受影响文件**：`tny-game-net-netty4-codec-protoex/build.gradle` 一处。
- **下游与 starter**：`tny-game-starter-net-netty4`（同时显式装配两桥，自声明完整）；`tny-game-net-demo`、`tny-game-integration-test`（P3 档显式引用，jprotobuf 各有自声明）——均为验证对象而非修改对象；`tny-game-bom` 走 constraints 形态不带传递依赖，不受影响。
- **与在途册关系**：并行版本治理工作已把 catalog 的 jprotobuf 版本推进至 2.4.23（未提交窗口的现状），与本册不冲突——本册删除的是 protoex 桥对整条坐标的引用行，与版本号无关；与 `expose-git-info-extension`（插件文件面）、`revise-release-branch-flow`（发布流程面）零文件交集。
- **验收基线**：protoex 桥 `generatePomFileForMavenJavaPublication` 产物 before 件（先运行生成任务再转存，按 openspec/config.yaml context"零差异验收基线抓样口径"）；落地后 POM 与 before 的 diff 恰为两个 `<dependency>` 块（`tny-game-codec-jprotobuf`、`jprotobuf`）移除、其余逐行零差异为第一判据；两桥模块编译测试全绿、消费面三模块编译全绿、`tasks --all` 剔噪清单零差异、`clean build` 全绿。
