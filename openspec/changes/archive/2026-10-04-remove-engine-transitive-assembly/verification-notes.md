# 验收记录：remove-engine-transitive-assembly

## 第 1、3 组（标注先行，顺序调整如实记录）：2026-10-04

- 任务序按 3.1/3.2 → 1.1 执行：探针的验证对象含注册标注，标注先行不破坏行为（net 仍兜底 Groovy），与 tasks 3.2
  "标注后探针仍绿"的互引一致。
- 设计 D3 实施修订（已回写 design.md）：@UnitInterface 原拟打契约，但 tny-game-expr 契约模块零依赖、标注契约需
  为注解引入 common-lifecycle 依赖边并扩大 expr 发布 POM，与 Non-Goal 冲突；改为具体引擎工厂类自声明
  （@UnitInterface + @Unit(unitInterfaces=ExprHolderFactory.class)），UnitLoader.forEachLoader 原生支持该通道。
  MvelTemplateHolderFactory 经核查为同引擎渲染变体不打标（避免装配 mvel 即双单元自冲突）。
- 1.1 探针（starter-net-netty4 test，真实 UnitLoadInitiator + @Bean 引擎匿名子类）BUILD SUCCESSFUL：
  @Bean 引擎被 getBeansWithAnnotation(@UnitInterface) 发现、按契约键注册恰一个、求值 1+2=3 逐值一致。
  设计 Risks 第一条（扫描覆盖不确定性）解除——前提是装配方以 @Bean/注册形态交付，非纯类路径放置；
  该语义由 starter-net-netty4 的既有 @Bean 装配点满足（NetAutoConfiguration:119-122）。

## 第 2 组（合同测试先行）：2026-10-04

- 2.1 新增 tny-game-net/src/test/.../dispatcher/ExprHolderEngineAssemblyTest（四场景：空注册表显式报错附配置位置、
  恰一注册解析成功、多注册冲突列清单、显式传入优先）。Expr/ExprHolderFactory 均多方法接口，stub 走 Mockito。
- 2.2 编译关 BUILD SUCCESSFUL；改造前红绿快照（预期内）：FAIL 空表报错、PASS 单注册、FAIL 多注册冲突、
  PASS 显式优先——两条 FAIL 正是任务 4 的实现标的。

## 第 4、5、6 组：拆焊点、basics 收缩、IT 装配显式化：2026-10-04

- 4.1：新增 package-private ExprHolderFactoryResolver（显式优先、零注册报"未装配表达式引擎"附配置位置原文、
  多注册报冲突附 simpleName 清单）；CommandPluginHolder 的 null 兜底与 DefaultMessageDispatcher:32 短构造器
  改经解析器/传 null，引擎 import 与 implementation 边删除（净源集零引擎触点）。合同测试四条全绿（改造前快照
  两条红正对应本组行为变化）。:tny-game-net/rpc/doc/net-netty4/starter-net-netty4 测试通过；
  net 发布 POM 与模块元数据 expr-groovy/expr-mvel 命中数均 0。
- 5.1：basics 补 api common-reflect 直达边（公开签名实现 cglib 接口，取 api 逐配置等值），引擎边降
  testImplementation（主源码零引擎、测试五处保留直 new——D2 裁决）。:basics/:oplog/:starter-basics 测试通过；
  basics 发布 POM 与 module 元数据 expr-groovy 命中 0。
- 6.1：IT harness 改用三参构造显式传 GroovyExprHolderFactory（装配位显式声明），IT 模块补
  integrationImplementation expr-groovy；compileIntegrationJava 通过、非 docker 集成测试 BUILD SUCCESSFUL（41s）。
  docker 用例不跑说明：net-demo 全仓 grep 无表达式形态插件属性（attribute 均为默认 "@null"），
  docker 用例执行面不触求值路径，求值证据链由合同测试、探针与 harness 编译承担，如实记录不虚构覆盖。

## 第 7 组：收口回归与判据留档：2026-10-04

- 7.1 clean build：首轮后台运行 29 秒即败（管道命令只留存尾部 5 行，失败明细未获取），同状态重跑
  BUILD SUCCESSFUL（344 任务 323 执行、FAILED 行零、含配置期守卫与门禁全程在位）——判定前次为
  瞬态环境竞争（并行会话构建活跃窗口），无明细不可复现，如实记录不以一次绿掩饰一次红。
- 7.2 发布面零引擎总核对（判据三件）：
  其一，net 与 basics 的 runtimeClasspath 解析报表（baseline/deps-{net,basics}-runtime-after.txt，
  逐工程单独调用抓取，按前册勘误后的方法论）expr-groovy/expr-mvel 命中 0，net 仍含 1 条 tny-game-expr
  契约边（符合设计）。
  其二，发布元数据：net 与 basics 的 publishToMavenLocal POM 与 Gradle module 文件引擎命中 0（各组内已核）。
  其三，改造前对照：前册归档基线 deps-runtime-before.txt 全仓引擎触点 42 行（implementation 形态的
  runtime 携带态），本册即消除该形态；before 件指向前册归档卷（本册启动未及另抓，如实注记）。
- 7.3 对外迁移口径（供发布说明引用）：升级到本版本后，(1) 经 starter-net-netty4 装配的应用行为不变
  （starter 的 @Bean 引擎 + 单元注册自生效）；(2) 直接 new DefaultMessageDispatcher/CommandPluginHolder
  两参形态且属性配置含 @ 表达式的应用，需改为三参显式传入 ExprHolderFactory（一行），或装配引擎并
  经框架注册；未处理者在首次求值时收到"未装配表达式引擎"错误（附配置属性原文与迁移指引），
  不再是静默 Groovy 求值；(3) 仅依赖 tny-game-basics 运行域传递 Groovy 的工程需自行声明引擎依赖。
