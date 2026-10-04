# Tasks

本变更不触碰产品模块与公共 API，项目规则中"公共 API 变更前置 JUnit 5 测试任务"不适用；
机制正确性的验收载体是临时 bare 仓库演练（任务组 2）与真实发布跟随记录（任务组 5）。
gradle 命令在本机需 `JAVA_HOME` 指向 JDK 21；写通道按设计 D6 走 git 子进程，本机执行
追加 `-PgitExe=/usr/bin/git`。

凡完成判据需要读取 Gradle 命令渲染出的文字，抓取环境一律钉定：`JAVA_HOME` 指向 Corretto 21，locale 用正常 UTF-8，不注入 `JAVA_TOOL_OPTIONS`，清点文本时剔除 Gradle 守护进程噪声行（启动、告警、进度、汇总、耗时提示）与空行。任务 1.1 要在 `-PdryRun` 预览运行的计划里核对一句中文，任务 1.4 要清点 `./gradlew tasks --group release` 列出的任务条目，两处都受这一环境约束。若某条判据要拿改动前的文本样本与改动后的文本样本互相比对，两份样本必须在同一个 Gradle 守护进程下抓取；否则中文描述行会被渲染成问号，判据随之失真。该教训见 `openspec/changes/rename-bench-suite-class/verification-notes.md` 留痕段记录的基线抓取两条口径教训；本变更若新增把任务输出文件转存作比对基线的步骤，必须先运行相应的生成任务再转存输出文件，否则抓到的是空文件。

实施顺序前置（开工时已核销）：`gradle-build-style` 全仓清理（变更 `sweep-gradle-build-style`，
提交 `0b4fca25`）与构建脚本 buildSrc 插件化（变更 `adopt-gradle-official-dsl`，提交 `ba7ea66b`）
均已合入 HEAD，组 1 的实施文件按新基线为 `buildSrc/src/main/groovy/tny.release.gradle`
（原 `gradle/release.gradle` 的插件化后继，git 派生事实经根工程类型化扩展 `gitFlow` 获取），
根脚本引用注释位于 `build.gradle:20`。`migrate-git-calls-to-grgit` 已于 2026-10-03 归档。

## 1. 重写 buildSrc/src/main/groovy/tny.release.gradle 为三步任务

- [x] 1.1 注册 `releaseCut`：参数与校验沿用现语义（`-PreleaseVersion` 裸三段号必填、
  同号黑名单、本地与远端分支标签撞名检查、跟踪文件脏树阻断、`-PdryRun` 预览输出计划），
  动作改为：按 `-PreleaseFrom`（缺省规则：当前分支为开发线形态时取当前分支；hotfix 必须
  显式给上一版本标签，无法推断时报错并给出 `-PreleaseFrom=v<上一版本>` 指引——设计 D3）
  切出 `<V>.release` 容器分支并推送容器，不创建标签。完成判据：
  `./gradlew releaseCut -PreleaseVersion=9.9.9 -PdryRun -PgitExe=/usr/bin/git` 输出计划
  且明确"标签由 releaseTag 在制品定型后创建"。
- [x] 1.2 注册 `releaseTag`：前置断言当前分支为 `<V>.release` 形态且其版本号与
  `-PreleaseVersion` 一致；本地与远端不得已有同名标签；附注标签 `v<V>` 以 git CLI
  （`tag -a`）创建在当前 HEAD 并推送标签。完成判据：演练（任务 2）中 bare 端
  `git ls-remote` 可见 `refs/tags/v9.9.9` 与 `refs/tags/v9.9.9^{}` 两行。
- [x] 1.3 注册 `releaseMergeBack`：前置断言当前分支为发布分支形态；推导开发线名与远端
  （沿用现 `resolveRemote` 语义）；`ahead` 计数为 0 时如实报告跳过；否则把容器独有提交
  按序 cherry-pick 重放到开发线头（本地无线时先按远端建线），普通推送开发线；冲突时
  `cherry-pick --abort` 并回到操作前状态，错误信息给出人工等价命令；容器分支与标签
  在任何路径下都不移动、不推送（设计 D1）。完成判据：演练断言"线头含重放、容器头与
  标签解引用纹丝不动"；`grep -n "force-with-lease" buildSrc/src/main/groovy/tny.release.gradle`
  零命中。
