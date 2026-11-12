# 验证记录

## consolidate-compile-baseline-blocks（2026-10-03）

- 环境：JDK 钉 Corretto 21.0.12.1（baseline/jvm-pin.txt），正常 UTF-8 locale、不注入 JAVA_TOOL_OPTIONS（前批变更确立的捕获口径）。
- 1.1 基线：剔噪任务清单 3477 行、`:tny-game-net` 的 MANIFEST（6 行）与生成 POM（151 行）存 baseline/。
- 2.1 合并实施：新建 tny.compile-baseline.gradle——缓存块 `configurations.configureEach` 两行与编码块 `tasks.withType(AbstractCompile/Javadoc).configureEach` 两段逐字迁入，头注释合写双职责与边界并按 D3 修正旧 cache-policy"仓路由在 tny.repositories"的失实指称（现属 settings.gradle 的 dependencyResolutionManagement）；-parameters 与 fork 留 java 线的差异说明原样保留。根脚本 gradleProjects 与 javaProjects 两块各两行并一行（净减两行）；指称收编三处（plugin-module 兄弟清单两项并一、java-module 类别枚举与第 74 行编码归属注，后者"参数留本线"的差异说明结构不动）。两旧文件移入 /tmp/gradle-retired-quarantine/。`projects -q` 通过。
- 3.1 零差异验收：`tasks --all` 剔噪清单段对基线**内容行逐行零差异**（唯一 diff 是一枚空行——before 捕获时 daemon 告警行被过滤后留下的空位，after 无此空位；清单内容 3477 行完全一致，属过滤器噪声非任务面差异）；`:tny-game-net` 的 MANIFEST 与 POM（clean 重产后）逐字节零差异；grep 两旧 id 于根脚本、settings、gradle/、buildSrc/src、各模块构建文件、docs、.github 零命中（唯一残留指称在 HANDOFF 规划记录文档，属记录面不清零）。
- 全量构建真实时间线（三次运行如实记录）：第一跑红于 buildSrc Groovy 编译"Unsupported class file major version 69"——该命令漏钉 JAVA_HOME、daemon 用了本机默认 JDK 25（Gradle 8.5 不兼容，环境操作失误非变更回归）；第二跑钉环境后红于 `:tny-game-common-lang:test` 的 `CollectionLockTest.exclusiveMixturesNeverOverlap`——与 fix-ci-unit-flakes 登记 #2/#3/#4 签名同型的负载偶红（本变更缓存块逐字未动，`configureEach` 语义与时序断言无涉），按登记标准单任务 `--rerun-tasks` 连续两跑 exit 0 全绿；第三跑完整 `clean build` **一次全绿**（1 分 14 秒，339 执行，零 FAILURE，日志存 baseline/cleanbuild-green-evidence.log）。
- 环境操作纪律（本册踩坑沉淀）：一切 gradlew 调用必须显式 `JAVA_HOME` 钉 Corretto 21（本机默认 JDK 25 会使 buildSrc 编译当场红）；后续变更工件的验证命令宜直接写明该前提。
- 流程偏差自我披露：本记录初版曾在 3.1 尚未真实执行时先行写入（一次命令批量执行返回缺失导致误判"已完成"），随后以实际取证重写全文——账本纪律：结论只能在证据之后落纸。
