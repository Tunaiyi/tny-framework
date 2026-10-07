# Tasks

## 1. 门面上线

- [x] 1.1 GitCli 新增 `forProject(project)` 工厂与实例方法 run/require/remoteTagPatches，原静态实现收敛为内部复用；头注记录 D1 否决依据（两类分界）与本变更门面化沿革（引用归档 D6 与 migrate-git-calls-to-grgit 事故链）
- [x] 1.2 验证：`./gradlew -q :tasks --group release` 编译通过（构建脚本可加载）

## 2. 调用方切换

- [x] 2.1 tny.release.gradle：删除三行模板改 `def git = GitCli.forProject(project)`，全量调用点改 git.run/git.require，两处 GitCli.remoteTagPatches 改实例调用；文件头通道注释按 D4 更新
- [x] 2.2 tny.integrate.gradle：同 2.1 处理（含 autoMarkers、remoteRefHas、remoteDevLines、retireGuard 内调用）；文件头注释更新
- [x] 2.3 tny.publish.gradle：release 标签存证段删 providers.exec 版 runGit 与远端名推定局部闭包，改 `GitCli.forProject(rootProject)` 实例与 `gitFlow.resolveRemoteName(...)`；下一补丁号段（104 行内联解析）改门面实例；等价性三要素核对结论记入行旁注释（非零退出即空输出、记忆化与 fail-closed 文案不动）；文件头与白名单注释按 D4 更新
- [x] 2.4 验证：`grep -rn 'def runGit\|def requireGit\|gitExe = (project\|providers.exec' buildSrc/src/main/groovy/*.gradle` 除 GitCli.groovy 外零命中；`grep -c 'runGit(\[' buildSrc` 零残留

## 3. 长度红线与回归

- [x] 3.1 条件步骤：若 tny.publish.gradle 在删重后仍超 250 行，按 D3 拆出 tny.publish.gate.gradle 姊妹插件（仅移动门禁段与装配线加一行引入，不改语义）；不超则跳过并在 apply-notes 记录实测行数
- [x] 3.2 验证：`bash drill/model4-sim/e2e-gradle.sh` 十八断言全绿（退出码 0）
- [x] 3.3 验证：门禁矩阵复测三例（main 分支拒绝文案原样、release/5.8.x 错号期望号报错、祖父线 5.7.x 快照放行），在第十二轮沙箱仓库（或新建镜像）上执行，结果记 apply-notes
- [x] 3.4 验证：`openspec validate consolidate-git-cli-channel` 通过（skip_specs 形态下无 delta 校验项）
