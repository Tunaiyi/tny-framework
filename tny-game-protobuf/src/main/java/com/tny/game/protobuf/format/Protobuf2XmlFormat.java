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
        try {
            StringBuilder text = new StringBuilder();
            print(message, text);
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException("Writing to a StringBuilder threw an IOException (should never happen).",
                    e);
        }
    }

    /**
     * Like {@code print()}, but writes directly to a {@code String} and returns it.
     */
    public static String printToString(UnknownFieldSet fields) {
        try {
            StringBuilder text = new StringBuilder();
            print(fields, text);
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException("Writing to a StringBuilder threw an IOException (should never happen).",
                    e);
        }
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
            // We special-case MessageSet elements for compatibility with
            // proto1.
            if (field.getContainingType().getOptions().getMessageSetWireFormat()
                && (field.getType() == FieldDescriptor.Type.MESSAGE) && (field.isOptional())
                // object equality
                && (field.getExtensionScope() == field.getMessageType())) {
                generator.print(field.getMessageType().getFullName());
            } else {
                generator.print(field.getFullName());
            }
            generator.print("\">");
        } else {
            generator.print("<");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print(">");
        }

        printFieldValue(field, value, generator);

        if (!field.isExtension()) {
            generator.print("</");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print(">");
        } else {
            generator.print("</extension>");
        }

    }

    private static void printFieldValue(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {
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
                generator.print(value.toString());
                break;

            case UINT32:
            case FIXED32:
                generator.print(FormatTextSupport.unsignedToString((Integer) value));
                break;

            case UINT64:
            case FIXED64:
                generator.print(FormatTextSupport.unsignedToString((Long) value));
                break;

            case STRING:
                generator.print(FormatTextSupport.escapeTextLegacy((String) value));
                break;

            case BYTES: {
                generator.print(FormatTextSupport.escapeBytesOctal((ByteString) value));
                break;
            }

            case ENUM: {
                generator.print(((EnumValueDescriptor) value).getName());
                break;
            }

            case MESSAGE:
            case GROUP:
                print((Message) value, generator);
                break;
        }
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

    /**
     * Represents a stream of tokens parsed from a {@code String}.
     * <p>
     * <p>
     * The Java standard library provides many classes that you might think would be useful for
     * implementing this, but aren't. For example:
     * <p>
     * <ul>
     * <li>{@code java.io.StreamTokenizer}: This almost does what we want -- or, at least, something
     * that would get us close to what we want -- except for one fatal flaw: It automatically
     * un-escapes strings using Java escape sequences, which do not include all the escape sequences
     * we need to support (e.g. '\x').
     * <li>{@code java.util.Scanner}: This seems like a great way at least to parse regular
     * expressions out of a stream (so we wouldn't have to load the entire input into a single
     * string before parsing). Sadly, {@code Scanner} requires that tokens be delimited with some
     * delimiter. Thus, although the text "foo:" should parse to two tokens ("foo" and ":"), {@code
     * Scanner} would recognize it only as a single token. Furthermore, {@code Scanner} provides no
     * way to inspect the contents of delimiters, making it impossible to keep track of line and
     * column numbers.
     * </ul>
     * <p>
     * <p>
     * Luckily, Java's regular expression support does manage to be useful to us. (Barely: We need
     * {@code Matcher.usePattern()}, which is new in Java 1.5.) So, we can use that, at least.
     * Unfortunately, this implies that we need to have the entire input in one contiguous string.
     */
    private static final class Tokenizer {

        private final CharSequence text;

        private final Matcher matcher;

        private String currentToken;

        // The character index within this.text at which the current token begins.
        private int pos = 0;

        // The line and column numbers of the current token.
        private int line = 0;

        private int column = 0;

        // The line and column numbers of the previous token (allows throwing
        // errors *after* consuming).
        private int previousLine = 0;

        private int previousColumn = 0;

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

        private static Pattern DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?",
                Pattern.CASE_INSENSITIVE);

        private static Pattern FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?",
                Pattern.CASE_INSENSITIVE);

        private static Pattern FLOAT_NAN = Pattern.compile("nanf?", Pattern.CASE_INSENSITIVE);

        /**
         * Construct a tokenizer that parses tokens from the given text.
         */
        public Tokenizer(CharSequence text) {
            this.text = text;
            this.matcher = WHITESPACE.matcher(text);
            skipWhitespace();
            nextToken();
        }

        /**
         * Are we at the end of the input?
         */
        public boolean atEnd() {
            return this.currentToken.length() == 0;
        }

        /**
         * Advance to the next token.
         */
        public void nextToken() {
            this.previousLine = this.line;
            this.previousColumn = this.column;

            // Advance the line counter to the current position.
            while (this.pos < this.matcher.regionStart()) {
                if (this.text.charAt(this.pos) == '\n') {
                    ++this.line;
                    this.column = 0;
                } else {
                    ++this.column;
                }
                ++this.pos;
            }

            // Match the next token.
            if (this.matcher.regionStart() == this.matcher.regionEnd()) {
                // EOF
                this.currentToken = "";
            } else {
                this.matcher.usePattern(TOKEN);
                if (this.matcher.lookingAt()) {
                    this.currentToken = this.matcher.group();
                    this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
                } else {
                    // Take one character.
                    this.currentToken = String.valueOf(this.text.charAt(this.pos));
                    this.matcher.region(this.pos + 1, this.matcher.regionEnd());
                }

                skipWhitespace();
            }
        }

        /**
         * Skip over any whitespace so that the matcher region starts at the next token.
         */
        private void skipWhitespace() {
            this.matcher.usePattern(WHITESPACE);
            if (this.matcher.lookingAt()) {
                this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
            }
        }

        /**
         * If the next token exactly matches {@code token}, consume it and return {@code true}.
         * Otherwise, return {@code false} without doing anything.
         */
        public boolean tryConsume(String token) {
            if (this.currentToken.equals(token)) {
                nextToken();
                return true;
            } else {
                return false;
            }
        }

        /**
         * If the next token exactly matches {@code token}, consume it. Otherwise, throw a
         * {@link ParseException}.
         */
        public void consume(String token) throws ParseException {
            if (!tryConsume(token)) {
                throw parseException("Expected \"" + token + "\".");
            }
        }

        /**
         * Returns {@code true} if the next token is an integer, but does not consume it.
         */
        public boolean lookingAtInteger() {
            if (this.currentToken.length() == 0) {
                return false;
            }

            char c = this.currentToken.charAt(0);
            return (('0' <= c) && (c <= '9')) || (c == '-') || (c == '+');
        }

        /**
         * If the next token is an identifier, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public String consumeIdentifier() throws ParseException {
            for (int i = 0; i < this.currentToken.length(); i++) {
                char c = this.currentToken.charAt(i);
                if ((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z'))
                    || (('0' <= c) && (c <= '9')) || (c == '_') || (c == '.') || (c == '"')) {
                    // OK
                } else {
                    throw parseException("Expected identifier. -" + c);
                }
            }

            String result = this.currentToken;
            // Need to clean-up result to remove quotes of any kind
            result = result.replaceAll("\"|'", "");
            nextToken();
            return result;
        }

        /**
         * If the next token is a 32-bit signed integer, consume it and return its value. Otherwise,
         * throw a {@link ParseException}.
         */
        public int consumeInt32() throws ParseException {
            try {
                int result = FormatTextSupport.parseInt32(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a 32-bit unsigned integer, consume it and return its value.
         * Otherwise, throw a {@link ParseException}.
         */
        public int consumeUInt32() throws ParseException {
            try {
                int result = FormatTextSupport.parseUInt32(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a 64-bit signed integer, consume it and return its value. Otherwise,
         * throw a {@link ParseException}.
         */
        public long consumeInt64() throws ParseException {
            try {
                long result = FormatTextSupport.parseInt64(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a 64-bit unsigned integer, consume it and return its value.
         * Otherwise, throw a {@link ParseException}.
         */
        public long consumeUInt64() throws ParseException {
            try {
                long result = FormatTextSupport.parseUInt64(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a double, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public double consumeDouble() throws ParseException {
            // We need to parse infinity and nan separately because
            // Double.parseDouble() does not accept "inf", "infinity", or "nan".
            if (DOUBLE_INFINITY.matcher(this.currentToken).matches()) {
                boolean negative = this.currentToken.startsWith("-");
                nextToken();
                return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            if (this.currentToken.equalsIgnoreCase("nan")) {
                nextToken();
                return Double.NaN;
            }
            try {
                double result = Double.parseDouble(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw floatParseException(e);
            }
        }

        /**
         * If the next token is a float, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public float consumeFloat() throws ParseException {
            // We need to parse infinity and nan separately because
            // Float.parseFloat() does not accept "inf", "infinity", or "nan".
            if (FLOAT_INFINITY.matcher(this.currentToken).matches()) {
                boolean negative = this.currentToken.startsWith("-");
                nextToken();
                return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            }
            if (FLOAT_NAN.matcher(this.currentToken).matches()) {
                nextToken();
                return Float.NaN;
            }
            try {
                float result = Float.parseFloat(this.currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw floatParseException(e);
            }
        }

        /**
         * If the next token is a boolean, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public boolean consumeBoolean() throws ParseException {
            if (this.currentToken.equals("true")) {
                nextToken();
                return true;
            } else if (this.currentToken.equals("false")) {
                nextToken();
                return false;
            } else {
                throw parseException("Expected \"true\" or \"false\".");
            }
        }

        /**
         * If the next token is a string, consume it and return its (unescaped) value. Otherwise,
         * throw a {@link ParseException}.
         */
        public String consumeString() throws ParseException {
            return consumeByteString().toStringUtf8();
        }

        /**
         * If the next token is a string, consume it, unescape it as a
         * {@link com.googlecode.protobuf.format.ByteString}, and return it. Otherwise, throw a
         * {@link ParseException}.
         */
        public ByteString consumeByteString() throws ParseException {
            // In XML String values inside TEXT node don't need to be wrapped in quotes
            /*char quote = currentToken.length() > 0 ? currentToken.charAt(0) : '\0';
            if ((quote != '\"') && (quote != '\'')) {
                throw parseException("Expected string.");
            }

            if ((currentToken.length() < 2)
                || (currentToken.charAt(currentToken.length() - 1) != quote)) {
                throw parseException("String missing ending quote.");
            }*/

            try {
                String escaped = this.currentToken; //.substring(1, currentToken.length() - 1);
                ByteString result = FormatTextSupport.unescapeBytes(escaped, false);
                nextToken();
                return result;
            } catch (FormatTextSupport.InvalidEscapeSequence e) {
                throw parseException(e.getMessage());
            }
        }

        /**
         * Returns a {@link ParseException} with the current line and column numbers in the
         * description, suitable for throwing.
         */
        public ParseException parseException(String description) {
            // Note: People generally prefer one-based line and column numbers.
            return new ParseException((this.line + 1) + ":" + (this.column + 1) + ": " + description);
        }

        /**
         * Returns a {@link ParseException} with the line and column numbers of the previous token
         * in the description, suitable for throwing.
         */
        public ParseException parseExceptionPreviousToken(String description) {
            // Note: People generally prefer one-based line and column numbers.
            return new ParseException((this.previousLine + 1) + ":" + (this.previousColumn + 1) + ": "
                                      + description);
        }

        /**
         * Constructs an appropriate {@link ParseException} for the given {@code
         * NumberFormatException} when trying to parse an integer.
         */
        private ParseException integerParseException(NumberFormatException e) {
            return parseException("Couldn't parse integer: " + e.getMessage());
        }

        /**
         * Constructs an appropriate {@link ParseException} for the given {@code
         * NumberFormatException} when trying to parse a float or double.
         */
        private ParseException floatParseException(NumberFormatException e) {
            return parseException("Couldn't parse number: " + e.getMessage());
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
        Tokenizer tokenizer = new Tokenizer(input);

        // Need to first consume the outer object name element
        consumeOpeningElement(tokenizer);

        while (!tokenizer.tryConsume("</")) { // Continue till the object is done
            mergeField(tokenizer, extensionRegistry, builder);
        }

        consumeClosingElement(tokenizer);
    }

    private static String consumeOpeningElement(Tokenizer tokenizer) throws ParseException {
        tokenizer.consume("<");
        String openingElement = tokenizer.consumeIdentifier();
        tokenizer.consume(">");
        return openingElement;
    }

    private static void consumeClosingElement(Tokenizer tokenizer) throws ParseException {
        tokenizer.tryConsume("</");
        //tokenizer.consume("/");
        tokenizer.nextToken();
        tokenizer.consume(">");
    }

    private static String consumeExtensionIdentifier(Tokenizer tokenizer) throws ParseException {
        tokenizer.consume("type");
        tokenizer.consume("=");
        return tokenizer.consumeIdentifier();
    }

    /**
     * Parse a single field from {@code tokenizer} and merge it into {@code builder}. If a ',' is
     * detected after the field ends, the next field will be parsed automatically
     */
    private static void mergeField(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws ParseException {
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
                throw tokenizer.parseExceptionPreviousToken("Extension \""
                                                            + name
                                                            + "\" not found in the ExtensionRegistry.");
            } else if (extension.descriptor.getContainingType() != type) {
                throw tokenizer.parseExceptionPreviousToken("Extension \"" + name
                                                            + "\" does not extend message type \""
                                                            + type.getFullName() + "\".");
            }

            field = extension.descriptor;
        } else {
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
                throw tokenizer.parseExceptionPreviousToken("Message type \"" + type.getFullName()
                                                            + "\" has no field named \"" + name
                                                            + "\".");
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

    private static Object handleValue(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder,
            FieldDescriptor field,
            ExtensionRegistry.ExtensionInfo extension) throws ParseException {

        Object value = null;
        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            value = handleObject(tokenizer, extensionRegistry, builder, field, extension);
        } else {
            value = handlePrimitive(tokenizer, field);
        }

        return value;
    }

    private static Object handlePrimitive(Tokenizer tokenizer, FieldDescriptor field) throws ParseException {
        Object value = null;
        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
                value = tokenizer.consumeInt32();
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
                value = tokenizer.consumeInt64();
                break;

            case UINT32:
            case FIXED32:
                value = tokenizer.consumeUInt32();
                break;

            case UINT64:
            case FIXED64:
                value = tokenizer.consumeUInt64();
                break;

            case FLOAT:
                value = tokenizer.consumeFloat();
                break;

            case DOUBLE:
                value = tokenizer.consumeDouble();
                break;

            case BOOL:
                value = tokenizer.consumeBoolean();
                break;

            case STRING:
                value = tokenizer.consumeString();
                break;

            case BYTES:
                value = tokenizer.consumeByteString();
                break;

            case ENUM: {
                Descriptors.EnumDescriptor enumType = field.getEnumType();

                if (tokenizer.lookingAtInteger()) {
                    int number = tokenizer.consumeInt32();
                    value = enumType.findValueByNumber(number);
                    if (value == null) {
                        throw tokenizer.parseExceptionPreviousToken("Enum type \""
                                                                    + enumType.getFullName()
                                                                    + "\" has no value with number "
                                                                    + number + ".");
                    }
                } else {
                    String id = tokenizer.consumeIdentifier();
                    value = enumType.findValueByName(id);
                    if (value == null) {
                        throw tokenizer.parseExceptionPreviousToken("Enum type \""
                                                                    + enumType.getFullName()
                                                                    + "\" has no value named \""
                                                                    + id + "\".");
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

    private static Object handleObject(Tokenizer tokenizer,
            ExtensionRegistry extensionRegistry,
            Message.Builder builder,
            FieldDescriptor field,
            ExtensionRegistry.ExtensionInfo extension) throws ParseException {

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
                throw tokenizer.parseException("Expected \"" + endToken + "\".");
            }
            mergeField(tokenizer, extensionRegistry, subBuilder);
        }

        value = subBuilder.build();
        return value;
    }

}
