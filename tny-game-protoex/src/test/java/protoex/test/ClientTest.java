/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package protoex.test;

import com.tny.game.common.runtime.*;
import com.tny.game.protoex.*;
import com.tny.game.protoex.annotations.*;
import org.junit.jupiter.api.*;

import java.util.Arrays;

public class ClientTest {

    private CTestObject createObject() {
        CTestObject object = new CTestObject();
        this.initTestObject(object);
        return object;
    }

    private void initTestObject(CTestObject object) {
        //		MapBuilder<TestKey, TestKey> mapBuilder = MapBuilder.newBuilder();
        //		Map<TestKey, TestKey> map = mapBuilder
        //				.put(TestKey.key("key1"), TestKey.key("value1"))
        //				.put(TestKey.key("key2"), TestKey.key("value2"))
        //				.put(TestKey.key("key3"), TestKey.key("value3"))
        //				.put(TestKey.key("key4"), TestKey.key("value4"))
        //				.put(TestKey.key("key5"), TestKey.key("value5"))
        //				.build();
        object.testChar = '日';
        object.testLong = Long.MAX_VALUE;
        object.testByte = Byte.MAX_VALUE;
        object.testInt = Integer.MIN_VALUE;
        object.testShot = Short.MAX_VALUE;
        object.testBoolean = false;
        object.testString = "abcdefghijklmnopqrstuvwxyz";
        object.testBytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
        //		object.testDouble = Double.MAX_VALUE;
        object.testFloat = Float.MIN_VALUE;
        object.testObject = TestKey.key("testObject -- Delay No More!! Why are you so diao!!");
        object.testImpObject = TestKey.key("testImpObject -- 丢雷楼谋!");
        object.testExcessObject = TestKey.key("testExcessObject");
        object.testExcessInt = 10000;
        object.testIntValues = Arrays.asList(1, 2, 3, 4, 5);
        object.testKeyExpValues = Arrays.asList(TestKey.key("A"), TestKey.key("B"), TestKey.key("C"));
        object.testKeyImpValues = Arrays.asList(TestKey.key("D"), TestKey.key("E"), TestKey.key("F"));
        object.testKeyUnpackedExpValues = Arrays.asList(TestKey.key("G"), TestKey.key("H"), TestKey.key("I"));
        object.testKeyUnpackedImpValues = Arrays.asList(TestKey.key("J"), TestKey.key("K"), TestKey.key("L"));
        //		object.testKeyExpMap = map;
        //		object.testKeyImpMap = map;
        //		object.testValueExpMap = map;
        //		object.testValueImpMap = map;
        //		object.testAllExpMap = map;
        //		object.testAllImpMap = map;
    }

    @Test
    public void test() {

        CTestObject object = this.createObject();
        ProtoExWriter writer = new ProtoExWriter();

        writer.writeMessage(object, TypeEncode.EXPLICIT);
        RunChecker.traceWithPrint("writer test");
        writer = new ProtoExWriter(1024, 32);
        for (int i = 0; i < 1000; i++) {
            writer.writeMessage(object, TypeEncode.EXPLICIT);
            writer.clear();
        }
        RunChecker.end("writer test");
        writer.writeMessage(object, TypeEncode.EXPLICIT);
        //		byte[] data = writer.toByteArray();
        //		System.out.println(Arrays.toString(data));
        //		System.out.println(Long.MAX_VALUE);
        //		System.out.println(Integer.MIN_VALUE);
        //		System.out.println(Double.MAX_VALUE);
        //		System.out.println(Float.MIN_VALUE);
        //		System.out.println(data.length + " " + writer.size());
        //		Assert.assertEquals(writer.size(), data.length);
        //
        //		CTestObject result = null;
        //		RunningChecker.startPrint("read test");
        //		for (int i = 0; i < 10000; i++) {
        //			result = new ProtoExReader(data).readMessage(CTestObject.class);
        //		}
        //		RunningChecker.endPrint();
        //
        //		System.out.println(result.testExcessInt);
        //		System.out.println(result.testExcessObject);
        //		System.out.println(result.testKeyUnpackedExpValues);
        //		System.out.println(result.testKeyUnpackedImpValues);
        //		Assert.assertEquals(result, object);
    }

