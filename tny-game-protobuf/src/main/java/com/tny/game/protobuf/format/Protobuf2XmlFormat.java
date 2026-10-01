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
public final class Protobuf2XmlFormat {

    /**
     * Outputs a textual representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public static void print(Message message, Appendable output) throws IOException {
        XmlGenerator generator = new XmlGenerator(output);
        final String messageName = message.getDescriptorForType().getName();
        generator.print("<");
        generator.print(messageName);
        generator.print(">");
        print(message, generator);
        generator.print("</");
        generator.print(messageName);
        generator.print(">");
    }

    /**
     * Outputs a textual representation of {@code fields} to {@code output}.
     */
    public static void print(UnknownFieldSet fields, Appendable output) throws IOException {
        XmlGenerator generator = new XmlGenerator(output);
        generator.print("<message>");
        printUnknownFields(fields, generator);
        generator.print("</message>");
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

    private static void print(Message message, XmlGenerator generator) throws IOException {

        for (Map.Entry<FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public static void printField(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {

        if (field.isRepeated()) {
            // Repeated field. Print each element.
            for (Object element : (List<?>) value) {
                printSingleField(field, element, generator);
            }
        } else {
            printSingleField(field, value, generator);
        }
    }

    private static void printSingleField(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {
        if (field.isExtension()) {
            generator.print("<extension type=\"");
            generator.print(FormatValueRenderer.extensionPrintName(field));
            generator.print("\">");
        } else {
            generator.print("<");
            generator.print(FormatValueRenderer.fieldPrintName(field));
            generator.print(">");
        }

        printFieldValue(field, value, generator);

        if (!field.isExtension()) {
            generator.print("</");
            generator.print(FormatValueRenderer.fieldPrintName(field));
            generator.print(">");
        } else {
            generator.print("</extension>");
        }

    }

    private static void printFieldValue(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {
        FormatValueRenderer.renderFieldValue(field, value, new FormatValueRenderer.ValueSink() {

            @Override
            public void printRaw(CharSequence text) throws IOException {
                generator.print(text);
            }

            @Override
            public void printString(String value) throws IOException {
                generator.print(FormatTextSupport.escapeTextLegacy(value));
            }

            @Override
            public void printBytes(ByteString value) throws IOException {
                generator.print(FormatTextSupport.escapeBytesOctal(value));
            }

            @Override
            public void printEnum(EnumValueDescriptor value) throws IOException {
                generator.print(value.getName());
            }

            @Override
            public void printMessage(Message value) throws IOException {
                print(value, generator);
            }

        });
    }

    private static void printUnknownFields(UnknownFieldSet unknownFields, XmlGenerator generator) throws IOException {
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            final String key = entry.getKey().toString();
            for (long value : field.getVarintList()) {
                printUnknownField(key, FormatTextSupport.unsignedToString(value), generator);
            }
            for (int value : field.getFixed32List()) {
                printUnknownField(key, String.format((Locale) null, "0x%08x", value), generator);
            }
            for (long value : field.getFixed64List()) {
                printUnknownField(key, String.format((Locale) null, "0x%016x", value), generator);
            }
            for (ByteString value : field.getLengthDelimitedList()) {
                printUnknownField(key, FormatTextSupport.escapeBytesOctal(value), generator);
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                generator.print("<unknown-field index=\"");
                generator.print(key);
                generator.print("\">");
                printUnknownFields(value, generator);
                generator.print("</unknown-field>");
            }
        }
    }

    private static void printUnknownField(CharSequence fieldKey,
            CharSequence fieldValue,
            XmlGenerator generator) throws IOException {
        generator.print("<unknown-field index=\"");
        generator.print(fieldKey);
        generator.print("\">");
        generator.print(fieldValue);
        generator.print("</unknown-field>");
    }

    /**
     * An inner class for writing text to the output stream.
     */
    static private final class XmlGenerator {

        Appendable output;

        public XmlGenerator(Appendable output) {
            this.output = output;
        }

        /**
         * Print text to the output stream.
         */
        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;

            write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size == 0) {
                return;
            }
            this.output.append(data);
        }

    }

    // =================================================================
    // Parsing

    // We use possesive quantifiers (*+ and ++) because otherwise the Java
    // regex matcher has stack overflows on large inputs.
    private static final Pattern WHITESPACE =
            Pattern.compile("(\\s|(#.*$))++", Pattern.MULTILINE);

    private static final Pattern TOKEN = Pattern.compile(
            "extension|" + "[a-zA-Z_\\s;@][0-9a-zA-Z_\\s;@+-]*+|" +        // an identifier with special handling for 'extension'
            "[.]?[0-9+-][0-9a-zA-Z_.+-]*+|" +             // a number
            "</|" +                                       // an '</' closing element marker
            "[\\\\0-9]++|" +                              // a \000 byte sequence for bytes handling
            "\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|" +       // a double-quoted string
            "\'([^\'\n\\\\]|\\\\.)*+(\'|\\\\?$)",         // a single-quoted string
            Pattern.MULTILINE);

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
    public static void merge(Readable input, Message.Builder builder) throws ParseException,
            IOException {
        Protobuf2XmlFormat.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
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
            Message.Builder builder) throws ParseException, IOException {
        // Read the entire input to a String then parse that.

        // If StreamTokenizer were not quite so crippled, or if there were a kind
        // of Reader that could read in chunks that match some particular regex,
        // or if we wanted to write a custom Reader to tokenize our stream, then
        // we would not have to read to one big String. Alas, none of these is
        // the case. Oh well.

        merge(FormatTextSupport.toStringBuilder(input), extensionRegistry, builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     */
    public static void merge(CharSequence input,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws ParseException {
        try {
            mergeInternal(new FormatTokenizerCore(input, TOKEN, FormatTokenizerCore.Kind.XML),
                          extensionRegistry, builder);
        } catch (FormatTokenizerCore.Failure f) {
            throw new ParseException(f.getMessage());
        }
    }

    private static void mergeInternal(FormatTokenizerCore tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws FormatTokenizerCore.Failure {

        // Need to first consume the outer object name element
        consumeOpeningElement(tokenizer);

        while (!tokenizer.tryConsume("</")) { // Continue till the object is done
            mergeField(tokenizer, extensionRegistry, builder);
        }

        consumeClosingElement(tokenizer);
    }

    private static String consumeOpeningElement(FormatTokenizerCore tokenizer) throws FormatTokenizerCore.Failure {
        tokenizer.consume("<");
        String openingElement = tokenizer.consumeIdentifier();
        tokenizer.consume(">");
        return openingElement;
    }

    private static void consumeClosingElement(FormatTokenizerCore tokenizer) throws FormatTokenizerCore.Failure {
        tokenizer.tryConsume("</");
        //tokenizer.consume("/");
        tokenizer.nextToken();
        tokenizer.consume(">");
    }

    private static String consumeExtensionIdentifier(FormatTokenizerCore tokenizer) throws FormatTokenizerCore.Failure {
        tokenizer.consume("type");
        tokenizer.consume("=");
        return tokenizer.consumeIdentifier();
    }

    /**
     * Parse a single field from {@code tokenizer} and merge it into {@code builder}. If a ',' is
     * detected after the field ends, the next field will be parsed automatically
     */
    private static void mergeField(FormatTokenizerCore tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws FormatTokenizerCore.Failure {
        FieldDescriptor field;
        Descriptors.Descriptor type = builder.getDescriptorForType();
        ExtensionRegistry.ExtensionInfo extension = null;

        tokenizer.consume("<"); // Needs to happen when the object starts.

        if (tokenizer.tryConsume("extension")) {
            // An extension.
            StringBuilder name = new StringBuilder(consumeExtensionIdentifier(tokenizer));
            while (tokenizer.tryConsume(".")) {
                name.append(".");
                name.append(tokenizer.consumeIdentifier());
            }

            extension = extensionRegistry.findImmutableExtensionByName(name.toString());

            if (extension == null) {
                throw new FormatTokenizerCore.Failure(tokenizer.previousTokenErrorMessage("Extension \""
                                                            + name
                                                            + "\" not found in the ExtensionRegistry."));
            } else if (extension.descriptor.getContainingType() != type) {
                throw new FormatTokenizerCore.Failure(tokenizer.previousTokenErrorMessage("Extension \"" + name
                                                            + "\" does not extend message type \""
                                                            + type.getFullName() + "\"."));
            }

            field = extension.descriptor;
        } else {
            if (tokenizer.currentToken().startsWith("unknown-field")) {
                // fix-registered-defects 3.2（design D3）：unknown fields 族内统一显式拒绝——
                // 打印形态 <unknown-field index="NNN"> 自带编号，失败信息含该编号
                // （原状为词法错位 "Expected identifier. --"）
                tokenizer.nextToken();
                tokenizer.consume("=");
                String number = tokenizer.consumeIdentifier();
                throw new FormatTokenizerCore.Failure(
                        tokenizer.errorMessage("Unknown field number: " + number + "."));
            }
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

            if (field == null) {
                throw new FormatTokenizerCore.Failure(tokenizer.previousTokenErrorMessage("Message type \"" + type.getFullName()
                                                            + "\" has no field named \"" + name
                                                            + "\"."));
            }
        }

        tokenizer.consume(">");

        Object value = handleValue(tokenizer, extensionRegistry, builder, field, extension);

        if (field.isRepeated()) {
            builder.addRepeatedField(field, value);
        } else {
            builder.setField(field, value);
        }

        // Need to consume the closing field element - </fieldName>
        consumeClosingElement(tokenizer);
    }

    private static Object handleValue(FormatTokenizerCore tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder,
            FieldDescriptor field,
            ExtensionRegistry.ExtensionInfo extension) throws FormatTokenizerCore.Failure {

        Object value = null;
        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            value = handleObject(tokenizer, extensionRegistry, builder, field, extension);
        } else {
            value = handlePrimitive(tokenizer, field);
        }

        return value;
    }

    private static Object handlePrimitive(FormatTokenizerCore tokenizer, FieldDescriptor field) throws FormatTokenizerCore.Failure {
        return FormatValueReader.readPrimitive(field, tokenizer);
    }

    private static Object handleObject(FormatTokenizerCore tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder,
            FieldDescriptor field,
            ExtensionRegistry.ExtensionInfo extension) throws FormatTokenizerCore.Failure {

        Object value;
        Message.Builder subBuilder;
        if (extension == null) {
            subBuilder = builder.newBuilderForField(field);
        } else {
            subBuilder = extension.defaultInstance.newBuilderForType();
        }

        //tokenizer.consume("<");
        String endToken = "</";

        while (!tokenizer.tryConsume(endToken)) {
            if (tokenizer.atEnd()) {
                throw new FormatTokenizerCore.Failure(tokenizer.errorMessage("Expected \"" + endToken + "\"."));
            }
            mergeField(tokenizer, extensionRegistry, subBuilder);
        }

        value = subBuilder.build();
        return value;
    }

}
