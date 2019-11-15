package com.tny.game.common.number;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public final class ScaleCharacterSets {

    private static final Map<CharSequence, ScaleCharacterSet> CHARACTER_SET_MAP = new ConcurrentHashMap<>();

    public static final ScaleCharacterSet BINARY = ScaleCharacterSets.of("01");

    public static final ScaleCharacterSet HEX_UPPER = ScaleCharacterSets.of("0123456789ABCDEF");

    public static final ScaleCharacterSet HEX_LOWER = ScaleCharacterSets.of("0123456789abcdef");

    public static final ScaleCharacterSet THIRTY_SIX_SCALE_UPPER = ScaleCharacterSets.of("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    public static final ScaleCharacterSet THIRTY_SIX_SCALE_LOWER = ScaleCharacterSets.of("0123456789abcdefghijklmnopqrstuvwxyz");

    public static final ScaleCharacterSet SIXTY_TWO_SCALE = ScaleCharacterSets.of("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

    public static ScaleCharacterSet of(char[] characters) {
        String charactersSet = new String(characters);
        return of(charactersSet);
    }

    public static ScaleCharacterSet of(CharSequence characterSet) {
        Set<Character> filterSet = new HashSet<>();
        ScaleCharacterSet set = CHARACTER_SET_MAP.get(characterSet);
        if (set != null)
            return set;
        char[] characters = new char[characterSet.length()];
        for (int index = 0; index < characters.length; index++) {
            char c = characterSet.charAt(index);
            if (!filterSet.add(c))
                throw new IllegalArgumentException("characterSet " + characterSet + " char " + c + " is exist");
            characters[index] = c;
        }
        set = new DefaultScaleCharacterSet(characters);
        return ifNull(CHARACTER_SET_MAP.putIfAbsent(set.getKey(), set), set);
    }

    private ScaleCharacterSets() {
    }

    public static class DefaultScaleCharacterSet implements ScaleCharacterSet {

        private char[] characters;

        private String key;

        private int length;

        private DefaultScaleCharacterSet(char[] characters) {
            this.characters = ArrayUtils.clone(characters);
            this.key = new String(this.characters);
            if (this.characters.length > 0)
                this.length = (byte) this.characters.length;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public char getChar(int value) {
            return characters[value];
        }

    }

}