    //	byte[] clientByte = {
    //			-61, 62, 0, -108, 2, 20, 4, -1, -1, -1, -1, -1, -1, -1, -1, 127, 8, 8, -1, -1, 1, 16, 12, -128, -128, -128, -128, 8, 12, 16, 127,
    //			24, 20, 1, 28, 24, 0, 36, 28, 26, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116,
    //			117, 118, 119, 120, 121, 122, 40, 32, 26, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86,
    //			87, 88, 89, 90, 4, 36, -27, -53, 1, 32, 40, 0, 1, 80, 54, 36, 4, 51, 116, 101, 115, 116, 79, 98, 106, 101, 99, 116, 32, 45, 45, 32,
    //			68, 101, 108, 97, 121, 32, 78, 111, 32, 77, 111, 114, 101, 33, 33, 32, 87, 104, 121, 32, 97, 114, 101, 32, 121, 111, 117, 32, 115,
    //			111, 32, 100, 105, 97, 111, 33, 33, 1, 84, 33, 36, 4, 30, 116, 101, 115, 116, 73, 109, 112, 79, 98, 106, 101, 99, 116, 32, 45, 45,
    //			32, -28, -72, -94, -23, -101, -73, -26, -91, -68, -24, -80, -117, 33, 48, 44, 6, 17, 1, 2, 3, 4, 5, 48, 48, 16, 3, 4, 36, 4, 1, 65,
    //			4, 36, 4, 1, 66, 4, 36, 4, 1, 67, 48, 52, 16, 3, 4, 36, 4, 1, 68, 4, 36, 4, 1, 69, 4, 36, 4, 1, 70, 48, 96, 16, 3, 4, 36, 4, 1, 71,
    //			4, 36, 4, 1, 72, 4, 36, 4, 1, 73, 48, 100, 16, 3, 4, 36, 4, 1, 74, 4, 36, 4, 1, 75, 4, 36, 4, 1, 76
    //	};
    //
    //	@Test
    //	public void readTest() {
    //		ProtoExReader reader = new ProtoExReader(this.clientByte);
    //		CTestObject result = reader.readMessage(CTestObject.class);
    //		System.out.println(result);
    //		System.out.println(result.testExcessObject);
    //		System.out.println(result.testKeyUnpackedExpValues);
    //		System.out.println(result.testKeyUnpackedImpValues);
    //	}
    //
    //	byte[] serverByte = { -61, 62, 0, -59, 2, 22, 4, -1, -1, -1, -1, -1, -1, -1, -1, 127, 10, 8, -1, -1, 1, 18, 12, -128, -128, -128, -128, -8,
    //	-1, -1, -1, -1, 1, 14, 16, 127, 26, 20, 1, 38, 28, 26, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113,
    //	114, 115, 116, 117, 118, 119, 120, 121, 122, 42, 32, 26, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85,
    //	86, 87, 88, 89, 90, 6, 36, -27, -53, 1, 34, 40, 0, 50, 44, 6, 17, 1, 2, 3, 4, 5, 50, 48, 17, -53, 31, 4, 38, 4, 1, 65, 4, 38, 4, 1, 66, 4,
    //	38, 4, 1, 67, 50, 52, 17, -53, 31, 4, 38, 4, 1, 68, 4, 38, 4, 1, 69, 4, 38, 4, 1, 70, -53, 31, 80, 54, 38, 4, 51, 116, 101, 115, 116, 79,
    //	98, 106, 101, 99, 116, 32, 45, 45, 32, 68, 101, 108, 97, 121, 32, 78, 111, 32, 77, 111, 114, 101, 33, 33, 32, 87, 104, 121, 32, 97, 114,
    //	101, 32, 121, 111, 117, 32, 115, 111, 32, 100, 105, 97, 111, 33, 33, 1, 84, 33, 38, 4, 30, 116, 101, 115, 116, 73, 109, 112, 79, 98, 106,
    //	101, 99, 116, 32, 45, 45, 32, -28, -72, -94, -23, -101, -73, -26, -91, -68, -24, -80, -117, 33, -53, 31, -56, 1, 19, 38, 4, 16, 116, 101,
    //	115, 116, 69, 120, 99, 101, 115, 115, 79, 98, 106, 101, 99, 116, 18, -52, 1, -112, 78, 50, 96, 25, 2, -53, 31, 0, 4, 38, 4, 1, 71, -53, 31,
    //	0, 4, 38, 4, 1, 72, -53, 31, 0, 4, 38, 4, 1, 73, 50, 100, 22, 2, 1, 0, 4, 38, 4, 1, 74, 1, 0, 4, 38, 4, 1, 75, 1, 0, 4, 38, 4, 1, 76 };
    //
    //	@Test
    //	public void readExcessTest() {
    //		ProtoExReader reader = new ProtoExReader(this.serverByte);
    //		CTestObject result = reader.readMessage(CTestObject.class);
    //		System.out.println(result);
    //	}

}
