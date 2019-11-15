package protoex;

import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.springframework.util.NumberUtils;

import java.lang.reflect.*;
import java.math.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.*;

import static com.tny.game.common.utils.StringAide.*;

public class ContentCodecTest {

    // protected abstract ContentCodec getContentCodec(CodecDefinition protocolDefinition);

    @Test
    public void testComplex() throws Exception {
        // 基于对象的协议测试
        MockComplexObject object = MockComplexObject.valueOf(0, "birdy", "hong", 10, Instant.now(), MockEnumeration.TERRAN);
        testConvert(MockComplexObject.class, object);

        // 基于枚举的协议测试
        testConvert(MockEnumeration.class, MockEnumeration.TERRAN);

        // 基于数组的协议测试
        MockEnumeration[] enumerationArray = MockEnumeration.values();
        testConvert(MockEnumeration[].class, enumerationArray);
        Object wrapArray = new Integer[]{0, 1, 2, 3, 4};
        testConvert(Integer[].class, wrapArray);
        Object primitiveArray = new int[]{0, 1, 2, 3, 4};
        testConvert(int[].class, primitiveArray);
        MockComplexObject[] objectArray = new MockComplexObject[]{object};
        testConvert(MockComplexObject[].class, objectArray);
        wrapArray = new Byte[]{0, 1, 2, 3, 4};
        testConvert(Byte[].class, wrapArray);
        primitiveArray = ContentCodecTest.class.getName().getBytes("UTF-8");
        testConvert(byte[].class, primitiveArray);

        // 基于集合的协议测试
        List<MockEnumeration> enumerationList = new ArrayList<>(enumerationArray.length);
        Collections.addAll(enumerationList, enumerationArray);
        testConvert(TypeUtils.parameterize(ArrayList.class, MockEnumeration.class), enumerationList);
        Set<MockEnumeration> enumerationSet = new HashSet<>(enumerationList);
        testConvert(TypeUtils.parameterize(HashSet.class, MockEnumeration.class), enumerationSet);

        List<Integer> integerList = new ArrayList<>(5);
        Collections.addAll(integerList, new Integer[]{0, 1, 2, 3, 4});
        testConvert(TypeUtils.parameterize(ArrayList.class, Integer.class), integerList);
        Set<Integer> integerSet = new TreeSet<>(integerList);
        testConvert(TypeUtils.parameterize(TreeSet.class, Integer.class), integerSet);

        List<MockComplexObject> objectList = new ArrayList<>(objectArray.length);
        Collections.addAll(objectList, objectArray);
        testConvert(TypeUtils.parameterize(ArrayList.class, MockComplexObject.class), objectList);
        Set<MockComplexObject> objectSet = new HashSet<>(objectList);
        testConvert(TypeUtils.parameterize(HashSet.class, MockComplexObject.class), objectSet);

        // 基于映射的协议测试
        Map<String, MockComplexObject> map = new HashMap<>();
        for (MockComplexObject element : objectList) {
            map.put(element.getFirstName(), element);
        }
        testConvert(TypeUtils.parameterize(HashMap.class, String.class, MockComplexObject.class), map);
    }

    @Test
    public void testBoolean() throws Exception {
        testConvert(AtomicBoolean.class, new AtomicBoolean(true));
        testConvert(AtomicBoolean.class, new AtomicBoolean(false));
        testConvert(Boolean.class, true);
        testConvert(Boolean.class, false);
        testConvert(boolean.class, true);
        testConvert(boolean.class, false);
    }

