package protoex.test;

import com.tny.game.common.utils.Logs;
import com.tny.game.common.collection.MapBuilder;
import com.tny.game.protoex.ProtoExReader;
import com.tny.game.protoex.ProtoExWriter;
import com.tny.game.protoex.annotations.TypeEncode;
import com.tny.game.protoex.field.FieldFormat;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProtoExTest {

    char[] charValue = {0, (char) 1, (char) -1, Character.MAX_VALUE, Character.MIN_VALUE};
    byte[] byteValue = {0, 1, -1, Byte.MAX_VALUE, Byte.MIN_VALUE};
    short[] shortValue = {0, 1, -1, Short.MAX_VALUE, Short.MIN_VALUE};
    int[] intValue = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE};
    long[] longValue = {0, 1, -1, Long.MAX_VALUE, Long.MIN_VALUE};
    float[] floatValue = {0, 1.5f, -1.5f, Float.MAX_VALUE, Float.MIN_VALUE};
    double[] doubleValue = {0, 1.5, -1.5, Double.MAX_VALUE, Double.MIN_VALUE};
    boolean[] booleanValue = {true, false};
    String[] stringValue = {"abcdefghijklmnopqlstuvwxyz", "ABCDEFGHIJKLMNOPQLSTUVWXYZ"};
    byte[][] bytesValue = {"abcdefghijklmnopqlstuvwxyz".getBytes(), "ABCDEFGHIJKLMNOPQLSTUVWXYZ".getBytes()};
    Object[] objectValue = {this.createTestObject(), this.createTestObject()};
    Collection<?>[] repeatMixValues = new Collection[]{
            Arrays.asList(this.createTestObject(), this.createTestSub(), 1000, 2000L, false, "abc", 100.1f, 200.123),
            Arrays.asList(this.createTestSub(), this.createTestSub(), 1000, 2000L, false, "abc", 100.1f, 200.123),
            Arrays.asList(this.createTestObject(), this.createTestObject(), 1000, 2000L, false, "abc", 100.1f, 200.123)};
    Collection<?>[] repeatValues = new Collection[]{
            Arrays.asList(this.createTestObject(), this.createTestSub()),
            Arrays.asList(this.createTestSub(), this.createTestSub()),
            Arrays.asList(this.createTestObject(), this.createTestObject())};
    Collection<?>[] repeatResult = new Collection[]{
            Arrays.asList(this.createTestObject(), this.createTestObject()),
            Arrays.asList(this.createTestObject(), this.createTestObject()),
            Arrays.asList(this.createTestObject(), this.createTestObject())};
    Map<?, ?>[] mapMixValues = new Map[]{
            MapBuilder.newBuilder()
                    .put(TestKey.key("testObject1"), this.createTestObject())
                    .put(TestKey.key("testObject2"), this.createTestObject())
                    .put("testObject", this.createTestObject())
                    .put("subObject", this.createTestSub())
                    .put(1000, this.createTestSub())
                    .put(1001, this.createTestSub())
                    .put(true, 100.1f)
                    .put(false, 100.2f)
                    .put(100.123, 10000L)
                    .put(100.321, 20000L)
                    .build(),
            MapBuilder.newBuilder()
                    .put(TestKey.key("testObject1"), this.createTestObject())
                    .put(TestKey.key("testObject2"), this.createTestObject())
                    .put("testObject", this.createTestObject())
                    .put("subObject", this.createTestSub())
                    .put(1000, this.createTestSub())
                    .put(1001, this.createTestSub())
                    .put(true, 100.1f)
                    .put(false, 100.2f)
                    .put(100.123, 10000L)
                    .put(100.321, 20000L)
                    .build(),
    };
    Map<?, ?>[] map_KeyExp_ValueImp_Values = new Map[]{
            MapBuilder.newBuilder()
                    .put(this.createTestObject("key1"), this.createTestObject("value1"))
                    .put(this.createTestSub("key2"), this.createTestObject("value2"))
                    .put(this.createTestObject("key3"), this.createTestSub("value3"))
                    .put(this.createTestSub("key4"), this.createTestSub("value4"))
                    .build(),
    };
    Map<?, ?>[] map_KeyExp_ValueImp_Result = new Map[]{
            MapBuilder.newBuilder()
                    .put(this.createTestObject("key1"), this.createTestObject("value1"))
                    .put(this.createTestSub("key2"), this.createTestObject("value2"))
                    .put(this.createTestObject("key3"), this.createTestObject("value3"))
                    .put(this.createTestSub("key4"), this.createTestObject("value4"))
                    .build(),
    };
    Map<?, ?>[] map_KeyImp_ValueExp_Values = new Map[]{
            MapBuilder.newBuilder()
                    .put(this.createTestObject("key1"), this.createTestObject("value1"))
                    .put(this.createTestSub("key2"), this.createTestObject("value2"))
                    .put(this.createTestObject("key3"), this.createTestSub("value3"))
                    .put(this.createTestSub("key4"), this.createTestSub("value4"))
                    .build(),
    };
    Map<?, ?>[] map_KeyImp_ValueExp_Result = new Map[]{
            MapBuilder.newBuilder()
                    .put(this.createTestObject("key1"), this.createTestObject("value1"))
                    .put(this.createTestObject("key2"), this.createTestObject("value2"))
                    .put(this.createTestObject("key3"), this.createTestSub("value3"))
                    .put(this.createTestObject("key4"), this.createTestSub("value4"))
                    .build(),
    };
    Map<?, ?>[] map_KeyImp_ValueImp_Values = new Map[]{
            MapBuilder.newBuilder()
                    .put(this.createTestObject("key1"), this.createTestObject("value1"))
                    .put(this.createTestSub("key2"), this.createTestObject("value2"))
                    .put(this.createTestObject("key3"), this.createTestSub("value3"))
                    .put(this.createTestSub("key4"), this.createTestSub("value4"))
                    .build(),
    };
    Map<?, ?>[] map_KeyImp_ValueImp_Result = new Map[]{
            MapBuilder.newBuilder()
                    .put(this.createTestObject("key1"), this.createTestObject("value1"))
                    .put(this.createTestObject("key2"), this.createTestObject("value2"))
                    .put(this.createTestObject("key3"), this.createTestObject("value3"))
                    .put(this.createTestObject("key4"), this.createTestObject("value4"))
                    .build(),
    };

    private ProtoExWriter createWrite() {
        return new ProtoExWriter();
    }

    private ProtoExReader createReader(byte[] data) {
        return new ProtoExReader(data);
    }

    private String msg(Object value, Object format, int time) {
        return Logs.format("read {} by {} at {} ", value.getClass(), format, time);
    }

    @Test
    public void testAll() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.charValue);
        this.write(writer, this.byteValue);
        this.write(writer, this.shortValue, FieldFormat.DEFAULT);
        this.write(writer, this.shortValue, FieldFormat.ZigZag);
        this.write(writer, this.shortValue, FieldFormat.Fixed);
        this.write(writer, this.intValue, FieldFormat.DEFAULT);
        this.write(writer, this.intValue, FieldFormat.ZigZag);
        this.write(writer, this.intValue, FieldFormat.Fixed);
        this.write(writer, this.longValue, FieldFormat.DEFAULT);
        this.write(writer, this.longValue, FieldFormat.ZigZag);
        this.write(writer, this.longValue, FieldFormat.Fixed);
        this.write(writer, this.floatValue);
        this.write(writer, this.doubleValue);
        this.write(writer, this.booleanValue);
        this.write(writer, this.stringValue);
        this.write(writer, this.bytesValue);
        this.write(writer, this.objectValue, TypeEncode.EXPLICIT);
        this.write(writer, this.objectValue, TypeEncode.IMPLICIT);
        this.write(writer, this.repeatMixValues, TestObject.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.repeatValues, TestObject.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.repeatValues, TestObject.class, false, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.repeatValues, TestObject.class, true, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.mapMixValues, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.map_KeyExp_ValueImp_Values, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.map_KeyImp_ValueExp_Values, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, this.map_KeyImp_ValueImp_Values, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);
        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.charValue);
        this.read(reader, this.byteValue);
        this.read(reader, this.shortValue, FieldFormat.DEFAULT);
        this.read(reader, this.shortValue, FieldFormat.ZigZag);
        this.read(reader, this.shortValue, FieldFormat.Fixed);
        this.read(reader, this.intValue, FieldFormat.DEFAULT);
        this.read(reader, this.intValue, FieldFormat.ZigZag);
        this.read(reader, this.intValue, FieldFormat.Fixed);
        this.read(reader, this.longValue, FieldFormat.DEFAULT);
        this.read(reader, this.longValue, FieldFormat.ZigZag);
        this.read(reader, this.longValue, FieldFormat.Fixed);
        this.read(reader, this.floatValue);
        this.read(reader, this.doubleValue);
        this.read(reader, this.booleanValue);
        this.read(reader, this.stringValue);
        this.read(reader, this.bytesValue);
        this.read(reader, this.objectValue, TypeEncode.EXPLICIT);
        this.read(reader, this.objectValue, TypeEncode.IMPLICIT);
        this.read(reader, this.repeatMixValues, TestObject.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.repeatResult, TestObject.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.repeatResult, TestObject.class, false, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.repeatResult, TestObject.class, true, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.mapMixValues, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.map_KeyExp_ValueImp_Result, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.map_KeyImp_ValueExp_Result, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, this.map_KeyImp_ValueImp_Result, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testChar() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.charValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.charValue);
    }

    public void write(ProtoExWriter writer, char[] values) {
        for (char value : values) {
            writer.writeChar(value);
        }
    }

    public void read(ProtoExReader reader, char[] values) {
        int time = 1;
        for (char value : values) {
            char readValue = reader.readChar();
            Assert.assertEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue);
        }
    }

    @Test
    public void testByte() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.bytesValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.bytesValue);
    }

    public void write(ProtoExWriter writer, byte[] values) {
        for (byte value : values) {
            writer.writeByte(value);
        }
    }

    public void read(ProtoExReader reader, byte[] values) {
        int time = 1;
        for (byte value : values) {
            byte readValue = reader.readByte();
            Assert.assertEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue);
        }
    }

    @Test
    public void testShort() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.shortValue, FieldFormat.DEFAULT);
        this.write(writer, this.shortValue, FieldFormat.ZigZag);
        this.write(writer, this.shortValue, FieldFormat.Fixed);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.shortValue, FieldFormat.DEFAULT);
        this.read(reader, this.shortValue, FieldFormat.ZigZag);
        this.read(reader, this.shortValue, FieldFormat.Fixed);
    }

    public void write(ProtoExWriter writer, short[] values, FieldFormat format) {
        for (short value : values) {
            writer.writeShort(value, format);
        }
    }

    public void read(ProtoExReader reader, short[] values, FieldFormat format) {
        int time = 1;
        for (short value : values) {
            short readValue = reader.readShort();
            Assert.assertEquals(this.msg(value, format, time++), value, readValue);
        }
    }

    @Test
    public void testInt() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.intValue, FieldFormat.DEFAULT);
        this.write(writer, this.intValue, FieldFormat.ZigZag);
        this.write(writer, this.intValue, FieldFormat.Fixed);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.intValue, FieldFormat.DEFAULT);
        this.read(reader, this.intValue, FieldFormat.ZigZag);
        this.read(reader, this.intValue, FieldFormat.Fixed);
    }

    public void write(ProtoExWriter writer, int[] values, FieldFormat format) {
        for (int value : values) {
            writer.writeInt(value, format);
        }
    }

    public void read(ProtoExReader reader, int[] values, FieldFormat format) {
        int time = 1;
        for (int value : values) {
            int readValue = reader.readInt();
            Assert.assertEquals(this.msg(value, format, time++), value, readValue);
        }
    }

    @Test
    public void testLong() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.longValue, FieldFormat.DEFAULT);
        this.write(writer, this.longValue, FieldFormat.ZigZag);
        this.write(writer, this.longValue, FieldFormat.Fixed);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.longValue, FieldFormat.DEFAULT);
        this.read(reader, this.longValue, FieldFormat.ZigZag);
        this.read(reader, this.longValue, FieldFormat.Fixed);
    }

    public void write(ProtoExWriter writer, long[] values, FieldFormat format) {
        for (long value : values) {
            writer.writeLong(value, format);
        }
    }

    public void read(ProtoExReader reader, long[] values, FieldFormat format) {
        int time = 1;
        for (long value : values) {
            long readValue = reader.readLong();
            Assert.assertEquals(this.msg(value, format, time++), value, readValue);
        }
    }

    @Test
    public void testFloat() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.floatValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.floatValue);
    }

    public void write(ProtoExWriter writer, float[] values) {
        for (float value : values) {
            writer.writeFloat(value);
        }
    }

    public void read(ProtoExReader reader, float[] values) {
        int time = 1;
        for (float value : values) {
            float readValue = reader.readFloat();
            Assert.assertEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue, 0.f);
        }
    }

    @Test
    public void testDouble() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.doubleValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.doubleValue);
    }

    public void write(ProtoExWriter writer, double[] values) {
        for (double value : values) {
            writer.writeDouble(value);
        }
    }

    public void read(ProtoExReader reader, double[] values) {
        int time = 1;
        for (double value : values) {
            double readValue = reader.readDouble();
            Assert.assertEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue, 0.0);
        }
    }

    @Test
    public void testBoolean() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.booleanValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.booleanValue);
    }

    public void write(ProtoExWriter writer, boolean[] values) {
        for (boolean value : values) {
            writer.writeBoolean(value);
        }
    }

    public void read(ProtoExReader reader, boolean[] values) {
        int time = 1;
        for (boolean value : values) {
            boolean readValue = reader.readBoolean();
            Assert.assertEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue);
        }
    }

    @Test
    public void testString() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.stringValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.stringValue);
    }

    public void write(ProtoExWriter writer, String[] values) {
        for (String value : values) {
            writer.writeString(value);
        }
    }

    public void read(ProtoExReader reader, String[] values) {
        int time = 1;
        for (String value : values) {
            String readValue = reader.readString();
            Assert.assertEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue);
        }
    }

    @Test
    public void testBytes() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.bytesValue);

        byte[] data = writer.toByteArray();
        System.out.println(data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.bytesValue);
    }

    public void write(ProtoExWriter writer, byte[][] values) {
        for (byte[] value : values) {
            writer.writeBytes(value);
        }
    }

    public void read(ProtoExReader reader, byte[][] values) {
        int time = 1;
        for (byte[] value : values) {
            byte[] readValue = reader.readBytes();
            Assert.assertArrayEquals(this.msg(value, FieldFormat.DEFAULT, time++), value, readValue);
        }
    }

    private TestObject createTestObject() {
        TestObject object = new TestObject();
        this.initTestObject(object);
        return object;
    }

    private TestSubObject createTestSub() {
        TestSubObject object = new TestSubObject();
        this.initTestObject(object);
        object.subField = "abcdefghijklMNOPQRSTUVWXYZ";
        return object;
    }

    private TestObject createTestObject(String name) {
        TestObject object = new TestObject();
        this.initTestObject(object);
        object.testString = name;
        return object;
    }

    private TestSubObject createTestSub(String name) {
        TestSubObject object = new TestSubObject();
        this.initTestObject(object);
        object.testString = name;
        object.subField = "abcdefghijklMNOPQRSTUVWXYZ";
        return object;
    }

    private void initTestObject(TestObject object) {
        MapBuilder<TestKey, TestKey> mapBuilder = MapBuilder.newBuilder();
        Map<TestKey, TestKey> map = mapBuilder
                .put(TestKey.key("key1"), TestKey.key("value1"))
                .put(TestKey.key("key2"), TestKey.key("value2"))
                .put(TestKey.key("key3"), TestKey.key("value3"))
                .put(TestKey.key("key4"), TestKey.key("value4"))
                .put(TestKey.key("key5"), TestKey.key("value5"))
                .build();
        object.testBoolean = false;
        object.testByte = 1;
        object.testString = "abcdefghijklmnopqrstuvwxyz";
        object.testBytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
        object.testChar = '测';
        object.testDouble = 10.9999;
        object.testFloat = 10.3f;
        object.testInt = 3000;
        object.testLong = 1000L;
        object.testShot = 10;
        object.testIntValues = Arrays.asList(1, 2, 3, 4, 5);
        object.testKeyExpValues = Arrays.asList(TestKey.key("A"), TestKey.key("B"), TestKey.key("C"));
        object.testKeyImpValues = new LinkedList<>(Arrays.asList(TestKey.key("D"), TestKey.key("E"), TestKey.key("F")));
        object.testKeyExpMap = map;
        object.testKeyImpMap = map;
        object.testValueExpMap = map;
        object.testValueImpMap = map;
        object.testAllExpMap = map;
        object.testAllImpMap = map;
        object.testAtomicBoolean = new AtomicBoolean(true);
        object.testAtomicInteger = new AtomicInteger(200);
        object.testAtomicLong = new AtomicLong(20001L);
        object.integers = new Integer[]{1, 2, 3};
        object.ints = new int[]{1, 2, 3};
        object.keys = new TestKey[]{TestKey.key("keys1"), TestKey.key("keys2"), TestKey.key("keys3")};
    }

    @Test
    public void testObject() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.objectValue, TypeEncode.EXPLICIT);
        this.write(writer, this.objectValue, TypeEncode.IMPLICIT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.objectValue, TypeEncode.EXPLICIT);
        this.read(reader, this.objectValue, TypeEncode.IMPLICIT);
    }

    public void write(ProtoExWriter writer, Object[] values, TypeEncode typeEncode) {
        for (Object value : values) {
            writer.writeMessage(value, typeEncode);
            System.out.println("writer.size() = " + writer.size());
        }
    }

    public void read(ProtoExReader reader, Object[] values, TypeEncode typeEncode) {
        int time = 1;
        for (Object value : values) {
            System.out.println(typeEncode + " " + time);
            Object readValue = reader.readMessage(TestObject.class);
            Assert.assertEquals(this.msg(value, typeEncode, time++), value, readValue);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIntCollection() {
        Collection<Integer>[] values = new Collection[]{Arrays.asList(1, 2, 3, -1 - 2 - 3), Arrays.asList(-1000, -2000, 2000, -1000)};

        ProtoExWriter writer = this.createWrite();

        this.write(writer, values, Integer.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, Integer.class, true, TypeEncode.EXPLICIT, FieldFormat.ZigZag);
        this.write(writer, values, Integer.class, true, TypeEncode.EXPLICIT, FieldFormat.Fixed);
        this.write(writer, values, Integer.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, Integer.class, false, TypeEncode.EXPLICIT, FieldFormat.ZigZag);
        this.write(writer, values, Integer.class, false, TypeEncode.EXPLICIT, FieldFormat.Fixed);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, values, Integer.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, Integer.class, true, TypeEncode.EXPLICIT, FieldFormat.ZigZag);
        this.read(reader, values, Integer.class, true, TypeEncode.EXPLICIT, FieldFormat.Fixed);
        this.read(reader, values, Integer.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, Integer.class, false, TypeEncode.EXPLICIT, FieldFormat.ZigZag);
        this.read(reader, values, Integer.class, false, TypeEncode.EXPLICIT, FieldFormat.Fixed);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStringCollection() {
        Collection<String>[] values = new Collection[]{
                Arrays.asList("abcdefghijklmnopqlstuvwxyz", "ABCDEFGHIJKLMNOPQLSTUVWXYZ"),
                Arrays.asList("ABCDEFGHIJKLMNOPQLSTUVWXYZ", "abcdefghijklmnopqlstuvwxyz")};

        ProtoExWriter writer = this.createWrite();

        this.write(writer, values, String.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, String.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, values, String.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, String.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testObjectCollection() {
        Collection<Object>[] values = new Collection[]{
                Arrays.asList(this.createTestObject(), this.createTestObject()),
                Arrays.asList(this.createTestObject(), this.createTestObject())};

        ProtoExWriter writer = this.createWrite();

        this.write(writer, values, TestObject.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, TestObject.class, true, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, TestObject.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, TestObject.class, false, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, values, TestObject.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, TestObject.class, true, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, TestObject.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, TestObject.class, false, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testMixUnpackedExplicitCollection() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.repeatMixValues, TestObject.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.repeatMixValues, TestObject.class, false, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testMixUnpackedImplicitCollection() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.repeatValues, TestObject.class, false, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.repeatResult, TestObject.class, false, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testMixPackedExplicitCollection() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.repeatValues, TestObject.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.repeatResult, TestObject.class, true, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testMixPackedImplicitCollection() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.repeatValues, TestObject.class, true, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.repeatResult, TestObject.class, true, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    public <T> void write(ProtoExWriter writer, Collection<?>[] values, Class<T> elementType, boolean packed, TypeEncode elTypeEncode, FieldFormat elFormat) {
        int time = 1;
        for (Collection<?> value : values) {
            String method = Logs.format("el({})-packed({}-elType({})-format({}))", elementType.getName(), packed, elementType, elFormat);
            System.out.println(method + " time : " + time++);
            writer.writeCollection(value, elementType, packed, elTypeEncode, elFormat);
        }
    }

    public <T> void read(ProtoExReader reader, Collection<?>[] values, Class<T> elementType, boolean packed, TypeEncode elTypeEncode, FieldFormat elFormat) {
        int time = 1;
        for (Collection<?> value : values) {
            Collection<?> readValue = reader.readCollection(elementType);
            String method = Logs.format("el({})-packed({}-elType({})-format({}))", elementType.getName(), packed, elementType, elFormat);
            Assert.assertEquals(this.msg(value, method, time++), value, readValue);
        }
    }

    @Test
    public void testString_IntMap() {
        Map<?, ?>[] values = new Map[]{
                MapBuilder.newBuilder()
                        .put("testObject", 1000)
                        .put("subObject", 1001)
                        .build(),
                MapBuilder.newBuilder()
                        .put("testObject", 2000)
                        .put("subObject", 2001)
                        .build()
        };

        ProtoExWriter writer = this.createWrite();

        this.write(writer, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.ZigZag);
        this.write(writer, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.Fixed);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.ZigZag);
        this.read(reader, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.Fixed);

    }

    @Test
    public void testInt_StringMap() {
        Map<?, ?>[] values = new Map[]{
                MapBuilder.newBuilder()
                        .put(1000, "testObject")
                        .put(1001, "subObject")
                        .build(),
                MapBuilder.newBuilder()
                        .put(2000, "testObject")
                        .put(2001, "subObject")
                        .build()
        };

        ProtoExWriter writer = this.createWrite();

        this.write(writer, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, String.class, TypeEncode.EXPLICIT, FieldFormat.ZigZag, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, String.class, TypeEncode.EXPLICIT, FieldFormat.Fixed, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, values, String.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, String.class, TypeEncode.EXPLICIT, FieldFormat.ZigZag, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, String.class, TypeEncode.EXPLICIT, FieldFormat.Fixed, Integer.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testObject_ObjectMap() {
        Map<?, ?>[] values = new Map[]{
                MapBuilder.newBuilder()
                        .put(TestKey.key("testObject1"), this.createTestObject())
                        .put(TestKey.key("testObject2"), this.createTestObject())
                        .build(),
                MapBuilder.newBuilder()
                        .put(TestKey.key("testObject1"), this.createTestObject())
                        .put(TestKey.key("testObject2"), this.createTestObject())
                        .build(),
        };

        ProtoExWriter writer = this.createWrite();

        this.write(writer, values, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, TestKey.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.write(writer, values, TestKey.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, values, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, TestKey.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);
        this.read(reader, values, TestKey.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void testMixExplicitMap() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.mapMixValues, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.mapMixValues, TestKey.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void test_KeyExp_ValueImp_Map() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.map_KeyExp_ValueImp_Values, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.map_KeyExp_ValueImp_Result, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void test_KeyImp_ValueExp_Map() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.map_KeyImp_ValueExp_Values, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.map_KeyImp_ValueExp_Result, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.EXPLICIT, FieldFormat.DEFAULT);

    }

    @Test
    public void test_KeyImp_ValueImp_Map() {

        ProtoExWriter writer = this.createWrite();

        this.write(writer, this.map_KeyImp_ValueImp_Values, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

        byte[] data = writer.toByteArray();
        System.out.println(data.length + " " + writer.size());
        Assert.assertEquals(writer.size(), data.length);

        ProtoExReader reader = this.createReader(data);

        this.read(reader, this.map_KeyImp_ValueImp_Result, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT, TestObject.class, TypeEncode.IMPLICIT, FieldFormat.DEFAULT);

    }

    public <T> void write(ProtoExWriter writer, Map<?, ?>[] values,
                          Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
                          Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        int time = 1;
        for (Map<?, ?> value : values) {
            String method = Logs.format("key({})-keyType({})-keyFormat({})-value({})-valueType({})-valueFormat({}))",
                    keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);
            System.out.println(method + " time : " + time++);
            writer.writeMap(value, keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);
        }
    }

    public <T> void read(ProtoExReader reader, Map<?, ?>[] values,
                         Class<?> keyType, TypeEncode keyTypeEncode, FieldFormat keyFormat,
                         Class<?> valueType, TypeEncode valueTypeEncode, FieldFormat valueFormat) {
        int time = 1;
        for (Map<?, ?> value : values) {
            Map<?, ?> readValue = reader.readMap(keyType, valueType);
            String method = Logs.format("key({})-keyType({})-keyFormat({})-value({})-valueType({})-valueFormat({}))",
                    keyType, keyTypeEncode, keyFormat, valueType, valueTypeEncode, valueFormat);

            Assert.assertEquals(this.msg(value, method, time++), value, readValue);
        }
    }
}
