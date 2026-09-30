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
import com.google.protobuf.UnknownFieldSet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 重构前行为钉桩（golden 快照）：五格式类 × printToString（及 printFiles/Collection/printFieldToString
 * 与 print(UF)/printToString(UF) 公开渲染面）的输出快照。期望字符串为收敛前现实现捕获输出，
 * 重构后期望值一字不改仍须全绿。现状差异专列：Html 的 STRING 值不转义（原样 toString）、
 * JavaProps 的 ENUM 不加引号、CouchDB 的 _id/_rev 字段名覆写、Xml 与 Json 的 escapeBytes
 * default 分支八进制 vs \\uXXXX 差异。
 */
public class FormatGoldenTest {

    private final FormatTestFixtures fx = new FormatTestFixtures();

    @Test
    public void xmlPrintToStringSnapshots() {
        assertEquals(GOLDEN_XML_FULL, Protobuf2XmlFormat.printToString(fx.full()));
        assertEquals(GOLDEN_XML_SPECIAL, Protobuf2XmlFormat.printToString(fx.special()));
        assertEquals(GOLDEN_XML_NEGSPECIAL, Protobuf2XmlFormat.printToString(fx.negSpecial()));
        assertEquals(GOLDEN_XML_NEGZERO, Protobuf2XmlFormat.printToString(fx.negZero()));
        assertEquals(GOLDEN_XML_UNKNOWN, Protobuf2XmlFormat.printToString(fx.withUnknown()));
        assertEquals(GOLDEN_XML_MSGSET, Protobuf2XmlFormat.printToString(fx.msgSet()));
        assertEquals(GOLDEN_XML_ESCAPES, Protobuf2XmlFormat.printToString(fx.escapes()));
        assertEquals(GOLDEN_XML_DOC, Protobuf2XmlFormat.printToString(fx.doc()));
        assertEquals(GOLDEN_XML_EMPTY, Protobuf2XmlFormat.printToString(fx.empty()));
    }

    @Test
    public void jsonPrintToStringSnapshots() {
        assertEquals(GOLDEN_JSON_FULL, Protobuf2JsonFormat.printToString(fx.full()));
        assertEquals(GOLDEN_JSON_SPECIAL, Protobuf2JsonFormat.printToString(fx.special()));
        assertEquals(GOLDEN_JSON_NEGSPECIAL, Protobuf2JsonFormat.printToString(fx.negSpecial()));
        assertEquals(GOLDEN_JSON_NEGZERO, Protobuf2JsonFormat.printToString(fx.negZero()));
        assertEquals(GOLDEN_JSON_UNKNOWN, Protobuf2JsonFormat.printToString(fx.withUnknown()));
        assertEquals(GOLDEN_JSON_MSGSET, Protobuf2JsonFormat.printToString(fx.msgSet()));
        assertEquals(GOLDEN_JSON_ESCAPES, Protobuf2JsonFormat.printToString(fx.escapes()));
        assertEquals(GOLDEN_JSON_DOC, Protobuf2JsonFormat.printToString(fx.doc()));
        assertEquals(GOLDEN_JSON_EMPTY, Protobuf2JsonFormat.printToString(fx.empty()));
    }

    @Test
    public void htmlPrintToStringSnapshots() {
        assertEquals(GOLDEN_HTML_FULL, Protobuf2HtmlFormat.printToString(fx.full()));
        assertEquals(GOLDEN_HTML_SPECIAL, Protobuf2HtmlFormat.printToString(fx.special()));
        assertEquals(GOLDEN_HTML_NEGSPECIAL, Protobuf2HtmlFormat.printToString(fx.negSpecial()));
        assertEquals(GOLDEN_HTML_NEGZERO, Protobuf2HtmlFormat.printToString(fx.negZero()));
        assertEquals(GOLDEN_HTML_UNKNOWN, Protobuf2HtmlFormat.printToString(fx.withUnknown()));
        assertEquals(GOLDEN_HTML_MSGSET, Protobuf2HtmlFormat.printToString(fx.msgSet()));
        assertEquals(GOLDEN_HTML_ESCAPES, Protobuf2HtmlFormat.printToString(fx.escapes()));
        assertEquals(GOLDEN_HTML_DOC, Protobuf2HtmlFormat.printToString(fx.doc()));
        assertEquals(GOLDEN_HTML_EMPTY, Protobuf2HtmlFormat.printToString(fx.empty()));
    }

