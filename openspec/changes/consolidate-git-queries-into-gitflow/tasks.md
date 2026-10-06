# Tasks

## 1. GitFlow 方法面

- [x] 1.1 GitFlow 新增 remoteRefs（守卫解析唯一位置）、remoteRefNames、remoteReleasedPatches、remoteBranchExists；解析守卫与教训注释集中于 remoteRefs 一处
- [x] 1.2 GitCli 删除 remoteTagPatches（解析并入 GitFlow），类头注改写为"纯通道：执行与取回"；通道分界句更新（查询一律经 GitFlow）
- [x] 1.3 验证：`./gradlew -q :tny-game-common-lang:properties` 编译与派生回归 5.7.x-SNAPSHOT

## 2. 调用点切换

- [x] 2.1 tny.release.gradle：remoteRefNames 局部闭包删除改 gitFlow.remoteRefNames；assertNextPatchNumber 改 gitFlow.remoteReleasedPatches；头部通道句更新
- [x] 2.2 tny.integrate.gradle：remoteRefHas/remoteReleaseLines/remoteDevLines 三闭包改派生（remoteBranchExists/remoteRefNames）；头部通道句更新；autoMarkers 与 rev-parse 簿记按 D4 保留并注明
- [x] 2.3 tny.publish.gate.gradle：标签存证 refMap 改 gitFlow.remoteRefs 过滤、下一补丁号段改 remoteReleasedPatches；等价性注记（记忆化、拒绝文案、fail-closed 不动）
- [x] 2.4 验证：`grep -l ls-remote buildSrc/src/main/groovy/tny.{release,integrate,publish.gate}.gradle` 零命中；`grep -rn 'GitCli\.remoteTagPatches' buildSrc/` 零命中

## 3. 回归与收尾

- [x] 3.1 验证：`bash drill/model4-sim/e2e-gradle.sh` 十八断言全绿（退出码 0）——覆盖 releaseCut 撞名判定、维护分支唯一性、集成顺序、向上合并冲突回滚等全部改走新查询面的链路
- [x] 3.2 验证：门禁矩阵三例复测（标签存证与下一补丁号为本次直接受击面）
- [x] 3.3 收尾：release-process.md 快速通道节通道句微调一句（查询经 GitFlow、执行经 GitCli 门面）；`openspec validate` 通过；apply-notes 记录回归
