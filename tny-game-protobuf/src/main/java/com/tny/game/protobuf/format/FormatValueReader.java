package com.tny.game.protobuf.format;
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

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

/**
 * Xml/Json/JavaProps 三类 handlePrimitive 标量类型分派的共享实现（reduce-code-duplication D2 收口：
 * 单分派实现 + 扫描面装饰策略入参，与打印面 FormatValueRenderer.ValueSink 对称）。
 * <p>
 * 类型 switch 各 case 的 consumeXxx 调用序列、ENUM 校验（数字/名称双路径）与
 * MESSAGE/GROUP 死分支 RuntimeException("Can't get here.") 为三份逐字克隆，收敛到
 * {@link #readPrimitive}；现状差异全部由 {@link Scanner} 实现承载，不做任何解析行为修正：
 * <ul>
 * <li>Xml/JavaProps 直接传 FormatTokenizerCore 本尊（其实现 Scanner&lt;Failure&gt;）：消费
 * 操作直通核心，异常为现状 Failure（"前一行:前列: 消息"）。</li>
 * <li>Json 传 Protobuf2JsonFormat 内的外壳适配器：消费操作经 Json.Tokenizer 外壳既有虚分派
 * （重抛本类现状 ParseException；ENUM 标识符读取经外壳 consumeIdentifier——
 * Protobuf2CouchDBFormat.Tokenizer 的 _id/_rev 覆写链保持参与分派，行为不变）。</li>
 * </ul>
 * 格式专属前缀（Json 的 "null" 字面量旁路、JavaProps 的 "=" 分隔符消费）留在各外壳方法，不入共享分派。
 */
final class FormatValueReader {

    private FormatValueReader() {
    }

    /**
     * 标量分派所需的现状扫描面。E 为本格式现状受检异常类型（Xml/Props: Failure；Json: ParseException）。
     */
    interface Scanner<E extends Exception> {

        boolean lookingAtInteger();

        String consumeIdentifier() throws E;

        int consumeInt32() throws E;

        long consumeInt64() throws E;

        int consumeUInt32() throws E;

        long consumeUInt64() throws E;

        float consumeFloat() throws E;

        double consumeDouble() throws E;

        boolean consumeBoolean() throws E;

        String consumeString() throws E;

        ByteString consumeByteString() throws E;

        /**
         * 构造"值不存在"错误的现状异常：消息带前一个 token 的"行:列:"前缀，类型为本类现状。
         */
        E positionedEnumFailure(String description);

    }

    /**
     * 三份 handlePrimitive 类型 switch 体逐字（D2：差异以 Scanner 参数承载，零行为修正）。
     */
    static <E extends Exception> Object readPrimitive(FieldDescriptor field, Scanner<E> scanner) throws E {
        Object value = null;
        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
                value = scanner.consumeInt32();
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
                value = scanner.consumeInt64();
                break;

            case UINT32:
            case FIXED32:
                value = scanner.consumeUInt32();
                break;

            case UINT64:
            case FIXED64:
                value = scanner.consumeUInt64();
                break;

            case FLOAT:
                value = scanner.consumeFloat();
                break;

            case DOUBLE:
                value = scanner.consumeDouble();
                break;

            case BOOL:
                value = scanner.consumeBoolean();
                break;

            case STRING:
                value = scanner.consumeString();
                break;

            case BYTES:
                value = scanner.consumeByteString();
                break;

            case ENUM: {
                EnumDescriptor enumType = field.getEnumType();

                if (scanner.lookingAtInteger()) {
                    int number = scanner.consumeInt32();
                    value = enumType.findValueByNumber(number);
                    if (value == null) {
                        throw scanner.positionedEnumFailure("Enum type \"" + enumType.getFullName()
                                                            + "\" has no value with number " + number + ".");
                    }
                } else {
                    String id = scanner.consumeIdentifier();
                    value = enumType.findValueByName(id);
                    if (value == null) {
                        throw scanner.positionedEnumFailure("Enum type \"" + enumType.getFullName()
                                                            + "\" has no value named \"" + id + "\".");
                    }
                }

                break;
            }

            case MESSAGE:
            case GROUP:
                throw new RuntimeException("Can't get here.");
        }
        return value;
    }

    /**
     * Json 现状面（D2 收口）：操作全部经 Protobuf2JsonFormat.Tokenizer 外壳既有虚分派——
     * ENUM 标识符读取随之保持 Protobuf2CouchDBFormat.Tokenizer 的 _id/_rev 覆写链参与；
     * 异常类型为 Json 现状 ParseException，错误定位经外壳 parseExceptionPreviousToken 现状构造。
     */
    static final class TokenizerScanner implements Scanner<Protobuf2JsonFormat.ParseException> {

        private final Protobuf2JsonFormat.Tokenizer tokenizer;

        TokenizerScanner(Protobuf2JsonFormat.Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }

        @Override
        public boolean lookingAtInteger() {
            return tokenizer.lookingAtInteger();
        }

        @Override
        public String consumeIdentifier() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeIdentifier();
        }

        @Override
        public int consumeInt32() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeInt32();
        }

        @Override
        public long consumeInt64() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeInt64();
        }

        @Override
        public int consumeUInt32() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeUInt32();
        }

        @Override
        public long consumeUInt64() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeUInt64();
        }

        @Override
        public float consumeFloat() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeFloat();
        }

        @Override
        public double consumeDouble() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeDouble();
        }

        @Override
        public boolean consumeBoolean() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeBoolean();
        }

        @Override
        public String consumeString() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeString();
        }

        @Override
        public ByteString consumeByteString() throws Protobuf2JsonFormat.ParseException {
            return tokenizer.consumeByteString();
        }

        @Override
        public Protobuf2JsonFormat.ParseException positionedEnumFailure(String description) {
            return tokenizer.parseExceptionPreviousToken(description);
        }

    }

}
