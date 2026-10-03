# Tasks

## 1. 前置确认
- [ ] 1.1 确认 rename-subprojects-baseline-plugin 已实施（tny.dependency-management.gradle 在位）；抓现状行为样本存 baseline/：`./gradlew :tny-game-net:jar` 产物文件名与 POM version 字段、`./gradlew releaseCutAndTag -PdryRun -PreleaseVersion=9.9.9 --console=plain` 输出、`./gradlew centralCheck` 报红文案。

## 2. 扩展落地
- [ ] 2.1 新增 tny/convention/GitInfo.groovy（动态字段与方法清单见 proposal；类头注明"根工程 git 派生契约，写入方唯一 tny.git，消费方按类型拉取"）；tny.git 重写为创建并填充 `gitInfo` 扩展、删除根 ext 派生字典（身份与成员键保留），推导闭包逐行迁移为方法；`./gradlew projects -q` 通过。
- [ ] 2.2 四处消费切换：tny.release（getByType + git.xxx → gitInfo.xxx）、tny.publications、tny.dependency-management、tny.central 各改类型化读取；grep 活代码确认 tny.git 原 ext 派生键（branchName/projectVersion/commitId/grgiter 等经 rootProject.ext 或裸属性形态）零残留；评估通过。

## 3. 零差异验收
- [ ] 3.1 行为样本比对：jar 文件名与 POM version 字段、releaseCutAndTag -PdryRun 输出、centralCheck 报红文案与 1.1 基线逐项一致（Grgit 相关文案不得变化）；组号违例探针红/绿如常。
- [ ] 3.2 全量：`./gradlew clean build` 绿；tasks --all 与 m2 清单对上一归档基线零差异；全部结论记 verification-notes.md。
