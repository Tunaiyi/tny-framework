# Apply Notes — consolidate-binary-conventions

本卷宗按任务组记录实施证据：探针结论回填 design.md，验收读数与破坏探针文案登记于此。

## 归档账目更正（任务 1.3，更正对象为已归档变更 pilot-binary-build-conventions 的卷宗文字，归档件原文不回改）

本册立项后的事后核验（openspec verify 转档为归档后审计，四路审计代理逐一开原文核对）确认 pilot 卷宗存在以下失实或欠缺，登记于此供后续册检索引用；已归档卷宗原文保持不动，历史文本继续由归档件与版本库共同承担。

1. **消费面枚举不完备**：pilot 的 proposal 第 31 行与 design 第 10 行断言"根 ext 五键的消费点经全仓 grep 复核为六个约定脚本共九行代码级引用"。该枚举漏掉支撑类消费点 `buildSrc/src/main/groovy/tny/convention/ModuleSetting.groovy:33`（`rootProject.ext.has('javaProjects')` 动态读取）。根 ext 五键删除后该读取恒假、三目兜底空集合，"发布线成员声明不发布即配置期报红"的自检自此静默失效。本册任务 4 修复并补测试。
2. **五路复验的"功能面全部 PASS"声称范围不实**：pilot apply-notes 第 71 行五路复验清单的复扫口径为 `*.gradle`，结构上覆盖不到 `*.groovy` 支撑类，上述失效守卫在其下存活。本册把扫描面教训改写为四类目录（根构建脚本、约定插件载体、模块构建文件、buildSrc 支撑类），并在任务 4.1/7.4 以扩展口径复核。
3. **任务 7.2 观察账登记缺位**：pilot 任务 7.2 勾选声称"归档后观察账登记两项挂后续册"，实际主规格与在途册均无落点，两项仅以两句话存续于归档 apply-notes 第 44、50 行且未指名承接册，不满足"移交归属的条目 MUST 指名承接册"。本册任务 9 指名承接并在册内执行销账。
4. **谓词数量记载与实际交付不符**：pilot tasks 4.1 与设计 D9 记载 `tny.projects` 扩展交付三个谓词方法，实际交付四个（另有 `isGameModule`，为 moduleProjects 的命名前缀判定）。属记载滞后，不影响行为；design 的 D4 谓词清单表述以现码为准。

**教训条款（登记供后续册引用）**：五键与旧 id 的消费面 grep 复核，扫描范围必须覆盖根构建脚本、约定插件载体（`buildSrc/src/main/groovy` 与注册块）、模块构建文件、buildSrc 支撑类（`*.groovy` 与 `*.java`）四类目录；仅按 `*.gradle` 口径的"零命中"不构成全仓复核。

## 组 1 规则开道

- 1.1 差量对号：两条 MODIFIED 以归档后主规格现文为底逐字扩写（载体句扩第三形态、长度句改单类计量），既有场景零丢失（容身之处需求 4 场景全保留加 1 新场景、扫读需求 3 场景全保留加 1 新场景）；ADDED 接线需求三场景齐全。`openspec validate consolidate-binary-conventions --strict` 零错误。
- 1.2 CLAUDE.md 第 26 行改写完成：归档前裸路径改为 `openspec/changes/archive/2026-10-07-pilot-binary-build-conventions`，并追加"完整接线规则的权威文本见 gradle-build-style 规格'约定插件接线规则按载体定'需求"指针；该句其余文字与另两句未动。

## 组 2 基线快照

（待记录：抓样环境、锚定提交、五样本行数、BOM 报红证据、耗时改造前三读。）
