# Tasks

## 1. 基线抓样

- [x] 1.1 按 openspec/config.yaml context"零差异验收基线抓样口径"一条抓基线存本变更目录 baseline/（转存型样件先运行生成任务再转存；本步首抓与第 3 节复跑在同一 Gradle daemon、同一 UTF-8 locale 钉定环境下抓取）：先运行 `./gradlew :tny-game-net-netty4-codec-protoex:generatePomFileForMavenJavaPublication`，再转存 `tny-game-net-netty4-codec-protoex/build/publications/mavenJava/pom-default.xml` 为 `pom-protoex-before.xml`；先运行 `./gradlew :tny-game-net-netty4-codec-protoex:dependencies --configuration apiElements --console=plain` 控制台转存解析集；`diff` 复核两桥构建文件仍逐字节相同并记录；复跑 protoex 桥 `grep -r "jprotobuf\|JProtobuf\|com\.baidu" src/` 零命中记录；`./gradlew tasks --all --console=plain` 剔噪清单段；两桥与 jprotobuf 桥自身测试现状绿记录（`./gradlew :tny-game-net-netty4-codec-protoex:test :tny-game-net-netty4-codec-jprotobuf:test`）。

## 2. 错登清除实施

- [x] 2.1 `tny-game-net-netty4-codec-protoex/build.gradle` 删除 `api project(':tny-game-codec-jprotobuf')` 与 `api libs.jprotobuf` 两行（含其各自相邻空行按现状文件形态处理）；`api project(':tny-game-net-netty4')`、`api project(':tny-game-protoex')` 与 `apply plugin: 'maven-publish'` 原位不动；`tny-game-net-netty4-codec-jprotobuf/build.gradle` 零触碰。验证：`./gradlew :tny-game-net-netty4-codec-protoex:test :tny-game-net-netty4-codec-jprotobuf:test` 全绿；`diff` 两桥文件不再相同（同文形态随错登清除消解，记录在案）。

## 3. 精确移除形态验收与消费面兜底

- [x] 3.1 第一判据：先运行 `./gradlew :tny-game-net-netty4-codec-protoex:generatePomFileForMavenJavaPublication`，转存产物为 `pom-protoex-after.xml`，对 `pom-protoex-before.xml` 逐行比对——差异恰为 `tny-game-codec-jprotobuf` 与 `com.baidu:jprotobuf` 两个 `<dependency>` 块（含子树）移除，其余逐行零差异（design D2；diff 形态不符即停回用户裁决，不静默吸收）；apiElements 解析集复跑与 before 仅差对应两条，旁证记录。
- [x] 3.2 消费面运行证据（design D3）：`./gradlew :tny-game-starter-net-netty4:build :tny-game-net-demo:build :tny-game-integration-test:compileIntegrationJava` 全绿——仓内零借道波及从 grep 静态证据升级为编译实跑证据。
- [x] 3.3 全量收口：`./gradlew clean build` 全绿（偶红按 fix-ci-unit-flakes 登记标准处置留痕）；`tasks --all` 剔噪清单对 1.1 基线逐行零差异；全部结论（含 BREAKING 公告要素：移除的两条坐标原文、适用场景与迁移路径）记 verification-notes.md。
