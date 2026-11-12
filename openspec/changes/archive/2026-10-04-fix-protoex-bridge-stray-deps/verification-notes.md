# 验收记录：fix-protoex-bridge-stray-deps

## 结论

验收通过，全绿收口。protoex 桥的发布 POM 精确收缩为两条真实装配面，第一判据（POM before/after 逐行比对恰移除两个完整依赖块）达成；两桥测试、消费面三点编译、任务清单、全仓 `clean build` 全绿。本册在仓内不修复任何其他册的债——但一个巧合事实记录在案：本册工作树全绿（1 分 3 秒）包含词表守卫修复（`adapt-commons-io-214-word-filter` 册，已归档、尚未入库）在内，提示远端最新失败轮次（run 37145218997）的 Unit tests 红与守卫修复未入库同因；该轮另两个红作业（Bench compile、IT）的根因未在本册逐一认定，留待入库后下一轮 CI 自然复核。

## 实施内容

- `tny-game-net-netty4-codec-protoex/build.gradle`：删除错登两行 `api project(':tny-game-codec-jprotobuf')` 与 `api libs.jprotobuf`（含相邻空行按文件现状形态收拢）；按运动惯例随改动补三行来由注释（指向 codec-bridge-assembly 能力与本册名）——纯注释，POM 与解析零影响，超额部分在 design 补注披露。
- `tny-game-net-netty4-codec-jprotobuf/build.gradle`：零触碰（diff 复核其内容未变）。

## BREAKING 公告要素（供 release notes）

`com.tnydev.game:tny-game-net-netty4-codec-protoex` 发布 POM 移除两条 compile 级依赖：

1. `com.tnydev.game:tny-game-codec-jprotobuf`（5.7.x-SNAPSHOT 随版本派生，含 `spring-boot-starter-logging`、`log4j-to-slf4j` 两项排除子树）；
2. `com.baidu:jprotobuf`（2.4.23，同两项排除子树）。

外部消费者若曾借道 protoex 桥传递获得 jprotobuf 栈，升级后需自行显式声明；声明后行为与借道时期逐值一致（规格 codec-bridge-assembly 兼容场景）。桥的类、SPI、协议格式零变化。

## 判据汇总（baseline/ 六件在档（pom before/after、apiElements before/after、tasks 清单 before/after））

| 判据 | 结果 |
|---|---|
| POM 逐行比对（第一判据） | 连续 32 行纯删除、零新增——恰为两完整 `<dependency>` 块（含子树）；after 全文 jprotobuf 零命中、依赖块计数 6→4 |
| apiElements 解析集旁证 | **无鉴别力，已修正**：该报告形态 before/after 均不逐条列 api 坐标（before 对目标坐标零命中），design 补注记明，不承担判据职能 |
| 两桥模块测试 | 全绿（改前改后各一轮） |
| 两桥文件同文形态 | 改前逐字节相同（before 记录在案）、改后 differ——重复形态随错登清除自然消解，无需另行治理 |
| 消费面编译证据（design D3） | `:tny-game-starter-net-netty4:build`、`:tny-game-net-demo:build`、`:tny-game-integration-test:compileIntegrationJava` 全绿——仓内零借道从 grep 静态证据升级为运行证据 |
| `tasks --all` 剔噪清单 | 3449 行逐行零差异（before/after 同窗同环境；行数与本运动早期 3457 行的差为并行册在途变更所致，与本册无涉，两侧自洽） |
| `clean build` | 全绿（1 分 3 秒） |

## 抓取口径执行记录

POM 与解析集均属"先运行生成任务再转存"：POM 在 1.1 与 3.1 各先跑 `generatePomFileForMavenJavaPublication` 再转存产物文件；控制台件同窗剔噪（含 daemon 工具链告警行与 problems-report 行）；before/after 全程同一 Gradle 8.14.5 daemon、UTF-8 钉定（diff 输出无问号噪声）。
