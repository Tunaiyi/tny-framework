package com.tny.game.protobuf.format;

import com.google.protobuf.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.*;

/**
 * Provide ascii text parsing and formatting support for proto2 instances. The implementation
 * largely follows google/protobuf/text_format.cc.
 * <p>
 * (c) 2009-10 Orbitz World Wide. All Rights Reserved.
 *
 * @author aantonov@orbitz.com Alex Antonov
 * <p>
 * Based on the original code by:
 * @author wenboz@google.com Wenbo Zhu
 * @author kenton@google.com Kenton Varda
 */
public class Protobuf2JavaPropsFormat {

    private Protobuf2JavaPropsFormat() {
    }

    /**
     * Outputs a textual representation of the Protocol Message supplied into
     * the parameter output. (This representation is the new version of the
     * classic "ProtocolPrinter" output from the original Protocol Buffer system)
     */
    public static void print(final Message message, final Appendable output)
            throws IOException {
        final JavaPropsGenerator generator = new JavaPropsGenerator(output);
        print(message, generator);
    }

    /**
     * Outputs a textual representation of {@code fields} to {@code output}.
     */
    public static void print(final UnknownFieldSet fields,
            final Appendable output)
            throws IOException {
        final JavaPropsGenerator generator = new JavaPropsGenerator(output);
        printUnknownFields(fields, generator);
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and
     * returns it.
     */
    public static String printToString(final Message message) {
        return FormatValueRenderer.printToStringVia(output -> print(message, output));
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and
     * returns it.
     */
    public static String printToString(final UnknownFieldSet fields) {
        return FormatValueRenderer.printToStringVia(output -> print(fields, output));
    }

    private static void print(final Message message,
            final JavaPropsGenerator generator)
            throws IOException {
        for (final Map.Entry<Descriptors.FieldDescriptor, Object> field :
                message.getAllFields().entrySet()) {
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public static void printField(final Descriptors.FieldDescriptor field,
            final Object value,
            final Appendable output)
            throws IOException {
        final JavaPropsGenerator generator = new JavaPropsGenerator(output);
        printField(field, value, generator);
    }

    public static String printFieldToString(final Descriptors.FieldDescriptor field,
            final Object value) {
        return FormatValueRenderer.printToStringVia(output -> printField(field, value, output));
    }

    private static void printField(final Descriptors.FieldDescriptor field,
            final Object value,
            final JavaPropsGenerator generator)
            throws IOException {
        if (field.isRepeated()) {
            // Repeated field.  Print each element.
            List<?> list = (List<?>) value;
            for (int i = 0; i < list.size(); i++) {
                printSingleField(field, list.get(i), i, generator);
            }
        } else {
            printSingleField(field, value, null, generator);
        }
    }

    private static void printSingleField(final Descriptors.FieldDescriptor field,
            final Object value, final Integer collectionIndex,
            final JavaPropsGenerator generator)
            throws IOException {
        if (field.isExtension()) {
            generator.print("[");
            generator.print(FormatValueRenderer.extensionPrintName(field));
            generator.print("]");
        } else {
            if (field.getType() != Descriptors.FieldDescriptor.Type.GROUP &&
                field.getType() != Descriptors.FieldDescriptor.Type.MESSAGE) {
                // The field is a primitive value, no need to unwind the path.
                generator.print(createFieldNameCollectionIndex(FormatValueRenderer.fieldPrintName(field), collectionIndex));
            }
        }

        if (field.getType() == Descriptors.FieldDescriptor.Type.GROUP) {
            // Groups must be serialized with their original capitalization.
            generator.indent(createFieldNameCollectionIndex(FormatValueRenderer.fieldPrintName(field), collectionIndex));
        } else if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            //generator.print(" {\n");
            generator.indent(createFieldNameCollectionIndex(FormatValueRenderer.fieldPrintName(field), collectionIndex));
        } else {
            generator.print("=");
        }

        printFieldValue(field, value, generator);

        if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            generator.outdent(createFieldNameCollectionIndex(FormatValueRenderer.fieldPrintName(field), collectionIndex));
            //generator.print("");
        } else if (field.getType() == Descriptors.FieldDescriptor.Type.GROUP) {
            generator.outdent(createFieldNameCollectionIndex(FormatValueRenderer.fieldPrintName(field), collectionIndex));
            //generator.print("");
        } else {
            generator.print("\n");
        }
    }

    private static String createFieldNameCollectionIndex(final String fieldName,
            final Integer collectionIndex)
            throws IOException {
        if (collectionIndex != null) {
            return fieldName + "[" + collectionIndex.toString() + "]";
        } else {
            return fieldName;
        }
    }

    private static void printFieldValue(final Descriptors.FieldDescriptor field,
            final Object value,
            final JavaPropsGenerator generator)
            throws IOException {
        FormatValueRenderer.renderFieldValue(field, value, new FormatValueRenderer.ValueSink() {

            @Override
            public void printRaw(CharSequence text) throws IOException {
                generator.print(text);
            }

            @Override
            public void printString(String value) throws IOException {
                generator.print("\"");
                generator.print(FormatTextSupport.escapeTextLegacy(value));
                generator.print("\"");
            }

            @Override
            public void printBytes(ByteString value) throws IOException {
                generator.print("\"");
                generator.print(FormatTextSupport.escapeBytesOctal(value));
                generator.print("\"");
            }

            @Override
            public void printEnum(Descriptors.EnumValueDescriptor value) throws IOException {
                // 现状：JavaProps 的 ENUM 不加引号，禁止顺手修
                generator.print(value.getName());
            }

            @Override
            public void printMessage(Message value) throws IOException {
                print(value, generator);
            }

        });
    }

    private static void printUnknownFields(final UnknownFieldSet unknownFields,
            final JavaPropsGenerator generator)
            throws IOException {
        for (final Map.Entry<Integer, UnknownFieldSet.Field> entry :
                unknownFields.asMap().entrySet()) {
            final UnknownFieldSet.Field field = entry.getValue();
            final String key = entry.getKey().toString();

            for (final long value : field.getVarintList()) {
                printUnknownValue(key, FormatTextSupport.unsignedToString(value), generator);
            }
            for (final int value : field.getFixed32List()) {
                printUnknownValue(key, String.format((Locale) null, "0x%08x", value), generator);
            }
            for (final long value : field.getFixed64List()) {
                printUnknownValue(key, String.format((Locale) null, "0x%016x", value), generator);
            }
            for (final ByteString value : field.getLengthDelimitedList()) {
                printUnknownQuoted(key, FormatTextSupport.escapeBytesOctal(value), generator);
            }
            for (final UnknownFieldSet value : field.getGroupList()) {
                //generator.print(entry.getKey().toString());
                //generator.print("={\n");
                generator.indent(key);
                printUnknownFields(value, generator);
                generator.outdent(key);
                //generator.print("}\n");
                generator.print("\n");
            }
        }
    }

    // 类内自克隆并表：varint/fixed32/fixed64 三个 for 循环只差取值表达式，输出形态逐字同现状
    private static void printUnknownValue(final String key, final CharSequence value,
            final JavaPropsGenerator generator)
            throws IOException {
        generator.print(key);
        generator.print("=");
        generator.print(value);
        generator.print("\n");
    }

    private static void printUnknownQuoted(final String key, final CharSequence value,
            final JavaPropsGenerator generator)
            throws IOException {
        generator.print(key);
        generator.print("=\"");
        generator.print(value);
        generator.print("\"\n");
    }


    /**
     * An inner class for writing text to the output stream.
     */
    private static final class JavaPropsGenerator {

        private Appendable output;

        private boolean atStartOfLine = true;

        private final StringBuilder indent = new StringBuilder();

        private JavaPropsGenerator(final Appendable output) {
            this.output = output;
        }

        /**
         * Indent text by two spaces.  After calling Indent(), two spaces will be
         * inserted at the beginning of each line of text.  Indent() may be called
         * multiple times to produce deeper indents.
         */
        public void indent(String objectPath) {
            this.indent.append(objectPath);
            this.indent.append(".");
            //atStartOfLine = true;
        }

        /**
         * Reduces the current indent level by two spaces, or crashes if the indent
         * level is zero.
         */
        public void outdent(String objectPath) {
            final int length = this.indent.length();
            final int objectPathLength = objectPath.length() + 1;
            if (length == 0) {
                throw new IllegalArgumentException(
                        " Outdent() without matching Indent().");
            }
            this.indent.delete(length - objectPathLength, length);
        }

        /**
         * Print text to the output stream.
         */
        public void print(final CharSequence text) throws IOException {
            final int size = text.length();
            int pos = 0;

            for (int i = 0; i < size; i++) {
                if (text.charAt(i) == '\n') {
                    write(text.subSequence(pos, size), i - pos + 1);
                    pos = i + 1;
                    this.atStartOfLine = true;
                }
            }
            write(text.subSequence(pos, size), size - pos);
        }

        private void write(final CharSequence data, final int size)
                throws IOException {
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

    private static final Pattern TOKEN = Pattern.compile(
            "[a-zA-Z_][0-9a-zA-Z_+-]*+|" +                // an identifier
            "[.]?[0-9+-][0-9a-zA-Z_.+-]*+|" +             // a number
            "\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|" +       // a double-quoted string
            "\'([^\'\n\\\\]|\\\\.)*+(\'|\\\\?$)",         // a single-quoted string
            Pattern.MULTILINE);

    /**
     * Thrown when parsing an invalid text format message.
     */
    public static class ParseException extends IOException {

        private static final long serialVersionUID = 3196188060225107702L;

        public ParseException(final String message) {
            super(message);
        }

    }

    /**
     * Parse a text-format message from {@code input} and merge the contents
     * into {@code builder}.
     */
    public static void merge(final Readable input,
            final Message.Builder builder)
            throws IOException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents
     * into {@code builder}.
     */
    public static void merge(final CharSequence input,
            final Message.Builder builder)
            throws ParseException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents
     * into {@code builder}.  Extensions will be recognized if they are
     * registered in {@code extensionRegistry}.
     */
    public static void merge(final Readable input,
            final ExtensionRegistry extensionRegistry,
            final Message.Builder builder)
            throws IOException {
        // Read the entire input to a String then parse that.

        // If StreamTokenizer were not quite so crippled, or if there were a kind
        // of Reader that could read in chunks that match some particular regex,
        // or if we wanted to write a custom Reader to tokenize our stream, then
        // we would not have to read to one big String.  Alas, none of these is
        // the case.  Oh well.

        merge(FormatTextSupport.toStringBuilder(input), extensionRegistry, builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents
     * into {@code builder}.  Extensions will be recognized if they are
     * registered in {@code extensionRegistry}.
     */
    public static void merge(final CharSequence input,
            final ExtensionRegistry extensionRegistry,
            final Message.Builder builder)
            throws ParseException {
        try {
            final FormatTokenizerCore tokenizer =
                    new FormatTokenizerCore(input, TOKEN, FormatTokenizerCore.Kind.PROPS);
            final Map<String, Message> subMessages = new HashMap<>();

            while (!tokenizer.atEnd()) {
                mergeField(tokenizer, extensionRegistry, subMessages, builder);
            }
        } catch (FormatTokenizerCore.Failure f) {
            throw new ParseException(f.getMessage());
        }
    }

    /**
     * Parse a single field from {@code tokenizer} and merge it into
     * {@code builder}.
     */
    private static void mergeField(final FormatTokenizerCore tokenizer,
            final ExtensionRegistry extensionRegistry,
            final Map<String, Message> subMessages,
            final Message.Builder builder) throws FormatTokenizerCore.Failure {
        Descriptors.FieldDescriptor field;
        final Descriptors.Descriptor type = builder.getDescriptorForType();
        ExtensionRegistry.ExtensionInfo extension = null;

        if (tokenizer.tryConsume("[")) {
            // An extension.
            final StringBuilder name =
                    new StringBuilder(tokenizer.consumeIdentifier());
            while (tokenizer.tryConsume(".")) {
                name.append('.');
                name.append(tokenizer.consumeIdentifier());
            }

            extension = extensionRegistry.findImmutableExtensionByName(name.toString());

            if (extension == null) {
                throw new FormatTokenizerCore.Failure(tokenizer.previousTokenErrorMessage(
                        "Extension \"" + name + "\" not found in the ExtensionRegistry."));
            } else if (extension.descriptor.getContainingType() != type) {
                throw new FormatTokenizerCore.Failure(tokenizer.previousTokenErrorMessage(
                        "Extension \"" + name + "\" does not extend message type \"" +
                        type.getFullName() + "\"."));
            }

            tokenizer.consume("]");

            field = extension.descriptor;
        } else {
            final String leading = tokenizer.currentToken();
            if (!leading.isEmpty() && Character.isDigit(leading.charAt(0))) {
                // fix-registered-defects 3.2（design D3）：unknown fields 族内统一显式拒绝——
                // 打印的数字键名路径前缀（如 995.2=42）取前导数字段作首个未知编号入失败信息
                // （原状为词法错位 "Expected identifier."；protobuf 字段名不可能以数字开头）
                int digits = 0;
                while (digits < leading.length() && Character.isDigit(leading.charAt(digits))) {
                    digits++;
                }
                throw new FormatTokenizerCore.Failure(
                        tokenizer.errorMessage("Unknown field number: " + leading.substring(0, digits) + "."));
            }
            final String name = tokenizer.consumeIdentifier();
            field = type.findFieldByName(name);

            // Group names are expected to be capitalized as they appear in the
            // .proto file, which actually matches their type names, not their field
            // names.
            if (field == null) {
                // Explicitly specify US locale so that this code does not break when
                // executing in Turkey.
                final String lowerName = name.toLowerCase(Locale.US);
                field = type.findFieldByName(lowerName);
                // If the case-insensitive match worked but the field is NOT a group,
                if (field != null && field.getType() != Descriptors.FieldDescriptor.Type.GROUP) {
                    field = null;
                }
            }
            // Again, special-case group names as described above.
            if (field != null && field.getType() == Descriptors.FieldDescriptor.Type.GROUP &&
                !field.getMessageType().getName().equals(name)) {
                field = null;
            }

            if (field == null) {
                throw new FormatTokenizerCore.Failure(tokenizer.previousTokenErrorMessage(
                        "Message type \"" + type.getFullName() +
                        "\" has no field named \"" + name + "\"."));
            }
        }

        Object value = null;
        Integer collectionIndex = null;

        if (field.isRepeated()) {
            tokenizer.consume("[");
            collectionIndex = tokenizer.consumeInt32();
            tokenizer.consume("]");
        }

        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {

            if (extension == null) {
                tokenizer.consume(".");
            } else {
                // fix-registered-defects 3.3：扩展字段（含 MessageSet 特判）打印产物为 `[全名]键名=值`——
                // `]` 后无点号分隔。读回按打印形态原样闭合（点号可选），打印与读回命名对称；
                // 非扩展路径仍照既有全名+点号规则 consume(".")，既有绿格零波及
                tokenizer.tryConsume(".");
            }
            //endToken = "}";

            final Message.Builder subBuilder;

            if (extension == null) {
                subBuilder = builder.newBuilderForField(field);
            } else {
                subBuilder = extension.defaultInstance.newBuilderForType();
            }
            final Message subMessage = subMessages.get(field.getFullName());
            if (subMessage != null) {
                subBuilder.mergeFrom(subMessage);
            }

            mergeField(tokenizer, extensionRegistry, subMessages, subBuilder);

            value = subBuilder.buildPartial();
            subMessages.put(field.getFullName(), (Message) value);

        } else {
            tokenizer.consume("=");

            value = FormatValueReader.readPrimitive(field, tokenizer);
        }

        if (field.isRepeated()) {
            int collectionCount = builder.getRepeatedFieldCount(field) - 1;
            if (collectionCount < collectionIndex) {
                // Need to initialize the list.  Apparently setRepeatedField does not initialize it :(
                builder.addRepeatedField(field, value);
            } else {
                builder.setRepeatedField(field, collectionIndex, value);
            }
        } else {
            builder.setField(field, value);
        }
    }

}
