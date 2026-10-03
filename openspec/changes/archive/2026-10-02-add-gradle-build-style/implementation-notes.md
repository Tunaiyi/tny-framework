# 实施记录（apply 阶段结果摘要，2026-10-02）

按任务组记录，供 verify 与 archive 阶段引用。

## 组 1：根 CLAUDE.md 规则节收缩为指针

`CLAUDE.md` 的"Gradle 构建脚本规则"一节已改为：适用范围句、权威出处指向（含归档前后两个路径与切换时机说明）、三句执行要点（声明式配置、程序性行为两类容身之处、触碰即改）。与规格八条 Requirement 逐条对照通过：三句要点分别落在需求一与需求八之内，指针节不含规格之外的细则正文。

## 组 2：违例形态基线盘点

盘点结果见同目录 `baseline.md`：旧式任务声明 6 处、spread 批量赋值 3 处、分号结尾真实代码 9 处（另有 2 处误命中已说明理由）、`settings.gradle` 注释 include 9 条；`obsolete/` 目录按项目规则排除。补充记录了两类机械清单之外的现状（dependencyManagement 字面量版本、 publications.gradle 超长度界线），均只作登记，本变更未修复任何 `.gradle` 文件。零新增违例验证通过：`git status --porcelain` 显示改动仅 `CLAUDE.md` 与新增 openspec 目录。

## 补充：openspec/config.yaml 挂载规则钩子（用户另行指示，2026-10-02）

verify 之后用户询问是否要把规则接入 `openspec/config.yaml`，结论为需要并已执行两处修改：context 一节新增"构建脚本形态"条目（声明 `.gradle` 文件受能力 `gradle-build-style` 约束，写明归档前后的双路径权威出处与"改构建文件先读规格"的动作要求）；operations.apply.guidance 新增第三条（按需求"存量违例按触碰即改渐进修正"执行：改动区域内顺手修正，区域外一律不动，全仓清扫另立变更）。验证：`config.yaml` 经 YAML 解析通过；`openspec instructions apply` 读回的 context 含 gradle-build-style、guidance 含触碰即改，两条钩子对后续变更的生成与实施阶段均可见。归档时需同步把 context 与 `CLAUDE.md` 指针节的"归档之前"路径表述收敛为最终路径（对应 verify 报告建议二）。

## 组 3：规格校验与收尾

`openspec validate add-gradle-build-style --strict` 零报错通过。`./gradlew help`（JAVA_HOME 指向本机 Corretto 21）BUILD SUCCESSFUL，用时 5 秒；输出的 Gradle 9.0 弃用警告来自存量旧式任务声明，与基线记录吻合，非本变更引入。本变更无受影响运行模块，无模块测试可跑，以构建冒烟替代（理由已在 tasks.md 写明）。
