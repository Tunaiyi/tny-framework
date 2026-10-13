# 验收记录：gitinfo-self-init-and-getters

## 结论

验收通过。根工程 git 派生契约完成两件事：派生值初始化收进类构造器、属性面转为只读（私有 final 字段加 getter，公开 setter 消灭）；契约随之由 expose-git-info-extension 引入时的名称更名为 gitFlow（扩展名）与 GitFlow（类名）。十二对行为样本对本册自抓的改造前基线全部逐行零差异，`clean build` 绿，旧名在活代码零残留，消费方除改名差分外零逻辑改动。

## 实施面（八个文件，改造前原件备份于 /tmp/gitflow-src-orig）

1. `buildSrc/src/main/groovy/tny/convention/GitInfo.groovy` 经 mv 更名 `GitFlow.groovy`：新增以 grgit 句柄为唯一入参的构造器，句柄首行收存，六个派生值按 branchName、branchVersion、projectVersion、commitId、commitTime、buildTime 的既有顺序在构造器内求值；三常量、句柄、六派生值全部转为 Groovy final 属性（私有 final 字段加自动 getter，不设 setter）；十个推导方法体逐行不变。
2. `tny.git.gradle` 由 21 行瘦身为一条语句：`project.extensions.create('gitFlow', GitFlow, grgit.open(currentDir: project.getRootDir()))`，外部赋值七行删除，头注释随形态与名称更新；审计指出的未引用局部变量一并清除。
3. 五个消费文件（tny.release、tny.publish、tny.publications、tny.dependency-management、tny.central）与根 build.gradle 注释行：`gitInfo`/`GitInfo` 一律随新名走（sed 纯改名），除此之外零字节变化。

## 判据结果汇总

| 判据 | 结果 |
|------|------|
| `projects -q`（实施后与文字修正后） | 各通过 |
| 旧名 gitInfo/GitInfo 活代码零残留检索 | 零命中（仅存于历史文档与本册、上一册工件的沿革叙述） |
| `gitFlow.<属性> =` 消费侧赋值形态检索 | 零命中（只读契约成立） |
| jar 产物文件名 / POM 全文 / POM version 字段 | 逐行零差异 |
| releaseCutAndTag -PdryRun 计划文案 | 逐行零差异 |
| centralCheck 报红块 | 逐行零差异 |
| 组号违例探针红样与恢复绿 | 红样逐行零差异，恢复绿 exit 0 |
| 门禁开发线直跑（含两条豁免打印） | 逐行零差异 |
| 克隆 9.9.9.release 门禁红 / releaseRebaseBack -PdryRun 红 / 9.9.release 门禁红 | 三份逐行零差异 |
| `tasks --all` 剔噪全文 | 3461 行逐行零差异 |
| publishToMavenLocal 后 m2 清单 | 679 行逐行零差异 |
| `clean build` | 全绿（1 分 07 秒，342 任务执行，无失败任务） |

## 抓样环境与口径（承上一册登记的偏差，全部前后同参数）

环境钉 Corretto 21.0.12.1、LANG=C.UTF-8、无 JAVA_TOOL_OPTIONS、`--console=plain`、同一 Gradle daemon；涉 git CLI 的抓取带 `-PgitExe=/usr/bin/git`（本机 PATH 首位件为 x86 专用的架构失配覆写通道），releaseRebaseBack 克隆探针沿用不带 gitExe 的原命令（该跑在既有缺陷处即红，不触 git CLI）；dryRun 样件剔 `[dryRun][warn]` 块的 git 状态清单行；发布形态探针在一次性本地克隆上抓取（grgit 不识别 worktree 指针式 .git 的沿革见上一册验收记录与设计决策 D6）。

## 基线世代说明与一处抓样脚本失误的如实登记

1. 本册改造前基线于凌晨 03:5x 自抓（十二对全量），未用上一册 baseline 件为锚（设计决策 D5：锚必须同环境现抓；工作树在上一册收口后另有提交与在途改动）。复跑窗口内工作树的目标文件无外部写入（以备份与 mtime 对证），未触发外科基线轮。
2. 改造后复跑脚本由改造前脚本派生时，文件名替换规则漏了 `.xml` 后缀，POM 全文件被改造后内容覆盖了改造前原件。修复与佐证：改造前的 POM 原文取自上一册的改造后件（两次抓取之间该模块构建输入未变，属同一树代际；POM 的 version 字段独立判据 `pom-version-before.txt` 未被波及且差分为零），重建后 POM 全文对比重跑，逐行零差异。判据语义未受损，登记此失误以示透明。

## 实施后对抗审计（四路核查加逐条反驳验证，十五代理零失败）

改名纯差分（六件全部为允许形态、含闭包形参与注释随名走）、类改造等价（十方法体逐字节不变、构造序与旧赋值序一致、十个属性读取面齐全）、插件与构造语义对抗（Gradle 8.14.5 的 `create(String, Class, Object...)` 带参重载经安装件接口核对、镜像工程探针实跑与本仓 `tny.module-setting.gradle` 同形先例三重证实；装饰子类 GitFlow_Decorated 构造委托与 final 只读属性经装饰读出正确值；只读属性外部写入被拒绝而消费面无写点）、风格与文字（许可证头逐字节同款、行数零加重、旧名残留面厘清）——确认必修清单为空；四条注释文字 minor（斜杠并列、会话代号"上一册"、电报式缩略、getter"声明与生成"表述矛盾）与一条未引用变量残留已在收口前全部修正并复验评估绿。

## 如实声明（设计决策 D4 的实测细化）

派生求值失败场景（改造前后都只在 HEAD 或当前分支读不到时发生）的报错链在新形态下于"Failed to apply plugin 'tny.git'"包裹层与原始异常之间多一行"Could not create an instance of type tny.convention.GitFlow"（审计探针实测对照），失败阶段、位置、退出语义与外层呈现不变；本册红线约束的是成功路径逐字节不变，失败路径该行差异按设计文档 D4（已按实测改写）声明为可接受的形态变化。buildTime 的跨分钟时钟差异按设计决策 D4 处置：该值无任何消费方与样件输出面，十二对判据不受触及。

## 遗留与移交登记（均非本册引入）

1. `HANDOFF-问题会话-2026-10-03.md`（历史交接文档）与本册、上一册的 openspec 工件仍含旧名沿革叙述，属历史文档，不在活代码零残留判据内。
2. 上一册登记的三项遗留（releaseRebaseBack 既有 split 转换缺陷、gitTag 内 `tags[0 as String]` 怪写法、tny.publish.gradle 超 250 行界线）原样保留，本册未触及亦未加重（273 行对 273 行）。
3. 上一册（expose-git-info-extension）与本册均未提交，同居工作树；提交切分依两册验收记录的实施面清单执行，本册为上述八个文件。
