/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.io.config;

import com.tny.game.common.collection.map.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.Map.Entry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Kun Yang on 2018/7/20.
 */
public class ConfigTest {

    enum Type {

        ENUM_OBJECT_1,
        ENUM_OBJECT_2,
        ENUM_OBJECT_3,
        ENUM_OBJECT_4,
        ENUM_OBJECT_5,
    }

    private static final int TYPE_SIZE = 4;

    private static final String NUM_TYPE = "num_key";

    private static final String STRING_TYPE = "string_key";

    private static final String BOOLEAN_TYPE = "boolean_key";

    private static final String EUMN_TYPE = "eumn_key";

    private static final String NUM_KEY = "." + NUM_TYPE;

    private static final String STRING_KEY = "." + STRING_TYPE;

    private static final String BOOLEAN_KEY = "." + BOOLEAN_TYPE;

    private static final String EUMN_KEY = "." + EUMN_TYPE;

    public static final List<String> TYPES = Arrays.asList(
            NUM_TYPE, STRING_TYPE, BOOLEAN_TYPE, EUMN_TYPE);

    private static final String L1_NUM_1 = "10";

    private static final String L1_STR_2 = "L1value2";

    private static final String L1_BOOLEAN_3 = "true";

    private static final String L1_EUMN_4 = Type.ENUM_OBJECT_1.name();

    private static final String L2_NUM_1 = "20";

    private static final String L2_STR_2 = "L2value2";

    private static final String L2_BOOLEAN_3 = "false";

    private static final String L2_EUMN_4 = Type.ENUM_OBJECT_2.name();

    private static final String L3_NUM_1 = "30";

    private static final String L3_STR_2 = "L3value2";

    private static final String L3_BOOLEAN_3 = "true";

    private static final String L3_EUMN_4 = Type.ENUM_OBJECT_3.name();

    private static final String L4_NUM_1 = "40";

    private static final String L4_STR_2 = "L4value2";

    private static final String L4_BOOLEAN_3 = "false";

    private static final String L4_EUMN_4 = Type.ENUM_OBJECT_4.name();

    private static final String L5_NUM_1 = "50";

    private static final String L5_STR_2 = "L5value2";

    private static final String L5_BOOLEAN_3 = "true";

    private static final String L5_EUMN_4 = Type.ENUM_OBJECT_5.name();

    private static final String HEAD_1 = "l1";

    private static final String HEAD_2 = HEAD_1 + ".l2";

    private static final String HEAD_3 = HEAD_2 + ".l3";

    private static final String HEAD_4 = HEAD_3 + ".l4";

    private static final String HEAD_5 = HEAD_4 + ".l5";

    private static final String FIND_TEST_KEY = HEAD_4 + "al5";

    private static final String FIND_TEST_VALUE = "find_test_value";

    private Config config;

    private final Map<Object, Object> map = MapBuilder.newBuilder()
            .put(HEAD_1 + NUM_KEY, L1_NUM_1)
            .put(HEAD_1 + STRING_KEY, L1_STR_2)
            .put(HEAD_1 + BOOLEAN_KEY, L1_BOOLEAN_3)
            .put(HEAD_1 + EUMN_KEY, L1_EUMN_4)
            .put(HEAD_2 + NUM_KEY, L2_NUM_1)
            .put(HEAD_2 + STRING_KEY, L2_STR_2)
            .put(HEAD_2 + BOOLEAN_KEY, L2_BOOLEAN_3)
            .put(HEAD_2 + EUMN_KEY, L2_EUMN_4)
            .put(HEAD_3 + NUM_KEY, L3_NUM_1)
            .put(HEAD_3 + STRING_KEY, L3_STR_2)
            .put(HEAD_3 + BOOLEAN_KEY, L3_BOOLEAN_3)
            .put(HEAD_3 + EUMN_KEY, L3_EUMN_4)
            .put(HEAD_4 + NUM_KEY, L4_NUM_1)
            .put(HEAD_4 + STRING_KEY, L4_STR_2)
            .put(HEAD_4 + BOOLEAN_KEY, L4_BOOLEAN_3)
            .put(HEAD_4 + EUMN_KEY, L4_EUMN_4)
            .put(HEAD_5 + NUM_KEY, L5_NUM_1)
            .put(HEAD_5 + STRING_KEY, L5_STR_2)
            .put(HEAD_5 + BOOLEAN_KEY, L5_BOOLEAN_3)
            .put(HEAD_5 + EUMN_KEY, L5_EUMN_4)
            .put(FIND_TEST_KEY, FIND_TEST_VALUE)
            .build();

