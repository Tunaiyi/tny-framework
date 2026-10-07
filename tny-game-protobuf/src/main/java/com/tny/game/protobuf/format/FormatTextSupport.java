package com.tny.game.protobuf.format;
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
 * fix-registered-defects 2.2 修正为 16³/16²/16¹/16⁰ 四位权展开还原，字节往返无损，残缺转义受控失败）；
 * 关闭时 {@code \\u} 与其余非法转义一致抛错。</li>
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
     * 短转义对表（本类内部 escapeBytes/escapeTextJson/unescapeTextJson 三处 switch 克隆的 D2 收口收敛单一事实源）：
     * 左列实际字符，右列转义序列（其第 2 字符即反转义方向的转义标识字符，Java does not recognize \a or \v,
     * apparently.——\a/\v 两行即承载此现状）。三处现状使用的子集以"表前缀行数"参数保留，不做统一：
     * escapeBytes 用全 10 行；escapeTextJson 用 JSON 短转义 7 行；unescapeTextJson 识别 8 行
     * （含 escapeTextJson 从不产出的单引号行）。
     */
    private static final char[] SHORT_ESCAPE_ACTUALS = {
            '\b', '\f', '\n', '\r', '\t', '\\', '"', '\'', 0x07, 0x0b};

    private static final String[] SHORT_ESCAPE_SEQUENCES = {
            "\\b", "\\f", "\\n", "\\r", "\\t", "\\\\", "\\\"", "\\'", "\\a", "\\v"};

    /** escapeBytes 现状使用行数：全表。 */
    private static final int BYTE_ESCAPE_ROWS = SHORT_ESCAPE_ACTUALS.length;
    /** escapeTextJson 现状使用行数：JSON 短转义 7 行。 */
    private static final int JSON_ESCAPE_ROWS = 7;
    /** unescapeTextJson 现状识别行数：JSON 7 行 + 单引号。 */
    private static final int JSON_UNESCAPE_ROWS = 8;
    /** 反转义查表未命中哨兵（表内实际字符均 &lt;= 0x5C，不可能与该值冲突）。 */
    private static final char NO_SHORT_ESCAPE = '\uFFFF';

    /**
     * 转义方向查表：按实际字符在前 rows 行内查找，命中返回转义序列，未命中返回 null。
     */
    private static String shortEscapeSequence(char c, int rows) {
        for (int i = 0; i < rows; i++) {
            if (SHORT_ESCAPE_ACTUALS[i] == c) {
                return SHORT_ESCAPE_SEQUENCES[i];
            }
        }
        return null;
    }

    /**
     * 反转义方向查表：按转义标识字符（转义序列的第 2 个字符）在前 rows 行内查找，
     * 命中返回实际字符（'b' -> '\b'），未命中返回 {@link #NO_SHORT_ESCAPE}。
     */
    private static char shortUnescape(char identifier, int rows) {
        for (int i = 0; i < rows; i++) {
            if (SHORT_ESCAPE_SEQUENCES[i].charAt(1) == identifier) {
                return SHORT_ESCAPE_ACTUALS[i];
            }
        }
        return NO_SHORT_ESCAPE;
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
            String escaped = shortEscapeSequence((char) b, BYTE_ESCAPE_ROWS);
            if (escaped != null) {
                builder.append(escaped);
            } else if (b >= 0x20) {
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
     * @param unicodeEscape Json 形态识别 \\uXXXX（fix-registered-defects 2.2 已修正为 16³/16²/16¹/16⁰ 权展开，
     *                      往返逐字节保真）；Xml/Html/JavaProps 形态不识别 \\u（残缺转义受控失败）
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
                                // fix-registered-defects 2.2：\\uXXXX 四位按权展开 16³/16²/16¹/16⁰ 还原
                                // （原 16*3/16*2/16*1 系数为在册钉桩缺陷，往返对高位字节有损）；
                                // 残缺转义改受控失败（消息与 unescapeTextJson 现状逐字一致），不再越界崩溃。
                                if (i + 4 >= input.length()) {
                                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\u' at end of string.");
                                }
                                // UTF8 escape
                                code = (16 * 16 * 16 * digitValue(input.charAt(i + 1))) +
                                       (16 * 16 * digitValue(input.charAt(i + 2))) +
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
            String escaped = shortEscapeSequence(c, JSON_ESCAPE_ROWS);
            if (escaped != null) {
                builder.append(escaped);
            } else if (c >= 0x0000 && c <= 0x001F) {
                // Check for other control characters
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
        }
        return builder.toString();
    }

    /**
     * fix-registered-defects 2.3（design D4）：Html 形态 STRING 值接入统一转义表——JSON 短转义行
     * （\\b \\f \\n \\r \\t \\\\ \\"）+ 尖括号标记分隔符行（\\&lt; 族以 \\< \\&gt; 序列承载），
     * 控制字符 \\uXXXX、代理对 \\uXXXX\\uXXXX，其余原样。值内标记/换行以转义序列承载后，
     * 文本结构不再被值内字符破坏（HtmlGenerator 的 &lt;br/&gt;/&lt;div&gt; 装饰层原样保留——
     * 族内统一策略与形态各自语法不冲突，装饰差异仍参数化承载于各形态 Sink）。
     */
    static String escapeTextHtml(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        CharacterIterator iter = new StringCharacterIterator(input);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            String escaped = shortEscapeSequence(c, JSON_ESCAPE_ROWS);
            if (escaped != null) {
                builder.append(escaped);
            } else if (c == '<') {
                builder.append("\\<");
            } else if (c == '>') {
                builder.append("\\>");
            } else if (c >= 0x0000 && c <= 0x001F) {
                builder.append(unicodeEscaped(c));
            } else if (Character.isHighSurrogate(c)) {
                builder.append(unicodeEscaped(c));
                c = iter.next();
                if (c == CharacterIterator.DONE) {
                    throw new IllegalArgumentException(
                            "invalid unicode string: unexpected high surrogate pair value without corresponding low value.");
                }
                builder.append(unicodeEscaped(c));
            } else {
                builder.append(c);
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
                    if (c == 'u') {
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
                    } else {
                        char unescaped = shortUnescape(c, JSON_UNESCAPE_ROWS);
                        if (unescaped != NO_SHORT_ESCAPE) {
                            builder.append(unescaped);
                        } else {
                            throw new InvalidEscapeSequence("Invalid escape sequence: '\\" + c + "'");
                        }
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
