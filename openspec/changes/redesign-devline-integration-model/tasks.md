# Tasks

## 1. 规格账本与词表先行

- [x] 1.1 将本变更 specs 增量合入主账本的准备工作：确认 release-versioning、central-publishing、branch-integration-gates 三个能力的 delta 与现文逐条对读，列出归档同步时的冲突点清单
- [x] 1.2 在 docs/release-process.md 头部新增"术语表"节（以 design.md D9 定稿词表为唯一来源），并加"何人把何内容经何机制移到何处"三行判定表（集成到主干、向上合并、cherry-pick 例外登记各一行）
- [x] 1.3 建立"历史资产身份映射登记表"（docs 内成文）：旧形态开发线群、`4.1.0.release`/`5.5.0.release`/`5.7.8.release` 容器分支、main 上 7 枚收编合并（含 3 枚变 7 枚的账本修正）、`v*-RELEASE` 历史遗留标签群、`6.0.x` 死线处置行，逐行给身份与状态
- [x] 1.4 验证：release-process.md 全文检索无"工位、电梯、登车、早切晚锁、发布容器、清账、补账、硬锚、上合、下传、在役、让位、基取、编年史、断代"诸词；判定表与词表、gates 规格三处用词逐字一致

## 2. GitFlow 形态解析与登记表读取

- [x] 2.1 GitFlow 增加 `dev/` 与 `release/` 前缀形态解析：`parseBranchVersion` 与 `parseProjectVersion` 识别两种新形态（坐标字符串去前缀保持 `N.M.x-SNAPSHOT`），旧形态 `N.M.x` 仅对登记表内条目放行
- [x] 2.2 新增登记表读取（文件放 `gradle/branch-legacy-registry.txt`，每行"分支名 | 身份（维护|开发|只读） | 依据变更名"），解析入口与白名单判定共用
- [x] 2.3 验证：`./gradlew -q properties` 或最小任务打印三条线（dev/5.8.x、release/5.8.x、祖父 5.7.x）的推导版本串，逐一比对预期；非法形态（`5.0.x.net`、`feat/x`）仍派生失败并报当前分支名

## 3. 发布任务改造（tny.release.gradle）

- [x] 3.1 releaseCut 语义改为"切 release 维护分支"：仅当对应开发版本分支已完成集成（main 包含其头提交）时允许执行，每系列唯一（本地与远端重名拒绝），切出基点为 main 分支头，不再接受 `-PreleaseFrom` 的一次性容器读法
- [x] 3.2 releaseTag 改造：守卫改为当前分支须为 `release/N.M.x` 形态（祖父登记线按登记身份放行）；执行下一补丁号校验（远端标签枚举期望号）；保留附注形态、幂等与轻量标签拒绝语义
- [x] 3.3 删除 releaseMergeBack 任务；其"冲突三方恢复"语义迁移进向上合并操作规约（docs 流程条目），不再以 gradle 任务承载
- [x] 3.4 新增 gradle 任务 `mergeUpward`：源维护分支整体合并进目标分支清单（补丁影响清单驱动），执行 D5 合并结果完整性检查（缺陷编号检索），缺失即不提交并打印缺失清单；支持 `-PdryRun` 预览
- [x] 3.5 验证：祖父轨 dryRun 三任务全序列输出与现行流程逐字对读；新形态用沙箱剧本（drill/model4-sim 场景 2、4、5 的等价 gradle 调用）在临时克隆里走通；`./gradlew buildSrc:test` 或等效编译检查通过

## 4. 门禁谓词重锚与 fail-closed

- [x] 4.1 `tny.publications.gradle` 签名强制谓词从分支名匹配 `.release` 改为版本形态判定（非 `-SNAPSHOT` 结尾即必签），缺密钥时 fail-closed 拒发
- [x] 4.2 `tny.central.gradle` Central 准入改为双条件（裸三段号且前两段与所在维护分支编号一致；祖父登记线编号取分支名本体），非发布形态快速失败信息同步改写
- [x] 4.3 `tny.publish.gradle` 白名单改为形态加登记表双层；release 标签核验逻辑不变但错误文案按新词表改写（核验/存证分工按 D9）
- [x] 4.4 验证：五组分支形态（dev、release、祖父、main、feat）× 三种版本串（快照、正确裸号、错号）的门禁矩阵逐项断言，含"缺密钥缺凭据机器上未签名构件不得入库"的 fail-closed 用例

