# 验证记录

## 基线与终验（simplify-demo-isolation-wiring）

- 1.1 基线：integrationTest dry-run 150 行有序清单、tny-game-net:test dry-run 68 行、IT 全绿 37s（存 baseline/）。
- 2.1 实施（按写码前修正注记的定稿形态）：tny.demo-app 三行自报登记；tny.it-demo-isolation 删除 projectsEvaluated 遍历与跨工程 jar 任务引用，dependsOn 改 configurations.integrationRuntimeClasspath，撮合/校验/拼装移入 doFirst（执行期），交集顺序按本模块直接依赖声明序，发布任务禁用段原样。
- 3.1 防线探针四判据：摘标记后 projects 绿（校验已移执行期）、dry-run 绿、实跑在执行前报红（文案含"交集为空/tnyDemoBlueprints/两个可能成因"）、恢复后绿且文件级一致。
- 3.2 零差异：IT 与 net 两份 dry-run 任务图与基线逐行零差异（证实配置携带依赖无新增边）；IT --rerun 37s 绿（demo 子进程用例端到端证明隔离字符串正确）；tasks --all 对 centralize 归档基线零差异；clean build 全绿 1m18s。
- 备份 /tmp/demo-app.bak、/tmp/it-iso.bak、/tmp/demo-mk.bak 留系统清理。
