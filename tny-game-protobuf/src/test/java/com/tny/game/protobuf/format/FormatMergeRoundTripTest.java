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
 * 重构前行为钉桩（round-trip）：五类 print → merge 往返现状账目。
 * 现状：Xml/Props 对 unknown fields 与 Xml 对转义特殊字符集（\\uXXXX/八进制/\\n\\r\\t\\"\\\\/高位字节）
 * 抛 ParseException（异常消息逐字钉死）；Json/CouchDB 的 unknown fields 被静默丢弃；
 * Json/CouchDB 的 BYTES 高位字节经 \\uffXX 的 \\u 还原公式为现状有损（合并后重打印快照钉死）；
 * Props 的 MessageSet 扩展往返不对称（抛 "Expected \".\"."）。以上现状一律保持，禁止顺手修。
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
        // UNKNOWN：现状往返不闭合，抛异常并逐字钉死消息
        Protobuf2XmlFormat.ParseException pe1 = assertThrows(Protobuf2XmlFormat.ParseException.class,
                () -> mergeRoundTrip("XML", fx.withUnknown()));
        assertEquals("1:42: Expected identifier. --", pe1.getMessage());
        assertEquals(fx.msgSet(), mergeRoundTrip("XML", fx.msgSet()));
        // ESCAPES：现状往返不闭合，抛异常并逐字钉死消息
        Protobuf2XmlFormat.ParseException pe2 = assertThrows(Protobuf2XmlFormat.ParseException.class,
                () -> mergeRoundTrip("XML", fx.escapes()));
        assertEquals("1:25: Expected \">\".", pe2.getMessage());
        assertEquals(fx.doc(), mergeRoundTrip("XML", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("XML", fx.empty()));
    }

    @Test
    public void jsonMergeRoundTripSnapshots() throws Exception {
        assertEquals(fx.full(), mergeRoundTrip("JSON", fx.full()));
        assertEquals(fx.special(), mergeRoundTrip("JSON", fx.special()));
        assertEquals(fx.negSpecial(), mergeRoundTrip("JSON", fx.negSpecial()));
        assertEquals(fx.negZero(), mergeRoundTrip("JSON", fx.negZero()));
        // UNKNOWN：现状往返不闭合但不抛异常，重打印结果快照钉死
        Message mJSON_UNKNOWN = mergeRoundTrip("JSON", fx.withUnknown());
        assertNotEquals(fx.withUnknown(), mJSON_UNKNOWN);
        assertEquals(RT_JSON_UNKNOWN_PRINT, Protobuf2JsonFormat.printToString(mJSON_UNKNOWN));
        assertEquals(fx.msgSet(), mergeRoundTrip("JSON", fx.msgSet()));
        // ESCAPES：现状往返不闭合但不抛异常，重打印结果快照钉死
        Message mJSON_ESCAPES = mergeRoundTrip("JSON", fx.escapes());
        assertNotEquals(fx.escapes(), mJSON_ESCAPES);
        assertEquals(RT_JSON_ESCAPES_PRINT, Protobuf2JsonFormat.printToString(mJSON_ESCAPES));
        assertEquals(fx.doc(), mergeRoundTrip("JSON", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("JSON", fx.empty()));
    }

    @Test
    public void propsMergeRoundTripSnapshots() throws Exception {
        assertEquals(fx.full(), mergeRoundTrip("PROPS", fx.full()));
        assertEquals(fx.special(), mergeRoundTrip("PROPS", fx.special()));
        assertEquals(fx.negSpecial(), mergeRoundTrip("PROPS", fx.negSpecial()));
        assertEquals(fx.negZero(), mergeRoundTrip("PROPS", fx.negZero()));
        // UNKNOWN：现状往返不闭合，抛异常并逐字钉死消息
        Protobuf2JavaPropsFormat.ParseException pe1 = assertThrows(Protobuf2JavaPropsFormat.ParseException.class,
                () -> mergeRoundTrip("PROPS", fx.withUnknown()));
        assertEquals("2:1: Expected identifier.", pe1.getMessage());
        // MSGSET：现状往返不闭合，抛异常并逐字钉死消息
        Protobuf2JavaPropsFormat.ParseException pe2 = assertThrows(Protobuf2JavaPropsFormat.ParseException.class,
                () -> mergeRoundTrip("PROPS", fx.msgSet()));
        assertEquals("1:30: Expected \".\".", pe2.getMessage());
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
        // UNKNOWN：现状往返不闭合但不抛异常，重打印结果快照钉死
        Message mCOUCH_UNKNOWN = mergeRoundTrip("COUCH", fx.withUnknown());
        assertNotEquals(fx.withUnknown(), mCOUCH_UNKNOWN);
        assertEquals(RT_COUCH_UNKNOWN_PRINT, Protobuf2CouchDBFormat.printToString(mCOUCH_UNKNOWN));
        assertEquals(fx.msgSet(), mergeRoundTrip("COUCH", fx.msgSet()));
        // ESCAPES：现状往返不闭合但不抛异常，重打印结果快照钉死
        Message mCOUCH_ESCAPES = mergeRoundTrip("COUCH", fx.escapes());
        assertNotEquals(fx.escapes(), mCOUCH_ESCAPES);
        assertEquals(RT_COUCH_ESCAPES_PRINT, Protobuf2CouchDBFormat.printToString(mCOUCH_ESCAPES));
        assertEquals(fx.doc(), mergeRoundTrip("COUCH", fx.doc()));
        assertEquals(fx.empty(), mergeRoundTrip("COUCH", fx.empty()));
    }

    private static final String RT_JSON_UNKNOWN_PRINT = "{\"opt_string\": \"known\"}";

    private static final String RT_JSON_ESCAPES_PRINT = "{\"opt_string\": \"q\\\"a'p\\\\bs\\nnr\\ttv \\u0001\\u001b\\u0007 vk é\\ud83d\\ude00\",\"opt_bytes\": \"\\a\\b\\v\\f\\u0000\\u0001\\u001f !\\\"\\'\\\\\u007f0\\uffafAz~\"}";

    private static final String RT_COUCH_UNKNOWN_PRINT = "{\"opt_string\": \"known\"}";

    private static final String RT_COUCH_ESCAPES_PRINT = "{\"opt_string\": \"q\\\"a'p\\\\bs\\nnr\\ttv \\u0001\\u001b\\u0007 vk é\\ud83d\\ude00\",\"opt_bytes\": \"\\a\\b\\v\\f\\u0000\\u0001\\u001f !\\\"\\'\\\\\u007f0\\uffafAz~\"}";

}