    @Test
    public void testNumber() throws Exception {
        testConvert(AtomicInteger.class, new AtomicInteger(Short.MIN_VALUE));
        testConvert(AtomicInteger.class, new AtomicInteger(Short.MAX_VALUE));
        testConvert(AtomicInteger.class, new AtomicInteger(0));
        testConvert(AtomicInteger.class, new AtomicInteger(Integer.MIN_VALUE));
        testConvert(AtomicInteger.class, new AtomicInteger(Integer.MAX_VALUE));

        testConvert(AtomicLong.class, new AtomicLong(Integer.MIN_VALUE));
        testConvert(AtomicLong.class, new AtomicLong(Integer.MAX_VALUE));
        testConvert(AtomicLong.class, new AtomicLong(0));
        testConvert(AtomicLong.class, new AtomicLong(Long.MIN_VALUE));
        testConvert(AtomicLong.class, new AtomicLong(Long.MAX_VALUE));

        // 基于数值的协议测试
        testConvert(Byte.class, NumberUtils.convertNumberToTargetClass(0, Byte.class));
        testConvert(Byte.class, Byte.MIN_VALUE);
        testConvert(Byte.class, Byte.MAX_VALUE);
        testConvert(Short.class, NumberUtils.convertNumberToTargetClass(Byte.MIN_VALUE, Short.class));
        testConvert(Short.class, NumberUtils.convertNumberToTargetClass(Byte.MAX_VALUE, Short.class));
        testConvert(Short.class, NumberUtils.convertNumberToTargetClass(0, Short.class));
        testConvert(Short.class, Short.MIN_VALUE);
        testConvert(Short.class, Short.MAX_VALUE);
        testConvert(Integer.class, NumberUtils.convertNumberToTargetClass(Short.MIN_VALUE, Integer.class));
        testConvert(Integer.class, NumberUtils.convertNumberToTargetClass(Short.MAX_VALUE, Integer.class));
        testConvert(Integer.class, NumberUtils.convertNumberToTargetClass(0, Integer.class));
        testConvert(Integer.class, Integer.MIN_VALUE);
        testConvert(Integer.class, Integer.MAX_VALUE);
        testConvert(Long.class, NumberUtils.convertNumberToTargetClass(Integer.MIN_VALUE, Long.class));
        testConvert(Long.class, NumberUtils.convertNumberToTargetClass(Integer.MAX_VALUE, Long.class));
        testConvert(Long.class, NumberUtils.convertNumberToTargetClass(0, Long.class));
        testConvert(Long.class, Long.MIN_VALUE);
        testConvert(Long.class, Long.MAX_VALUE);

        testConvert(Float.class, NumberUtils.convertNumberToTargetClass(Long.MIN_VALUE, Float.class));
        testConvert(Float.class, NumberUtils.convertNumberToTargetClass(Long.MAX_VALUE, Float.class));
        testConvert(Float.class, NumberUtils.convertNumberToTargetClass(0, Float.class));
        testConvert(Float.class, Float.MIN_VALUE);
        testConvert(Float.class, Float.MAX_VALUE);
        testConvert(Double.class, NumberUtils.convertNumberToTargetClass(Float.MIN_VALUE, Double.class));
        testConvert(Double.class, NumberUtils.convertNumberToTargetClass(Float.MAX_VALUE, Double.class));
        testConvert(Double.class, NumberUtils.convertNumberToTargetClass(0, Double.class));
        testConvert(Double.class, Double.MIN_VALUE);
        testConvert(Double.class, Double.MAX_VALUE);
        BigInteger bigInteger = new BigInteger(String.valueOf(Long.MAX_VALUE));
        bigInteger = bigInteger.add(bigInteger);
        testConvert(BigInteger.class, bigInteger);
        BigDecimal bigDecimal = new BigDecimal(bigInteger);
        bigDecimal = bigDecimal.add(bigDecimal);
        testConvert(BigDecimal.class, bigDecimal);
    }

    @Test
    public void testNull() throws Exception {
        // 基于Null的协议测试
        // 基于对象的协议测试
        MockComplexObject object = MockComplexObject.valueOf(0, "birdy", null, 10, Instant.now(), MockEnumeration.TERRAN);
        testConvert(MockComplexObject.class, null);
        testConvert(MockComplexObject.class, object);

        // 基于枚举的协议测试
        testConvert(MockEnumeration.class, null);

        // 基于数组的协议测试
        Integer[] integerArray = new Integer[]{0, null};
        testConvert(Integer[].class, integerArray);
        MockComplexObject[] objectArray = new MockComplexObject[]{object, null};
        testConvert(MockComplexObject[].class, objectArray);

        // 基于集合的协议测试
        List<MockComplexObject> objectList = new ArrayList<>(objectArray.length);
        Collections.addAll(objectList, objectArray);
        testConvert(TypeUtils.parameterize(ArrayList.class, MockComplexObject.class), objectList);
        // testConvert(ArrayList.class, objectList);
        Set<MockComplexObject> objectSet = new HashSet<>(objectList);
        testConvert(TypeUtils.parameterize(HashSet.class, MockComplexObject.class), objectSet);
        // testConvert(HashSet.class, objectSet);

        // 基于映射的协议测试
        Map<String, MockComplexObject> map = new HashMap<>();
        map.put(object.getFirstName(), object);
        map.put("null", null);
        testConvert(TypeUtils.parameterize(HashMap.class, String.class, MockComplexObject.class), map);
        // testConvert(HashMap.class, map);
    }

