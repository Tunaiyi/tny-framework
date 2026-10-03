# 验证记录

## rename-project-checks-plugin（2026-10-03）

- 环境：JDK 钉 Corretto 21.0.12.1，正常 UTF-8 locale、不注入 JAVA_TOOL_OPTIONS（前两本变更确立的捕获口径）。
- 1.1 基线：`tasks --all` 剔进度/汇总/耗时行后 3480 行、`projects -q` 绿，存 baseline/；交叉现场确认——第 4、46 行指称已因 rename-module-modes-plugin 先行落地为 tny.module-setting，按 D3 本变更不触碰。
- 2.1 改名实施：`tny.project-checks.gradle` 经 git mv 移名 `tny.module-checker.gradle`（插件 id 由文件名派生随之更换）；根 `build.gradle:42` 应用行随改（第 41 行引入注释不含 id，未动）；文件头按 D2 补一行命名来由，历史路径"前身为 gradle/project-checks.gradle"按记录原则保留；正文对账逻辑零改动。`projects -q` 通过。
- 3.1 验收：`grep -rn "tny\.project-checks"` 于根脚本、settings、gradle/、buildSrc/src、各模块构建文件、docs、.github 零命中（插件头注释"gradle/project-checks.gradle"历史路径不含 `tny.` 前缀，本不在判据形态内）；`tasks --all` 清单对基线逐行零差异——首次比对出现 4 行差异，全部为冷 daemon 启动环境噪声（"Starting a Gradle Daemon"、"> Configure project :"、一条 git 执行告警行及空行），剔除环境行后两份 3457 行逐行零差异（插件无任务注册，构造性零差异如期）。
- 全量构建事件记录（如实披露）：改造后第一次 `clean build` 报红于 `:tny-game-common-lang:test`——失败用例 `CollectionLockTest.exclusiveMixturesNeverOverlap`，断言消息"读读应可并行驻留（观测不到任何并行说明互斥过紧）expected true but was false"，与账本 fix-ci-unit-flakes 诊断登记 #2/#3/#4 签名完全同型（该登记明言此类偶红出现在全量并行负载下、单任务复跑必绿、buildSrc 引入期已排除迁移相关性），本例为其第四次实况；按登记标准处置单任务 `--rerun-tasks` 复跑两次全绿。第二次 `clean build` 全绿（1 分 7 秒，339 执行，日志存 baseline/cleanbuild-green-evidence.log；首跑红日志留档 baseline/cleanbuild-1st-flake.log）。归因环境偶红而非本变更：改名只动文件名与一行应用语句，测试面零关联。
- 机器环境注记：冷 daemon 求值期出现一条 `Cannot run program "/usr/local/bin/git" … Bad CPU type in executable` 告警——本机 /usr/local/bin 下存在与 Apple Silicon 架构不符的 git 二进制，新起 daemon 的 PATH 若选中它则 exec 失败（本次求值容错继续、构建未受影响）。属机器卫生问题，建议清理该 intel 残留或在 shell PATH 前置 arm64 git；与本变更无关，留此记录。
