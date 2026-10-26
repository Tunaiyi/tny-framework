# 实施期验证记录（按任务组追加）

## 组 1（历史清单与黑名单文件）

- 1.1 三方对账完成：制品库匿名 browse REST 取证、21 条 git tag、2 条 release 分支，结论与逐条核对表见本目录 reconciliation.md。发现制品库比 tag 账本少 14 个早期版本（差异方向"有标签无制品"，判定为服务器重建丢档），黑名单按两侧并集共 23 项保守收录。
- 1.2 `gradle/released-legacy.txt` 已创建，23 个裸号，含版权头、用途注释、维护规则（只增不减）。

## 组 2（分支形态白名单门禁）

- 环境事实：本机 shell 默认 JDK 为 25（类文件 69），Gradle 8.5 不兼容；本变更全部构建验证以 `JAVA_HOME=corretto-21.0.12.1` 执行。
- 2.1 验证通过：临时分支 `task-test-branch` 上执行 `./gradlew :tny-game-common-lang:publish -x test`，门禁拒绝，错误信息含当前分支名与两种合法形态说明；临时分支已删除。
- 2.2 验证通过：`5.7.x` 分支 `properties` 输出 `version: 5.7.x-SNAPSHOT`；同分支执行门禁任务 `checkPublishPrerequisites -x test` 零拒绝记录（开发线通过白名单与快照形态校验）。
- 组测试：`./gradlew :tny-game-common-lang:test` 共 250 例，唯一失败为 `CiCircuitProbeTest.circuitProbe()`——该失败是并行变更 fix-ci-unit-flakes 故意放置的自证空弹（源码注释明确"请勿修复、取证后回滚"），与本变更无关；本变更未引入测试回归。后续各组引用同一事实时不再重复展开。

## 组 3（同号黑名单与四段号拒绝）

- 3.1 验证通过：临时分支 `3.4.1.release` 上门禁同时报告"已以 -RELEASE 后缀形态发布并记录…禁止以裸号形态复用同号"（裸号派生生效后，该分支上仅黑名单与标签缺失两条拒绝，形态误报消失）。
- 3.2 验证通过：临时分支 `5.7.8.1.release` 被拒并给出"不符合三段号加 .release 的命名形态"专门文案。
- 组 3 后重跑 `:tny-game-common-lang:test` 无回归（结果见组 5 干净重跑记录）。
- 事故记录（如实）：首次 V3.1 试跑误用 `git switch -c 5.5.0.release`——该分支在本地已存在导致静默失败，且清理命令把已存在的历史本地分支 `5.5.0.release` 误删；已从远端跟踪引用 `github/5.5.0.release` 原样恢复（提交 `5cd3ab2a` 一致，跟踪关系重建），远端从未受影响。教训：门禁验证的临时分支必须先确认名称未被占用。

## 组 4（发布标签前置存证）

- 4.1 验证通过：`gitTag` 闭包改为宽 glob（`v[0-9]*`）加严格正则过滤（`v\d+\.\d+\.\d+`）。探针证据：当前 HEAD（祖先链仅含旧代 `v3.4.1-RELEASE`）返回 null；创建本地附注标签 `v9.9.9` 后返回该标签；测试标签已删除。CLI 实测发现 glob 中 `*` 可吞掉 `-RELEASE` 尾巴（`v3.4.1-RELEASE` 命中 `v[0-9]*.[0-9]*.[0-9]*`），故闭包内注释记录了二次过滤的原因与"过渡期最近标签为旧代时返回 null"的已知局限。
- 4.2 代码落地并验证拒绝链：远端读取走 `git ls-remote`（附注标签含 `^{}` 解引用行、轻量标签没有；指向比对用解引用后 SHA）；结果按"分支+提交+标签"在根工程记忆化，一次构建只查一次远端。场景证据：临时 `5.7.8.release`（远端无该标签）→"在远端 'github' 不存在"拒绝；远端仅有旧代 `v3.4.1-RELEASE` 时裸号 `v3.4.1` 仍判缺（精确名匹配成立）。环境适配：本机 PATH 上 Homebrew git 为 x86_64、Gradle 守护进程为 aarch64，exec 报 Bad CPU type，门禁因此提供 `gitExe` 属性覆盖（默认 PATH 上的 git，验证以 `-PgitExe=/usr/bin/git` 执行）；曾尝试 JGit 路线，子工程脚本类加载器解析不到 jgit 类，回退 CLI。"补标签后校验通过"的正向路径需要向远端推送一次临时测试标签（外向动作），已留待用户确认，与任务 7.1 一并执行。
- 4.3 验证通过：`:tny-game-common-lang:test` 与 `:tny-game-net:test` 强制重跑（`--rerun-tasks`）249 例与 167 例全部通过，零失败。首次组合跑出现的单例失败与用例数波动为陈旧 up-to-date 结果与并行串扰混叠，干净重跑后消失；另注：此前一直失败的 `CiCircuitProbeTest` 已不在本轮结果中，推断并行变更 fix-ci-unit-flakes 已按其 2.3 回滚自证空弹。

