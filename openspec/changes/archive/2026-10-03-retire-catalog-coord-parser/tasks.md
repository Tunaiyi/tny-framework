# Tasks

## 1. 前置验证（设计 D1，备份-恢复纪律）

- [x] 1.1 备份 tny.subprojects-baseline.gradle；仅把 commonsIo 一个条目改为经官方访问器取依赖信息（动态属性 `libs."$alias"` 与 `VersionCatalog.findDependency(alias)` 两写法先各试一种），运行 `./gradlew :tny-game-common-io:dependencies --configuration runtimeClasspath --console=plain` 确认 commons-io 仍以 2.8.0 被供版（与现行为一致）；验证结论（哪种写法可用）记入 verification-notes.md，保留备份待实施完成后一并清理。

## 2. 全量替换与删除

- [x] 2.1 按 1.1 确认的写法，在 tny.subprojects-baseline.gradle 内加三行本地辅助方法（按 D2 用公开属性逐段拼"组:名:版本"，不用 toString），列表循环改调该辅助方法；更新该段注释（删除对 CatalogCoord 的指涉）；`./gradlew projects -q` 评估通过。
- [x] 2.2 将 buildSrc/src/main/groovy/tny/catalog/ 整目录移入 /tmp/gradle-retired-quarantine/（CatalogCoord.groovy 及空目录）；grep 确认全仓无 CatalogCoord 与 tny.catalog 残留引用；`./gradlew projects -q` 通过。

## 3. 零差异验收

- [x] 3.1 四模块（tny-game-net/tny-game-doc/tny-game-namnspace/tny-game-expr-graaljs）dependencies 报表对 openspec/changes/archive/2026-10-02-centralize-resolution-config/baseline/（节规范化比对）零差异；`./gradlew publishToMavenLocal` 后 BOM 与 net 两份 POM 及 m2 构件清单零差异；`./gradlew clean build` 全绿；遇案卷在册的偶红按单任务复跑取证纪律处理；三项结论记入 verification-notes.md。
