# 基线快照 README — consolidate-binary-conventions

锚定提交：4653dd6f（任务组 1 规则开道合入后的 HEAD）。

## 抓样环境（改造前后必须逐字同判据）

- `JAVA_HOME=/Users/kgtny/Library/Java/JavaVirtualMachines/corretto-21.0.12.1/Contents/Home`（Corretto 21，按 context 零差异抓样口径）
- `PATH` 前置 `/opt/homebrew/bin:/usr/bin`。来由：本机 `/usr/local/bin/git` 是只含 x86_64 与 i386 的历史遗留通用二进制，在本机 CPU 上执行报 error 86（Bad CPU type）——pilot apply-notes 第 3 至 5 行登记过的同一环境事实；构建期 `tny.git` 经 GitCli 以 ProcessBuilder 调用 git，PATH 不修正则配置期即失败。
- `GRADLE_OPTS=-Dorg.gradle.daemon.registry.base=/tmp/tny-cbc-daemons`：本册全部构建命令用独立守护进程注册表。来由：本机上另有并行会话的守护进程在位，按默认注册表可能借用或污染其环境（其进程 PATH 内含坏 git 二进制）；独立注册表下本册首跑起守护进程、后续复用同一进程，改造前后同一守护进程同一环境的判据由该隔离达成（这是 context "同一 Gradle daemon"条目的达成方式，特此登记）。
- `JAVA_TOOL_OPTIONS` 未注入；文件编码 UTF-8 由 `gradle.properties` 的 `org.gradle.jvmargs=-Dfile.encoding=UTF-8` 钉住。
- 剔噪口径：比对时剔除守护进程启动行（`Starting a Gradle Daemon...`）、`BUILD ...` 尾行、`actionable tasks` 行与空行；样件原文全量入库，剔噪命令在比对步现场施加并记录于 apply-notes。

## 样件清单（改造前，2026-10-07 抓取）

| 文件 | 内容 | 行数 |
|---|---|---|
| `tasks-all-before.txt` | `./gradlew tasks --all` 全量任务图 | 3588 |
| `deps-net-compile-before.txt` | `:tny-game-net:dependencies --configuration compileClasspath` | 73 |
| `deps-net-runtime-before.txt` | `:tny-game-net:dependencies --configuration runtimeClasspath` | 94 |
| `pom-java-line-before.xml` | java 线 `:tny-game-net:generatePomFileForMavenJavaPublication` 产物转存（先运行生成任务后转存） | 167 |
| `pom-plugin-line-before.xml` | 插件线 `:tny-game-doc-gradle:generatePomFileForPluginMavenPublication` 产物转存 | 76 |
| `bom-gate-defect-before.txt` | `:tny-game-bom:publish --dry-run` 在 HEAD 的报红全文（门禁半配对缺陷实测证据；报错行：Task with path 'checkPublishPrerequisites' not found in project ':tny-game-bom'） | 全文 |
| `help-timing-before.txt` | warm 守护进程下 `./gradlew -q help` 三连 `/usr/bin/time` 读数 | 2.95 / 2.13 / 2.09 秒 |

与 pilot 归档卷宗的对照注记：pilot 五样本记 tasks 图 3550 行、POM 167 与 76 行——POM 行数与本基线逐一致，任务图行数差异来自其间在途册（redesign 组 3/4 发布任务双语义与门禁插件等）新增的合法任务面，本册以后即以此 3588 行基线为准。