- [x] 1.4 整文件按 `gradle-build-style` 八条需求自查（惰性注册、配置期无程序性控制流、
  单引号纪律、区块顺序、注释只记原因与规格出处），更新根 `build.gradle` 中
  `apply plugin: 'tny.release'` 行上方的引用注释为三个新任务名（当前位于
  `build.gradle:20`），并删除退役的 `releaseCutAndTag`、`releaseRebaseBack` 全部残留。
  完成判据：`./gradlew tasks --group release` 恰列三个任务；未参与实施的读者按需求
  第七条做三十秒扫读并答出"三个动作各固化什么"。

## 2. 临时 bare 仓库演练（机制验收）

- [x] 2.1 常规发布序列演练：临时 bare 加克隆、线上先行提交，依次执行 releaseCut、
  releaseTag、（模拟发布完成）、releaseMergeBack——切支后零新提交场景应报告跳过；
  断言线头、容器头、标签解引用三者与合回前逐项相同。完成判据：断言全部通过。
- [x] 2.2 hotfix 序列演练：从上一版本标签切基（显式 `-PreleaseFrom=v...`），容器上造
  修复提交并推送，线上并行推进若干提交，releaseTag 打在修复头，releaseMergeBack 重放；
  断言线头包含修复的重放提交、容器头等于修复提交本身、标签解引用不变、全程无强推
  （bare 端 reflog/ls-remote 核对）。完成判据：规格新增需求的前两个场景在此留证。
- [x] 2.3 冲突路径演练：构造修复与线下推进同文件冲突，releaseMergeBack 应自动中止并
  报错；断言三方（线、容器、标签）与执行前完全一致，错误信息含人工等价命令。
  完成判据：规格"重放合回遇到冲突时全体保持原状"场景留证。

## 3. 文档与命令改写

- [ ] 3.1 重写 `docs/release-process.md` 为三层分支模型总章（设计 D8/D9）：分支角色总表
  （main 顶点、`N.M.x` 冻结线、`N.M.K.release` 容器、`vN.M.K` 标签各自的职责、允许的
  操作与禁令）；七条生命周期流程各成一节，每节按"触发条件、命令序列、验证点、禁止项"
  展开——创建特性版本分支（从 main 切 `5.8.x` 的开线仪式清单：推送新线、CI 线入口登记、
  旧线维护态声明、主版本另附兼容性评估）、创建特性分支（基取 main 头）、rebase 特性分支
  （定期变基到 origin/main，进入 main 前以 rebase-merge 收尾，force-push 仅限个人工位）、
  发布特性版本（5.8.0 在 5.8.x 线头走容器三步）、创建 fix 分支（基取 main，main 无该
  代码路径时基取受影响线头）、rebase fix 分支、同步 fix 提交（D4 两站规则与
  `git cherry` 对账，波及线清单写在 PR 描述）；发布三步时序与容器标签不可移动律、
  `5.7.x-SNAPSHOT` 语义收窄的契约变更声明（BREAKING，含新坐标启用口径）；删除"变基重写
  与证据语义注记"的整段自圆文字；保留旧任务名到新任务的映射一行。完成判据：文中
  `releaseCutAndTag`、`releaseRebaseBack` 仅出现在映射说明；七条流程逐节通过"未参与读者
  一遍读懂"的文字规则检验；流程中的命令与脚本实现逐条对应。
