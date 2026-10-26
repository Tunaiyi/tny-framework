# Proposal

## Why

tny.subprojects-baseline 的名字（"子工程基线"）没有说出它的主体内容：全部子工程的依赖管理声明——八个 BOM 导入与二十项托管版本表（用户定名 tny.dependency-management，指向其内容重心）。插件改名是纯标识变更，改后"看 id 知职责"。

## What Changes

- 文件 tny.subprojects-baseline.gradle 改名 tny.dependency-management.gradle（插件 id 随之）；根构建脚本装配线引入行、插件自身与 tny.plugin-module / tny.java-module 文件头对该插件的指称同步更新；其"形态约定/边界"注释中"基线"措辞改为"依赖管理"。
- 文件内容除注释措辞外零改动（含两行组号版本派生与 idea/maven-publish 应用——名字聚焦主体，附属职责随文件走，已在 design 记录该命名取舍）。
- skip_specs：插件 id 属实现标识，不涉及任何规格条文与外部行为。

## Capabilities

### New Capabilities / Modified Capabilities

无。

## Impact

- **受影响文件**：改名主体 1 个、引用 4 处（根脚本 1 行、自身头注释、plugin-module 与 java-module 头注释各 1 处）。
- **验收**：评估通过、clean build 全绿、tasks --all 对实施前自抓基线零差异（id 不出现在任何任务名与产物面，预期零差异）。历史归档与 verification-notes 中的旧名指称不回改（历史记录原则）。
