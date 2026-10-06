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

package com.tny.game.common.number;

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
        if (set != null) {
            return set;
        }
        char[] characters = new char[characterSet.length()];
        for (int index = 0; index < characters.length; index++) {
            char c = characterSet.charAt(index);
            if (!filterSet.add(c)) {
                throw new IllegalArgumentException("characterSet " + characterSet + " char " + c + " is exist");
            }
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
            this.characters = characters.clone();
            this.key = new String(this.characters);
            // 真实长度（曾被 (byte) 截断：256 字符集回绕为 0，超 127 基数静默退化为十进制假成功）
            this.length = this.characters.length;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public char getChar(int value) {
            return this.characters[value];
        }

    }

}
