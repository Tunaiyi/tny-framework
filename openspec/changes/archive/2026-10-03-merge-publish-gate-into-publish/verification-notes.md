# 验证记录

## merge-publish-gate-into-publish（2026-10-03）

- 环境：JDK 显式钉 Corretto 21.0.12.1。
- 1.1 前置核对（D2）：两处共享仓发布任务判定谓词做签名比对（提取任务名精确匹配项与 startsWith/endsWith 判据逐条对照）**语义全等、主体逐字相同**，仅周边注释措辞不同——无语义差异，按 D2 可合流，未触发停止条款。基线六件存 baseline/（双 POM、m2 清单 679 文件、三处实断言、两份 publish -m 干跑、空凭据双向探针、tasks --all 3475 行）。
- 2.1 合并实施：tny.publish 吸收 tny.publish-gate 全部正文（第 17 至 150 行逐字追加，谓词合流为唯一局部 `publishesToSharedRepository`，属性断言块与门禁挂接块共用之），合并件 185 行（250 界线内）；文件头改为双层拦截（属性必在/一致性门禁）双段职责注释；tny.publish-gate.gradle 移入 /tmp/gradle-retired-quarantine/。
- **实施修正注记一（挂接谓词自排除改名字比较）**：原挂接行 `it != checkPublishPrerequisites` 的裸名引用会在首个任务添加事件实名化门禁任务；合并后 tny.publish 应用于 gradleProjects 线（第 51 行，早于模块自身 plugins{} 求值），实名化时 java-gradle-plugin 的 check 尚未注册、构建报红（doc-gradle 评估失败实测）。改为 `it.name != 'checkPublishPrerequisites'`，与对象排除语义全等且不触发实名化。
- **实施修正注记二（门禁任务依赖改名字串）**：`dependsOn tasks.named('check')` 的即时解析同样被 matching 活视图引发的连带实名化提前触发（堆栈实证：addLater→addPending→realize 链）；改为 `dependsOn 'check'` 把解析推迟到任务图构建期，语义全等。两处修正均为迁移引发（原两文件应用时点均在 java 插件之后故未暴露），未改变门禁断言逻辑与文案。
- 2.2 引用收编（**较变更清单扩面**，立项清单不完备、按"活代码零命中"判据现场定界补齐）：除清单所列根装配行、tny-game-bom plugins 行、tny.publications 两处注释外，另收编 tny.git.gradle 第 6 行边界指称、tny.central.gradle 第 65 行叙述指称、tny.java-module.gradle 第 6 行兄弟插件清单（删 tny.publish-gate 并补 tny.publish）、docs/release-process.md 第 57 行机制指称，共八处；grep `tny\.publish-gate` 于全部现行面零命中。
- 3.1 差异清单核对（原文）：`tasks --all` 与基线 diff 恰一行为允许差异——新增 `tny-game-doc-gradle:checkPublishPrerequisites`，其余 3474 行零差异；java 线代表（net、common-io）与 BOM 的 checkPublishPrerequisites 实断言绿，doc-gradle 新增实断言绿（其内联仓路由经门禁首次校验通过，dev 线快照形态合规）；net 的 publish -m 干跑任务图与基线一致（比对须剔除 daemon 的 "Invalid Java installation" 工具链扫描告警行——其出现位置随缓存状态漂移，属噪声非图差异）；doc-gradle 的 publish -m 干跑按批准收紧新增 check 跟随链与门禁挂接（compileTest*、test、validatePlugins、check、checkPublishPrerequisites 及 plugin marker 发布任务挂接，共 11 行），java 侧与基线一致；空凭据双向探针语义不变——本地链通过、共享仓任务以同一消息 fail-fast（"required publishing property 'RELEASE_REPOSITORY_URL' is empty or missing."）。
- 3.2：`./gradlew clean build` 全绿（339 执行，日志存 baseline/cleanbuild-green-evidence.log）；publishToMavenLocal 后 m2 清单 679 文件与基线零差异；net 与 BOM 两份生成 POM 逐字节零差异。
