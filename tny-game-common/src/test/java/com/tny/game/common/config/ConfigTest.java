package com.tny.game.common.config;

import com.tny.game.common.collection.*;
import org.junit.*;

import java.util.*;
import java.util.Map.Entry;

import static org.junit.Assert.*;

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


    public static final int TYPE_SIZE = 4;

    public static final String NUM_TYPE = "num_key";
    public static final String STRING_TYPE = "string_key";
    public static final String BOOLEAN_TYPE = "boolean_key";
    public static final String EUMN_TYPE = "eumn_key";


    public static final String NUM_KEY = "." + NUM_TYPE;
    public static final String STRING_KEY = "." + STRING_TYPE;
    public static final String BOOLEAN_KEY = "." + BOOLEAN_TYPE;
    public static final String EUMN_KEY = "." + EUMN_TYPE;

    public static final List<String> TYPES = Arrays.asList(
            NUM_TYPE, STRING_TYPE, BOOLEAN_TYPE, EUMN_TYPE);

    public static final String L1_NUM_1 = "10";
    public static final String L1_STR_2 = "L1value2";
    public static final String L1_BOOLEAN_3 = "true";
    public static final String L1_EUMN_4 = Type.ENUM_OBJECT_1.name();
    public static final String L2_NUM_1 = "20";
    public static final String L2_STR_2 = "L2value2";
    public static final String L2_BOOLEAN_3 = "false";
    public static final String L2_EUMN_4 = Type.ENUM_OBJECT_2.name();
    public static final String L3_NUM_1 = "30";
    public static final String L3_STR_2 = "L3value2";
    public static final String L3_BOOLEAN_3 = "true";
    public static final String L3_EUMN_4 = Type.ENUM_OBJECT_3.name();
    public static final String L4_NUM_1 = "40";
    public static final String L4_STR_2 = "L4value2";
    public static final String L4_BOOLEAN_3 = "false";
    public static final String L4_EUMN_4 = Type.ENUM_OBJECT_4.name();
    public static final String L5_NUM_1 = "50";
    public static final String L5_STR_2 = "L5value2";
    public static final String L5_BOOLEAN_3 = "true";
    public static final String L5_EUMN_4 = Type.ENUM_OBJECT_5.name();


    public static final String HEAD_1 = "l1";
    public static final String HEAD_2 = HEAD_1 + ".l2";
    public static final String HEAD_3 = HEAD_2 + ".l3";
    public static final String HEAD_4 = HEAD_3 + ".l4";
    public static final String HEAD_5 = HEAD_4 + ".l5";


    public static final String FIND_TEST_KEY = HEAD_4 + "al5";
    public static final String FIND_TEST_VALUE = "find_test_value";

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

    @Before
    public void setUp() throws Exception {
        config = new PropertiesConfig(map);
    }


    @Test
    public void getParent() {
        assertFalse(config.getParent().isPresent());
        assertTrue(config.child(HEAD_5).getParent().isPresent());
    }

    @Test
    public void parentKey() {
        assertEquals(config.parentKey(), "");
        assertEquals(config.child(HEAD_5).parentKey(), HEAD_5);
    }

    @Test
    public void parentHeadKey() {
        assertEquals(config.parentHeadKey(), "");
        assertEquals(config.child(HEAD_5).parentHeadKey(), HEAD_5 + ".");
    }

    @Test
    public void getStr() {
        map.forEach((k, v) -> assertEquals(config.getString(k.toString()), v));
        map.forEach((k, v) -> assertNull(config.getString(k.toString() + 1)));
    }

    @Test
    public void getStr1() {
        String def = "default_value";
        map.forEach((k, v) -> assertEquals(config.getStr(k.toString() + 1, def), def));
    }

    @Test
    public void getInt() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getInt : " + k.toString());
                assertEquals(config.getInt(k.toString()), Integer.parseInt(v.toString()));
            }
        });
    }

    @Test
    public void getInt1() {
        int def = 1;
        map.forEach((k, v) -> assertEquals(config.getInt(k.toString() + 1, def), def));
    }

    @Test
    public void getLong() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getLong : " + k.toString());
                assertEquals(config.getInt(k.toString()), Long.parseLong(v.toString()));
            }
        });
    }

    @Test
    public void getLong1() {
        long def = 1L;
        map.forEach((k, v) -> assertEquals(config.getLong(k.toString() + 1, def), def));
    }

    @Test
    public void getDouble() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getDouble : " + k.toString());
                assertEquals(config.getDouble(k.toString()), Double.parseDouble(v.toString()), 0.0);
            }
        });
    }

    @Test
    public void getDouble1() {
        double def = 1;
        map.forEach((k, v) -> assertEquals(config.getDouble(k.toString() + 1, def), def, 0.0));
    }

    @Test
    public void getFloat() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getFloat : " + k.toString());
                assertEquals(config.getFloat(k.toString()), Float.parseFloat(v.toString()), 0.0F);
            }
        });
    }

    @Test
    public void getFloat1() {
        float def = 1F;
        map.forEach((k, v) -> assertEquals(config.getFloat(k.toString() + 1, def), def, 0.0));
    }

    @Test
    public void getBoolean() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(BOOLEAN_KEY)) {
                System.out.println("getBoolean : " + k.toString());
                assertEquals(config.getBoolean(k.toString()), Boolean.parseBoolean(v.toString()));
            }
        });
    }

    @Test
    public void getBoolean1() {
        boolean def = false;
        map.forEach((k, v) -> assertEquals(config.getBoolean(k.toString() + 1, def), def));
    }

    @Test
    public void getByte() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(NUM_KEY)) {
                System.out.println("getByte : " + k.toString());
                assertEquals(config.getByte(k.toString()), (byte) Integer.parseInt(v.toString()));
            }
        });
    }

    @Test
    public void getByte1() {
        byte def = 1;
        map.forEach((k, v) -> assertEquals(config.getByte(k.toString() + 1, def), def));
    }

    @Test
    public void getObject() {
        map.forEach((k, v) -> assertEquals(config.getObject(k.toString()), v));
        map.forEach((k, v) -> assertNull(config.getObject(k.toString() + 1)));
    }

    @Test
    public void getObject1() {
        String def = "default_value";
        map.forEach((k, v) -> assertEquals(config.getObject(k.toString() + 1, def), def));
    }

    @Test
    public void getEnum() {
        map.forEach((k, v) -> {
            if (k.toString().endsWith(EUMN_KEY)) {
                System.out.println("getEnum : " + k.toString());
                assertEquals(config.getEnum(k.toString(), Type.class), Type.valueOf(v.toString()));
            }
        });
    }

    @Test
    public void getEnum1() {
        Type def = Type.ENUM_OBJECT_1;
        map.forEach((k, v) -> assertEquals(config.getEnum(k.toString() + 1, def), def));
    }

    @Test
    public void entrySet() {
        Collection<Entry<String, Object>> entries = config.entrySet();
        assertEquals(map.size(), entries.size());
        for (Entry<String, Object> entry : entries) {
            Object value = map.get(entry.getKey());
            assertEquals(value, entry.getValue());
        }
    }

    @Test
    public void keySet() {
        Collection<String> keys = config.keySet();
        assertEquals(map.size(), keys.size());
        for (String key : keys) {
            assertTrue(map.containsKey(key));
        }
    }

    @Test
    public void values() {
        Collection<Object> values = config.values();
        assertEquals(map.size(), values.size());
        for (Object value : values) {
            assertTrue(map.containsValue(value));
        }
    }

    @Test
    public void find() {
        Map<String, Object> map = config.find(HEAD_5 + ".*");
        assertEquals(TYPE_SIZE + 1, map.size());
        assertTrue(map.containsKey(FIND_TEST_KEY));
        map = config.find(HEAD_5.replace(".", "\\.") + ".*");
        assertEquals(TYPE_SIZE, map.size());
        assertFalse(map.containsKey(FIND_TEST_KEY));
    }

    @Test
    public void child() {
        Config l4Config = config.child(HEAD_4);
        Collection<Entry<String, Object>> l4l5Entries = l4Config.entrySet();
        assertEquals(TYPE_SIZE * 2, l4l5Entries.size());
        assertEquals(config.getInt(L4_NUM_1), l4Config.getInt(L4_NUM_1));
        assertEquals(config.getString(L4_STR_2), l4Config.getString(L4_STR_2));
        assertEquals(config.getBoolean(L4_BOOLEAN_3), l4Config.getBoolean(L4_BOOLEAN_3));
        assertEquals(config.getEnum(L4_EUMN_4, Type.class), l4Config.getEnum(L4_BOOLEAN_3, Type.class));
        assertEquals(config.getInt("l5." + L4_NUM_1), l4Config.getInt(L5_NUM_1));
        assertEquals(config.getString("l5." + L4_STR_2), l4Config.getString(L5_STR_2));
        assertEquals(config.getBoolean("l5." + L4_BOOLEAN_3), l4Config.getBoolean(L5_BOOLEAN_3));
        assertEquals(config.getEnum("l5." + L4_EUMN_4, Type.class), l4Config.getEnum(L5_BOOLEAN_3, Type.class));

        Config l5Config = config.child(HEAD_5);
        Collection<Entry<String, Object>> l5Entries = l5Config.entrySet();
        assertEquals(TYPE_SIZE * 1, l5Entries.size());
        assertEquals(config.getInt(L4_NUM_1), l5Config.getInt(L5_NUM_1));
        assertEquals(config.getString(L4_STR_2), l5Config.getString(L5_STR_2));
        assertEquals(config.getBoolean(L4_BOOLEAN_3), l5Config.getBoolean(L5_BOOLEAN_3));
        assertEquals(config.getEnum(L4_EUMN_4, Type.class), l5Config.getEnum(L5_BOOLEAN_3, Type.class));
    }

}