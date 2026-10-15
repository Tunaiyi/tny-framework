# Design

## Context
动机见 proposal.md - Why。改名目标 tny.dependency-management 由用户定名（覆盖此前"subprojects-defaults"候选）。

## Goals / Non-Goals
Goals：id 见名知义；行为零变化。Non-Goals：不拆文件（两行组号版本派生与两个核心插件应用随迁不另立插件——拆分会引入第二个文件与新的应用顺序，收益不成比例）；不改历史归档措辞。

## Decisions
**D1 命名取舍如实记录。** 新名突出主体（依赖管理声明占文件九成体量），两行派生与 idea/maven-publish 应用为附属；备选"拆出 tny.project-identity"否决——成员派生与身份在根 ext（主账本第九条需求的通道分工），再拆违反 consolidate 时代确立的"通道分工"纪律。
**D2 引用面以 grep 定界。** tny.subprojects-baseline 在活代码的指称共 4 处（根装配线行、自身头、plugin-module 头、java-module 头），逐处替换后 grep 清零属验收项；历史归档不改。

## Risks / Trade-offs
- [id 改名致 IDE 已打开标签失效] → 一次性成本，无构建影响。
- [名字未覆盖两行派生的语义缝隙] → 文件头注释第一行即声明全职责清单，见名疑义有注释兜底。

## Migration Plan
单批次：git mv 等价（移动文件）+ 四处引用替换 + 评估与零差异验收。回滚还原文件名与四处引用。
