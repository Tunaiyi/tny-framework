# Verification Summary（fix-common-audit-findings）

## 门禁结果（10.1）

- 命令：`./gradlew test -x :tny-game-namnspace-etcd:test` → **BUILD SUCCESSFUL**
- 全量扫描：19 个产出 test-results 的模块、**609 个用例、零 failure/zero error**
- 六模块分跑（组 1~7 验证任务）：digest 39、io 全绿（含既有 ConfigTest）、lang 66（含新增转换 7 例）、lifecycle 12、reflect 16、scheduler 36，全部 BUILD SUCCESSFUL
- 下游重点模块：`tny-game-net`、`tny-game-net-netty4`、`tny-game-protoex` 测试通过（串行 worker、字节编解码、链式缓冲三条契约的活体验证）

### 已知排除（环境债，非本变更引入）

`tny-game-namnspace-etcd:test` 排除。根因实测（jstack）：`EtcdNamespaceExplorerTest` 在 `@BeforeEach` 对 `http://127.0.0.1:2379` 的 `removeAll(...).get()` **无超时阻塞**——本地无实 etcd 时整个 test 任务无限挂起（本会话三次全量 `test` 全部挂在此处，前两次误判为工具链问题）。该类未接入 `add-integration-testing` 建立的两级验证通道（应标 `@Tag("integration")`/`@Tag("docker")` 或加 `@EnabledIf` + 超时），属存量债，应另立变更迁移，**本变更未顺手修改**。CI 若在具备 etcd 的环境执行则该排除不需要；本地开发者注意勿以无排除的全量 `test` 为门禁。

## IDE「not exported」告警收口（10.3）

结论：**代码零改动，告警为 IDE 工程模型失同步**。证据链：
1. 涉事接口 `LifecyclePriority` 本就是 `public interface`（`tny-game-common-lifecycle/.../LifecyclePriority.java:16`）；
2. 全仓无任何 `module-info.java`；发布 jar 仅含 `Automatic-Module-Name` 清单属性（自动模块导出全部包，语义上不存在 "not exported"）；
3. javac 全量编译（54 项目 `classes testClasses`）通过——公共签名不引用不可访问类型已由编译器证明；
4. 根因实证：`.idea/modules.xml` 仅注册 10/54 个模块，`tny-game-common-lifecycle` 未被导入，IDE 符号解析落到 mavenLocal 的 5.7.x-SNAPSHOT 同名 jar（其自动模块名恰为告警中的 `tny.game.common.lifecycle`）。

处置指引：IDEA 中 Reload All Gradle Projects；若仍现则 Invalidate Caches 重启。mavenLocal 陈旧快照 jar 的清理由用户决定（发布产物，未动）。

## 收尾项落实（8.1 / 9.x）

- 8.1：三个注册器 `value()` 调用点形态一致（无强制转型），`putIfAbsentLifecycle` 泛型签名生效，lifecycle 测试全绿。
- 9.1 红基线留痕：实现前 7 例中 5 例失败，红因逐条核对均为严格 cast 缺宽松路径（CCE），非意外异常；
- 9.2/9.3：`WrapperObjectMap` 三取值通道改走与非包装实现同一转换引擎（热点直取首步即 isInstance，直取路径成本不变），7 例转绿 + lang 全模块零回归；守约哨兵用例钉死"两实现逐路径结论一致"。

## validate（10.2）

`openspec validate fix-common-audit-findings --strict` → valid。

## BREAKING 公告（10.4）

见同目录 `release-note.md`：按模块分组的行为变更清单 + 兼容性核查结论（无报文格式变更；下游仅需核对错型配置读取与"超时后副作用依赖"两类用法）。


## 归档顺序备注（第二轮追加）

后续变更 `fix-common-dormant-defects`（第二轮休眠簇收口）已实施：其 specs 全部为新能力、与本变更差量零交叠；
两变更归档与本仓 specs 账本合入次序固定为 **本变更（首轮）→ 第二轮**，第二轮 tasks 13.4 即此说明。
