/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.protobuf.format;

import com.google.protobuf.Message;
import com.tny.game.protobuf.format.test.FormatTestProtos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * fix-registered-defects 3.2（protobuf-text-fidelity 差量 Requirement「未知字段处置策略全形态统一为显式失败」，
 * design D3 裁"拒绝"，否决丢弃+告警）：同一份含未知字段编号的打印文本，每种可读回形态读回 MUST 抛显式失败，
 * 失败信息可定位首个未知编号（995）；不存在任何形态返回"缺少该字段但成功"的对象。
 * <p>
 * 形态说明：五打印形态中 Html 无 merge 读回面（本变更公共签名冻结、不新增），"全形态统一拒绝"落为
 * 四个可读回形态（XML/JSON/PROPS/COUCH）一致拒绝——Json/CouchDB 原"静默丢弃"共用同一读回通道，一并翻为拒绝。
 * 五形态归一样例（格式不承诺对外稳定，只钉 编号+位置 最小组合）：{@code 行:列: Unknown field number: 995.}
 */
public class FormatUnknownFieldRejectionTest {

    private final FormatTestFixtures fx = new FormatTestFixtures();

    @Test
    public void unknownFieldNumberRejectedConsistentlyAcrossReadableForms() {
        final Message original = fx.withUnknown();

        Protobuf2XmlFormat.ParseException xe = assertThrows(Protobuf2XmlFormat.ParseException.class, () -> {
            FormatTestProtos.AllTypes.Builder b = FormatTestProtos.AllTypes.newBuilder();
            Protobuf2XmlFormat.merge(Protobuf2XmlFormat.printToString(original), fx.registry(), b);
        });
        assertUnifiedRejectionSample(xe.getMessage());

        Protobuf2JsonFormat.ParseException je = assertThrows(Protobuf2JsonFormat.ParseException.class, () -> {
            FormatTestProtos.AllTypes.Builder b = FormatTestProtos.AllTypes.newBuilder();
            Protobuf2JsonFormat.merge(Protobuf2JsonFormat.printToString(original), fx.registry(), b);
        });
        assertUnifiedRejectionSample(je.getMessage());

        Protobuf2JavaPropsFormat.ParseException pe = assertThrows(Protobuf2JavaPropsFormat.ParseException.class, () -> {
            FormatTestProtos.AllTypes.Builder b = FormatTestProtos.AllTypes.newBuilder();
            Protobuf2JavaPropsFormat.merge(Protobuf2JavaPropsFormat.printToString(original), fx.registry(), b);
        });
        assertUnifiedRejectionSample(pe.getMessage());

        Protobuf2CouchDBFormat.ParseException ce = assertThrows(Protobuf2CouchDBFormat.ParseException.class, () -> {
            FormatTestProtos.AllTypes.Builder b = FormatTestProtos.AllTypes.newBuilder();
            Protobuf2CouchDBFormat.merge(Protobuf2CouchDBFormat.printToString(original), fx.registry(), b);
        });
        assertUnifiedRejectionSample(ce.getMessage());
    }

    /**
     * 正常路径回归面（Scenario「已知字段读回不受影响」）：不含未知字段的常规打印文本四形态读回仍全部成功，
     * 行为与差量前一致（与 FormatMergeRoundTripTest 既有全绿格互补钉桩）。
     */
    @Test
    public void knownFieldTextStillMergesSuccessfully() throws Exception {
        final Message original = fx.full();

        FormatTestProtos.AllTypes.Builder xb = FormatTestProtos.AllTypes.newBuilder();
        Protobuf2XmlFormat.merge(Protobuf2XmlFormat.printToString(original), fx.registry(), xb);
        assertTrue(xb.build().equals(original));

        FormatTestProtos.AllTypes.Builder jb = FormatTestProtos.AllTypes.newBuilder();
        Protobuf2JsonFormat.merge(Protobuf2JsonFormat.printToString(original), fx.registry(), jb);
        assertTrue(jb.build().equals(original));

        FormatTestProtos.AllTypes.Builder pb = FormatTestProtos.AllTypes.newBuilder();
        Protobuf2JavaPropsFormat.merge(Protobuf2JavaPropsFormat.printToString(original), fx.registry(), pb);
        assertTrue(pb.build().equals(original));

        FormatTestProtos.AllTypes.Builder cb = FormatTestProtos.AllTypes.newBuilder();
        Protobuf2CouchDBFormat.merge(Protobuf2CouchDBFormat.printToString(original), fx.registry(), cb);
        assertTrue(cb.build().equals(original));
    }

    private static void assertUnifiedRejectionSample(String message) {
        assertTrue(message.matches("(?s)\\d+:\\d+:.*"), "失败信息需带 行:列: 位置前缀: " + message);
        assertTrue(message.contains("Unknown field number: 995"),
                "失败信息需可定位首个未知编号且五形态样例一致: " + message);
    }

}
