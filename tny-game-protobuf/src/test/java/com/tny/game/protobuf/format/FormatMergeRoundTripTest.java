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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 行为账目（round-trip，fix-registered-defects 翻转后）：四类可读回形态 print → merge 契约。
 * <ul>
 * <li>2.1 翻转：Json/Couch 的 BYTES 高位字节 \\uffXX 按四位权展开（16³/16²/16¹/16⁰）还原，往返逐字节相等
 * 且二次打印逐字一致（全形态矩阵另见 FormatEscapeFidelityTest）。</li>
 * <li>3.1 翻转：Xml 对转义特殊字符集（八进制/短转义/高位字节）打印产物读回闭合，成功且值等。</li>
 * <li>3.2 翻转：unknown fields 全形态统一显式拒绝且失败信息可定位首个未知编号（原 Xml/Props 词法错位、
 * 原 Json/Couch 静默丢弃；样例面见 FormatUnknownFieldRejectionTest）。</li>
 * <li>3.3 翻转：Props MessageSet 扩展打印与读回命名对称，产物原样读回往返闭合（原 "Expected \".\"."）。</li>
 * </ul>
 * 其余既有格期望值零改动，禁止顺手修。
 */
public class FormatMergeRoundTripTest {

    private final FormatTestFixtures fx = new FormatTestFixtures();

    private Message mergeRoundTrip(String tag, Message original) throws Exception {
        String printed;
        Message.Builder builder = original.newBuilderForType();
        switch (tag) {
            case "XML":
                printed = Protobuf2XmlFormat.printToString(original);
                Protobuf2XmlFormat.merge(printed, fx.registry(), builder);
                break;
            case "JSON":
                printed = Protobuf2JsonFormat.printToString(original);
                Protobuf2JsonFormat.merge(printed, fx.registry(), builder);
                break;
            case "PROPS":
                printed = Protobuf2JavaPropsFormat.printToString(original);
                Protobuf2JavaPropsFormat.merge(printed, fx.registry(), builder);
                break;
            case "COUCH":
                printed = Protobuf2CouchDBFormat.printToString(original);
                Protobuf2CouchDBFormat.merge(printed, fx.registry(), builder);
                break;
            default:
                throw new IllegalArgumentException(tag);
        }
        return builder.build();
    }

    private static final String[] RT_TAGS = {"XML", "JSON", "PROPS", "COUCH"};

