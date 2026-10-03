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

/* 
 Copyright (c) 2009, Orbitz World Wide
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
 this list of conditions and the following disclaimer in the documentation 
 and/or other materials provided with the distribution.
 * Neither the name of the Orbitz World Wide nor the names of its contributors 
 may be used to endorse or promote products derived from this software 
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.*;

/**
 * Provide ascii text parsing and formatting support for proto2 instances. The implementation
 * largely follows google/protobuf/text_format.cc.
 * <p>
 * (c) 2009-10 Orbitz World Wide. All Rights Reserved.
 *
 * @author eliran.bivas@gmail.com Eliran Bivas
 * @author aantonov@orbitz.com Alex Antonov
 * <p>
 * Based on the original code by:
 * @author wenboz@google.com Wenbo Zhu
 * @author kenton@google.com Kenton Varda
 */
public class Protobuf2JsonFormat {

    public static String printToString(Collection<Message> messages) throws IOException {
        StringBuilder output = new StringBuilder();
        print(messages, output);
        return output.toString();
    }

    public static void print(Collection<Message> messages, Appendable output) throws IOException {
        output.append("[");
        boolean first = true;
        for (Message message : messages) {
            if (!first) {
                output.append(",");
            }
            print(message, output);
            first = false;
        }
        output.append("]");
    }

