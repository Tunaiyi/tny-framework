# Tasks

## 1. 断言先行（结构违约复现）

- [x] 1.1 `GuideLifecycleTest` 追加两用例（server 两类 guide 各一）：反射预置 `bootstrap` 字段为哨兵实例 → `close()` → 断言字段已被置 null（当前 close 不清缓存，应红）；并断言重开前置状态：close 后组字段与 bootstrap 字段同时为 null（"下次使用必全新"的结构合同）
- [x] 1.2 运行记录红灯到 `red-baseline.md`

## 2. 实现

- [x] 2.1 `NettyServerGuide.close()`：置空组字段处同步 `this.bootstrap = null`
- [x] 2.2 `NettyRelayServerGuide.close()`：同上

## 3. 回归与收口

- [x] 3.1 `./gradlew :tny-game-net-netty4:test -Dorg.gradle.java.home=<corretto-21>` 全量绿（新 2 例红转绿 + 既有 GuideLifecycle/PacketGate/RelayHandler 等全部）
- [x] 3.2 release-note：违约来源（外部审计交叉验证发现）、履行对象（net-guide-lifecycle scenario）、client guide 豁免裁定、与后续 share-guide-event-loop-groups 变更的边界声明
