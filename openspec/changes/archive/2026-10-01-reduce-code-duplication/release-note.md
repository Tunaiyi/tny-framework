# Release Note（reduce-code-duplication）

## 兼容性声明：零 BREAKING

全仓 `./gradlew test` 门禁 929 用例零失败；所有 public/protected 消费者可见签名、类层级（除下述纯增量）、报文/JSON/ProtoEx 序列化格式零变更。下游无需任何代码或配置改动即可升级。

- 唯一层级变化（D7 豁免）：`tny-game-starter-data` 三 `*Properties` 与 `tny-game-starter-net-netty4` 两 `*Properties` 各新增同包 public abstract 父类（纯增量；属性键、方法签名、链式 setter 返回类型、全部 Spring 注解留子类原位）。instanceof 判定只增不减；Binder 绑定回归测试钉桩通过。
- 新增包私有共享件（不可外部命名）：`FormatTextSupport/FormatValueRenderer/FormatTokenizerCore/FormatValueReader`（protobuf）、`FutureSyncSupport`+转换引擎件（common-lang）、reflect 骨架、net `VoidCommandPluginSupport`（两 Void 插件保持互不为子类型，`@ConditionalOnClass` 筛选面不变）。
- 缺陷零修复申明：`ObjectMap.getFloat(key,def)` 的 float.class CCE 现状、`NumberAide.sub` null 分支、protobuf Html 不转义、Json `\u` 还原高位字节有损等 20+ 项发现**全部按现状钉桩保留**，待另立变更（见记忆账目与各组"遗留登记"）。

## 去重战果（岛级度量 + 模块净行数）

| 族 | M1 岛消化 | 主源净行数 |
|---|---|---|
| protobuf 五格式类 | 1,408 → 残余 54（均 JUSTIFIED_KEEP） | 首轮 -1,497；收口压缩再 -65 |
| worker 双 future | Sync 状态机 ~250 行共享 | -80（-5 压缩批） |
| ObjectMap/Wrapper 转换链 | 107 → 门面骨架保留 108（KEEP） | 含于 lang -80 |
| NumberAide 五算术段 | ~60 | 含于 lang -80 |
| reflect 两对平行类 | 96+83 → 骨架共享 | -33（门面委托微增抵） |
| basics 注册表族 | ~366 转发段 | -72 |
| net 测试脚手架 | ~260（测试侧） | -241（已提交 971298da） |
| starter properties + EntityManager | ~255 | -154 |
| net Void 插件对 | 31 | -31 |

**总账**：M1 冗余行 2,653 → 1,086（全口径 -59.1%；main/混合口径 -63.2%）；未达全口径 60% 线的 26 行缺口全部位于已论证保留岛，按 D9/9.1 条款不为凑指标破坏裁决（详见 verification.md 度量收口表）。
