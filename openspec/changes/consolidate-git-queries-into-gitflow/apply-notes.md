# Apply Notes

## 实施记录（2026-10-06）

十个任务一次会话内全部完成，外部行为零变化（本变更声明 skip_specs）。

### 方法面落地形态（D1）

GitFlow 新增四个远端查询方法：`remoteRefs`（守卫解析唯一位置，返回"引用名→提交号"
映射，含 `^{}` 解引用行）、`remoteRefNames`（键集合，存在性与前缀撞名判定）、
`remoteReleasedPatches`（吸收原 GitCli.remoteTagPatches 的补丁号枚举）、
`remoteBranchExists`（精确键查询）。原三个插件里的五处 ls-remote 解析全部改走上述
派生方法；release 的远端头对齐段（原 `ls-remote refs/heads/<current>` 加 find 过滤）
改走 `remoteRefs` 精确键取值，较原 find-contains 写法更严格（前缀撞名行不再可能命中）。

### 切换中发现并一并收编的调用点

任务清单按设计爆炸半径列了六处，实际切换时 release 插件还有一处头注未列的裸
ls-remote（releaseTag 的远端头对齐，tny.release.gradle 原 :150），同属"任何
ls-remote 解析只存在于 GitFlow 一处"的验收口径，一并切换。这正是机械化验收 grep
的价值：先写验收再数调用点的清单会漏，grep 零命中兜底。

### GitCli 收缩结果

删除 remoteTagPatches 后 GitCli 剩 65 行，仅含 forProject/run/require——纯通道
（执行与取回），类头注已改写为通道与语义分居的定型表述。

### 回归证据

- 编译与派生回归：`./gradlew -q :tny-game-common-lang:properties` 输出
  `version: 5.7.x-SNAPSHOT`，与切换前一致（1.3）。
- 机械化验收：`grep -l ls-remote` 对三个插件脚本零命中；`grep -rn
  remoteTagPatches buildSrc/` 零命中（2.4）。
- 端到端：`bash drill/model4-sim/e2e-gradle.sh` 十八断言全绿、FAIL 计数 0，
  覆盖 releaseCut 系列唯一撞名判定、集成顺序检查、下一补丁号校验、标签存证与
  向上合并冲突回滚等全部改走新查询面的链路（3.1）。
- 门禁矩阵复测：G1（非发布形态拒绝）=1、G3b（缺标签拒绝）=1、G4（合法维护分支
  放行）=0，与基线一致（3.2）。
- `openspec validate consolidate-git-queries-into-gitflow` 通过。

### 尺寸与纪律核对

切换后五个涉改文件最大 237 行（GitFlow），全部低于约定插件 250 行上限；
D4 白名单（动作强耦合的 rev-parse/log 簿记）在 integrate 头注与 autoMarkers 注释
两处注明，验收 grep 不误伤。