    @BeforeEach
    public void setUp() throws Exception {
        this.config = new PropertiesConfig(this.map);
    }

    @Test
    public void getParent() {
        assertFalse(this.config.getParent().isPresent());
        assertTrue(this.config.child(HEAD_5).getParent().isPresent());
    }

    @Test
    public void parentKey() {
        assertEquals(this.config.parentKey(), "");
        assertEquals(this.config.child(HEAD_5).parentKey(), HEAD_5);
    }

    @Test
    public void parentHeadKey() {
        assertEquals(this.config.parentHeadKey(), "");
        assertEquals(this.config.child(HEAD_5).parentHeadKey(), HEAD_5 + ".");
    }

    @Test
    public void getStr() {
        this.map.forEach((k, v) -> assertEquals(this.config.getString(k.toString()), v));
        this.map.forEach((k, v) -> assertNull(this.config.getString(k.toString() + 1)));
    }

    @Test
    public void getStr1() {
        String def = "default_value";
        this.map.forEach((k, v) -> assertEquals(this.config.getStr(k.toString() + 1, def), def));
    }

    @Test
    public void getInt() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getInt : " + k.toString());
                assertEquals(this.config.getInt(k.toString()), Integer.parseInt(v.toString()));
            }
        });
    }

    @Test
    public void getInt1() {
        int def = 1;
        this.map.forEach((k, v) -> assertEquals(this.config.getInt(k.toString() + 1, def), def));
    }

    @Test
    public void getLong() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getLong : " + k.toString());
                assertEquals(this.config.getInt(k.toString()), Long.parseLong(v.toString()));
            }
        });
    }

    @Test
    public void getLong1() {
        long def = 1L;
        this.map.forEach((k, v) -> assertEquals(this.config.getLong(k.toString() + 1, def), def));
    }

    @Test
    public void getDouble() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getDouble : " + k.toString());
                assertEquals(this.config.getDouble(k.toString()), Double.parseDouble(v.toString()), 0.0);
            }
        });
    }

    @Test
    public void getDouble1() {
        double def = 1;
        this.map.forEach((k, v) -> assertEquals(this.config.getDouble(k.toString() + 1, def), def, 0.0));
    }

    @Test
    public void getFloat() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getFloat : " + k.toString());
                assertEquals(this.config.getFloat(k.toString()), Float.parseFloat(v.toString()), 0.0F);
            }
        });
    }

    @Test
    public void getFloat1() {
        float def = 1F;
        this.map.forEach((k, v) -> assertEquals(this.config.getFloat(k.toString() + 1, def), def, 0.0));
    }

    @Test
    public void getBoolean() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(BOOLEAN_KEY)) {
                System.out.println("getBoolean : " + k.toString());
                assertEquals(this.config.getBoolean(k.toString()), Boolean.parseBoolean(v.toString()));
            }
        });
    }

    @Test
    public void getBoolean1() {
        boolean def = false;
        this.map.forEach((k, v) -> assertEquals(this.config.getBoolean(k.toString() + 1, def), def));
    }

    @Test
    public void getByte() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getByte : " + k.toString());
                assertEquals(this.config.getByte(k.toString()), (byte)Integer.parseInt(v.toString()));
            }
        });
    }

    @Test
    public void getByte1() {
        byte def = 1;
        this.map.forEach((k, v) -> assertEquals(this.config.getByte(k.toString() + 1, def), def));
    }

    @Test
    public void getObject() {
        this.map.forEach((k, v) -> assertEquals(this.config.getObject(k.toString()), v));
        this.map.forEach((k, v) -> assertNull(this.config.getObject(k.toString() + 1)));
    }

    @Test
    public void getObject1() {
        String def = "default_value";
        this.map.forEach((k, v) -> assertEquals(this.config.getObject(k.toString() + 1, def), def));
    }

    @Test
    public void getEnum() {
        this.map.forEach((k, v) -> {
            if (k.toString().endsWith(EUMN_KEY)) {
                System.out.println("getEnum : " + k.toString());
                assertEquals(this.config.getEnum(k.toString(), Type.class), Type.valueOf(v.toString()));
            }
        });
    }

    @Test
    public void getEnum1() {
        Type def = Type.ENUM_OBJECT_1;
        this.map.forEach((k, v) -> assertEquals(this.config.getEnum(k.toString() + 1, def), def));
    }

    @Test
    public void entrySet() {
        Collection<Entry<String, Object>> entries = this.config.entrySet();
        assertEquals(this.map.size(), entries.size());
        for (Entry<String, Object> entry : entries) {
            Object value = this.map.get(entry.getKey());
            assertEquals(value, entry.getValue());
        }
    }

    @Test
    public void keySet() {
        Collection<String> keys = this.config.keySet();
        assertEquals(this.map.size(), keys.size());
        for (String key : keys) {
            assertTrue(this.map.containsKey(key));
        }
    }

    @Test
    public void values() {
        Collection<Object> values = this.config.values();
        assertEquals(this.map.size(), values.size());
        for (Object value : values) {
            assertTrue(this.map.containsValue(value));
        }
    }

    @Test
    public void find() {
        Map<String, Object> map = this.config.find(HEAD_5 + ".*");
        assertEquals(TYPE_SIZE + 1, map.size());
        assertTrue(map.containsKey(FIND_TEST_KEY));
        map = this.config.find(HEAD_5.replace(".", "\\.") + ".*");
        assertEquals(TYPE_SIZE, map.size());
        assertFalse(map.containsKey(FIND_TEST_KEY));
    }

    @Test
    public void child() {
        Config l4Config = this.config.child(HEAD_4);
        Collection<Entry<String, Object>> l4l5Entries = l4Config.entrySet();
        assertEquals(TYPE_SIZE * 2, l4l5Entries.size());
        assertEquals(this.config.getInt(L4_NUM_1), l4Config.getInt(L4_NUM_1));
        assertEquals(this.config.getString(L4_STR_2), l4Config.getString(L4_STR_2));
        assertEquals(this.config.getBoolean(L4_BOOLEAN_3), l4Config.getBoolean(L4_BOOLEAN_3));
        assertEquals(this.config.getEnum(L4_EUMN_4, Type.class), l4Config.getEnum(L4_BOOLEAN_3, Type.class));
        assertEquals(this.config.getInt("l5." + L4_NUM_1), l4Config.getInt(L5_NUM_1));
        assertEquals(this.config.getString("l5." + L4_STR_2), l4Config.getString(L5_STR_2));
        assertEquals(this.config.getBoolean("l5." + L4_BOOLEAN_3), l4Config.getBoolean(L5_BOOLEAN_3));
        assertEquals(this.config.getEnum("l5." + L4_EUMN_4, Type.class), l4Config.getEnum(L5_BOOLEAN_3, Type.class));

        Config l5Config = this.config.child(HEAD_5);
        Collection<Entry<String, Object>> l5Entries = l5Config.entrySet();
        assertEquals(TYPE_SIZE * 1, l5Entries.size());
        assertEquals(this.config.getInt(L4_NUM_1), l5Config.getInt(L5_NUM_1));
        assertEquals(this.config.getString(L4_STR_2), l5Config.getString(L5_STR_2));
        assertEquals(this.config.getBoolean(L4_BOOLEAN_3), l5Config.getBoolean(L5_BOOLEAN_3));
        assertEquals(this.config.getEnum(L4_EUMN_4, Type.class), l5Config.getEnum(L5_BOOLEAN_3, Type.class));
    }

}