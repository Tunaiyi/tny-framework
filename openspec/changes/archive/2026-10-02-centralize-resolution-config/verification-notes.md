# 验证记录

## 批次 1（settings 统一解析面）

- 基线六件于 1.1/1.2 现采（build 绿 1m2s）；迁移后 clean build 绿（1m5s），tasks/deps（python 节规范化复比，首轮 shell grep 的 DEPS-DIFF 为过滤对齐伪差）/m2/两 POM/buildEnvironment 六比对全零。
- 2.3 实测证伪 D1 原选模式：PREFER_SETTINGS 对工程私设仓仅忽略+告警（rc=0），不满足"配置期报红"的机器强制意图；升级为 FAIL_ON_PROJECT_REPOS 后复探——违例 rc=1 报红（文案指向 settings 偏好与违例构建文件），恢复绿，违例代码经备份还原文件级一致。design D1/tasks 措辞已按实施修正同步。

## 批次 2（标量属性面）

- centralValidationTimeoutMinutes：带键与 -P 置空两跑 centralCheck 均按分支语义报红（回退与生效路径都不改变拒绝行为）；dockerSocketCandidates：无键与显式单候选两跑 integrationTest 全绿（43s/33s，与基线 35-38s 同量级）。

## 批次 3 与终验

- 4.1：doc-gradle 快照判据统一为 endsWith('-SNAPSHOT') 并附防漂注释；publishToMavenLocal --rerun 成功、m2 全清单与基线零差异（判据仅影响上传路由不影响 POM 内容，且现版本两判据选择同一仓）。com/tny/game 下 5.7.x-SNAPSHOT 历史条目为组号迁移前遗留，基线与时同集，非本变更引入。
- 4.2 终验：clean build 绿（1m5s）；tasks/deps/m2/两 POM/buildEnvironment 六向比对全部 ZERO；centralCheck 分支拒绝、releaseCutAndTag -PdryRun 预览、checkPublishPrerequisites 实断言绿、空凭据双向（本链绿/共享仓报红）逐项通过。
- 结论：解析通道统一面（settings FAIL_ON_PROJECT_REPOS）+ 标量属性面 + doc-gradle 判据收敛全部落地，零差异红线保持。
