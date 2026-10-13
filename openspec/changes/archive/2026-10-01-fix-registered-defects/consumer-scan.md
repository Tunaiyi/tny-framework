# 表达式资源消费面扫描（tasks 1.1，basics/数值语义面）

日期：2026-10-01。口径：全仓 `.properties/.json/.lua/.js/.groovy/.mvel/.yml/.yaml/.xml`（排除 build 产物与 .git），
扫描 `−112` 方向错误常数对与 `ofItemId/itemIdOf/ofModelId/aliasHead` 位号解析面依赖。

## 命令与结果

```
find . -type f \( -name "*.properties" -o -name "*.json" -o -name "*.lua" -o -name "*.js" \
     -o -name "*.groovy" -o -name "*.mvel" -o -name "*.yml" -o -name "*.yaml" -o -name "*.xml" \) \
  | grep -v "/build/" | xargs grep -ln -- "-112"
→ 0 命中
find ...（同上文件集）| xargs grep -ln "ofItemId\|itemIdOf\|ofModelId\|aliasHead"
→ 0 命中
```

## 结论

1. **表达式/配置资源零依赖 `−112`**：D2 窄类型提升修正（`add((byte)100,(short)300)` 由 −112 变 400）无脚本侧补偿逻辑受害者；
   release-note 按"全仓 grep 常数对 0 命中则公告即可"口径执行即可（D2 验证条款满足）。
   附注：仓内 `-112` 仅出现在 NumberAide 注释/钉桩与 protoex 测试的字节数组字面量（与表达式资源无关）。
2. **表达式/配置资源零依赖 ofItemId 位号解析面**：D5 修正的仓外可见消费面即 design「爆炸半径实测」所列
   GameExplorer×2（Java 调用点，非资源）；MVEL/GraalJS 配置脚本（如 `ItemExample.yml` 的 `fx:` 域）
   仅引用 `itemAlias` 文本（经 ModelManager 装载链），不触 ofAlias/ofItemId。
3. **basics 门面 Java 消费点清单（6.1/6.2 前置）**：
   - `DemandTypes.check(String/int)`：全仓 Java **零消费点**（仅 EnumRegistryFamilyContractTest 钉桩格）——
     严格化翻转无既有调用方受害者，release-note 只作行为公告。
   - `ItemTypes.ofAlias(String)`：生产消费点 1 处 = starter-basics `GameExplorer.getModelManager(String)`
     （`getItemByAlias` 链）；其余为钉桩测试。空串/纯分隔符输入由 IOOBE 改 IAE 受控失败。
   - `ItemTypes.ofItemId(long)`：生产消费点 2 处 = `GameExplorer.getItem(AnyId)`（L72）与
     `GameExplorer.getItemManager(long)`（L293），当前恒解析段 0——修复后真实归位（release-note 点名运营侧核对道具表）。
   - `ItemTypes.ofModelId(int)`：`GameExplorer.getModelManager(int)`/`getItemManager(int)` 与
     `dto/DemandResultDTO`（L72）使用；本变更不动 ofModelId 语义。
   - 正向生成面：`BaseSingleStuffOwner` L52 `itemIdOf(idIndexCounter++)`（入库产出全局号）——
     ofItemId 单一商式即其逆运算，号段证据见 red-baseline-basics.md。
