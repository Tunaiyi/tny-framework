# Tasks

## 1. GitFlow 换心

- [ ] 1.1 GitFlow 构造改为 `GitFlow(GitCli cli, Map registry, String releaseVersion)`，六个派生值以 CLI 求值（LC_ALL=C 经 GitCli 统一注入；porcelain 显式 `--porcelain=1`）；删除 grgiter 字段；新增查询方法 branchNames/tagNames/statusEntries/trackingRemote/describeNearestTag，porcelain 状态码到原 grgit 桶位的等价映射表写入注释
- [ ] 1.2 GitCli.run 注入 `LC_ALL=C` 环境（一行 env 修改，头注补 locale 教训引用：零差异抓样口径同源）
- [ ] 1.3 验证：`./gradlew -q :tasks --group release` 编译通过；`./gradlew -q properties` 在 5.7.x 上 version 仍为 5.7.x-SNAPSHOT（派生值回归主锚）

## 2. 消费点切换

- [ ] 2.1 tny.git.gradle：构造调用改传 `GitCli.forProject(project)`；删除 linked-worktree 守卫段（gitdir 判据与其报错文案），头注改写为"布局无限制，发布快速通道用独立完整克隆属运营纪律"
- [ ] 2.2 tny.release.gradle：trackedDirty 改 statusEntries 口径、branch/tag 存在性检查改 branchNames/tagNames、remoteRefNames 改 CLI ls-remote（经 GitCli，heads/tags 参数保留）、existing 标签查找保留 tag+解引用语义改 tagNames＋rev-parse；grgiter 引用清零
- [ ] 2.3 tny.integrate.gradle：trackedDirty、branchNames（lowerLines 枚举）等 grgiter 消费点同法替换；grgiter 引用清零
- [ ] 2.4 验证：`grep -rn 'grgiter\|grgit\|Grgit\|ajoberstar' buildSrc/src/main/groovy build.gradle` 零命中（注释中历史沿革引用除外，逐条判断）

## 3. 依赖与归因清理

- [ ] 3.1 build.gradle 删除 grgit 的 plugins 声明行与 apply 行；gradle 版本目录（如声明在该处）清除 grgit 条目；`./gradlew -q :tasks` 编译通过
- [ ] 3.2 publish.yml/snapshot-mirror.yml 中"门禁经 grgit 读分支名"归因注释更正为 detached HEAD 通用行为（checkout -B 兜底保留），release-process.md 恢复条目与执行环境节按 D4 改写

## 4. 回归

- [ ] 4.1 验证：`bash drill/model4-sim/e2e-gradle.sh` 十八断言全绿（退出码 0）
- [ ] 4.2 验证：门禁矩阵三例（main 拒绝文案、release 正号仅剩缺标签、祖父线放行）在演练沙箱复测
- [ ] 4.3 验证：**worktree 冒烟**——在演练沙箱仓库 `git worktree add ../wt-verify` 后该目录内 `./gradlew -q help` 成功、`git rev-parse --abbrev-ref HEAD` 读出分支名；脏检查五态样本（干净/跟踪修改/staged 新增/未跟踪/删除）在临时 worktree 内用 releaseCut dryRun 的告警行为逐项比对映射表
- [ ] 4.4 收尾：`openspec validate retire-grgit-channel` 通过；apply-notes 记录回归与实测行数
