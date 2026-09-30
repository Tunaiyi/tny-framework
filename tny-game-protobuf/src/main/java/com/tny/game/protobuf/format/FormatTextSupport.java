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

import java.io.IOException;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Protobuf2*Format 五格式类共享的转义/反转义与数字解析工具（reduce-code-duplication D2：包私有共享件）。
 * <p>
 * 方法体逐字搬运自原五类的克隆段，现状差异以参数保留、不做任何行为修正：
 * <ul>
 * <li>{@link #escapeBytes(ByteString, LowByteMode)}：非可打印 ASCII 低位字节 default 分支——Xml/Html/JavaProps
 * 走三-digit 八进制（{@link LowByteMode#OCTAL}），Json 走 \\uXXXX（{@link LowByteMode#UNICODE}）。</li>
 * <li>{@link #unescapeBytes(CharSequence, boolean)}：{@code \\uXXXX} 识别仅 Json 开启（unicodeEscape=true，
 * 含其现状四位还原公式）；关闭时 {@code \\u} 与其余非法转义一致抛错。</li>
 * <li>escapeText/unescapeText：Json 为 JSON 语义（{@link #escapeTextJson}/{@link #unescapeTextJson}），
 * Xml/Html/JavaProps 为字节八进制语义（{@link #escapeTextLegacy}/{@link #unescapeTextLegacy}），两套现状并列保留。</li>
 * </ul>
 */
final class FormatTextSupport {

    private FormatTextSupport() {
    }

    /**
     * escapeBytes 中非可打印低位字节（b &lt; 0x20，含负数高位字节强转 char 后 ≥ 0xFF00）的输出形态。
     */
    enum LowByteMode {
        OCTAL, UNICODE
    }

    /**
     * Xml/Html/JavaProps 现状：低位/高位字节八进制转义。
     */
    static String escapeBytesOctal(ByteString input) {
        return escapeBytes(input, LowByteMode.OCTAL);
    }

    /**
     * Json 现状：低位/高位字节 \\uXXXX 转义。
     */
    static String escapeBytesUnicode(ByteString input) {
        return escapeBytes(input, LowByteMode.UNICODE);
    }

    /**
     * Escapes bytes in the format used in protocol buffer text format, which is the same as the
     * format used for C string literals. All bytes that are not printable 7-bit ASCII characters
     * are escaped, as well as backslash, single-quote, and double-quote characters. Characters for
     * which no defined short-hand escape sequence is defined will be escaped using 3-digit octal
     * sequences.
     */
    static String escapeBytes(ByteString input, LowByteMode lowByteMode) {
        StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); i++) {
            byte b = input.byteAt(i);
            switch (b) {
                // Java does not recognize \a or \v, apparently.
                case 0x07:
                    builder.append("\\a");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case 0x0b:
                    builder.append("\\v");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\'':
                    builder.append("\\\'");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                default:
                    if (b >= 0x20) {
                        builder.append((char) b);
                    } else if (lowByteMode == LowByteMode.UNICODE) {
                        final String unicodeString = unicodeEscaped((char) b);
                        builder.append(unicodeString);
                    } else {
                        builder.append('\\');
                        builder.append((char) ('0' + ((b >>> 6) & 3)));
                        builder.append((char) ('0' + ((b >>> 3) & 7)));
                        builder.append((char) ('0' + (b & 7)));
                    }
                    break;
            }
        }
        return builder.toString();
    }

    static String unicodeEscaped(char ch) {
        StringBuilder builder = new StringBuilder(6);
        appendEscapedUnicode(builder, ch);
        return builder.toString();
    }

    /**
     * Un-escape a byte sequence as escaped using
     * {@link #escapeBytes(ByteString, LowByteMode)}. Two-digit hex escapes (starting with "\x") are
     * also recognized.
     *
     * @param unicodeEscape Json 现状识别 \\uXXXX（其还原公式为既有形态，原样保留）；Xml/Html/JavaProps 现状不识别
     */
    static ByteString unescapeBytes(CharSequence input, boolean unicodeEscape) throws InvalidEscapeSequence {
        byte[] result = new byte[input.length()];
        int pos = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\\') {
                if (i + 1 < input.length()) {
                    ++i;
                    c = input.charAt(i);
                    if (isOctal(c)) {
                        // Octal escape.
                        int code = digitValue(c);
                        if ((i + 1 < input.length()) && isOctal(input.charAt(i + 1))) {
                            ++i;
                            code = code * 8 + digitValue(input.charAt(i));
                        }
                        if ((i + 1 < input.length()) && isOctal(input.charAt(i + 1))) {
                            ++i;
                            code = code * 8 + digitValue(input.charAt(i));
                        }
                        result[pos++] = (byte) code;
                    } else {
                        switch (c) {
                            case 'a':
                                result[pos++] = 0x07;
                                break;
                            case 'b':
                                result[pos++] = '\b';
                                break;
                            case 'f':
                                result[pos++] = '\f';
                                break;
                            case 'n':
                                result[pos++] = '\n';
                                break;
                            case 'r':
                                result[pos++] = '\r';
                                break;
                            case 't':
                                result[pos++] = '\t';
                                break;
                            case 'v':
                                result[pos++] = 0x0b;
                                break;
                            case '\\':
                                result[pos++] = '\\';
                                break;
                            case '\'':
                                result[pos++] = '\'';
                                break;
                            case '"':
                                result[pos++] = '\"';
                                break;

                            case 'x':
                                // hex escape
                                int code = 0;
                                if ((i + 1 < input.length()) && isHex(input.charAt(i + 1))) {
                                    ++i;
                                    code = digitValue(input.charAt(i));
                                } else {
                                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\x' with no digits");
                                }
                                if ((i + 1 < input.length()) && isHex(input.charAt(i + 1))) {
                                    ++i;
                                    code = code * 16 + digitValue(input.charAt(i));
                                }
                                result[pos++] = (byte) code;
                                break;

                            case 'u':
                                if (!unicodeEscape) {
                                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\" + c
                                                                    + "'");
                                }
                                // UTF8 escape
                                code = (16 * 3 * digitValue(input.charAt(i + 1))) +
                                       (16 * 2 * digitValue(input.charAt(i + 2))) +
                                       (16 * digitValue(input.charAt(i + 3))) +
                                       digitValue(input.charAt(i + 4));
                                i = i + 4;
                                result[pos++] = (byte) code;
                                break;

                            default:
                                throw new InvalidEscapeSequence("Invalid escape sequence: '\\" + c
                                                                + "'");
                        }
                    }
                } else {
                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\' at end of string.");
                }
            } else {
                result[pos++] = (byte) c;
            }
        }

        return ByteString.copyFrom(result, 0, pos);
    }

    /**
     * Thrown by {@link #unescapeBytes(CharSequence, boolean)} and {@link #unescapeTextJson(String)}
     * when an invalid escape sequence is seen. 原五类各自的包私有 InvalidEscapeSequence/
     * InvalidEscapeSequenceException 克隆合并为本单一类型（消息串逐字保持）。
     */
    static class InvalidEscapeSequence extends IOException {

        private static final long serialVersionUID = 1L;

        public InvalidEscapeSequence(String description) {
            super(description);
        }

    }

    /**
     * Like {@link #escapeBytesOctal(ByteString)}, but escapes a text string (Xml/Html/JavaProps 现状).
     * Non-ASCII characters are first encoded as UTF-8, then each byte is escaped individually as a
     * 3-digit octal escape. Yes, it's weird.
     */
    static String escapeTextLegacy(String input) {
        return escapeBytesOctal(ByteString.copyFromUtf8(input));
    }

    /**
     * Un-escape a text string as escaped using {@link #escapeTextLegacy(String)} (Xml/JavaProps 现状).
     * Two-digit hex escapes (starting with "\x") are also recognized.
     */
    static String unescapeTextLegacy(String input) throws InvalidEscapeSequence {
        return unescapeBytes(input, false).toStringUtf8();
    }

    /**
     * Implements JSON string escaping as specified <a href="http://www.ietf.org/rfc/rfc4627.txt">here</a>
     * (Json 现状).
     * <ul>
     * <li>The following characters are escaped by prefixing them with a '\' : \b,\f,\n,\r,\t,\,"</li>
     * <li>Other control characters in the range 0x0000-0x001F are escaped using the \\uXXXX notation</li>
     * <li>UTF-16 surrogate pairs are encoded using the \\uXXXX\\uXXXX notation</li>
     * <li>any other character is printed as-is</li>
     * </ul>
     */
    static String escapeTextJson(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        CharacterIterator iter = new StringCharacterIterator(input);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            switch (c) {
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                default:
                    // Check for other control characters
                    if (c >= 0x0000 && c <= 0x001F) {
                        appendEscapedUnicode(builder, c);
                    } else if (Character.isHighSurrogate(c)) {
                        // Encode the surrogate pair using 2 six-character sequence (\\uXXXX\\uXXXX)
                        appendEscapedUnicode(builder, c);
                        c = iter.next();
                        if (c == CharacterIterator.DONE) {
                            throw new IllegalArgumentException(
                                    "invalid unicode string: unexpected high surrogate pair value without corresponding low value.");
                        }
                        appendEscapedUnicode(builder, c);
                    } else {
                        // Anything else can be printed as-is
                        builder.append(c);
                    }
                    break;
            }
        }
        return builder.toString();
    }

    static void appendEscapedUnicode(StringBuilder builder, char ch) {
        String prefix = "\\u";
        if (ch < 0x10) {
            prefix = "\\u000";
        } else if (ch < 0x100) {
            prefix = "\\u00";
        } else if (ch < 0x1000) {
            prefix = "\\u0";
        }
        builder.append(prefix).append(Integer.toHexString(ch));
    }

    /**
     * Un-escape a text string as escaped using {@link #escapeTextJson(String)} (Json 现状：字符级
     * 反转义，仅 JSON 短转义 + \\uXXXX，不识别八进制/\a/\x)。
     */
    static String unescapeTextJson(String input) throws InvalidEscapeSequence {
        StringBuilder builder = new StringBuilder();
        char[] array = input.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (c == '\\') {
                if (i + 1 < array.length) {
                    ++i;
                    c = array[i];
                    switch (c) {
                        case 'b':
                            builder.append('\b');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'n':
                            builder.append('\n');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case '\\':
                            builder.append('\\');
                            break;
                        case '"':
                            builder.append('\"');
                            break;
                        case '\'':
                            builder.append('\'');
                            break;
                        case 'u':
                            // read the next 4 chars
                            if (i + 4 < array.length) {
                                ++i;
                                int code = Integer.parseInt(new String(array, i, 4), 16);
                                // this cast is safe because we know how many chars we read
                                builder.append((char) code);
                                i += 3;
                            } else {
                                throw new InvalidEscapeSequence("Invalid escape sequence: '\\u' at end of string.");
                            }
                            break;
                        default:
                            throw new InvalidEscapeSequence("Invalid escape sequence: '\\" + c + "'");
                    }
                } else {
                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\' at end of string.");
                }
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    /**
     * Is this an octal digit?
     */
    private static boolean isOctal(char c) {
        return ('0' <= c) && (c <= '7');
    }

    /**
     * Is this a hex digit?
     */
    private static boolean isHex(char c) {
        return (('0' <= c) && (c <= '9')) || (('a' <= c) && (c <= 'f'))
               || (('A' <= c) && (c <= 'F'));
    }

    /**
     * Interpret a character as a digit (in any base up to 36) and return the numeric value. This is
     * like {@code Character.digit()} but we don't accept non-ASCII digits.
     */
    private static int digitValue(char c) {
        if (('0' <= c) && (c <= '9')) {
            return c - '0';
        } else if (('a' <= c) && (c <= 'z')) {
            return c - 'a' + 10;
        } else {
            return c - 'A' + 10;
        }
    }

    /**
     * Convert an unsigned 32-bit integer to a string.
     */
    static String unsignedToString(int value) {
        if (value >= 0) {
            return Integer.toString(value);
        } else {
            return Long.toString((value) & 0x00000000FFFFFFFFL);
        }
    }

    /**
     * Convert an unsigned 64-bit integer to a string.
     */
    static String unsignedToString(long value) {
        if (value >= 0) {
            return Long.toString(value);
        } else {
            // Pull off the most-significant bit so that BigInteger doesn't think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63).toString();
        }
    }

    /**
     * Parse a 32-bit signed integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively.
     */
    static int parseInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, true, false);
    }

    /**
     * Parse a 32-bit unsigned integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively. The result is coerced to a (signed) {@code int}
     * when returned since Java has no unsigned integer type.
     */
    static int parseUInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, false, false);
    }

    /**
     * Parse a 64-bit signed integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively.
     */
    static long parseInt64(String text) throws NumberFormatException {
        return parseInteger(text, true, true);
    }

    /**
     * Parse a 64-bit unsigned integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively. The result is coerced to a (signed) {@code long}
     * when returned since Java has no unsigned long type.
     */
    static long parseUInt64(String text) throws NumberFormatException {
        return parseInteger(text, false, true);
    }

    private static long parseInteger(String text, boolean isSigned, boolean isLong) throws NumberFormatException {
        int pos = 0;

        boolean negative = false;
        if (text.startsWith("-", pos)) {
            if (!isSigned) {
                throw new NumberFormatException("Number must be positive: " + text);
            }
            ++pos;
            negative = true;
        }

        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        } else if (text.startsWith("0", pos)) {
            radix = 8;
        }

        String numberText = text.substring(pos);

        long result = 0;
        if (numberText.length() < 16) {
            // Can safely assume no overflow.
            result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }

            // Check bounds.
            // No need to check for 64-bit numbers since they'd have to be 16 chars
            // or longer to overflow.
            if (!isLong) {
                if (isSigned) {
                    if ((result > Integer.MAX_VALUE) || (result < Integer.MIN_VALUE)) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: "
                                                        + text);
                    }
                } else {
                    if ((result >= (1L << 32)) || (result < 0)) {
                        throw new NumberFormatException("Number out of range for 32-bit unsigned integer: "
                                                        + text);
                    }
                }
            }
        } else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }

            // Check bounds.
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: "
                                                        + text);
                    }
                } else {
                    if (bigValue.bitLength() > 32) {
                        throw new NumberFormatException("Number out of range for 32-bit unsigned integer: "
                                                        + text);
                    }
                }
            } else {
                if (isSigned) {
                    if (bigValue.bitLength() > 63) {
                        throw new NumberFormatException("Number out of range for 64-bit signed integer: "
                                                        + text);
                    }
                } else {
                    if (bigValue.bitLength() > 64) {
                        throw new NumberFormatException("Number out of range for 64-bit unsigned integer: "
                                                        + text);
                    }
                }
            }

            result = bigValue.longValue();
        }

        return result;
    }

    private static final int READ_BUFFER_SIZE = 4096;

    // TODO(chrisn): See if working around java.io.Reader#read(CharBuffer)
    // overhead is worthwhile
    static StringBuilder toStringBuilder(Readable input) throws IOException {
        StringBuilder text = new StringBuilder();
        CharBuffer buffer = CharBuffer.allocate(READ_BUFFER_SIZE);
        while (true) {
            int n = input.read(buffer);
            if (n == -1) {
                break;
            }
            buffer.flip();
            text.append(buffer, 0, n);
        }
        return text;
    }

}
