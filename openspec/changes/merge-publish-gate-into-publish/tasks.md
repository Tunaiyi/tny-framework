# Tasks

## 1. 前置核对与基线
- [ ] 1.1 diff tny.publish 与 tny.publish-gate 的共享仓任务判定段（D2 前置）：确认逐字一致或列出语义差异（有差异即停回用户处）；抓基线：三处 checkPublishPrerequisites 实断言、`./gradlew :tny-game-net:publish -m` 与 `./gradlew :tny-game-doc-gradle:publish -m` 干跑清单、空凭据双向探针、tasks --all 与 m2/两 POM 存 baseline/。

## 2. 合并实施
- [ ] 2.1 tny.publish 吸收 tny.publish-gate 全部内容（谓词合流为局部 def、校验闭包/任务注册/挂接逐行迁入、文件头改为"错误发布拦截"双段职责注释）；tny.publish-gate.gradle 移入 /tmp/gradle-retired-quarantine/；`./gradlew projects -q` 通过。
- [ ] 2.2 引用收编：build.gradle javaProjects 行与 tny-game-bom plugins{} 删 tny.publish-gate 行；tny.publications 两处注释指称更新；grep 活代码 tny.publish-gate 零命中；评估通过。

## 3. 验收
- [ ] 3.1 差异清单核对：`./gradlew tasks --all` 与基线 diff 应仅含 tny-game-doc-gradle 门禁行新增（逐行定性，超出即停回退）；java 线与 BOM 的 checkPublishPrerequisites 实断言绿、doc-gradle 新增实断言绿；两 publish -m 干跑挂接对 doc-gradle 生效、java 侧与基线一致；空凭据双向探针语义不变。
- [ ] 3.2 `./gradlew clean build` 绿；m2 与两份 POM 零差异；全部结论（含允许差异清单原文）记 verification-notes.md。
