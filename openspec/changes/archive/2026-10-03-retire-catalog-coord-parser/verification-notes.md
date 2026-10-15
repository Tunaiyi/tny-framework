# 验证记录

- 1.1 前置验证：动态属性取名 `libs."commonsIo".get()` 在 buildSrc 官方访问器上可用（rc=0，commons-io 以 2.8.0 正常供版），D1 回退分支未启用；验证现场已恢复后正式实施。
- 2.1 tny.subprojects-baseline 新增三行本地方法 managedCoordinate(别名)：逐段公开属性拼"组:名:版本"（不依赖 toString），20 项列表循环全部改走该方法；注释同步。
- 2.2 tny/catalog 目录（含 CatalogCoord.groovy，约 80 行自写解析）移入 /tmp/gradle-retired-quarantine/tny-catalog-dir；grep 复核 buildSrc 与全部构建文件无 CatalogCoord/tny.catalog 残留；tny/convention/BenchSuite.groovy 仍在用、保留。
- 3.1 零差异：四模块 dependencies 报表对 centralize 归档基线节规范化 ZERO；publishToMavenLocal 后 BOM/net 两份 POM 与 m2 全清单零差异；clean build 全绿（1m12s，无偶红）。
- 备份 /tmp/rccp-baseline.bak 留存待系统清理。

## 用户定稿调整（"还是使用 libs"）

- 集中声明列表由"别名字符串列表+循环"改为 20 行显式 `dependency managedCoordinate(libs.<别名>.get())`：每个条目直接书写官方访问器（IDE 补全、逐行可读），managedCoordinate 退为接收依赖对象按公开属性拼字符串的三行本地方法。文件 55→67 行，行数守恒让位于用户明确要的表达形态（记录于此，与 condense 变更的收敛方向不冲突——condense 消灭的是 coord() 全限定调用样板，本轮进一步消灭别名字符串）。
- 复验：四模块依赖解析报告对基线节规范化 ZERO；BOM/net 两份 POM 零差异；projects 评估绿。
- 命名定稿：辅助方法按项目既有词汇（"坐标"= 组:名:版本）命名为 coordinateString(provider)，调用点简化为 dependency coordinateString(libs.别名)（Provider 直接传入、内部取值）；managedCoordinate 全部清除。复验：评估绿、四报表/两 POM 对基线零差异。
- 二次改名（用户指出 coordinateString 与依赖管理插件既有 coordinates 概念撞名）：经选项对齐定名 dependencyNotation——用 Gradle 官方文档对"组:名:版本"声明形式的标准称谓 notation。复验：评估绿、四报表零差异、两份 POM 零差异。