    @Test
    public void propsPrintToStringSnapshots() {
        assertEquals(GOLDEN_PROPS_FULL, Protobuf2JavaPropsFormat.printToString(fx.full()));
        assertEquals(GOLDEN_PROPS_SPECIAL, Protobuf2JavaPropsFormat.printToString(fx.special()));
        assertEquals(GOLDEN_PROPS_NEGSPECIAL, Protobuf2JavaPropsFormat.printToString(fx.negSpecial()));
        assertEquals(GOLDEN_PROPS_NEGZERO, Protobuf2JavaPropsFormat.printToString(fx.negZero()));
        assertEquals(GOLDEN_PROPS_UNKNOWN, Protobuf2JavaPropsFormat.printToString(fx.withUnknown()));
        assertEquals(GOLDEN_PROPS_MSGSET, Protobuf2JavaPropsFormat.printToString(fx.msgSet()));
        assertEquals(GOLDEN_PROPS_ESCAPES, Protobuf2JavaPropsFormat.printToString(fx.escapes()));
        assertEquals(GOLDEN_PROPS_DOC, Protobuf2JavaPropsFormat.printToString(fx.doc()));
        assertEquals(GOLDEN_PROPS_EMPTY, Protobuf2JavaPropsFormat.printToString(fx.empty()));
    }

    @Test
    public void couchPrintToStringSnapshots() {
        assertEquals(GOLDEN_COUCH_FULL, Protobuf2CouchDBFormat.printToString(fx.full()));
        assertEquals(GOLDEN_COUCH_SPECIAL, Protobuf2CouchDBFormat.printToString(fx.special()));
        assertEquals(GOLDEN_COUCH_NEGSPECIAL, Protobuf2CouchDBFormat.printToString(fx.negSpecial()));
        assertEquals(GOLDEN_COUCH_NEGZERO, Protobuf2CouchDBFormat.printToString(fx.negZero()));
        assertEquals(GOLDEN_COUCH_UNKNOWN, Protobuf2CouchDBFormat.printToString(fx.withUnknown()));
        assertEquals(GOLDEN_COUCH_MSGSET, Protobuf2CouchDBFormat.printToString(fx.msgSet()));
        assertEquals(GOLDEN_COUCH_ESCAPES, Protobuf2CouchDBFormat.printToString(fx.escapes()));
        assertEquals(GOLDEN_COUCH_DOC, Protobuf2CouchDBFormat.printToString(fx.doc()));
        assertEquals(GOLDEN_COUCH_EMPTY, Protobuf2CouchDBFormat.printToString(fx.empty()));
    }

    @Test
    public void unknownFieldSetOverloadSnapshots() {
        final UnknownFieldSet uf = fx.unknownFieldSet();
        assertEquals(GOLDEN_XML_UF, Protobuf2XmlFormat.printToString(uf));
        assertEquals(GOLDEN_JSON_UF, Protobuf2JsonFormat.printToString(uf));
        assertEquals(GOLDEN_HTML_UF, Protobuf2HtmlFormat.printToString(uf));
        assertEquals(GOLDEN_PROPS_UF, Protobuf2JavaPropsFormat.printToString(uf));
        assertEquals(GOLDEN_COUCH_UF, Protobuf2CouchDBFormat.printToString(uf));
    }

    @Test
    public void jsonCollectionAndPrintFilesSnapshots() throws IOException {
        final List<Message> messages = fx.collection();
        assertEquals(GOLDEN_JSON_COLLECTION, Protobuf2JsonFormat.printToString(messages));

        StringBuilder sb = new StringBuilder();
        Protobuf2JsonFormat.printFiles(fx.full(), sb);
        assertEquals(GOLDEN_JSON_FILES_FULL, sb.toString());

        StringBuilder sb2 = new StringBuilder();
        Protobuf2JsonFormat.printFiles(fx.withUnknown(), sb2);
        assertEquals(GOLDEN_JSON_FILES_UNKNOWN, sb2.toString());
    }

