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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xml/Json/JavaProps 三类 Tokenizer 的共享机制层（reduce-code-duplication D2：解析层按"能过即收"收敛）。
 * <p>
 * 机制段（词法扫描、空白跳过、tryConsume/consume、类型化数值/布尔消费、行列定位消息格式化）为三份逐字
 * 克隆，收敛到本包私有 final 类；现状差异以构造参数保留、不做任何解析行为修正：
 * <ul>
 * <li>TOKEN 词法正则：各格式保留自有 Pattern 由外壳传入（Xml 的 extension/尖括号/八进制串特判 vs Json/Props
 * 的常规正则）。</li>
 * <li>{@link Kind}：consumeIdentifier 与 consumeString/consumeByteString 的三份现状形态——
 * XML 无引号字节串+可点号剥引号标识符；JSON 带引号 + JSON 语义文本反转义；PROPS 严格标识符 +
 * 相邻引号串自动拼接。</li>
 * <li>失败消息（"line:col: desc" 前缀）逐字保持；外壳捕获 {@link Failure} 后按各自 public
 * ParseException 类型重新构造，异常类型与消息文本对调用方完全不变。</li>
 * <li>本类同时实现 {@link FormatValueReader.Scanner}（Xml/JavaProps 的现状扫描面即本类本身，
 * 消费操作直通；包私有类型上的 public 方法对包外不可命名，不构成发布面变更）。</li>
 * </ul>
 */
final class FormatTokenizerCore implements FormatValueReader.Scanner<FormatTokenizerCore.Failure> {

    /**
     * 三种现状词法/值消费形态。
     */
    enum Kind {
        XML, JSON, PROPS
    }

    /**
     * 携带已按现状格式化的 "行:列: 描述" 消息的内部失败；外壳捕获后转为本格式既有 ParseException 类型。
     */
    static final class Failure extends Exception {

        private static final long serialVersionUID = 1L;

        Failure(String message) {
            super(message);
        }

    }

    // We use possesive quantifiers (*+ and ++) because otherwise the Java
    // regex matcher has stack overflows on large inputs.
    private static final Pattern WHITESPACE =
            Pattern.compile("(\\s|(#.*$))++", Pattern.MULTILINE);

    private static final Pattern DOUBLE_INFINITY = Pattern.compile(
            "-?inf(inity)?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern FLOAT_INFINITY = Pattern.compile(
            "-?inf(inity)?f?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern FLOAT_NAN = Pattern.compile(
            "nanf?",
            Pattern.CASE_INSENSITIVE);

    private final CharSequence text;

    private final Matcher matcher;

    private final Pattern tokenPattern;

    private final Kind kind;

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

    FormatTokenizerCore(CharSequence text, Pattern tokenPattern, Kind kind) {
        this.text = text;
        this.tokenPattern = tokenPattern;
        this.kind = kind;
        this.matcher = WHITESPACE.matcher(text);
        skipWhitespace();
        nextToken();
    }

    /**
     * Are we at the end of the input?
     */
    boolean atEnd() {
        return this.currentToken.length() == 0;
    }