    /**
     * Outputs a textual representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public static void printFiles(Message message, Appendable output) throws IOException {
        JsonGenerator generator = new JsonGenerator(output);
        print(message, generator);
    }

    /**
     * Outputs a textual representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public static void print(Message message, Appendable output) throws IOException {
        JsonGenerator generator = new JsonGenerator(output);
        generator.print("{");
        print(message, generator);
        generator.print("}");
    }

    /**
     * Outputs a textual representation of {@code fields} to {@code output}.
     */
    public static void print(UnknownFieldSet fields, Appendable output) throws IOException {
        JsonGenerator generator = new JsonGenerator(output);
        generator.print("{");
        printUnknownFields(fields, generator);
        generator.print("}");
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and returns it.
     */
    public static String printToString(Message message) {
        return FormatValueRenderer.printToStringVia(output -> print(message, output));
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and returns it.
     */
    public static String printToString(UnknownFieldSet fields) {
        return FormatValueRenderer.printToStringVia(output -> print(fields, output));
    }

    protected static void print(Message message, JsonGenerator generator) throws IOException {

        for (Iterator<Map.Entry<FieldDescriptor, Object>> iter = message.getAllFields().entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<FieldDescriptor, Object> field = iter.next();
            printField(field.getKey(), field.getValue(), generator);
            if (iter.hasNext()) {
                generator.print(",");
            }
        }
        if (message.getUnknownFields().asMap().size() > 0) {
            generator.print(", ");
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public static void printField(FieldDescriptor field, Object value, JsonGenerator generator) throws IOException {

        printSingleField(field, value, generator);
    }

    private static void printSingleField(FieldDescriptor field,
            Object value,
            JsonGenerator generator) throws IOException {
        if (field.isExtension()) {
            generator.print("\"");
            generator.print(FormatValueRenderer.extensionPrintName(field));
            generator.print("\"");
        } else {
            generator.print("\"");
            generator.print(FormatValueRenderer.fieldPrintName(field));
            generator.print("\"");
        }

        // Done with the name, on to the value

        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            generator.print(": ");
            generator.indent();
        } else {
            generator.print(": ");
        }

        if (field.isRepeated()) {
            // Repeated field. Print each element.
            generator.print("[");
            for (Iterator<?> iter = ((List<?>) value).iterator(); iter.hasNext(); ) {
                printFieldValue(field, iter.next(), generator);
                if (iter.hasNext()) {
                    generator.print(",");
                }
            }
            generator.print("]");
        } else {
            printFieldValue(field, value, generator);
            if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
                generator.outdent();
            }
        }
    }

    private static void printFieldValue(FieldDescriptor field, Object value, JsonGenerator generator) throws IOException {
        FormatValueRenderer.renderFieldValue(field, value, new FormatValueRenderer.ValueSink() {

            @Override
            public void printRaw(CharSequence text) throws IOException {
                generator.print(text);
            }

            @Override
            public void printString(String value) throws IOException {
                generator.print("\"");
                generator.print(FormatTextSupport.escapeTextJson(value));
                generator.print("\"");
            }

            @Override
            public void printBytes(ByteString value) throws IOException {
                generator.print("\"");
                generator.print(FormatTextSupport.escapeBytesUnicode(value));
                generator.print("\"");
            }

            @Override
            public void printEnum(EnumValueDescriptor value) throws IOException {
                generator.print("\"");
                generator.print(value.getName());
                generator.print("\"");
            }

            @Override
            public void printMessage(Message value) throws IOException {
                generator.print("{");
                print(value, generator);
                generator.print("}");
            }

        });
    }

    protected static void printUnknownFields(UnknownFieldSet unknownFields, JsonGenerator generator) throws IOException {
        boolean firstField = true;
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            if (firstField) {
                firstField = false;
            } else {
                generator.print(", ");
            }

            generator.print("\"");
            generator.print(entry.getKey().toString());
            generator.print("\"");
            generator.print(": [");

            boolean firstValue = true;
            for (long value : field.getVarintList()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    generator.print(", ");
                }
                generator.print(FormatTextSupport.unsignedToString(value));
            }
            for (int value : field.getFixed32List()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    generator.print(", ");
                }
                generator.print(String.format((Locale) null, "0x%08x", value));
            }
            for (long value : field.getFixed64List()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    generator.print(", ");
                }
                generator.print(String.format((Locale) null, "0x%016x", value));
            }
            for (ByteString value : field.getLengthDelimitedList()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    generator.print(", ");
                }
                generator.print("\"");
                generator.print(FormatTextSupport.escapeBytesUnicode(value));
                generator.print("\"");
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                if (firstValue) {
                    firstValue = false;
                } else {
                    generator.print(", ");
                }
                generator.print("{");
                printUnknownFields(value, generator);
                generator.print("}");
            }
            generator.print("]");
        }
    }


    /**
     * An inner class for writing text to the output stream.
     */
    protected static class JsonGenerator {

        Appendable output;

        boolean atStartOfLine = true;

        StringBuilder indent = new StringBuilder();

        public JsonGenerator(Appendable output) {
            this.output = output;
        }

        /**
         * Indent text by two spaces. After calling Indent(), two spaces will be inserted at the
         * beginning of each line of text. Indent() may be called multiple times to produce deeper
         * indents.
         */
        public void indent() {
            this.indent.append("  ");
        }

        /**
         * Reduces the current indent level by two spaces, or crashes if the indent level is zero.
         */
        public void outdent() {
            int length = this.indent.length();
            if (length == 0) {
                throw new IllegalArgumentException(" Outdent() without matching Indent().");
            }
            this.indent.delete(length - 2, length);
        }

        /**
         * Print text to the output stream.
         */
        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;

            for (int i = 0; i < size; i++) {
                if (text.charAt(i) == '\n') {
                    this.write(text.subSequence(pos, size), i - pos + 1);
                    pos = i + 1;
                    this.atStartOfLine = true;
                }
            }
            this.write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size == 0) {
                return;
            }
            if (this.atStartOfLine) {
                this.atStartOfLine = false;
                this.output.append(this.indent);
            }
            this.output.append(data);
        }

    }

    // =================================================================
    // Parsing

    protected static class Tokenizer {

        private final FormatTokenizerCore core;

        // We use possesive quantifiers (*+ and ++) because otherwise the Java
        // regex matcher has stack overflows on large inputs.
        private static final Pattern WHITESPACE =
                Pattern.compile("(\\s|(#.*$))++", Pattern.MULTILINE);

        private static final Pattern TOKEN = Pattern.compile(
                "[a-zA-Z_][0-9a-zA-Z_+-]*+|" +                // an identifier
                "[.]?[0-9+-][0-9a-zA-Z_.+-]*+|" +             // a number
                "\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|" +       // a double-quoted string
                "'([^'\n\\\\]|\\\\.)*+('|\\\\?$)",         // a single-quoted string
                Pattern.MULTILINE);

        /**
         * Construct a tokenizer that parses tokens from the given text.
         */
        public Tokenizer(CharSequence text) {
            this.core = new FormatTokenizerCore(text, TOKEN, FormatTokenizerCore.Kind.JSON);
        }

        /**
         * Are we at the end of the input?
         */
        public boolean atEnd() {
            return this.core.atEnd();
        }

        /**
         * Advance to the next token.
         */
        public void nextToken() {
            this.core.nextToken();
        }

        /**
         * If the next token exactly matches {@code token}, consume it and return {@code true}.
         * Otherwise, return {@code false} without doing anything.
         */
        public boolean tryConsume(String token) {
            return this.core.tryConsume(token);
        }

        /**
         * If the next token exactly matches {@code token}, consume it. Otherwise, throw a {@link ParseException}.
         */
        public void consume(String token) throws ParseException {
            if (!this.core.tryConsume(token)) {
                throw new ParseException(this.core.errorMessage("Expected \"" + token + "\"."));
            }
        }

        /**
         * Returns {@code true} if the next token is an integer, but does not consume it.
         */
        public boolean lookingAtInteger() {
            return this.core.lookingAtInteger();
        }

        /**
         * Returns {@code true} if the next token is a boolean (true/false), but does not consume it.
         */
        public boolean lookingAtBoolean() {
            return this.core.lookingAtBoolean();
        }

        /**
         * @return currentToken to which the Tokenizer is pointing.
         */
        public String currentToken() {
            return this.core.currentToken();
        }

        /**
         * If the next token is an identifier, consume it and return its value. Otherwise, throw a {@link ParseException}.
         */
        public String consumeIdentifier() throws ParseException {
            try {
                return this.core.consumeIdentifier();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public int consumeInt32() throws ParseException {
            try {
                return this.core.consumeInt32();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public int consumeUInt32() throws ParseException {
            try {
                return this.core.consumeUInt32();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public long consumeInt64() throws ParseException {
            try {
                return this.core.consumeInt64();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public long consumeUInt64() throws ParseException {
            try {
                return this.core.consumeUInt64();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public double consumeDouble() throws ParseException {
            try {
                return this.core.consumeDouble();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public float consumeFloat() throws ParseException {
            try {
                return this.core.consumeFloat();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        public boolean consumeBoolean() throws ParseException {
            try {
                return this.core.consumeBoolean();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        /**
         * If the next token is a string, consume it and return its (unescaped) value. Otherwise,
         * throw a {@link ParseException}.
         */
        public String consumeString() throws ParseException {
            try {
                return this.core.consumeString();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        /**
         * If the next token is a string, consume it, unescape it as a {@link ByteString}, and return it. Otherwise,
         * throw a {@link ParseException}.
         */
        public ByteString consumeByteString() throws ParseException {
            try {
                return this.core.consumeByteString();
            } catch (FormatTokenizerCore.Failure f) {
                throw new ParseException(f.getMessage());
            }
        }

        /**
         * Returns a {@link ParseException} with the current line and column numbers in the
         * description, suitable for throwing.
         */
        public ParseException parseException(String description) {
            // Note: People generally prefer one-based line and column numbers.
            return new ParseException(this.core.errorMessage(description));
        }

        /**
         * Returns a {@link ParseException} with the line and column numbers of the previous token
         * in the description, suitable for throwing.
         */
        public ParseException parseExceptionPreviousToken(String description) {
            // Note: People generally prefer one-based line and column numbers.
            return new ParseException(this.core.previousTokenErrorMessage(description));
        }

    }

    /**
     * Thrown when parsing an invalid text format message.
     */
    public static class ParseException extends IOException {

        private static final long serialVersionUID = 1L;

        public ParseException(String message) {
            super(message);
        }

    }

    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     */
    public static void merge(Readable input, Message.Builder builder) throws IOException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     */
    public static void merge(CharSequence input, Message.Builder builder) throws ParseException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     */
    public static void merge(Readable input,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws IOException {
        // Read the entire input to a String then parse that.

        // If StreamTokenizer were not quite so crippled, or if there were a kind
        // of Reader that could read in chunks that match some particular regex,
        // or if we wanted to write a custom Reader to tokenize our stream, then
        // we would not have to read to one big String. Alas, none of these is
        // the case. Oh well.

        merge(toStringBuilder(input), extensionRegistry, builder);
    }

    // TODO(chrisn): See if working around java.io.Reader#read(CharBuffer) overhead 已随共享工具收敛
    protected static StringBuilder toStringBuilder(Readable input) throws IOException {
        return FormatTextSupport.toStringBuilder(input);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     */
    public static void merge(CharSequence input,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws ParseException {
        Tokenizer tokenizer = new Tokenizer(input);

        // Based on the state machine @ http://json.org/

        tokenizer.consume("{"); // Needs to happen when the object starts.
        while (!tokenizer.tryConsume("}")) { // Continue till the object is done
            mergeField(tokenizer, extensionRegistry, builder);
        }
        // Test to make sure the tokenizer has reached the end of the stream.
        if (!tokenizer.atEnd()) {
            throw tokenizer
                    .parseException("Expecting the end of the stream, but there seems to be more data!  Check the input for a valid JSON format.");
        }
    }

    private static final Pattern DIGITS = Pattern.compile(
            "[0-9]",
            Pattern.CASE_INSENSITIVE);

    /**
     * fix-registered-defects 3.2：名字文本是否为纯数字（即打印产物中的未知字段编号键）。
     */
    private static boolean isFieldNumberText(String name) {
        if (name.isEmpty()) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse a single field from {@code tokenizer} and merge it into {@code builder}. If a ',' is
     * detected after the field ends, the next field will be parsed automatically
     */
    protected static void mergeField(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws ParseException {
        FieldDescriptor field;
        Descriptor type = builder.getDescriptorForType();
        ExtensionRegistry.ExtensionInfo extension = null;
        boolean unknown = false;

        String name = tokenizer.consumeIdentifier();
        field = type.findFieldByName(name);

        // Group names are expected to be capitalized as they appear in the
        // .proto file, which actually matches their type names, not their field
        // names.
        if (field == null) {
            // Explicitly specify US locale so that this code does not break when
            // executing in Turkey.
            String lowerName = name.toLowerCase(Locale.US);
            field = type.findFieldByName(lowerName);
            // If the case-insensitive match worked but the field is NOT a group,
            if ((field != null) && (field.getType() != FieldDescriptor.Type.GROUP)) {
                field = null;
            }
        }
        // Again, special-case group names as described above.
        if ((field != null) && (field.getType() == FieldDescriptor.Type.GROUP)
            && !field.getMessageType().getName().equals(name)) {
            field = null;
        }

        // Last try to lookup by field-index if 'name' is numeric,
        // which indicates a possible unknown field
        if (field == null && DIGITS.matcher(name).matches()) {
            field = type.findFieldByNumber(Integer.parseInt(name));
            unknown = true;
        }

        // Finally, look for extensions
        extension = extensionRegistry.findImmutableExtensionByName(name);
        if (extension != null) {
            if (extension.descriptor.getContainingType() != type) {
                throw tokenizer.parseExceptionPreviousToken("Extension \"" + name
                                                            + "\" does not extend message type \""
                                                            + type.getFullName() + "\".");
            }
            field = extension.descriptor;
        }

        if (field == null) {
            if (isFieldNumberText(name)) {
                // fix-registered-defects 3.2（design D3）：未知字段编号族内统一显式拒绝——
                // 失败信息含该编号（原状为 handleMissingField 静默丢弃）
                throw tokenizer.parseExceptionPreviousToken("Unknown field number: " + name + ".");
            }
            // 非编号的未知名字段维持既有旁路通道（不在本差量承诺面，禁止顺手扩大）
            handleMissingField(tokenizer, extensionRegistry, builder);
        }

        if (field != null) {
            tokenizer.consume(":");
            boolean array = tokenizer.tryConsume("[");

            if (array) {
                while (!tokenizer.tryConsume("]")) {
                    handleValue(tokenizer, extensionRegistry, builder, field, extension, unknown);
                    tokenizer.tryConsume(",");
                }
            } else {
                handleValue(tokenizer, extensionRegistry, builder, field, extension, unknown);
            }
        }

        if (tokenizer.tryConsume(",")) {
            // Continue with the next field
            mergeField(tokenizer, extensionRegistry, builder);
        }
    }

    private static void handleMissingField(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws ParseException {
        tokenizer.tryConsume(":");
        if ("{".equals(tokenizer.currentToken())) {
            // Message structure
            tokenizer.consume("{");
            do {
                tokenizer.consumeIdentifier();
                handleMissingField(tokenizer, extensionRegistry, builder);
            } while (tokenizer.tryConsume(","));
            tokenizer.consume("}");
        } else if ("[".equals(tokenizer.currentToken())) {
            // Collection
            tokenizer.consume("[");
            do {
                handleMissingField(tokenizer, extensionRegistry, builder);
            } while (tokenizer.tryConsume(","));
            tokenizer.consume("]");
        } else { //if (!",".equals(tokenizer.currentToken)){
            // Primitive value
            if ("null".equals(tokenizer.currentToken())) {
                tokenizer.consume("null");
            } else if (tokenizer.lookingAtInteger()) {
                tokenizer.consumeInt64();
            } else if (tokenizer.lookingAtBoolean()) {
                tokenizer.consumeBoolean();
            } else {
                tokenizer.consumeString();
            }
        }
    }

    private static void handleValue(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder,
            FieldDescriptor field,
            ExtensionRegistry.ExtensionInfo extension,
            boolean unknown) throws ParseException {

        Object value = null;
        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            value = handleObject(tokenizer, extensionRegistry, builder, field, extension, unknown);
        } else {
            value = handlePrimitive(tokenizer, field);
        }
        if (value != null) {
            if (field.isRepeated()) {
                builder.addRepeatedField(field, value);
            } else {
                builder.setField(field, value);
            }
        }
    }

    private static Object handlePrimitive(Tokenizer tokenizer, FieldDescriptor field) throws ParseException {
        Object value = null;
        if ("null".equals(tokenizer.currentToken())) {
            tokenizer.consume("null");
            return value;
        }
        return FormatValueReader.readPrimitive(field, new FormatValueReader.TokenizerScanner(tokenizer));
    }

    private static Object handleObject(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder,
            FieldDescriptor field,
            ExtensionRegistry.ExtensionInfo extension,
            boolean unknown) throws ParseException {

        Message.Builder subBuilder;
        if (extension == null) {
            subBuilder = builder.newBuilderForField(field);
        } else {
            subBuilder = extension.defaultInstance.newBuilderForType();
        }

        if (unknown) {
            ByteString data = tokenizer.consumeByteString();
            try {
                subBuilder.mergeFrom(data);
                return subBuilder.build();
            } catch (InvalidProtocolBufferException e) {
                throw tokenizer.parseException("Failed to build " + field.getFullName() + " from " + data);
            }
        }

        tokenizer.consume("{");
        String endToken = "}";

        while (!tokenizer.tryConsume(endToken)) {
            if (tokenizer.atEnd()) {
                throw tokenizer.parseException("Expected \"" + endToken + "\".");
            }
            mergeField(tokenizer, extensionRegistry, subBuilder);
            if (tokenizer.tryConsume(",")) {
                // there are more fields in the object, so continue
                continue;
            }
        }

        return subBuilder.build();
    }

}
