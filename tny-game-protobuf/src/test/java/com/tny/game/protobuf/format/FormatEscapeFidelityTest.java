/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protobuf.format;

import com.google.protobuf.Message;
import com.tny.game.protobuf.format.test.FormatTestProtos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * fix-registered-defects 2.1（protobuf-text-fidelity 差量 Requirement「文本值转义对所有输出形态一致生效且字节保真」/
 * 「打印到读回对转义特殊字符集全形态闭合」）：全形态 print→merge 往返矩阵。
 * <p>
 * 矩阵断言：转义特殊字符集与 \\u 四位权展开样本经任一可读回形态（XML/JSON/PROPS/COUCH）打印后读回，
 * 往返逐字节相等，且对读回产物二次打印与首次打印逐字一致；畸形文本（残缺转义）读回 MUST 抛带位置信息的
 * 显式失败。Html 形态无读回面（本变更签名冻结，不新增 merge），其转义生效由 FormatGoldenTest 的
 * P_HTML_ESCAPES 格单独翻转。
 */
public class FormatEscapeFidelityTest {

    private final FormatTestFixtures fx = new FormatTestFixtures();

    private static final String[] FORM_TAGS = {"XML", "JSON", "PROPS", "COUCH"};

    private Message roundTrip(String tag, Message original) throws Exception {
        Message.Builder builder = original.newBuilderForType();
        switch (tag) {
            case "XML":
                Protobuf2XmlFormat.merge(Protobuf2XmlFormat.printToString(original), fx.registry(), builder);
                break;
            case "JSON":
                Protobuf2JsonFormat.merge(Protobuf2JsonFormat.printToString(original), fx.registry(), builder);
                break;
            case "PROPS":
                Protobuf2JavaPropsFormat.merge(Protobuf2JavaPropsFormat.printToString(original), fx.registry(), builder);
                break;
            case "COUCH":
                Protobuf2CouchDBFormat.merge(Protobuf2CouchDBFormat.printToString(original), fx.registry(), builder);
                break;
            default:
                throw new IllegalArgumentException(tag);
        }
        return builder.build();
    }

    /**
     * Scenario「非法文本读回仍显式失败」的失败面钉点：消息带 "行:列:" 位置前缀与 Invalid escape sequence 描述。
     */
    private static void assertPositionedFailure(String message) {
        assertTrue(message.matches("(?s)\\d+:\\d+:.*Invalid escape sequence.*"), message);
    }

    private static String print(String tag, Message message) {
        switch (tag) {
            case "XML":
                return Protobuf2XmlFormat.printToString(message);
            case "JSON":
                return Protobuf2JsonFormat.printToString(message);
            case "PROPS":
                return Protobuf2JavaPropsFormat.printToString(message);
            case "COUCH":
                return Protobuf2CouchDBFormat.printToString(message);
            default:
                throw new IllegalArgumentException(tag);
        }
    }

    /**
     * Scenario「高位字节字符串全形态往返无损」×「特殊字符集打印产物可完整读回」：
     * escapes()（引号/反斜杠/控制符/高位字节集）与 unicodeWeights()（\\u 四位权展开逐位样本）
     * 对四种可读回形态逐一断言字节保真与二次打印一致。
     */
    @Test
    public void allFormEscapeMatrixRoundTripIsByteExact() throws Exception {
        for (String tag : FORM_TAGS) {
            for (Message original : new Message[]{fx.escapes(), fx.unicodeWeights()}) {
                String firstPrint = print(tag, original);
                Message merged = roundTrip(tag, original);
                assertEquals(original, merged, tag + " 往返应逐字节相等: " + firstPrint);
                assertEquals(firstPrint, print(tag, merged), tag + " 二次打印应与首次逐字一致");
            }
        }
    }

    /**
     * Scenario「高位字节字符串全形态往返无损」的公式钉点：\\uff80/\\uffff 按 16³/16²/16¹/16⁰ 还原；
     * 现状错误公式（16*3/16*2/16*1）产出的 `ﾯ`（重打印快照 `\\uffaf`）与 `0` 退化值 MUST 失效。
     */
    @Test
    public void unicodeWeightFormulaRestoresHighBytesExact() throws Exception {
        Message merged = roundTrip("JSON", fx.unicodeWeights());
        assertEquals(fx.unicodeWeights(), merged);
        String rePrint = Protobuf2JsonFormat.printToString(merged);
        assertTrue(rePrint.contains("\\ufff0"), "高位字节应按权还原为 \\ufff0 打印形态: " + rePrint);
        assertTrue(rePrint.contains("\\uff8f"), "高位字节应按权还原为 \\uff8f 打印形态: " + rePrint);
        assertTrue(rePrint.contains("\\uffff"), "高位字节应按权还原为 \\uffff 打印形态: " + rePrint);
        assertFalse(rePrint.contains("\\uffaf"), "现状错误公式产物 \\uffaf（ﾯ）应失效: " + rePrint);
        assertFalse(rePrint.contains("\\uffa0"), "现状错误公式产物 \\uffa0 应失效: " + rePrint);
    }

    /**
     * Scenario「非法文本读回仍显式失败（错误路径）」：残缺 \\u 转义（JSON/Couch 通道）与残缺尾反斜杠
     * （XML/Props 八进制通道）均抛带 "行:列:" 位置信息的显式失败，不静默产出部分填充对象。
     */
    @Test
    public void malformedEscapeTextRejectedWithPosition() {
        Protobuf2JsonFormat.ParseException je = assertThrows(Protobuf2JsonFormat.ParseException.class,
                () -> Protobuf2JsonFormat.merge("{\"opt_bytes\": \"\\ua0\"}", fx.registry(),
                        FormatTestProtos.AllTypes.newBuilder()));
        assertPositionedFailure(je.getMessage());

        Protobuf2CouchDBFormat.ParseException ce = assertThrows(Protobuf2CouchDBFormat.ParseException.class,
                () -> Protobuf2CouchDBFormat.merge("{\"opt_bytes\": \"\\ua0\"}", fx.registry(),
                        FormatTestProtos.AllTypes.newBuilder()));
        assertPositionedFailure(ce.getMessage());

        Protobuf2XmlFormat.ParseException xe = assertThrows(Protobuf2XmlFormat.ParseException.class,
                () -> Protobuf2XmlFormat.merge("<AllTypes><opt_bytes>ab\\</opt_bytes></AllTypes>", fx.registry(),
                        FormatTestProtos.AllTypes.newBuilder()));
        assertPositionedFailure(xe.getMessage());

        Protobuf2JavaPropsFormat.ParseException pe = assertThrows(Protobuf2JavaPropsFormat.ParseException.class,
                () -> Protobuf2JavaPropsFormat.merge("opt_bytes=\"ab\\u8\"\n", fx.registry(),
                        FormatTestProtos.AllTypes.newBuilder()));
        assertPositionedFailure(pe.getMessage());
    }

}
