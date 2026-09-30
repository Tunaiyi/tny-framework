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
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import java.io.IOException;

/**
 * 五格式类共享的值渲染分派（reduce-code-duplication D2：类型 switch 单实现 + 装饰策略入参）。
 * <p>
 * {@link #renderFieldValue} 承载四类（Xml/Html/Json/JavaProps）printFieldValue 的逐字同构类型分派骨架，
 * 引号/转义/包裹等装饰差异全部由各格式传入自己的 {@link ValueSink} 实现表达——Html 的 STRING 值原样
 * toString 不转义、JavaProps 的 ENUM 不加引号、Json 的 STRING/BYTES/ENUM 加引号且转义器为 JSON 形态、
 * Xml 的 STRING/BYTES/ENUM 一律不加引号，全部现状保持，收敛不改行为。
 * <p>
 * {@link #extensionPrintName} 承载四份逐字相同的 field.isExtension() + MessageSet 特判选名逻辑；
 * {@link #fieldPrintName} 承载 GROUP 用类型名、其余用字段名的四份相同选名逻辑。
 */
final class FormatValueRenderer {

    private FormatValueRenderer() {
    }

    /**
     * 类型分派的装饰出口。各格式的实现只保留本格式既有装饰差异，不做任何类型语义修正。
     */
    interface ValueSink {

        /**
         * 输出已格式化好的标量文本（INT/FLOAT/BOOL/UINT 族，原 toString 或 unsignedToString 结果）。
         */
        void printRaw(CharSequence text) throws IOException;

        /**
         * 输出 STRING 值。实现自行决定引号与转义器（含 Html 现状的原样输出不转义）。
         */
        void printString(String value) throws IOException;

        /**
         * 输出 BYTES 值。实现自行决定引号与转义形态（OCTAL vs \\uXXXX）。
         */
        void printBytes(ByteString value) throws IOException;

        /**
         * 输出 ENUM 值名。实现自行决定是否加引号。
         */
        void printEnum(EnumValueDescriptor value) throws IOException;

        /**
         * 输出嵌套 MESSAGE/GROUP（递归回本格式的 print(Message, generator)）。
         */
        void printMessage(Message value) throws IOException;

    }

    static void renderFieldValue(FieldDescriptor field, Object value, ValueSink sink) throws IOException {
        switch (field.getType()) {
            case INT32:
            case INT64:
            case SINT32:
            case SINT64:
            case SFIXED32:
            case SFIXED64:
            case FLOAT:
            case DOUBLE:
            case BOOL:
                // Good old toString() does what we want for these types.
                sink.printRaw(value.toString());
                break;

            case UINT32:
            case FIXED32:
                sink.printRaw(FormatTextSupport.unsignedToString((Integer) value));
                break;

            case UINT64:
            case FIXED64:
                sink.printRaw(FormatTextSupport.unsignedToString((Long) value));
                break;

            case STRING:
                sink.printString((String) value);
                break;

            case BYTES: {
                sink.printBytes((ByteString) value);
                break;
            }

            case ENUM: {
                sink.printEnum((EnumValueDescriptor) value);
                break;
            }

            case MESSAGE:
            case GROUP:
                sink.printMessage((Message) value);
                break;
        }
    }

    /**
     * extension 字段的打印名：MessageSet 兼容特判（四格式逐字相同的现状条件）。
     */
    static String extensionPrintName(FieldDescriptor field) {
        // We special-case MessageSet elements for compatibility with proto1.
        if (field.getContainingType().getOptions().getMessageSetWireFormat()
            && (field.getType() == FieldDescriptor.Type.MESSAGE) && (field.isOptional())
            // object equality
            && (field.getExtensionScope() == field.getMessageType())) {
            return field.getMessageType().getFullName();
        }
        return field.getFullName();
    }

    /**
     * 非 extension 字段的打印名：GROUP 用消息类型原名，其余用字段名（四格式逐字相同的现状逻辑）。
     */
    static String fieldPrintName(FieldDescriptor field) {
        if (field.getType() == FieldDescriptor.Type.GROUP) {
            // Groups must be serialized with their original capitalization.
            return field.getMessageType().getName();
        }
        return field.getName();
    }

}
