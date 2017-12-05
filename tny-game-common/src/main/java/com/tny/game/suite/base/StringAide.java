package com.tny.game.suite.base;

import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Created by Kun Yang on 16/3/9.
 */
public class StringAide {

    /**
     * 如果 value is blank 返回 value, 否则返回elseValue
     *
     * @param value     值
     * @param elseValue 其他值
     * @return value is blank 返回 value, 否则返回elseValue
     */
    public static String ifBlankElse(String value, String elseValue) {
        if (isBlank(value))
            return value;
        return elseValue;
    }

    /**
     * 如果 value is blank 返回 value, 否则返回elseValue.get()
     *
     * @param value     值
     * @param elseValue 其他值提供者
     * @return value is blank 返回 value, 否则返回elseValue.get()
     */
    public static String ifBlankElse(String value, Supplier<String> elseValue) {
        if (isBlank(value))
            return value;
        return elseValue.get();
    }

    /**
     * 如果 value is not blank 返回 value, 否则返回elseValue
     *
     * @param value     值
     * @param elseValue 其他值
     * @return value is not blank 返回 value, 否则返回elseValue
     */
    public static String ifNotBlankElse(String value, String elseValue) {
        if (isNoneBlank(value))
            return value;
        return elseValue;
    }


    /**
     * 如果 value is not blank 返回 value, 否则返回elseValue.get()
     *
     * @param value     值
     * @param elseValue 其他值提供者
     * @return value is not blank 返回 value, 否则返回elseValue.get()
     */
    public static String ifNotBlankElse(String value, Supplier<String> elseValue) {
        if (isNoneBlank(value))
            return value;
        return elseValue.get();
    }


}
