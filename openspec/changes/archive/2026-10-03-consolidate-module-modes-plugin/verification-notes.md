# 验证记录

## 合法性探针（任务 1.1）

- afterEvaluate 窗口内 evaluationDependsOn 合法：`:tny-game-net:dependencies` 报表路径下对全部工程依赖边强制评估成功（PROBE-OK 七条、executed=true、无异常）。design D2 主路径成立，回退分支未启用。

## 实施（任务 2.1-2.3）

- 新增 tny.convention.ModuleModes（Mode 枚举 + enableApp/enableUnpublished + 静态 enabled 查询，未评估目标查询即抛错防误用）与 tny.module-modes 插件；三模块声明改写（demo=enableApp，bench/IT=enableUnpublished）；tny.project-checks 合同改依赖边就地核对；tny.demo-isolation doFirst 改扩展直查（其报错前缀 tny.it-demo-isolation→tny.demo-isolation 为文案变化点）；tny.demo-app、tny.unpublished 两标记与 tnyDemoBlueprints/tnyUnpublishedProjects 两键废除。

## 回归与零差异（任务 3.1-3.3）

- 探针A：报表路径非法依赖恢复报红（zero-enumeration 登记版的静默漏检已修复），文案列双方路径与声明处；恢复绿。
- 探针B：摘 enableApp 后 integrationTest 执行前报红"受控隔离目标交集为空"；恢复绿。
- 探针C：摘 enableUnpublished 后同一非法样本放行、装回即报红——合同严格跟随声明；三文件级比对一致。
- 零差异：clean build 绿 1m24s；tasks --all/四报表/m2/BOM与net POM 对 zero-enumeration 基线全部 ZERO；bench publish 计数 2 与基线一致（禁用面等价）；integrationTest 38s 绿。
- 清扫：两旧键与两旧插件 id 全仓零引用（ModuleModes 类头两条"取代说明"注释为有意保留的历史指认）；settings.gradle 约定锚点行同步改指 tny.module-modes。