    @Test
    public void testTime() throws Exception {
        // 基于时间的协议测试
        Date date = new Date(0L);
        testConvert(Date.class, date);
        date = new Date();
        testConvert(Date.class, date);
        Instant instant = Instant.ofEpochMilli(0L);
        testConvert(Instant.class, instant);
        instant = Instant.now();
        testConvert(Instant.class, instant);
    }

    @Test
    public void testType() throws Exception {
        // 基于数组类型测试
        Type type = TypeUtils.genericArrayType(MockComplexObject.class);
        testConvert(GenericArrayType.class, type);
        type = TypeUtils.genericArrayType(type);
        testConvert(GenericArrayType.class, type);
        type = TypeUtils.genericArrayType(byte.class);
        testConvert(GenericArrayType.class, type);
        type = TypeUtils.genericArrayType(Byte.class);
        testConvert(GenericArrayType.class, type);
        testConvert(MockComplexObject[].class.getClass(), MockComplexObject[].class);
        testConvert(byte[].class.getClass(), byte[].class);

        // 基于布尔类型测试
        type = AtomicBoolean.class;
        testConvert(type.getClass(), type);
        type = boolean.class;
        testConvert(type.getClass(), type);
        type = Boolean.class;
        testConvert(type.getClass(), type);

        // 基于集合类型测试
        type = TypeUtils.parameterize(ArrayList.class, MockComplexObject.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtils.parameterize(LinkedList.class, type);
        testConvert(ParameterizedType.class, type);
        type = TypeUtils.parameterize(HashSet.class, byte.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtils.parameterize(TreeSet.class, Byte.class);
        testConvert(ParameterizedType.class, type);

        // 基于枚举类型测试
        type = MockEnumeration.class;
        testConvert(type.getClass(), type);

        // 基于映射类型测试
        type = TypeUtils.parameterize(HashMap.class, String.class, MockComplexObject.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtils.parameterize(HashMap.class, String.class, type);
        testConvert(ParameterizedType.class, type);
        type = TypeUtils.parameterize(HashMap.class, byte.class, byte.class);
        testConvert(ParameterizedType.class, type);
        type = TypeUtils.parameterize(HashMap.class, Byte.class, Byte.class);
        testConvert(ParameterizedType.class, type);

        // 基于数值类型测试
        type = AtomicInteger.class;
        testConvert(type.getClass(), type);
        type = AtomicLong.class;
        testConvert(type.getClass(), type);
        type = byte.class;
        testConvert(type.getClass(), type);
        type = short.class;
        testConvert(type.getClass(), type);
        type = int.class;
        testConvert(type.getClass(), type);
        type = long.class;
        testConvert(type.getClass(), type);
        type = float.class;
        testConvert(type.getClass(), type);
        type = double.class;
        testConvert(type.getClass(), type);
        type = Byte.class;
        testConvert(type.getClass(), type);
        type = Short.class;
        testConvert(type.getClass(), type);
        type = Integer.class;
        testConvert(type.getClass(), type);
        type = Long.class;
        testConvert(type.getClass(), type);
        type = Float.class;
        testConvert(type.getClass(), type);
        type = Double.class;
        testConvert(type.getClass(), type);
        type = BigInteger.class;
        testConvert(type.getClass(), type);
        type = BigDecimal.class;
        testConvert(type.getClass(), type);

        // 基于对象类型测试
        type = MockComplexObject.class;
        testConvert(type.getClass(), type);

        // 基于字符串类型测试
        type = char.class;
        testConvert(type.getClass(), type);
        type = Character.class;
        testConvert(type.getClass(), type);
        type = String.class;
        testConvert(type.getClass(), type);

        // 基于时间类型测试
        type = Date.class;
        testConvert(type.getClass(), type);
        type = Instant.class;
        testConvert(type.getClass(), type);
    }

    private void testPerformance(Type type, Object instance) {
        ProtoExWriter writer = new ProtoExWriter();
        writer.writeMessage(instance, TypeEncode.EXPLICIT);
        byte[] data = writer.toByteArray();
        String message = format("格式化{}大小:{},{}", type.getTypeName(), data.length, Arrays.toString(data));
        System.out.println(message);

        Instant now = null;
        int times = 100000;
        now = Instant.now();
        for (int index = 0; index < times; index++) {
            writer = new ProtoExWriter();
            writer.writeMessage(instance, TypeEncode.EXPLICIT);
        }
        System.out.println(format("编码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));

        now = Instant.now();
        for (int index = 0; index < times; index++) {
            ProtoExReader reader = new ProtoExReader(data);
            reader.readMessage((Class<?>) type);
        }
        System.out.println(format("解码{}次一共消耗{}毫秒.", times, System.currentTimeMillis() - now.toEpochMilli()));
    }

    @Test
    public void testPerformance() {
        Collection<Type> protocolClasses = new LinkedList<>();
        protocolClasses.add(MockComplexObject.class);
        protocolClasses.add(MockEnumeration.class);
        protocolClasses.add(MockSimpleObject.class);

        protocolClasses.add(ArrayList.class);
        protocolClasses.add(HashSet.class);
        protocolClasses.add(TreeSet.class);
        // CodecDefinition definition = CodecDefinition.valueOf(protocolClasses);
        // ContentCodec contentCodec = this.getContentCodec(definition);

        String message = format("[ProtoEx]编解码性能测试");
        System.out.println(message);

        int size = 100;

        Type type = MockComplexObject.class;
        Object instance = MockComplexObject.valueOf(Integer.MAX_VALUE, "birdy", "hong", size, Instant.now(), MockEnumeration.TERRAN);
        testPerformance(type, instance);

        type = MockSimpleObject.class;
        instance = MockSimpleObject.valueOf(0, "birdy");
        testPerformance(type, instance);

        String[] stringArray = new String[size];
        for (int index = 0; index < size; index++) {
            stringArray[index] = "mickey" + index;
        }
        type = String[].class;
        instance = stringArray;
        testPerformance(type, instance);

        ArrayList<String> stringList = new ArrayList<>(size);
        Collections.addAll(stringList, stringArray);
        type = TypeUtils.parameterize(ArrayList.class, String.class);
        instance = stringList;
        testPerformance(type, instance);

        HashSet<String> stringSet = new HashSet<>(size);
        Collections.addAll(stringSet, stringArray);
        type = TypeUtils.parameterize(HashSet.class, String.class);
        instance = stringSet;
        testPerformance(type, instance);
    }

    private void testConvert(Type type, Object value) throws Exception {
        // Collection<Type> protocolClasses = new LinkedList<>();
        // protocolClasses.add(MockComplexObject.class);
        // protocolClasses.add(MockEnumeration.class);
        // protocolClasses.add(MockSimpleObject.class);
        //
        // protocolClasses.add(ArrayList.class);
        // protocolClasses.add(HashSet.class);
        // protocolClasses.add(TreeSet.class);
        // CodecDefinition definition = CodecDefinition.valueOf(protocolClasses);
        // ContentCodec contentCodec = this.getContentCodec(definition);

        ProtoExWriter writer = new ProtoExWriter();

        writer.writeMessage(value, TypeEncode.EXPLICIT);
        byte[] data = writer.toByteArray();
        // byte[] data = contentCodec.encode(type, value);

        ProtoExReader reader = new ProtoExReader(data);
        if (type == AtomicBoolean.class) {
            AtomicBoolean left = (AtomicBoolean) value;
            AtomicBoolean right = reader.readMessage(AtomicBoolean.class);
            Assert.assertTrue(TypeUtils.isInstance(left, type));
            Assert.assertTrue(TypeUtils.isInstance(right, type));
            Assert.assertThat(right.get(), CoreMatchers.equalTo(left.get()));
        } else if (type == AtomicInteger.class || type == AtomicLong.class) {
            Number left = (Number) value;
            Number right = (Number) reader.readMessage((Class<?>) type);
            Assert.assertTrue(TypeUtils.isInstance(left, type));
            Assert.assertTrue(TypeUtils.isInstance(right, type));
            Assert.assertThat(right.longValue(), CoreMatchers.equalTo(left.longValue()));
        } else {
            Object left = value;
            Object right = reader.readMessage((Class<?>) type);
            if (value != null) {
                Assert.assertTrue(TypeUtils.isInstance(left, type));
                Assert.assertTrue(TypeUtils.isInstance(right, type));
            }
            Assert.assertThat(right, CoreMatchers.equalTo(left));
        }
    }

}
