# Tasks

## 1. 根 CLAUDE.md 规则节收缩为指针

- [x] 1.1 把根 `CLAUDE.md` 的"Gradle 构建脚本规则"一节改写为指针形态：保留适用范围句、权威出处指向（归档前为 `openspec/changes/add-gradle-build-style/specs/gradle-build-style/spec.md`，归档生效后为 `openspec/specs/gradle-build-style/spec.md`，文中写明这一切换时机）、三句执行要点（配置以声明式语句为主；程序性行为只允许任务动作块与 `gradle/` 编排脚本两类位置；存量违例按触碰即改渐进修正），删除九条细则正文，避免两处文本漂移。
- [x] 1.2 验证：将指针节与规格差量的八条 Requirement 逐条对照，确认指针节残留的每一句具体规则都能在规格中找到对应需求编号；对不上的句子要么删除，要么先补进规格再保留。

## 2. 违例形态基线盘点（供后续变更判定"触碰即改"范围）

- [x] 2.1 在仓库全部 `.gradle` 文件上运行 grep 清单并记录现状基线：旧式任务声明（行首 `task ` 加名称）、spread 批量赋值（`]*.options` 类形态）、以分号结尾的语句行、`settings.gradle` 中注释掉的 `include` 行；结果写入变更目录 `baseline.md`，逐项列出文件、行号与形态类别，并注明"基线只作记录，本变更不修复"。验证：逐类数量与抽查行号一致。
- [x] 2.2 验证：`git diff --name-only` 与 `git status --porcelain` 确认本变更未触碰任何 `.gradle` 文件（改动集合只含 openspec 工件与 `CLAUDE.md`），即本变更自身零新增违例。

## 3. 规格校验与收尾

- [x] 3.1 运行 `openspec validate add-gradle-build-style --strict` 并通过。验证：命令零报错退出，八条 Requirement 各含正常与违例两个 Scenario，Purpose 满足长度下限。
- [x] 3.2 运行 `./gradlew help` 确认构建健康。验证：输出与变更前一致。说明：本变更不涉及公共 API 或协议行为，也无人力可影响的运行模块，仓库任务规则中"每任务组末尾跑受影响模块 :test"在此以构建健康冒烟替代，不存在可运行的目标模块测试。
