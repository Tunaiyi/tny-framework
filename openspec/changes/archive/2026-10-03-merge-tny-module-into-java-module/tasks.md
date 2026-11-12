# Tasks

## 1. 前置确认与基线抓样

- [x] 1.1 前置确认：merge-dependency-sources-into-java-module 变更已实施归档（两变更同编 tny.java-module 头注释第 6 行兄弟清单区与根 javaProjects 装配块，先后落地使本变更实施时现场文本稳定）。固定环境后抓基线存本变更目录 baseline/：`export JAVA_HOME` 选定本机 Corretto 21 并全程沿用；`./gradlew tasks --all --console=plain | sed '/^BUILD SUCCESSFUL in /d'` 的有序任务清单段；先 `./gradlew :tny-game-net:clean` 再 `./gradlew :tny-game-net:jar` 后自产物解包的 MANIFEST.MF 全文，并把 daemon 的 `java.version` 值抄入 baseline 注记（Created-By 属性随 JVM 漂移，比对须同 JVM）；`./gradlew :tny-game-net:generatePomFileForMavenJavaPublication` 生成的 POM 文件全文。

## 2. 合并实施

- [x] 2.1 双块迁入 tny.java-module：jar 清单四属性行（含 Automatic-Module-Name 连字符换点号的来由注释）并入该文件既有的 `tasks.named('jar').configure` 块（同任务两段配置合为一个块，D3）；`publishing { publications { mavenJava(MavenPublication) {...} } }` 块逐行迁入置于 idea 块之后作文件末段（区块顺序需求"最后是发布与签名"，D4；块内 from 先于 versionMapping 的次序原样保持），`plugins { id 'maven-publish' }` 冗余头不迁入（接收文件 plugins 块已有该项，D2）；接收文件头注释三点收编（D4）——伞句职责列举扩入"jar 清单四属性与 mavenJava 构件接线"、共享块类别枚举删"与构件接线"并去掉括号清单中 tny.tny-module 一项、第 36 行"均经 tny.tny-module 的 from components.java 自动挂入发布"改写为本文件 publishing 段自指，"边界"段不变；tny.tny-module.gradle 移入 /tmp/gradle-retired-quarantine/；根 build.gradle 的 configure(javaProjects) 装配块删除 `apply plugin: 'tny.tny-module'` 一行。验证：`./gradlew projects -q` 通过。

## 3. 零差异验收

- [x] 3.1 判据先行核对：复跑前确认 daemon 的 `java.version` 与 1.1 记录一致（不一致先对齐再比）。三件探针与基线比对：剥离耗时尾行后的 `tasks --all` 任务清单段逐行零差异；`:tny-game-net` 的 MANIFEST.MF 四属性（clean 或 `--rerun-tasks` 保证是新产物）与生成 POM 全文对基线逐字节零差异（D2 语义等价的兜底判据，出现任何差异即停回用户处裁决，不静默放宽）；`grep -rn "tny\.tny-module" build.gradle settings.gradle buildSrc/src tny-*/build.gradle` 活代码零命中；`./gradlew clean build` 全绿。全部结论记 verification-notes.md（含 JVM 版本记录与三份探针比对结果）。