    @Test
    public void propsPrintFieldSnapshots() {
        assertEquals(GOLDEN_PROPS_FIELD_REP, Protobuf2JavaPropsFormat.printFieldToString(
                fx.allTypesDescriptor().findFieldByName("rep_int32"), fx.full().getRepInt32List()));
        assertEquals(GOLDEN_PROPS_FIELD_STR, Protobuf2JavaPropsFormat.printFieldToString(
                fx.allTypesDescriptor().findFieldByName("opt_string"), "v"));
        assertEquals(GOLDEN_PROPS_FIELD_BYTES, Protobuf2JavaPropsFormat.printFieldToString(
                fx.allTypesDescriptor().findFieldByName("opt_bytes"), fx.escapes().getOptBytes()));
        assertEquals(GOLDEN_PROPS_FIELD_ENUM, Protobuf2JavaPropsFormat.printFieldToString(
                fx.allTypesDescriptor().findFieldByName("opt_enum"),
                fx.full().getField(fx.allTypesDescriptor().findFieldByName("opt_enum"))));
    }

    // ================================================================
    // 重构前捕获的现状快照（golden 账目），期望值禁止改动
    // ================================================================

    private static final String GOLDEN_XML_FULL = "<AllTypes><opt_int32>-123</opt_int32><opt_int64>-1234567890123</opt_int64><opt_uint32>4294967295</opt_uint32><opt_uint64>18446744073709551615</opt_uint64><opt_sint32>-7</opt_sint32><opt_sint64>-8</opt_sint64><opt_fixed32>4294967295</opt_fixed32><opt_fixed64>18446744073709551614</opt_fixed64><opt_sfixed32>-5</opt_sfixed32><opt_sfixed64>-6</opt_sfixed64><opt_float>1.5</opt_float><opt_double>-2.25</opt_double><opt_bool>true</opt_bool><opt_string>hello</opt_string><opt_bytes>ABC</opt_bytes><opt_enum>BLUE</opt_enum><opt_nested><name>inner</name><count>3</count></opt_nested><OptGroup><inner>group-val</inner></OptGroup><rep_int32>1</rep_int32><rep_int32>-2</rep_int32><rep_packed>10</rep_packed><rep_packed>20</rep_packed><rep_string>s1</rep_string><rep_string>s2</rep_string><rep_nested><name>n0</name><count>0</count></rep_nested><rep_nested><name>n1</name><count>1</count></rep_nested><rep_enum>RED</rep_enum><rep_enum>GREEN</rep_enum><extension type=\"tny.protobuf.test.ext_string\">ext-val</extension><extension type=\"tny.protobuf.test.ext_int\">777</extension></AllTypes>";

    private static final String GOLDEN_XML_SPECIAL = "<AllTypes><opt_float>NaN</opt_float><opt_double>Infinity</opt_double><opt_bool>false</opt_bool></AllTypes>";

    private static final String GOLDEN_XML_NEGSPECIAL = "<AllTypes><opt_float>-Infinity</opt_float><opt_double>-Infinity</opt_double></AllTypes>";

    private static final String GOLDEN_XML_NEGZERO = "<AllTypes><opt_float>-0.0</opt_float><opt_double>-0.0</opt_double></AllTypes>";

    private static final String GOLDEN_XML_UNKNOWN = "<AllTypes><opt_string>known</opt_string><unknown-field index=\"995\"><unknown-field index=\"2\">42</unknown-field></unknown-field><unknown-field index=\"996\">ld</unknown-field><unknown-field index=\"996\">\\000\u007f\\377</unknown-field><unknown-field index=\"997\">0x0123456789abcdef</unknown-field><unknown-field index=\"998\">0x1234abcd</unknown-field><unknown-field index=\"999\">12345</unknown-field><unknown-field index=\"999\">1</unknown-field></AllTypes>";

    private static final String GOLDEN_XML_MSGSET = "<TestMessageSet><extension type=\"tny.protobuf.test.MsgSetExt\"><m>7</m></extension></TestMessageSet>";

    private static final String GOLDEN_XML_ESCAPES = "<AllTypes><opt_string>q\\\"a\\'p\\\\bs\\nnr\\ttv \\001\\033\\a vk \\303\\251\\360\\237\\230\\200</opt_string><opt_bytes>\\a\\b\\v\\f\\000\\001\\037 !\\\"\\'\\\\\u007f\\200\\377Az~</opt_bytes></AllTypes>";

    private static final String GOLDEN_XML_DOC = "<Doc><id>doc-1</id><rev>1-abc</rev><version>42</version></Doc>";

