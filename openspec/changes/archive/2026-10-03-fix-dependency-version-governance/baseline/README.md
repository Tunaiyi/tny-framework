# 改造前基线抓样记录（任务 1.2）

抓样时间：2026-10-03（本变更实施窗口内）。
环境钉住（按 openspec/config.yaml 零差异验收基线抓样口径唯一权威文本）：LC_ALL=en_US.UTF-8；
同一 Gradle daemon 连续执行三条抓取命令（8.14.5 分发，daemon 复用见抓取顺序）；JDK 为默认 Corretto 21；
命令行丢弃 stderr 的 daemon 原生访问告警噪声行（2>/dev/null），报表正文无空行噪声。
命令形态：`./gradlew -q <各模块>:dependencies --configuration runtimeClasspath|compileClasspath`
与 `./gradlew -q :tny-game-integration-test:dependencies --configuration integrationRuntimeClasspath`。

覆盖模块（15 个）：namnspace-etcd、actor、common-lang、common-digest、common-lifecycle、doc、protoex、
oplog-log4j、boot-log4j2、net、basics、redisson、starter-net-netty4、starter-basics、data-redisson；
外加 integration-test 的 integrationRuntimeClasspath 单件。

现状快照要点（供后续 after 件比对时理解预期差异）：
- log4j-api 解析为 2.21.1（Spring Boot 覆盖态，即本册要修复的失守形态）；
- guava 全图钉扎为 32.0.1-jre（集中托管强制形态，jetcd 通道的 33.0.0-jre 请求被压降）；
- slf4j-api 解析为 2.0.10（声明值生效，经 slf4j-parent 父链形态）。

## 方法论勘误（第 3 组复盘中发现，如实记录）
多工程合并单次调用的抓取形态存在并行输出串扰：before 件的工程段内容互串、段边界错位
（工程头之后配置节顺序在不同调用间不稳定），故逐行整文件 diff 失真。修正措施：
其一，比对判据改用"去树形符号后的内容行全集"（comm 集合差）——第 3 组据此确认零漂移；
其二，自第 5 组起的 after 抓取一律改为按工程单独调用，杜绝串扰。
