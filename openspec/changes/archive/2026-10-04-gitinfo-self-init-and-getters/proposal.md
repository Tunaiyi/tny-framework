# Proposal

## Why

GitInfo（根工程的 git 派生事实契约类，由 openspec change expose-git-info-extension 引入）现在把六个派生值（branchName、branchVersion、projectVersion、commitId、commitTime、buildTime）做成"外部赋值"形态：类体留空字段，求值填充的六行语句外加句柄赋值行全在 tny.git 插件脚本里完成；并且所有属性是 Groovy 可读写属性，自动带公开 setter。这带来三处不顺。其一，契约的构造过程劈在两个文件里，读者要看 GitInfo 和 tny.git 两处合起来才知道字段值何时、按什么顺序定下。其二，派生事实一旦求值就不可变，但公开 setter 让任何代码都能在装载后改写 gitInfo.branchName 或 projectVersion——"唯一写入方"的约定只靠纪律维持，没有类型层面兜底。其三，六个派生值全部依赖类内的 grgiter 句柄与推导方法，它们本应在构造时随句柄一次算完收齐。用户定调：派生值初始化收敛进 GitInfo 自身（构造即完成），并给属性提供 getter。

## What Changes

- GitInfo 新增以 grgit 句柄为唯一入参的构造器：句柄在构造器内收存，六个派生值按现有时序（先分支名，其后分支裸版本、项目版本、提交号、提交时间，最后构建时间戳）由构造器就地自初始化。
- 全部属性（三个后缀常量、grgiter 句柄、六个派生值）收敛为私有 final 字段并对外提供 getter；消费方现有的属性访问语法（gitInfo.branchName 形态）由 getter 承接，逐字节渲染不变；公开 setter 面随之消灭，不可变性由类型保证。
- tny.git 瘦身为一条语句：`project.extensions.create('gitFlow', GitFlow, grgit.open(currentDir: project.getRootDir()))`——打开仓库句柄内联为构造实参，一步完成构造与注册；原七行外部赋值语句与句柄局部变量删除；插件文件头注释随形态更新（构造职责移交类内）。
- 十个推导方法方法体逐行不变（沿用 expose-git-info-extension 设计决策 D2 的"不顺手重写"纪律）。
- 契约命名同批更张（用户在 apply 指令中裁决）：根工程扩展名 `gitInfo` 更名 `gitFlow`，实现类 `tny.convention.GitInfo` 更名 `tny.convention.GitFlow`；五个消费文件（tny.release、tny.publish、tny.publications、tny.dependency-management、tny.central）与根 build.gradle 注释行因此发生的是**纯改名差分**——引用标识符随名走，逻辑与文案零改动。改名与本册构造收敛合并执行，避免对同一批文件二次立项动刀。
- 行为零差异红线：派生字符串逐字节不变（jar 产物文件名与 POM version 字段负责证明）、releaseCutAndTag -PdryRun 文案不变、centralCheck 报红文案不变、组号违例探针与发布门禁探针红绿如常——与上一册同款十二对判据全部逐行零差异；`clean build` 绿；tasks-all 与 m2 清单对**本册自抓**的改造前重抓件零差异（本册不锚上一册基线件：工作树自上一册抓样后已有多册落地与其他在途改动，逐行锚点必须同环境现抓）。
- 如实声明一项固有形态而非回归：buildTime 是构造时刻的分钟粒度时钟值，改造前后两遍复跑若跨过分钟边界，该字段本身的取值会不同；上一册审计已证实全仓无消费方、十二对判据无任何样件输出该值，判据不受影响。

## Capabilities

### New Capabilities / Modified Capabilities

无。GitInfo 契约类的内部构造形态调整，对外部可见行为零变化；本册声明 skip_specs。

## Impact

- **受影响文件**：`buildSrc/src/main/groovy/tny/convention/GitInfo.groovy` 更名为 `GitFlow.groovy`（构造器、私有 final 字段与 getter、类名与类头注释随改）、`buildSrc/src/main/groovy/tny.git.gradle`（瘦身为一条带参内联语句：开句柄作为构造实参，注册扩展名 `gitFlow`）、五个消费文件与根 build.gradle 注释行（纯改名：`gitInfo`/`GitInfo` 随新名走）。
- **依赖关系**：排在 expose-git-info-extension 之后实施——上一册的产物（GitInfo 类与 tny.git 的扩展填充形态）是本册的编辑对象；上一册尚未归档提交，本册在其工作树现状上动工。
- **消费面**：CI、文档、下游均无感知（属性访问语法与渲染字节不变）。
