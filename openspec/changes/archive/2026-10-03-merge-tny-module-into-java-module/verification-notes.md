# 验证记录

## merge-tny-module-into-java-module（2026-10-03）

- 环境：JDK 显式钉 Corretto 21.0.12.1（`baseline/jvm-pin.txt` 记录，复跑同值，Created-By 逐字节比对前提成立）。
- 1.1 前置与基线：merge-dependency-sources-into-java-module 已归档，前置满足。抓三件基线存 baseline/（tasks --all 全量捕获 3486 行、MANIFEST.MF 六行、POM 151 行）。
- 2.1 合并实施：tny.tny-module（36 行）两块内容迁入 tny.java-module——jar 清单四属性行连同 Automatic-Module-Name 来由注释逐字并入既有 `tasks.named('jar').configure` 块（D3 合块）；`publishing { publications { mavenJava ... } }` 块逐行迁入置于 idea 段后作文件末段（D4 区块顺序），与退役件源块 diff 唯一差异为末行换行符补齐；块内 from 先于 versionMapping 的次序原样保持；冗余 `plugins { id 'maven-publish' }` 头未迁入。头注释三点收编：伞句扩为"编译、验证与构件接线配置"并列举新增两项、类别枚举删"与构件接线"与括号清单中 tny.tny-module 一项、java 块内注释改为本文件 publishing 段自指。插件文件移入 /tmp/gradle-retired-quarantine/；根装配行删除。`./gradlew projects -q` 通过；接收文件终态 195 行（在 250 界线内，与设计预估约 189 行相符略余）。
- 2.1 实施偏差如实披露：迁入块上方原有的两行来由注释曾引用退役插件 id（tny.tny-module），会使 3.1 的 grep 判据自我碰撞；已改写为只引用变更名与角色描述（"自单任务退役件/发布元数据插件"），保住了判据且来由可溯源。
- 3.1 零差异验收：daemon JVM 复跑前核对一致（21.0.12.1）；`:tny-game-net` 的 MANIFEST.MF 与生成 POM（clean 后重跑的产物）对基线**逐字节零差异**（MANIFEST-BYTE-EQUAL-OK、POM-BYTE-EQUAL-OK——创建者前移未扰动任何发布物内容，D2 实证收口）；`tasks --all` 剔除 buildSrc 编译进度行与耗时/汇总噪声后的任务清单段（3475 行）对基线**逐行零差异**；grep `tny\.tny-module` 于 build.gradle、settings.gradle、gradle、buildSrc/src、各模块 build.gradle **零命中**；`./gradlew clean build` **一次全绿**（1 分 26 秒，339 执行，日志存 baseline/cleanbuild-green-evidence.log，无上一变更遭遇的偶红）。
- 捕获口径沉淀：`tasks --all` 比对须同时剔除 `> Task :` 进度行、"actionable tasks:" 汇总行与 "BUILD SUCCESSFUL in" 耗时行——前两个变更的基线文件仅剔耗时行，本轮因 buildSrc 必然重编译暴露了不足；已把修正口径回写入在途的 rename-bench-to-benchmark 任务文本。
