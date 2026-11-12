# 验证记录

## rename-module-modes-plugin（2026-10-03）

- 环境：JDK 钉 Corretto 21.0.12.1；任务清单捕获的两处判据执行教训（均与改名无关）：首次 after 捕获误在 LC_ALL=C 下运行，JVM 把 stdout 中文打成问号产生大段假差异；重抓时经 JAVA_TOOL_OPTIONS 注入 UTF-8 又引入首行 "Picked up JAVA_TOOL_OPTIONS" 提示行——剔除该行后对基线逐行零差异（原始基线无 JAVA_TOOL_OPTIONS 故无此行）。统一捕获口径应以"LANG/LC_ALL 正常 + 不注入 JAVA_TOOL_OPTIONS"为准。
- 1.1 基线：无强前置确认达成；tasks --all 剔噪清单段 3476 行、projects 绿存 baseline/；现场 grep 定界旧 id 指称共 13 处（根级两注释、project-checks 两处、integration-test 一处、ModuleModes.groovy 两处、主体文件内部两处、三模块应用行与调用块各三处），与工件清点吻合。
- 2.1 改名实施：`tny.module-modes.gradle` git mv 为 `tny.module-setting.gradle`；主体内部头注释与 `extensions.create` 改 `moduleSetting`（实现类与 "ModuleModes.enabled:" 报错串按 D1 不动）；三模块应用行与调用块随改；settings.gradle 第 50 行权威文本随改（D2）；project-checks 第 4 行头注释与第 46 行报错文案换指称（D3）；integration-test 第 101 行、ModuleModes.groovy 第 1 与 47 行注释随改；历史变更名 consolidate-module-modes-plugin 指称原样。projects -q 通过。
- 3.1 验收：防线探针——`tny-game-net-demo` 调用块临时改回旧扩展名后评估报红，原文：`Could not find method moduleModes() for arguments [...] on project ':tny-game-net-demo' of type org.gradle.api.Project`（Groovy 动态方法未定义形态，不钉死措辞的判据成立），恢复后评估复绿；tasks --all 剔噪清单段对基线逐行零差异（扩展名与插件 id 不进任务名，构造性零差异如期）；`grep "tny\.module-modes"` 于根脚本、settings、gradle/、buildSrc/src、各模块构建文件零命中（历史变更名 consolidate-module-modes-plugin 与类名 ModuleModes 不在判据形态内）；`grep "moduleModes"`（区分大小写）全仓现行文件零命中；`./gradlew clean build` 全绿（339 执行）。
- 结论：插件 id、扩展名、全部现行指称一步改齐，声明语义与合同行为零变化。

- 归档后复验（bench 目录迁移引出的跨册缺口回归处置）：bench 模块改名后其 build.gradle 曾带回旧插件 id（混批回滚二次损害，独立 fix 提交修复并推送）；本轮全仓大小写敏感复扫 `tny\.module-modes` 与 `moduleModes` 双形态零命中，`jmhSuiteVerify` 对账绿（4 routine/5 devtest/1 探针全归族）、枚举 31 键与前册换代映射基线精确一致、清单 3457 行仅空行位差、demo 调用块旧扩展名临时回改复现 `Could not find method moduleModes()` 红并恢复复验绿、`clean build` 全绿（339 执行，零 FAILURE）。demo 文件现名引用在位（第 7 行 moduleSetting）。
