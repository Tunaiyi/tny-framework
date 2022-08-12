/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-05-09 16:48
 */
public class LocaleAide {

    public static final Map<String, Locale> LOCALE_MAP = new ConcurrentHashMap<>();

    public static final String SCRIPTS_SEPARATE = "-";

    public static final String REGION_SEPARATE = "_";

    public static final int LANGUAGE_SIZE = 2;

    public static final int REGION_SIZE = 2;

    public static final int SCRIPTS_SIZE = 4;

    private LocaleAide() {
    }

    public static Locale of(String mark) {
        return LOCALE_MAP.computeIfAbsent(mark, LocaleAide::pares);
    }

    private static Locale pares(String mark) {
        List<String> values = slicing(mark);
        Locale.Builder builder = new Locale.Builder();
        boolean canBuild = false;
        for (String value : values) {
            if (isLanguage(value)) {
                builder.setLanguage(value);
                canBuild = true;
            }
            if (isScripts(value)) {
                builder.setScript(value);
                canBuild = true;
            }
            if (isRegion(value)) {
                builder.setRegion(value);
                canBuild = true;
            }
        }
        if (!canBuild) {
            throw new IllegalArgumentException(format("Locale pares {} error", mark));
        }
        return builder.build();
    }

    public static boolean isLanguage(String value) {
        if (value.length() != LANGUAGE_SIZE) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isUpperCase(character)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isScripts(String value) {
        if (value.length() != SCRIPTS_SIZE) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (index == 0) {
                if (Character.isLowerCase(character)) {
                    return false;
                }
            } else {
                if (Character.isUpperCase(character)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isRegion(String value) {
        if (value.length() != REGION_SIZE) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isLowerCase(character)) {
                return false;
            }
        }
        return true;
    }

    private static List<String> slicing(String mark) {
        List<String> localeStrings = new ArrayList<>();
        int scriptsIndex = mark.indexOf(SCRIPTS_SEPARATE);
        String temp = mark;
        if (scriptsIndex >= 0) {
            localeStrings.add(temp.substring(0, scriptsIndex));
            temp = temp.substring(scriptsIndex + 1);
        }
        int countryIndex = temp.indexOf(REGION_SEPARATE);
        if (countryIndex >= 0) {
            localeStrings.add(temp.substring(0, countryIndex));
            temp = temp.substring(countryIndex + 1);
        }
        localeStrings.add(temp);
        return localeStrings;
    }

    public static List<String> allMarks(Locale locale) {
        String language = locale.getLanguage();
        String script = locale.getScript();
        String country = locale.getCountry();
        List<String> locales = new ArrayList<>();
        if (isNotBlank(language) && isNotBlank(script) && isNotBlank(country)) {
            locales.add(mark(language, script, country));
        }
        if (isNotBlank(language) && isNotBlank(script)) {
            locales.add(mark(language, script, null));
        }
        if (isNotBlank(language) && isNotBlank(country)) {
            locales.add(mark(language, null, country));
        }
        if (isNotBlank(language)) {
            locales.add(mark(language, null, null));
        }
        if (isNotBlank(script) && isNotBlank(country)) {
            locales.add(mark(null, script, country));
        }
        if (isNotBlank(script)) {
            locales.add(mark(null, script, null));
        }
        if (isNotBlank(country)) {
            locales.add(mark(null, null, country));
        }
        return locales;
    }

    public static String mark(String language, String script, String country) {
        if (isNotBlank(language) && isNotBlank(script) && isNotBlank(country)) {
            return language + SCRIPTS_SEPARATE + script + REGION_SEPARATE + country;
        } else if (isNotBlank(language) && isNotBlank(script)) {
            return language + SCRIPTS_SEPARATE + script;
        } else if (isNotBlank(language) && isNotBlank(country)) {
            return language + REGION_SEPARATE + country;
        } else if (isNotBlank(language)) {
            return language;
        } else if (isNotBlank(script) && isNotBlank(country)) {
            return script + REGION_SEPARATE + country;
        } else if (isNotBlank(script)) {
            return script;
        } else if (isNotBlank(country)) {
            return country;
        }
        throw new IllegalArgumentException("error locale {}");
    }

    public static void main(String[] args) {
        System.out.println(LocaleAide.pares("zh-Hans_CN"));
        System.out.println(LocaleAide.pares("zh_CN"));
        System.out.println(LocaleAide.pares("zh"));
        System.out.println(LocaleAide.pares("CN").getDisplayCountry());
    }

}
