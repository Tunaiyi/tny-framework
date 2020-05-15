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
        if (!canBuild)
            throw new IllegalArgumentException(format("Locale pares {} error", mark));
        return builder.build();
    }

    public static boolean isLanguage(String value) {
        if (value.length() != LANGUAGE_SIZE)
            return false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isUpperCase(character))
                return false;
        }
        return true;
    }

    public static boolean isScripts(String value) {
        if (value.length() != SCRIPTS_SIZE)
            return false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (index == 0) {
                if (Character.isLowerCase(character))
                    return false;
            } else {
                if (Character.isUpperCase(character))
                    return false;
            }
        }
        return true;
    }

    public static boolean isRegion(String value) {
        if (value.length() != REGION_SIZE)
            return false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isLowerCase(character))
                return false;
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

    public static void main(String[] args) {
        System.out.println(LocaleAide.pares("zh-Hans_CN"));
        System.out.println(LocaleAide.pares("zh_CN"));
        System.out.println(LocaleAide.pares("zh"));
        System.out.println(LocaleAide.pares("CN").getDisplayCountry());
    }

}