    /**
     * Advance to the next token.
     */
    void nextToken() {
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
            this.matcher.usePattern(this.tokenPattern);
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
    boolean tryConsume(String token) {
        if (this.currentToken.equals(token)) {
            nextToken();
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the next token exactly matches {@code token}, consume it; otherwise failure with the
     * 现状 "Expected \"token\"." 消息。
     */
    void consume(String token) throws Failure {
        if (!tryConsume(token)) {
            throw new Failure(errorMessage("Expected \"" + token + "\"."));
        }
    }

    /**
     * Returns {@code true} if the next token is an integer, but does not consume it.
     */
    public    boolean lookingAtInteger() {
        if (this.currentToken.length() == 0) {
            return false;
        }

        char c = this.currentToken.charAt(0);
        return (('0' <= c) && (c <= '9')) || (c == '-') || (c == '+');
    }

    /**
     * Returns {@code true} if the next token is a boolean (true/false), but does not consume it.
     */
    boolean lookingAtBoolean() {
        if (this.currentToken.length() == 0) {
            return false;
        }

        return ("true".equals(this.currentToken) || "false".equals(this.currentToken));
    }

    /**
     * @return currentToken to which the Tokenizer is pointing.
     */
    String currentToken() {
        return this.currentToken;
    }

    /**
     * "行:列: 描述" 现状格式（当前 token 位置，一线制行列号）。
     */
    String errorMessage(String description) {
        // Note: People generally prefer one-based line and column numbers.
        return (this.line + 1) + ":" + (this.column + 1) + ": " + description;
    }

    /**
     * "行:列: 描述" 现状格式（前一个 token 位置）。
     */
    String previousTokenErrorMessage(String description) {
        // Note: People generally prefer one-based line and column numbers.
        return (this.previousLine + 1) + ":" + (this.previousColumn + 1) + ": "
               + description;
    }

    /**
     * If the next token is an identifier, consume it and return its value. Otherwise, failure.
     * PROPS 现状：字符集不含点号、不剥引号、消息无尾随字符；XML/JSON 现状：允许点号并剥除引号。
     */
    public    String consumeIdentifier() throws Failure {
        if (this.kind == Kind.PROPS) {
            for (int i = 0; i < this.currentToken.length(); i++) {
                final char c = this.currentToken.charAt(i);
                if (('a' <= c && c <= 'z') ||
                    ('A' <= c && c <= 'Z') ||
                    ('0' <= c && c <= '9') ||
                    (c == '_') //|| (c == '.')
                ) {
                    // OK
                } else {
                    throw new Failure(errorMessage("Expected identifier."));
                }
            }

            final String result = this.currentToken;
            nextToken();
            return result;
        }

        for (int i = 0; i < this.currentToken.length(); i++) {
            char c = this.currentToken.charAt(i);
            if ((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z'))
                || (('0' <= c) && (c <= '9')) || (c == '_') || (c == '.') || (c == '"')) {
                // OK
            } else {
                throw new Failure(errorMessage("Expected identifier. -" + c));
            }
        }

        String result = this.currentToken;
        // Need to clean-up result to remove quotes of any kind
        result = result.replaceAll("\"|'", "");
        nextToken();
        return result;
    }

    /**
     * If the next token is a 32-bit signed integer, consume it and return its value.
     */
    public    int consumeInt32() throws Failure {
        try {
            int result = FormatTextSupport.parseInt32(this.currentToken);
            nextToken();
            return result;
        } catch (NumberFormatException e) {
            throw new Failure(integerParseException(e));
        }
    }

    /**
     * If the next token is a 32-bit unsigned integer, consume it and return its value.
     */
    public    int consumeUInt32() throws Failure {
        try {
            int result = FormatTextSupport.parseUInt32(this.currentToken);
            nextToken();
            return result;
        } catch (NumberFormatException e) {
            throw new Failure(integerParseException(e));
        }
    }

    /**
     * If the next token is a 64-bit signed integer, consume it and return its value.
     */
    public    long consumeInt64() throws Failure {
        try {
            long result = FormatTextSupport.parseInt64(this.currentToken);
            nextToken();
            return result;
        } catch (NumberFormatException e) {
            throw new Failure(integerParseException(e));
        }
    }

    /**
     * If the next token is a 64-bit unsigned integer, consume it and return its value.
     */
    public    long consumeUInt64() throws Failure {
        try {
            long result = FormatTextSupport.parseUInt64(this.currentToken);
            nextToken();
            return result;
        } catch (NumberFormatException e) {
            throw new Failure(integerParseException(e));
        }
    }

    /**
     * If the next token is a double, consume it and return its value.
     */
    public    double consumeDouble() throws Failure {
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
            throw new Failure(floatParseException(e));
        }
    }

    /**
     * If the next token is a float, consume it and return its value.
     */
    public    float consumeFloat() throws Failure {
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
            throw new Failure(floatParseException(e));
        }
    }

    /**
     * If the next token is a boolean, consume it and return its value.
     */
    public    boolean consumeBoolean() throws Failure {
        if (this.currentToken.equals("true")) {
            nextToken();
            return true;
        } else if (this.currentToken.equals("false")) {
            nextToken();
            return false;
        } else {
            throw new Failure(errorMessage("Expected \"true\" or \"false\"."));
        }
    }

    /**
     * If the next token is a string, consume it and return its (unescaped) value.
     */
    public    String consumeString() throws Failure {
        if (this.kind == Kind.JSON) {
            char quote = this.currentToken.length() > 0 ? this.currentToken.charAt(0) : '\0';
            if ((quote != '\"') && (quote != '\'')) {
                throw new Failure(errorMessage("Expected string."));
            }

            if ((this.currentToken.length() < 2)
                || (this.currentToken.charAt(this.currentToken.length() - 1) != quote)) {
                throw new Failure(errorMessage("String missing ending quote."));
            }

            try {
                String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
                String result = FormatTextSupport.unescapeTextJson(escaped);
                nextToken();
                return result;
            } catch (FormatTextSupport.InvalidEscapeSequence e) {
                throw new Failure(errorMessage(e.getMessage()));
            }
        }
        return consumeByteString().toStringUtf8();
    }

    /**
     * If the next token is a string, consume it, unescape it as a {@link ByteString}, and return it.
     * XML 现状：TEXT 节点无引号包裹，整 token 走八进制字节反转义；JSON 现状：带引号 + 识别 \\uXXXX；
     * PROPS 现状：带引号且相邻字面量自动拼接（C/Python 风格）。
     */
    public    ByteString consumeByteString() throws Failure {
        if (this.kind == Kind.PROPS) {
            List<ByteString> list = new ArrayList<>();
            consumeByteString(list);
            while (this.currentToken.startsWith("'") || this.currentToken.startsWith("\"")) {
                consumeByteString(list);
            }
            return ByteString.copyFrom(list);
        }

        if (this.kind == Kind.JSON) {
            char quote = this.currentToken.length() > 0 ? this.currentToken.charAt(0) : '\0';
            if ((quote != '\"') && (quote != '\'')) {
                throw new Failure(errorMessage("Expected string."));
            }

            if ((this.currentToken.length() < 2)
                || (this.currentToken.charAt(this.currentToken.length() - 1) != quote)) {
                throw new Failure(errorMessage("String missing ending quote."));
            }

            try {
                String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
                ByteString result = FormatTextSupport.unescapeBytes(escaped, true);
                nextToken();
                return result;
            } catch (FormatTextSupport.InvalidEscapeSequence e) {
                throw new Failure(errorMessage(e.getMessage()));
            }
        }

        // In XML String values inside TEXT node don't need to be wrapped in quotes
        // fix-registered-defects 3.1：原单 token 通道覆盖不住 Xml 打印产物中的转义特殊字符集
        // （`\"` `\'` `\\` 与八进制/短转义混段时词法碎片化），改为从当前 token 起点按原文直读到
        // 元素闭合标记 `</`，再统一走 unescapeBytes 还原——打印产物对转义字符集读回闭合，
        // 且原文直读同时保住值内裸空格/控制序列边界（token 拼接时代的静默丢失面）。
        int start = this.pos;
        int end = -1;
        for (int k = start; k + 1 < this.text.length(); k++) {
            if (this.text.charAt(k) == '<' && this.text.charAt(k + 1) == '/') {
                end = k;
                break;
            }
        }
        if (end < 0) {
            throw new Failure(errorMessage("Expected \"</\"."));
        }
        if (end == start) {
            // 空值元素：currentToken 已是 "</"，无需重扫
            return ByteString.EMPTY;
        }
        try {
            String escaped = this.text.subSequence(start, end).toString();
            ByteString result = FormatTextSupport.unescapeBytes(escaped, false);
            this.matcher.region(end, this.matcher.regionEnd());
            nextToken();
            return result;
        } catch (FormatTextSupport.InvalidEscapeSequence e) {
            throw new Failure(errorMessage(e.getMessage()));
        }
    }

    /**
     * Like {@link #consumeByteString()} for PROPS but adds each token of the string to the given
     * list.  String literals (whether bytes or text) may come in multiple adjacent tokens which are
     * automatically concatenated, like in C or Python.
     */
    private void consumeByteString(List<ByteString> list) throws Failure {
        final char quote = this.currentToken.length() > 0 ? this.currentToken.charAt(0)
                                                          : '\0';
        if (quote != '\"' && quote != '\'') {
            throw new Failure(errorMessage("Expected string."));
        }

        if (this.currentToken.length() < 2 ||
            this.currentToken.charAt(this.currentToken.length() - 1) != quote) {
            throw new Failure(errorMessage("String missing ending quote."));
        }

        try {
            final String escaped =
                    this.currentToken.substring(1, this.currentToken.length() - 1);
            final ByteString result = FormatTextSupport.unescapeBytes(escaped, false);
            nextToken();
            list.add(result);
        } catch (FormatTextSupport.InvalidEscapeSequence e) {
            throw new Failure(errorMessage(e.getMessage()));
        }
    }

    @Override
    public Failure positionedEnumFailure(String description) {
        return new Failure(previousTokenErrorMessage(description));
    }

    /**
     * Constructs the "Couldn't parse integer: ..."现状消息 with location prefix.
     */
    private String integerParseException(NumberFormatException e) {
        return errorMessage("Couldn't parse integer: " + e.getMessage());
    }

    /**
     * Constructs the "Couldn't parse number: ..."现状消息 with location prefix.
     */
    private String floatParseException(NumberFormatException e) {
        return errorMessage("Couldn't parse number: " + e.getMessage());
    }

}
