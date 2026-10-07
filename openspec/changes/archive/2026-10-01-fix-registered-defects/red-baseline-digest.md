# 红基线 — digest 域（design D7：RSA 文本入口吞异常返 null → 显式失败）

取证命令：`./gradlew :tny-game-common-digest:test --tests "com.tny.game.common.digest.rsa.RSAUtilsTest" --rerun-tasks`（2026-10-01，JDK 21.0.12.1）
结果：`14 tests completed, 1 failed — BUILD FAILED`

## task 7.1 — 翻转 `RSAUtilsTest.illegalKeyTextFailsUnderControl` 的"返 null"半段

翻转前该格钉桩（去重轮遗留登记原文方向）：

```java
// 模/指数入口：非法数值吞异常返 null（该入口不抛受检异常，调用方必须判空）
assertNull(RSAUtils.getPublicKey("非数字", "x"), "模非法时公钥重建必须返回空");
assertNull(RSAUtils.getPrivateKey("非数字", "x"), "模非法时私钥重建必须返回空");
```

翻转为差量承诺方向（非法数字文本 + 非法编码两形态、公/私两入口共四断言）后取红，首个翻断即红：

```
RSAUtilsTest > illegalKeyTextFailsUnderControl() FAILED
    org.opentest4j.AssertionFailedError: Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.
        at com.tny.game.common.digest.rsa.RSAUtilsTest.illegalKeyTextFailsUnderControl(RSAUtilsTest.java:199)
```

- L199 = `assertThrows(IllegalArgumentException.class, () -> RSAUtils.getPublicKey("非数字", "x"))`
- 红因：`RSAUtils.getPublicKey/getPrivateKey` 现实现 `catch (Exception e) { e.printStackTrace(); return null; }`——
  `new BigInteger("非数字")` 的 NumberFormatException 被吞，方法正常返 null，故"nothing was thrown"。
- 同格其余三条翻转断言（`getPrivateKey("非数字","x")`、`getPublicKey("0","3")`、`getPrivateKey("0","3")`）
  被首个断言失败短路未执行到，同为吞异常返 null 形态，红因一致。
- 同格既有 toPrivateKey/toPublicKey 三条 `InvalidKeySpecException` 断言零改动（对表方向来源），保持绿。
- 其余 13 例（含合法路径 `keyRebuildFromModulusAndExponent`）本次运行全部保持绿——翻转未波及其他格。

## 转绿承诺（task 7.2）

两入口 catch 不再返 null：数值解析失败包装 `IllegalArgumentException`（消息指明公钥/私钥与失败环节，cause 透传
NumberFormatException），密钥材料生成失败包装 `IllegalArgumentException`（cause 透传 InvalidKeySpecException，
与 toKey 侧同方向）。公共签名（方法名/参数/返回类型/throws 受检面）不动——包装为未受检异常以保持"编译不变"。