- [ ] 3.2 改写 `.claude/commands/tny/release.md`：常规与 hotfix 统一为三步命令序列
  （hotfix 段落显式给出 `-PreleaseFrom=v<上一版本>`），删除"不要用 releaseCutAndTag"
  特例段；环境前置一节按新通道基线更新（写操作需 `-PgitExe`，只读不需）。
  完成判据：命令文件中每条示例命令的任务名与参数在脚本中真实存在，hotfix 与常规
  使用同一组任务名。

## 4. 仓库运维步骤（每步都是外向动作，执行前逐项向用户确认）

- [ ] 4.1 main 转正为开发顶点：执行 `git push github 5.7.x:main`（一次性普通快进——
  已实测 main 是 5.7.x 的严格祖先，非快进即拒是天然保险丝）；GitHub 默认分支保持
  `main` 不动，不开任何发布豁免。完成判据：`git ls-remote github refs/heads/main` 与
  `refs/heads/5.7.x` 同头；此后新特性提交只进 main。
- [ ] 4.2 删除本地化石分支 `master`（与 main 同头，无独有提交，已实测）。完成判据：
  `git branch --list master` 为空。
- [ ] 4.3 `.github/workflows/build.yml` 推送触发清单核对转正：`main` 由死项转为活主干
  入口（保留并确认语义），开发线条目以形态通配或显式列举现役线；以一次真实 push
  （不含 `[skip ci]`）分别验证 main 与现役线触发命中，通配不成立则退回显式列举并在
  开线仪式清单（任务 3.1）补登记步骤。完成判据：Actions 页面可见两条 push 各自触发的
  工作流运行。
- [ ] 4.4 把设计开放问题的处置结果记入变更目录：`release.published` 触发器是否由
  `releaseTag` 顺带创建 GitHub Release 来激活（本机无 gh CLI，候选为 REST API 推送或
  把触发器改为 tag push 事件）——请用户拍板，未拍板前 CI 发布通道维持手动触发口径。
  完成判据：变更目录新增一段决定记录。

## 5. 收口

- [ ] 5.1 全配置冒烟：三个任务各执行一次 `-PdryRun`（含 hotfix 参数形态），配置期与
  执行期零异常；`./gradlew :tny-game-bom:checkPublishPrerequisites -PgitExe=/usr/bin/git`
  复跑确认门禁四重本地校验不受影响。完成判据：全部零堆栈退出。
- [ ] 5.2 规格差量逐场景对账：新增需求"发布容器与标签一经创建不可移动"的四个场景
  分别标注证据位置（场景 1、2 在任务 2.1/2.2 演练记录，场景 3 在任务 2.3，场景 4 的
  门禁侧断言登记为"首个真实发布 5.7.9 时跟随核对"并入 apply-notes）；五个既有需求
  逐项标注不受影响理由。完成判据：对账表写入变更目录。
- [ ] 5.3 在各组完成时把结果摘要记入变更目录 `apply-notes.md`（含演练输出节选），
  并在其中补记：`migrate-git-calls-to-grgit` 已于 2026-10-03 归档，其写通道统一目标的
  终结定案登记在本变更账本（该会话归档摘要已成历史，终结说明以本册为准）。
  完成判据：文件存在且覆盖任务组 1 至 4 的执行结果。
- [ ] 5.4 起草 `5.7.x-SNAPSHOT` 语义收窄的下游迁移说明（契约变更声明：特性今后只随
  次版本线到达、滚动坐标字符串不变无需改依赖声明、新坐标 `5.8.x-SNAPSHOT` 的启用时点
  挂钩首个特性版本分支创建）入变更目录，投递时点=首个次版本线开线之后随发布通知发出
  （投递本身是外向动作，执行前向用户确认）。完成判据：文稿存在且与任务 3.1 的契约
  声明段逐句一致。
- [ ] 5.5 七条流程与实现面的终检：任务 3.1 文档中的每条命令与 `.claude/commands/
  tny/release.md`、`gradle/release.gradle` 当时形态逐条对照；三层白名单（D9）在文档、
  脚本、工作流三处的表述无互相矛盾。完成判据：终检清单全绿并入 apply-notes。
