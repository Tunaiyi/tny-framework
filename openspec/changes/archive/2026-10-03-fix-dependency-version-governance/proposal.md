# Proposal

## Why

依赖版本体系审计（2026-10-03，六维分析经对抗复核）证实：集中托管面的正确性押在 BOM 导入语句的隐式次序语义上，这条"后导入的 BOM 覆盖先导入的 BOM"契约在仓内既无注释记载也无配置期守卫，并且已经造成真实失守——`gradle.properties` 声明的 log4j2 版本 2.22.1 从未生效，已发布构件元数据实测烤入的是 Spring Boot 3.2.1 管理的 2.21.1；集中托管清单还把 jetcd-core 传递要求的 guava 强制降级。与此同时，体系内积累了若干"声明面与消费面脱节"的死键、死边与悬空模块条目。本册修复这些治理缺陷，让"具名事实源声明什么，构建就解析到什么"成为有守卫的合同，而不是依赖书写顺序的巧合。

## What Changes

- 修正 BOM 导入次序失守：把 log4j-bom 导入语句移到 spring-boot-dependencies 之后，使声明的 log4j2 版本真正生效；在导入块头部注释写明覆盖次序契约。
- 新增配置期版本面对账守卫：对账"托管生效版本"与 `gradle.properties` 与版本目录的声明值，关键族（log4j、netty、jackson、slf4j、protobuf、testcontainers）不一致即配置阶段报红，落点为既有跨工程对账约定插件的第三项校验。
- slf4j 的 BOM 导入坐标从非契约形态的 parent 工件改为官方的 `org.slf4j:slf4j-bom`（解析结果等值，消除"靠父链巧合生效"的脆弱形态）。
- 修正 guava 强制降级：把版本目录 guava 声明值从 32.0.1-jre 升为 33.0.0-jre（jetcd-core 传递要求的最低满足值），保留集中托管条目。实施验证推翻了最初拟定的"摘除托管条目交回原生裁决"方案——托管插件会把带具体版本号的直连依赖隐式转为全工程强制托管，摘除条目不改变压降结果；改走升钉路线经用户 2026-10-03 裁决确认，这是本册唯一一处第三方版本数值变更。
- 补建 grpc 托管层：导入 grpc 官方 BOM 并新增大头版本键，版本取 jetcd-core 当前传递值使解析结果等值，终结 etcd 传输栈 grpc 与 vertx 版本无事实源的状态。
- 删除 `gradle.properties` 四个零消费方死键（groovyVersion、graalvmVersion、icu4jVersion、springCloudVersion），消除同值双写漂移陷阱；根构建脚本删除两条全仓零应用点的插件死声明（Spring Boot 插件与 Gradle 插件发布插件的 apply false 声明），并清理插件模块构建文件里被注释掉的发布配置残留。
- 新增发布前置门禁：发布线构件的直接依赖坐标携带快照、候选发布或里程碑形态即发布失败；已知的两处预发布坐标（tools-template 快照、quartz 候选版）登记入带理由与处置去向的豁免清单，本册不改它们的版本数值（升版属后续版本升级册）。
- **BREAKING** 发布依赖面收缩四处：删除 tny-game-redisson 对顶层模块 tny-game-boot 的 api 死边（全模块源码零引用）；tny-game-net 与 tny-game-basics 对脚本引擎实现模块 expr-groovy 的 api 边降为 implementation，tny-game-doc 对 expr-mvel 的 api 边降为 implementation，同时为 tny-game-basics 补上对接口模块 tny-game-expr 的直达 api 边（其公开签名使用的接口类型此前完全靠引擎实现模块传递获得）。下游若曾依赖这些传递坐标编译，需要自行显式声明。
- 删除 settings.gradle 中悬空的 tny-game-codec-protoex 引入条目（该模块无任何被跟踪的构建文件，却以发布线身份参与装配线并可能被写进 BOM 约束面）；公共测试夹具自动挂载排除宿主工程自身。
- 修正版本目录三处与事实矛盾的注释（jprotobuf 的"由 protobuf-bom 管理"表述、coord() 辅助函数表述、cglib 的托管条目表述），并对账九项"只被集中托管清单钉版本而全仓无声明级消费"的 commons 族条目：逐一经解析树核对传递落点，无落点者连同托管声明与目录条目一并移除，有落点者在托管清单注释登记所钉的传递坐标作为保留理由。

不列入本册（登记为后续处置）：第三方已知漏洞坐标的升版批（jackson、commons-beanutils、commons-compress、xstream、log4j2 修复线、quartz 转正式版、protobuf 与 jprotobuf 代际评估）、tny-game-mongodb 对 boot 的真实耦合所需的接口下沉、已发布 POM 中 epoll 分类件缺版本元素的修复、jmh 版本书写点收编（基准线正被其他在途变更编辑，避免冲突）、io.spring.dependency-management 插件升 1.1.7（列为 Gradle 基线升级的前置项）、tny-game-net-demo 与 tny-game-actor 的发布线归属裁决。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `gradle-build-style`：需求"依赖版本与坐标由单一事实源管理"扩展——事实源声明的版本必须与构建实际生效的托管版本对账一致，零消费方的版本声明键与只钉版本而无任何消费（含传递落点）的托管条目均属违例；新增需求"托管版本面在配置期与声明事实源对账"，规定守卫的可观察行为。
- `release-versioning`：新增需求"发布线构件的依赖坐标为发布级形态"——发布前置门禁校验发布线构件的直接依赖不得携带快照、候选发布或里程碑后缀，豁免须以带理由的登记清单形式存在且在门禁输出中可见。

## Impact

- 构建脚本与事实源文件：`gradle.properties`（删四键、增 grpc 版本键）、`gradle/libs.versions.toml`（注释修正、可能的托管条目移除、grpc 族视设计裁决）、`settings.gradle`（删悬空 include）、根 `build.gradle`（删两条插件死声明）、`buildSrc/src/main/groovy/tny.dependency-management.gradle`（导入次序、slf4j 坐标、grpc 导入、托管清单）、`buildSrc/src/main/groovy/tny.module-checker.gradle`（新增对账与发布形态门禁）、`buildSrc/src/main/groovy/tny.java-module.gradle`（测试夹具挂载排除自身）、`tny-game-redisson`、`tny-game-net`、`tny-game-basics`、`tny-game-doc`、`tny-game-doc-gradle` 五个模块的 `build.gradle`。
- 解析结果的实际变化：其一，log4j 族生效版本从 2.21.1 恢复为声明值 2.22.1（属于"恢复声明"而非"升版"）；其二，guava 族全图解析值从 32.0.1-jre 统一上移至 33.0.0-jre（升钉经用户裁决，jetcd-core 的要求使其为必须的最低值）；其余守卫、死键删除、注释修正与门禁项不改变解析结果。
- 受影响下游模块与对应 starter：tny-game-net 的引擎 api 边收缩沿 net-netty4、net-test、rpc、net-demo、integration-test、tny-benchmark 传播，间接影响 starter-net-netty4 与 starter-net-apm-skywalking；tny-game-basics 的边收缩沿 oplog、starter-basics、net-demo 传播；tny-game-doc 的边收缩沿 doc-gradle 传播；tny-game-redisson 的 boot 死边删除沿 data-redisson、starter-redisson、starter-data 传播。
- 验证面：受影响模块的 `./gradlew :模块:test` 全绿；发布构件的 POM 与 Gradle 模块元数据按新增守卫逐项对账通过；本册触碰的 .gradle 文件按 gradle-build-style 的"存量违例触碰即改"处理改动区域内形态。