## 组 5（GA 派生切换裸号）

- 5.1 验证通过：临时分支 `5.7.8.release` 上 `properties` 输出 `version: 5.7.8`（裸号）；`5.7.x` 上仍为 `version: 5.7.x-SNAPSHOT`（快照形态不变）。`RELEASE_PACK_SUFFIX` 派生引用已移除，常量保留处已注释其历史用途。
- 5.2 验证通过：组 3、组 4 场景输出的全部拒绝文案均为新规则措辞（裸号形态、分支形态、远端存证），无"应当以 -RELEASE 结尾"类旧表述残留。
- 5.3 验证通过：同组 4 记录——两模块 `--rerun-tasks` 全绿（249/167，零失败）。

## 组 6（流程文档与消费契约）

- 6.1 交付：`docs/release-process.md` 覆盖开线、常规发布、hotfix、线退役四则流程，含门禁五重校验说明与两项 Nexus 运维前置条件；逐条对照 spec 五个需求的操作性路径（裸号、快照稳定、白名单、黑名单、标签先行）。
- 6.2 交付：README 新增"版本与消费契约"三条（快照精确锁定禁入区间、生产锁裸号正式版按 SemVer 升级、弃用宽限一个次版本），目录同步收录。措辞与 spec 需求一一对应。人工核销由评审（/opsx:verify）执行。

## 组 4 正向路径补记（远端测试标签，已获用户授权）

- 4.2 完整验证：推送一次性附注标签 v9.9.9（指向 HEAD）至远端后，临时发布分支 `9.9.9.release` 上门禁 BUILD SUCCESSFUL（ls-remote 解析、^{} 解引用比对、单远端兜底解析、根工程记忆化全链路走通）；随即删除远端标签（`[deleted] v9.9.9`）与本地标签、临时分支，`git ls-remote --tags github` 对 v9.9.9 与 v5.7.8 均零命中，仓库恢复原状。

## 组 7（集成空跑）

- 7.1 四步序列全部通过：缺标签拒绝 → 补标签通过 → 黑名单命中拒绝（`3.4.1.release`）→ 合法裸号 publishToMavenLocal 产出 `9.9.9` 坐标（用 `-Dmaven.repo.local=/tmp/tny-999-m2` 隔离，真实 ~/.m2 零渗入）。
- 空跑暴露并修复上游谓词缺陷：原 `publishesToSharedRepository` 排除条款只挡住 `publishToMavenLocal` 本身，其依赖 `publishMavenJavaPublicationToMavenLocal` 仍被挂上门禁，本地发布链实际未直通；改为按尾缀 `ToMavenLocal` 排除整条链（规格语义：门禁只约束共享仓发布）。
- 终局清洁检查：临时分支与本地/远端测试标签零残留；`5.5.0.release` 历史分支保持已恢复状态；工作区当前分支 5.7.x。

## verify 阶段修复记录（用户指示解决全部 WARNING/SUGGESTION）

- W1 重复注释：删除 publications.gradle 标签校验块的旧注释残留（原 110-114 行五行块），保留终版六行说明。
- W2 设计回写：design.md D6 增加实施补记（ls-remote 为 D3 实际数据源，gitTag 闭包定位为标签账本查询设施），消除"闭包充当 D3 输入"的不实表述。
- W3 场景补证：以一次性 init 脚本（/tmp/w3-force-version.gradle，afterEvaluate 注入错误版本 5.8.0）实跑触发"开发线发布裸号被拒绝"：拒绝文案与规格场景逐字相符（含"裸号正式版必须经由发布分支产生"），未修改任何仓库文件。
- W4 部署末端：发布分支到 Nexus releases 仓的真实上传因属外向变更不在空跑范围；补证点定在首个真实 GA（5.7.8）按 docs/release-process.md 执行时留档。
- W5 谓词修复回写：design.md 新增决策 D7，记录 publishesToSharedRepository 按尾缀 ToMavenLocal 排除本地发布链的缺陷修复与否决备选。
- S1 间距：problems.join 改为 "\n- "，复验双问题输出第二行起前导空格正常。
- S2 常量注释：RELEASE_PACK_SUFFIX 定义处补两行历史用途注释（git.gradle:6-7）。
- 回归：修复后 :tny-game-common-lang:test 全绿（结果见命令输出），3.4.1.release 拒绝链复跑正常，临时分支与 init 脚本均已清理。