    private static final String GOLDEN_XML_EMPTY = "<AllTypes></AllTypes>";

    private static final String GOLDEN_XML_UF = "<message><unknown-field index=\"995\"><unknown-field index=\"2\">42</unknown-field></unknown-field><unknown-field index=\"996\">ld</unknown-field><unknown-field index=\"996\">\\000\u007f\\377</unknown-field><unknown-field index=\"997\">0x0123456789abcdef</unknown-field><unknown-field index=\"998\">0x1234abcd</unknown-field><unknown-field index=\"999\">12345</unknown-field><unknown-field index=\"999\">1</unknown-field></message>";

    private static final String GOLDEN_JSON_FULL = "{\"opt_int32\": -123,\"opt_int64\": -1234567890123,\"opt_uint32\": 4294967295,\"opt_uint64\": 18446744073709551615,\"opt_sint32\": -7,\"opt_sint64\": -8,\"opt_fixed32\": 4294967295,\"opt_fixed64\": 18446744073709551614,\"opt_sfixed32\": -5,\"opt_sfixed64\": -6,\"opt_float\": 1.5,\"opt_double\": -2.25,\"opt_bool\": true,\"opt_string\": \"hello\",\"opt_bytes\": \"ABC\",\"opt_enum\": \"BLUE\",\"opt_nested\": {\"name\": \"inner\",\"count\": 3},\"OptGroup\": {\"inner\": \"group-val\"},\"rep_int32\": [1,-2],\"rep_packed\": [10,20],\"rep_string\": [\"s1\",\"s2\"],\"rep_nested\": [{\"name\": \"n0\",\"count\": 0},{\"name\": \"n1\",\"count\": 1}],\"rep_enum\": [\"RED\",\"GREEN\"],\"tny.protobuf.test.ext_string\": \"ext-val\",\"tny.protobuf.test.ext_int\": 777}";

    private static final String GOLDEN_JSON_SPECIAL = "{\"opt_float\": NaN,\"opt_double\": Infinity,\"opt_bool\": false}";

    private static final String GOLDEN_JSON_NEGSPECIAL = "{\"opt_float\": -Infinity,\"opt_double\": -Infinity}";

    private static final String GOLDEN_JSON_NEGZERO = "{\"opt_float\": -0.0,\"opt_double\": -0.0}";

    private static final String GOLDEN_JSON_UNKNOWN = "{\"opt_string\": \"known\", \"995\": [{\"2\": [42]}], \"996\": [\"ld\", \"\\u0000\u007f\\uffff\"], \"997\": [0x0123456789abcdef], \"998\": [0x1234abcd], \"999\": [12345, 1]}";

    private static final String GOLDEN_JSON_MSGSET = "{\"tny.protobuf.test.MsgSetExt\": {\"m\": 7}}";

    private static final String GOLDEN_JSON_ESCAPES = "{\"opt_string\": \"q\\\"a'p\\\\bs\\nnr\\ttv \\u0001\\u001b\\u0007 vk é\\ud83d\\ude00\",\"opt_bytes\": \"\\a\\b\\v\\f\\u0000\\u0001\\u001f !\\\"\\'\\\\\u007f\\uff80\\uffffAz~\"}";

    private static final String GOLDEN_JSON_DOC = "{\"id\": \"doc-1\",\"rev\": \"1-abc\",\"version\": 42}";

    private static final String GOLDEN_JSON_EMPTY = "{}";

    private static final String GOLDEN_JSON_UF = "{\"995\": [{\"2\": [42]}], \"996\": [\"ld\", \"\\u0000\u007f\\uffff\"], \"997\": [0x0123456789abcdef], \"998\": [0x1234abcd], \"999\": [12345, 1]}";

