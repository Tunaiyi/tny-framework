# Design

## Context

动机见 proposal.md - Why。现状（工作树实态）：`tny.git.gradle` 第 12 至 21 行以"先创建空扩展、再逐行外部赋值"填充 GitInfo；GitInfo 的十个属性（常量三、句柄一、派生值六）全部是 Groovy 可读写属性，自动带公开 getter 与 setter；消费五件经属性语法读取。上一册验收记录（expose-git-info-extension 的 verification-notes.md）留有十二对判据的取法与剔噪口径，本册沿用其方法与本仓"零差异验收基线抓样口径"，但改造前样本全部本册现抓。

## Goals / Non-Goals

**Goals：** 派生值的构造职责收进契约类内（句柄注入、构造即求值齐备）；属性面转为只读（私有 final 字段加 getter），消灭公开 setter；tny.git 收敛为"开句柄、带参创建"两步；契约更名为 `gitFlow`/`GitFlow`（用户 apply 指令裁决）；除改名外消费方零逻辑改动、外部行为零差异。

**Non-Goals：** 不改任何推导方法体与求值时序语义（上一册设计决策 D2 的"逐行迁移、不顺手重写"纪律在本册继续生效）；不改消费文件的逻辑、文案与结构（改名只动标识符）；不借机裁剪八个零消费的死键与两个死方法（gitHeadCommit、isConfigChange——裁剪属另一裁决面）；不并插件文件、不改 tny.git 插件标识名；openspec 册件（含上一册工件与归档件）内的历史名称引用不改写。

## Decisions

**D1 句柄经构造器注入，扩展以 `extensions.create(name, type, args...)` 一步创建注册。** 被否决备选一：保留无参 create 后调用类内 `init(handle)` 方法补初始化——setter 面以方法形态回归，构造后仍可变，违背本册目标。被否决备选二：tny.git 内 `new GitInfo(handle)` 后 `extensions.add(name, instance)`——绕过 Gradle 的统一装饰通道，与仓内其余扩展（如 tny.benchmark-module 对 BenchmarkSuite 的 create 用法）形态不齐。create 带参重载由 Gradle 官方文档承诺，装饰与访问器注册与无参形态同一通道。
**D2 不可变性用"私有 final 字段加 getter"表达。** Groovy 对 `final` 属性自动生成 getter、抑制 setter，消费方属性访问语法逐字不变；UPPER_SNAKE 常量属性名与 JavaBeans 规则相容（getRELEASE_VERSION_SUFFIX 对应的属性名不解低首字母，读取形态现状即证）。句柄字段类型保持动态（本仓 buildSrc 不引入 grgit 编译期依赖的约束延续，见 expose-git-info-extension 设计决策 D1），其 getter 返回 Object 语义，消费方 `gitInfo.grgiter.branch.list()` 形态经 getter 取句柄后调用链不变。
**D3 构造器内调用的是本类方法，运行在原类上下文。** Gradle 装饰经子类生成，但构造器体先于装饰生效，六个派生值经 `gitBranchName()` 等方法就地求值不受装饰影响；求值顺序严格保持上一册填充序：branchName 先，branchVersion、projectVersion、commitId、commitTime 随其后，buildTime 以系统时钟收尾。方法体与实测教训注释逐行原样（D2 纪律）。
**D4 失败面如实声明。** 开句柄失败与旧形态同层同文案（`grgit.open` 两种形态都先于构造调用、直接在插件脚本报出）。派生求值失败（改造前后都只在 HEAD 或当前分支读不到时发生）的报错链按审计探针实测登记差异：旧形态在插件应用失败的包裹层下直接列原始异常；新形态在其间多一行"Could not create an instance of type tny.convention.GitFlow."。失败阶段、位置、退出语义与"Failed to apply plugin 'tny.git'"外层呈现均不变，成功路径逐字节不变红线不受触及；失败路径多出的这一行按本册口径属可接受形态变化，验收记录如实声明，不做捕获翻译。buildTime 的分钟粒度时钟值在两遍复跑跨分钟时本身不同，但其无任何消费与样件输出面（上一册审计记录在案），判据不受影响——该声明写入验收记录。
**D6 改名与收敛同批执行，范围含扩展名与类名。** 用户在 apply 指令同时下达 `gitInfo` 与 `GitInfo` 更名 `gitFlow` 与 `GitFlow`：登记标识符与类名同步改，消费五件、根 build.gradle 注释、类文件文件名与类头注释随名走，全仓活代码以旧名零残留为验收判据之一。被否决备选：改名另立第三册——同一批文件连续两次动刀、两轮基线抓样，成本与并发窗口风险双倍，且两事同为"契约内部形态调整、外部零行为"同一性质。判据面不受改名影响：十二对样件文本均不含扩展名与类名（样件为产物名、POM、门禁与 centralCheck 文案、任务清单、m2 路径清单）。
**D5 判据锚全部本册自抓。** 工作树自上一册外科基线轮后又经多册提交与在途改动（含 adopt-gradle-8-14-baseline 册的插件与 wrapper 面），上一册 baseline/ 件不再是本判据同环境件；本册按同款十二对探针自抓改造前样本，若实施窗口内再现并发写入，沿用上一册"外科基线轮"（临时回退本册八个文件改动、他人改动原样保留、重抓终版改造前样本、恢复后复跑）保证净效果隔离。

## Risks / Trade-offs

- [final 字段与 Gradle 装饰器共存的兼容性] → 构造器注入加 final 属常规形态，但本仓首次用于扩展类；`projects -q` 与十二对探针作实证兜底，若装饰层拦截 setter 抑制导致红，退回显式 getter 方法形态（字段显式 `private final` 加手写 getter），语义等价。
- [上一册未归档即动其产物，两册改动同居一个未提交工作树] → 提交切分由验收记录登记文件清单（本册八个文件），与上一册实施面互不遮蔽；若用户在两册归档顺序上有安排，以册序执行不冲突。
- [并发会话在途写入构建树] → D5 的现抓与外科基线轮口径兜底。
- [消费方若有对 gitInfo 属性的写入点将即红] → 全仓检索 `gitInfo.<属性> =` 赋值形态应为零命中（上一册消费面审计已定案消费方只读），实施步骤含此检索；命中即属现状违例，停止并上报裁决，不静默豁免。

## Migration Plan

前置确认上一册在位并自抓改造前基线 → 契约类改造加改名（GitFlow.groovy：构造器、final 属性、getter、类名）与 tny.git 瘦身、注册名改 `gitFlow` → 消费五件与根 build.gradle 纯改名差分 → 旧名活代码零残留检索、赋值形态零命中检索、评估绿 → 十二对判据加 `clean build`、tasks-all、m2 零差异验收，结论记 verification-notes.md。回滚为本册触碰的八个文件整体还原（类文件恢复旧名旧文、tny.git、消费五件、根注释行）。
