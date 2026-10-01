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

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import com.tny.game.protobuf.format.test.FormatTestProtos;
import com.tny.game.protobuf.format.test.FormatTestProtos.AllTypes;
import com.tny.game.protobuf.format.test.FormatTestProtos.Color;
import com.tny.game.protobuf.format.test.FormatTestProtos.Doc;
import com.tny.game.protobuf.format.test.FormatTestProtos.Nested;
import com.tny.game.protobuf.format.test.FormatTestProtos.TestMessageSet;

import java.util.Arrays;
import java.util.List;

/**
 * golden/round-trip 钉桩共用测试夹具：覆盖全标量类型、repeated（packed 与非 packed）、嵌套、group、
 * 普通 extension、MessageSet 特判 extension、unknown fields 全五类、转义特殊字符集与浮点特殊值。
 */
public final class FormatTestFixtures {

    private final ExtensionRegistry registry = ExtensionRegistry.newInstance();

    public FormatTestFixtures() {
        FormatTestProtos.registerAllExtensions(registry);
    }

    public ExtensionRegistry registry() {
        return registry;
    }

    public Descriptors.Descriptor allTypesDescriptor() {
        return AllTypes.getDescriptor();
    }

    public AllTypes full() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptInt32(-123);
        b.setOptInt64(-1234567890123L);
        b.setOptUint32(-1);
        b.setOptUint64(-1L);
        b.setOptSint32(-7);
        b.setOptSint64(-8L);
        b.setOptFixed32(-1);
        b.setOptFixed64(-2L);
        b.setOptSfixed32(-5);
        b.setOptSfixed64(-6L);
        b.setOptFloat(1.5f);
        b.setOptDouble(-2.25);
        b.setOptBool(true);
        b.setOptString("hello");
        b.setOptBytes(ByteString.copyFromUtf8("ABC"));
        b.setOptEnum(Color.BLUE);
        b.setOptNested(Nested.newBuilder().setName("inner").setCount(3).build());
        b.setOptGroup(AllTypes.OptGroup.newBuilder().setInner("group-val").build());
        b.addRepInt32(1);
        b.addRepInt32(-2);
        b.addRepPacked(10);
        b.addRepPacked(20);
        b.addRepString("s1");
        b.addRepString("s2");
        b.addRepNested(Nested.newBuilder().setName("n0").setCount(0).build());
        b.addRepNested(Nested.newBuilder().setName("n1").setCount(1).build());
        b.addRepEnum(Color.RED);
        b.addRepEnum(Color.GREEN);
        b.setExtension(FormatTestProtos.extString, "ext-val");
        b.setExtension(FormatTestProtos.extInt, 777);
        return b.build();
    }

    public AllTypes special() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptFloat(Float.NaN);
        b.setOptDouble(Double.POSITIVE_INFINITY);
        b.setOptBool(false);
        return b.build();
    }

    public AllTypes negSpecial() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptFloat(Float.NEGATIVE_INFINITY);
        b.setOptDouble(Double.NEGATIVE_INFINITY);
        return b.build();
    }

    public AllTypes negZero() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptFloat(-0.0f);
        b.setOptDouble(-0.0d);
        return b.build();
    }

    public UnknownFieldSet unknownFieldSet() {
        return UnknownFieldSet.newBuilder()
                .addField(999, UnknownFieldSet.Field.newBuilder()
                        .addVarint(12345L)
                        .addVarint(1L)
                        .build())
                .addField(998, UnknownFieldSet.Field.newBuilder()
                        .addFixed32(0x1234abcd)
                        .build())
                .addField(997, UnknownFieldSet.Field.newBuilder()
                        .addFixed64(0x123456789abcdefL)
                        .build())
                .addField(996, UnknownFieldSet.Field.newBuilder()
                        .addLengthDelimited(ByteString.copyFromUtf8("ld"))
                        .addLengthDelimited(ByteString.copyFrom(new byte[]{0x00, 0x7F, (byte) 0xFF}))
                        .build())
                .addField(995, UnknownFieldSet.Field.newBuilder()
                        .addGroup(UnknownFieldSet.newBuilder()
                                .addField(2, UnknownFieldSet.Field.newBuilder().addVarint(42L).build())
                                .build())
                        .build())
                .build();
    }

    public AllTypes withUnknown() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptString("known");
        b.mergeUnknownFields(unknownFieldSet());
        return b.build();
    }

    public TestMessageSet msgSet() {
        return TestMessageSet.newBuilder()
                .setExtension(FormatTestProtos.MsgSetExt.msgSetExt,
                        FormatTestProtos.MsgSetExt.newBuilder().setM(7).build())
                .build();
    }

    public AllTypes escapes() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptString("q\"a'p\\bs\nnr\ttv \u0001\u001b\u0007 vk é😀");
        b.setOptBytes(ByteString.copyFrom(new byte[]{
                0x07, 0x08, 0x0B, 0x0C, 0x00, 0x01, 0x1F, 0x20, 0x21, 0x22, 0x27, 0x5C,
                0x7F, (byte) 0x80, (byte) 0xFF, 'A', 'z', '~'}));
        return b.build();
    }

    /**
     * fix-registered-defects 2.1 矩阵专用：\\u 四位权展开逐位取值样本
     * （0x0F 仅末位、0xF0/0x8F/0xF8/0xFF 覆盖 16³/16²/16¹/16⁰ 各位权，Json/Couch 打印为 \\uffXX 形态）。
     */
    public AllTypes unicodeWeights() {
        AllTypes.Builder b = AllTypes.newBuilder();
        b.setOptBytes(ByteString.copyFrom(new byte[]{
                0x0F, (byte) 0xF0, (byte) 0x8F, (byte) 0xF8, (byte) 0xFF, 0x7F}));
        return b.build();
    }

    public Doc doc() {
        return Doc.newBuilder().setId("doc-1").setRev("1-abc").setVersion(42).build();
    }

    public AllTypes empty() {
        return AllTypes.newBuilder().build();
    }

    public List<Message> collection() {
        return Arrays.asList(full(), doc());
    }
}