    private static final String GOLDEN_HTML_FULL = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_int32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-123</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_int64</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-1234567890123</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_uint32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">4294967295</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_uint64</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">18446744073709551615</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_sint32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-7</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_sint64</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-8</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_fixed32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">4294967295</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_fixed64</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">18446744073709551614</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_sfixed32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-5</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_sfixed64</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-6</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_float</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">1.5</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_double</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-2.25</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_bool</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">true</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_string</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"hello\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_bytes</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"ABC\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_enum</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">BLUE</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_nested</span> <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\"><span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\"><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">name</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"inner\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">count</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">3</span><br/></span></div><span style=\"color: red;\">}</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">OptGroup</span> <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\"><span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\"><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">inner</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"group-val\"</span><br/></span></div><span style=\"color: red;\">}</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_int32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">1</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_int32</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-2</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_packed</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">10</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_packed</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">20</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_string</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"s1\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_string</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"s2\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_nested</span> <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\"><span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\"><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">name</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"n0\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">count</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">0</span><br/></span></div><span style=\"color: red;\">}</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_nested</span> <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\"><span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\"><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">name</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"n1\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">count</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">1</span><br/></span></div><span style=\"color: red;\">}</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_enum</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">RED</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rep_enum</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">GREEN</span><br/>[<span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">tny.protobuf.test.ext_string</span>]: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"ext-val\"</span><br/>[<span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">tny.protobuf.test.ext_int</span>]: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">777</span><br/></body></html>";

    private static final String GOLDEN_HTML_SPECIAL = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_float</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">NaN</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_double</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">Infinity</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_bool</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">false</span><br/></body></html>";

    private static final String GOLDEN_HTML_NEGSPECIAL = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_float</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-Infinity</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_double</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-Infinity</span><br/></body></html>";

    private static final String GOLDEN_HTML_NEGZERO = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_float</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-0.0</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_double</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">-0.0</span><br/></body></html>";

    private static final String GOLDEN_HTML_UNKNOWN = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_string</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"known\"</span><br/>995 <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\">2: 42<br/></div><span style=\"color: red;\">}</span><br/>996: \"ld\"<br/>996: \"\\000\u007f\\377\"<br/>997: 0x0123456789abcdef<br/>998: 0x1234abcd<br/>999: 12345<br/>999: 1<br/></body></html>";

    private static final String GOLDEN_HTML_MSGSET = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.TestMessageSet</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.TestMessageSet</div>[<span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">tny.protobuf.test.MsgSetExt</span>] <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\"><span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\"><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">m</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">7</span><br/></span></div><span style=\"color: red;\">}</span><br/></body></html>";

    private static final String GOLDEN_HTML_ESCAPES = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_string</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"<br/>nr\ttv \u0001\u001b\u0007 vk é😀\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">opt_bytes</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"\\a\\b\\v\\f\\000\\001\\037 !\\\"\\'\\\\\u007f\\200\\377Az~\"</span><br/></body></html>";

    private static final String GOLDEN_HTML_DOC = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.Doc</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.Doc</div><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">id</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"doc-1\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">rev</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">\"1-abc\"</span><br/><span style=\"font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;\">version</span>: <span style=\"color: #3300FF;font-size: 13px; font-family: sans-serif;\">42</span><br/></body></html>";

    private static final String GOLDEN_HTML_EMPTY = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /><title>tny.protobuf.test.AllTypes</title></head><body><div style=\"color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;\">message : tny.protobuf.test.AllTypes</div></body></html>";

    private static final String GOLDEN_HTML_UF = "<html><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" /></head><body>995 <span style=\"color: red;\">{</span><br/><div style=\"margin-left: 25px\">2: 42<br/></div><span style=\"color: red;\">}</span><br/>996: \"ld\"<br/>996: \"\\000\u007f\\377\"<br/>997: 0x0123456789abcdef<br/>998: 0x1234abcd<br/>999: 12345<br/>999: 1<br/></body></html>";

    private static final String GOLDEN_PROPS_FULL = "opt_int32=-123\nopt_int64=-1234567890123\nopt_uint32=4294967295\nopt_uint64=18446744073709551615\nopt_sint32=-7\nopt_sint64=-8\nopt_fixed32=4294967295\nopt_fixed64=18446744073709551614\nopt_sfixed32=-5\nopt_sfixed64=-6\nopt_float=1.5\nopt_double=-2.25\nopt_bool=true\nopt_string=\"hello\"\nopt_bytes=\"ABC\"\nopt_enum=BLUE\nopt_nested.name=\"inner\"\nopt_nested.count=3\nOptGroup.inner=\"group-val\"\nrep_int32[0]=1\nrep_int32[1]=-2\nrep_packed[0]=10\nrep_packed[1]=20\nrep_string[0]=\"s1\"\nrep_string[1]=\"s2\"\nrep_nested[0].name=\"n0\"\nrep_nested[0].count=0\nrep_nested[1].name=\"n1\"\nrep_nested[1].count=1\nrep_enum[0]=RED\nrep_enum[1]=GREEN\n[tny.protobuf.test.ext_string]=\"ext-val\"\n[tny.protobuf.test.ext_int]=777\n";

    private static final String GOLDEN_PROPS_SPECIAL = "opt_float=NaN\nopt_double=Infinity\nopt_bool=false\n";

    private static final String GOLDEN_PROPS_NEGSPECIAL = "opt_float=-Infinity\nopt_double=-Infinity\n";

    private static final String GOLDEN_PROPS_NEGZERO = "opt_float=-0.0\nopt_double=-0.0\n";

    private static final String GOLDEN_PROPS_UNKNOWN = "opt_string=\"known\"\n995.2=42\n\n996=\"ld\"\n996=\"\\000\u007f\\377\"\n997=0x0123456789abcdef\n998=0x1234abcd\n999=12345\n999=1\n";

    private static final String GOLDEN_PROPS_MSGSET = "[tny.protobuf.test.MsgSetExt]m=7\n";

    private static final String GOLDEN_PROPS_ESCAPES = "opt_string=\"q\\\"a\\'p\\\\bs\\nnr\\ttv \\001\\033\\a vk \\303\\251\\360\\237\\230\\200\"\nopt_bytes=\"\\a\\b\\v\\f\\000\\001\\037 !\\\"\\'\\\\\u007f\\200\\377Az~\"\n";

    private static final String GOLDEN_PROPS_DOC = "id=\"doc-1\"\nrev=\"1-abc\"\nversion=42\n";

    private static final String GOLDEN_PROPS_EMPTY = "";

    private static final String GOLDEN_PROPS_UF = "995.2=42\n\n996=\"ld\"\n996=\"\\000\u007f\\377\"\n997=0x0123456789abcdef\n998=0x1234abcd\n999=12345\n999=1\n";

    private static final String GOLDEN_COUCH_FULL = "{\"opt_int32\": -123,\"opt_int64\": -1234567890123,\"opt_uint32\": 4294967295,\"opt_uint64\": 18446744073709551615,\"opt_sint32\": -7,\"opt_sint64\": -8,\"opt_fixed32\": 4294967295,\"opt_fixed64\": 18446744073709551614,\"opt_sfixed32\": -5,\"opt_sfixed64\": -6,\"opt_float\": 1.5,\"opt_double\": -2.25,\"opt_bool\": true,\"opt_string\": \"hello\",\"opt_bytes\": \"ABC\",\"opt_enum\": \"BLUE\",\"opt_nested\": {\"name\": \"inner\",\"count\": 3},\"OptGroup\": {\"inner\": \"group-val\"},\"rep_int32\": [1,-2],\"rep_packed\": [10,20],\"rep_string\": [\"s1\",\"s2\"],\"rep_nested\": [{\"name\": \"n0\",\"count\": 0},{\"name\": \"n1\",\"count\": 1}],\"rep_enum\": [\"RED\",\"GREEN\"],\"tny.protobuf.test.ext_string\": \"ext-val\",\"tny.protobuf.test.ext_int\": 777}";

    private static final String GOLDEN_COUCH_SPECIAL = "{\"opt_float\": NaN,\"opt_double\": Infinity,\"opt_bool\": false}";

    private static final String GOLDEN_COUCH_NEGSPECIAL = "{\"opt_float\": -Infinity,\"opt_double\": -Infinity}";

    private static final String GOLDEN_COUCH_NEGZERO = "{\"opt_float\": -0.0,\"opt_double\": -0.0}";

    private static final String GOLDEN_COUCH_UNKNOWN = "{\"opt_string\": \"known\", \"995\": [{\"2\": [42]}], \"996\": [\"ld\", \"\\u0000\u007f\\uffff\"], \"997\": [0x0123456789abcdef], \"998\": [0x1234abcd], \"999\": [12345, 1]}";

    private static final String GOLDEN_COUCH_MSGSET = "{\"tny.protobuf.test.MsgSetExt\": {\"m\": 7}}";

    private static final String GOLDEN_COUCH_ESCAPES = "{\"opt_string\": \"q\\\"a'p\\\\bs\\nnr\\ttv \\u0001\\u001b\\u0007 vk é\\ud83d\\ude00\",\"opt_bytes\": \"\\a\\b\\v\\f\\u0000\\u0001\\u001f !\\\"\\'\\\\\u007f\\uff80\\uffffAz~\"}";

    private static final String GOLDEN_COUCH_DOC = "{\"_id\": \"doc-1\",\"_rev\": \"1-abc\",\"version\": 42}";

    private static final String GOLDEN_COUCH_EMPTY = "{}";

    private static final String GOLDEN_COUCH_UF = "{\"995\": [{\"2\": [42]}], \"996\": [\"ld\", \"\\u0000\u007f\\uffff\"], \"997\": [0x0123456789abcdef], \"998\": [0x1234abcd], \"999\": [12345, 1]}";

    private static final String GOLDEN_JSON_COLLECTION = "[{\"opt_int32\": -123,\"opt_int64\": -1234567890123,\"opt_uint32\": 4294967295,\"opt_uint64\": 18446744073709551615,\"opt_sint32\": -7,\"opt_sint64\": -8,\"opt_fixed32\": 4294967295,\"opt_fixed64\": 18446744073709551614,\"opt_sfixed32\": -5,\"opt_sfixed64\": -6,\"opt_float\": 1.5,\"opt_double\": -2.25,\"opt_bool\": true,\"opt_string\": \"hello\",\"opt_bytes\": \"ABC\",\"opt_enum\": \"BLUE\",\"opt_nested\": {\"name\": \"inner\",\"count\": 3},\"OptGroup\": {\"inner\": \"group-val\"},\"rep_int32\": [1,-2],\"rep_packed\": [10,20],\"rep_string\": [\"s1\",\"s2\"],\"rep_nested\": [{\"name\": \"n0\",\"count\": 0},{\"name\": \"n1\",\"count\": 1}],\"rep_enum\": [\"RED\",\"GREEN\"],\"tny.protobuf.test.ext_string\": \"ext-val\",\"tny.protobuf.test.ext_int\": 777},{\"id\": \"doc-1\",\"rev\": \"1-abc\",\"version\": 42}]";

    private static final String GOLDEN_JSON_FILES_FULL = "\"opt_int32\": -123,\"opt_int64\": -1234567890123,\"opt_uint32\": 4294967295,\"opt_uint64\": 18446744073709551615,\"opt_sint32\": -7,\"opt_sint64\": -8,\"opt_fixed32\": 4294967295,\"opt_fixed64\": 18446744073709551614,\"opt_sfixed32\": -5,\"opt_sfixed64\": -6,\"opt_float\": 1.5,\"opt_double\": -2.25,\"opt_bool\": true,\"opt_string\": \"hello\",\"opt_bytes\": \"ABC\",\"opt_enum\": \"BLUE\",\"opt_nested\": {\"name\": \"inner\",\"count\": 3},\"OptGroup\": {\"inner\": \"group-val\"},\"rep_int32\": [1,-2],\"rep_packed\": [10,20],\"rep_string\": [\"s1\",\"s2\"],\"rep_nested\": [{\"name\": \"n0\",\"count\": 0},{\"name\": \"n1\",\"count\": 1}],\"rep_enum\": [\"RED\",\"GREEN\"],\"tny.protobuf.test.ext_string\": \"ext-val\",\"tny.protobuf.test.ext_int\": 777";

    private static final String GOLDEN_JSON_FILES_UNKNOWN = "\"opt_string\": \"known\", \"995\": [{\"2\": [42]}], \"996\": [\"ld\", \"\\u0000\u007f\\uffff\"], \"997\": [0x0123456789abcdef], \"998\": [0x1234abcd], \"999\": [12345, 1]";

    private static final String GOLDEN_PROPS_FIELD_REP = "rep_int32[0]=1\nrep_int32[1]=-2\n";

    private static final String GOLDEN_PROPS_FIELD_STR = "opt_string=\"v\"\n";

    private static final String GOLDEN_PROPS_FIELD_BYTES = "opt_bytes=\"\\a\\b\\v\\f\\000\\001\\037 !\\\"\\'\\\\\u007f\\200\\377Az~\"\n";

    private static final String GOLDEN_PROPS_FIELD_ENUM = "opt_enum=BLUE\n";

}
