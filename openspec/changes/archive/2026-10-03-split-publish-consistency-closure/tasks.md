# Tasks

## 1. 基线与探针 before 抓样

- [x] 1.1 抓基线存本变更目录 baseline/（捕获口径：JDK 钉 Corretto 21、正常 locale、不注入 JAVA_TOOL_OPTIONS、before 与 after 同一 daemon 环境；本步样件均为控制台输出转存，不存在先转存后运行的暴露，实施中若新增转存某任务输出文件的样件，必须先运行生成该文件的任务再转存，否则抓到的是空文件）：`./gradlew :tny-game-net:checkPublishPrerequisites :tny-game-bom:checkPublishPrerequisites :tny-game-doc-gradle:checkPublishPrerequisites` 三形态实断言绿记录；`./gradlew :tny-game-net:publish -m` 干跑任务图；空凭据双向探针（`-PRELEASE_REPOSITORY_URL= -PSNAPSHOT_REPOSITORY_URL= -PNEXUS_USERNAME= -PNEXUS_PASSWORD=` 时共享仓任务 fail-fast 消息与本地链直通）；错登三探针按 design D3 各存全文红文案（版本形态红、临时发布分支标签存证红、黑名单占号红；临时分支用后即删）；`./gradlew tasks --all` 剔噪清单；`wc -l` 拆分前行数与 `validatePublishConsistency` 闭包行界（51 至 169 行）记录。

## 2. 拆分实施

- [x] 2.1 `tny.publish.gradle` 按 design D1/D2 把 `validatePublishConsistency` 拆为 `validateBranchVersionShape`、`validateRepositoryRouting`、`validateLegacyVersionReuse`、`validateReleaseTagAttested` 四个同文件辅助闭包（`runGit` 与远端名推定留在存证段体内），主闭包按原顺序收集 problems 并以原 hint 三元式合流抛出；四类消息文案与追加顺序逐字承前，各段来由注释随段迁移零删减；文件头职责注释补门禁内部结构一句。验证：`./gradlew projects -q` 通过；`wc -l` 文件 ≤250 且四段各 ≤40；diff 校验消息行文本相对基线件零改动。若任何文案需改动才能落地，即停回用户裁决（D2 红线）。

## 3. 零差异验收

- [x] 3.1 复跑比对：三形态实断言、`publish -m` 干跑、空凭据双向探针、错登三探针（含临时分支建删）与 1.1 基线逐字/逐项一致，problems 顺序一致；`tasks --all` 剔噪清单与基线逐行零差异；`./gradlew clean build` 全绿——偶红按 fix-ci-unit-flakes 登记标准处置留痕。全部结论记 verification-notes.md。