## 5. 持续集成与平台核对

- [x] 5.1 探测工作流先行：在一次性 workflow 中打印 `dev/*.*.x`、`release/*.*.x`、`*.*.x` 三种过滤器对现仓分支的实测命中集，结论记入验证卷宗（Actions 通配斜杠语义以实测为准）
- [x] 5.2 `build.yml` 快照触发与 `snapshot-mirror.yml` 逐线枚举改为 `dev/` 路径加祖父登记条目，两处均加"匹配集合非空"断言步骤
- [x] 5.3 GitHub 平台配置核对清单执行记录（用户操作面）：分支保护对 main/dev/release 三类的 force 与 delete 拒绝、仓库合并按钮三态现状、必要时 pre-receive 钩子立项判断——核对结果与期望差异逐项登记
- [x] 5.4 验证：探测工作流命中集与预期一致；改动后的两个工作流各跑一次空转演练（不产出制品的 dry 分支）确认非空断言生效

## 6. 提交纪律与登记表落地

- [x] 6.1 commit-msg 钩子（版本库内成文脚本，安装说明进 docs）：修复类提交（fix 前缀或补丁影响清单声明的提交）必须携带缺陷编号；feat 提交不受限
- [x] 6.2 PR 模板更新：修复类必填"该补丁影响到的 release 维护分支清单"字段（不受影响须逐线写理由）；特性类移除该字段
- [x] 6.3 cherry-pick 例外登记模板（工单尾部或登记文件）：来源哈希、判定人、判定日期、不可整条合并原因、目标发版号
- [x] 6.4 验证：构造四种提交样本（带号修复、无号修复、特性、登记完整的例外），钩子与 CI 校验的放行与拒绝结果逐项比对预期

## 7. 文档与动画按新模型重写

- [x] 7.1 release-process.md 七条流程整体重写为：开开发分支、feat 工作流、集成到主干、切维护分支与发版、fix 与补丁影响清单、向上合并与例外、退役；每节命令以 `github` 远端名书写
- [x] 7.2 全仓旧词清扫（依据 1.3 登记表之外的活文档）：docs、README、`.claude/commands/tny/release.md` 技能文案、buildSrc 注释——按 D9 词表替换并复查"标签存证五变体"统一
- [x] 7.3 `docs/branch-model.html` 重绘为四分支形态静态图（dev/ 与 release/ 前缀、集成与向上合并两类箭头）
- [x] 7.4 `docs/branch-flow-animation.html` 按新模型重写剧本（集成顺序拦截、向上合并、完整性检查拦截、祖父轨对照为新场景），沿用已验证的渲染引擎；程序化自检（引用完整、边可见性、标签零重叠、`node --check`）通过
- [x] 7.5 验证：三处 HTML 与 release-process.md 交叉用词一致；对活文档跑 D9 禁用词全量 grep，命中数清零

## 8. 演练序列与收尾

- [ ] 8.1 祖父轨陪跑：下一个 5.7 补丁发版（如 5.7.9）走旧流程为主、新工具链 `-PdryRun` 全程对照，对照差异登记入验证卷宗
- [ ] 8.2 首个新形态系列全生命周期演练：dev 开线、集成到主干（顺序检查与同步合并实测）、切 release、发 N.M.0、一发补丁、向上合并（含一次真实冲突与完整性检查）、退役检查——逐事件留命令输出记录
- [x] 8.3 压力测试判决遗留项复算：main 收编合并计数更新（3→7→实测值）写入账本修正提交；`git rev-list --merges` 口径按新模型重述（集成事件数=first-parent 合并节点数）
- [ ] 8.4 收尾：本变更 tasks 全勾后跑 `/opsx:verify` 循环；归档时按 1.1 冲突清单同步主规格；Open Questions 两项（6.0 编号策略、GitHub Releases 页自动化）确认保持未决并登记去向
- [ ] 8.5 验证：`openspec validate redesign-devline-integration-model` 通过；演练序列全部输出在验证卷宗可检索；主规格同步后 `openspec show release-versioning --type spec` 现文与新词表零冲突
