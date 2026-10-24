# 收口记录（rename-branch-terminology）

## 基线与结果

- 改动前命中 53 处（`terminology-hits-before.txt`），改动后除规格需求标题引用
  （"发布容器与标签一经创建不可移动"，账本原文保留）外零残留；
  `tny.integration-test.gradle` 与 `tny.publish.gradle` 的"容器"命中为 Docker 容器与
  Gradle TaskContainer 他义，逐条核对不属于替换范围。
- 文案回归 diff（`terminology-baseline.txt` 对 `terminology-after.txt`）：差异全部为
  名词替换与构建耗时行，句式、判定、顺序零漂移。
- 规格账本映射行已立于 `docs/release-process.md` 角色总表前（版本开发分支=开发线分支、
  版本发布分支=发布分支，并登记旧称冻结线/容器）。

## 待用户裁决的后续项（本册未动）

规格主账本 `release-versioning` 第七条需求**标题**仍叫"发布容器与标签一经创建不可移动"。
若要求账本用语与本册口径一致，需一册 RENAMED 差量（纯改名、行为零变化）；不改亦自洽
（文档已有映射行），由用户定夺。

## 术语替换的机制性说明

替换采用词组白名单两轮脚本（先保护规格标题引用，再组合词后处理独立词），并做叠词与
句式顺滑检查；未使用全局盲替换。