    @Test
    public void xmlMergeRoundTripSnapshots() throws Exception {
        assertEquals(fx.full(), mergeRoundTrip("XML", fx.full()));
        assertEquals(fx.special(), mergeRoundTrip("XML", fx.special()));
        assertEquals(fx.negSpecial(), mergeRoundTrip("XML", fx.negSpecial()));
        assertEquals(fx.negZero(), mergeRoundTrip("XML", fx.negZero()));
        // UNKNOWN（3.2 翻转）：统一显式拒绝并定位首个未知编号 995（原词法错位 "Expected identifier. --"）
        Protobuf2XmlFormat.ParseException pe1 = assertThrows(Protobuf2XmlFormat.ParseException.class,
                () -> mergeRoundTrip("XML", fx.withUnknown()));
        assertUnknownNumberLocated(pe1.getMessage());
        assertEquals(fx.msgSet(), mergeRoundTrip("XML", fx.msgSet()));
        // ESCAPES（3.1 翻转）：打印产物对转义特殊字符集（引号/反斜杠族）词法闭合，读回成功且值逐字节等
        assertEquals(fx.escapes(), mergeRoundTrip("XML", fx.escapes()));
        assertEquals(fx.doc(), mergeRoundTrip("XML", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("XML", fx.empty()));
    }

    @Test
    public void jsonMergeRoundTripSnapshots() throws Exception {
        assertEquals(fx.full(), mergeRoundTrip("JSON", fx.full()));
        assertEquals(fx.special(), mergeRoundTrip("JSON", fx.special()));
        assertEquals(fx.negSpecial(), mergeRoundTrip("JSON", fx.negSpecial()));
        assertEquals(fx.negZero(), mergeRoundTrip("JSON", fx.negZero()));
        // UNKNOWN（3.2 翻转）：静默丢弃改统一显式拒绝，失败信息定位首个未知编号 995
        Protobuf2JsonFormat.ParseException je1 = assertThrows(Protobuf2JsonFormat.ParseException.class,
                () -> mergeRoundTrip("JSON", fx.withUnknown()));
        assertUnknownNumberLocated(je1.getMessage());
        assertEquals(fx.msgSet(), mergeRoundTrip("JSON", fx.msgSet()));
        // ESCAPES（2.1 翻转）：\\uffXX 四位权展开还原公式修正后，往返逐字节相等且二次打印逐字一致
        Message mJSON_ESCAPES = mergeRoundTrip("JSON", fx.escapes());
        assertEquals(fx.escapes(), mJSON_ESCAPES);
        assertEquals(Protobuf2JsonFormat.printToString(fx.escapes()),
                Protobuf2JsonFormat.printToString(mJSON_ESCAPES));
        assertEquals(fx.doc(), mergeRoundTrip("JSON", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("JSON", fx.empty()));
    }

    @Test
    public void propsMergeRoundTripSnapshots() throws Exception {
        assertEquals(fx.full(), mergeRoundTrip("PROPS", fx.full()));
        assertEquals(fx.special(), mergeRoundTrip("PROPS", fx.special()));
        assertEquals(fx.negSpecial(), mergeRoundTrip("PROPS", fx.negSpecial()));
        assertEquals(fx.negZero(), mergeRoundTrip("PROPS", fx.negZero()));
        // UNKNOWN（3.2 翻转）：统一显式拒绝并定位首个未知编号 995（原词法错位 "2:1: Expected identifier."）
        Protobuf2JavaPropsFormat.ParseException pe1 = assertThrows(Protobuf2JavaPropsFormat.ParseException.class,
                () -> mergeRoundTrip("PROPS", fx.withUnknown()));
        assertUnknownNumberLocated(pe1.getMessage());
        // MSGSET（3.3 翻转）：打印短名路径与读回命名归一，往返闭合且值等（原 "1:30: Expected \".\"."）
        assertEquals(fx.msgSet(), mergeRoundTrip("PROPS", fx.msgSet()));
        assertEquals(fx.escapes(), mergeRoundTrip("PROPS", fx.escapes()));
        assertEquals(fx.doc(), mergeRoundTrip("PROPS", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("PROPS", fx.empty()));
    }

    @Test
    public void couchMergeRoundTripSnapshots() throws Exception {
        assertEquals(fx.full(), mergeRoundTrip("COUCH", fx.full()));
        assertEquals(fx.special(), mergeRoundTrip("COUCH", fx.special()));
        assertEquals(fx.negSpecial(), mergeRoundTrip("COUCH", fx.negSpecial()));
        assertEquals(fx.negZero(), mergeRoundTrip("COUCH", fx.negZero()));
        // UNKNOWN（3.2 翻转）：与 Json 同读回通道，静默丢弃改统一显式拒绝并定位首个未知编号 995
        Protobuf2CouchDBFormat.ParseException ce1 = assertThrows(Protobuf2CouchDBFormat.ParseException.class,
                () -> mergeRoundTrip("COUCH", fx.withUnknown()));
        assertUnknownNumberLocated(ce1.getMessage());
        assertEquals(fx.msgSet(), mergeRoundTrip("COUCH", fx.msgSet()));
        // ESCAPES（2.1 翻转）：继承 Json 还原通道，公式修正后往返逐字节相等且二次打印逐字一致
        Message mCOUCH_ESCAPES = mergeRoundTrip("COUCH", fx.escapes());
        assertEquals(fx.escapes(), mCOUCH_ESCAPES);
        assertEquals(Protobuf2CouchDBFormat.printToString(fx.escapes()),
                Protobuf2CouchDBFormat.printToString(mCOUCH_ESCAPES));
        assertEquals(fx.doc(), mergeRoundTrip("COUCH", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("COUCH", fx.empty()));
    }

    /**
     * 3.2 差量承诺面（design D3 裁"拒绝"，失败消息最小组合为 编号+位置，格式不承诺对外稳定——钉"可定位首个未知编号"）：
     * 失败消息 MUST 含首个未知编号 995（five-form 样例格式见 FormatUnknownFieldRejectionTest 统一钉桩）。
     */
    private static void assertUnknownNumberLocated(String message) {
        assertTrue(message.contains("995"), message);
    }

}
