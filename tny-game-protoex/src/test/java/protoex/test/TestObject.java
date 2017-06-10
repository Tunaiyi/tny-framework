package protoex.test;

import com.google.common.base.MoreObjects;
import com.tny.game.protoex.annotations.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@ProtoEx(1000)
public class TestObject {

    @ProtoExField(1)
    public long testLong;

    @ProtoExField(2)
    public short testShot;

    @ProtoExField(3)
    public int testInt;

    @ProtoExField(4)
    public byte testByte;

    @ProtoExField(5)
    public float testFloat;

    @ProtoExField(6)
    public double testDouble;

    @ProtoExField(7)
    public String testString;

    @ProtoExField(8)
    public byte[] testBytes;

    @ProtoExField(9)
    public char testChar;

    @ProtoExField(10)
    public boolean testBoolean;

    @ProtoExField(11)
    @ProtoExElement
    public Collection<Integer> testIntValues;

    @ProtoExField(12)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public Collection<TestKey> testKeyExpValues;

    @ProtoExField(13)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public LinkedList<TestKey> testKeyImpValues;

    @ProtoExField(14)
    @ProtoExEntry(value = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public Map<TestKey, TestKey> testValueExpMap;

    @ProtoExField(15)
    @ProtoExEntry(value = @ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public Map<TestKey, TestKey> testValueImpMap;

    @ProtoExField(16)
    @ProtoExEntry(key = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public Map<TestKey, TestKey> testKeyExpMap;

    @ProtoExField(17)
    @ProtoExEntry(key = @ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public Map<TestKey, TestKey> testKeyImpMap;

    @ProtoExField(18)
    @ProtoExEntry(
            key = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT),
            value = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public Map<TestKey, TestKey> testAllExpMap;

    @ProtoExField(19)
    @ProtoExEntry(
            key = @ProtoExConf(typeEncode = TypeEncode.IMPLICIT),
            value = @ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public Map<TestKey, TestKey> testAllImpMap;

    @ProtoExField(20)
    public AtomicInteger testAtomicInteger;

    @ProtoExField(21)
    public AtomicLong testAtomicLong;

    @ProtoExField(22)
    public AtomicBoolean testAtomicBoolean;

    @ProtoExField(23)
    public int [] ints;

    @ProtoExField(24)
    public Integer [] integers;

    @ProtoExField(25)
    public TestKey [] keys;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((testAllExpMap == null) ? 0 : testAllExpMap.hashCode());
        result = prime * result + ((testAllImpMap == null) ? 0 : testAllImpMap.hashCode());
        result = prime * result + (testBoolean ? 1231 : 1237);
        result = prime * result + testByte;
        result = prime * result + Arrays.hashCode(testBytes);
        result = prime * result + testChar;
        long temp;
        temp = Double.doubleToLongBits(testDouble);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + Float.floatToIntBits(testFloat);
        result = prime * result + testInt;
        result = prime * result + ((testIntValues == null) ? 0 : testIntValues.hashCode());
        result = prime * result + ((testKeyExpMap == null) ? 0 : testKeyExpMap.hashCode());
        result = prime * result + ((testKeyExpValues == null) ? 0 : testKeyExpValues.hashCode());
        result = prime * result + ((testKeyImpMap == null) ? 0 : testKeyImpMap.hashCode());
        result = prime * result + ((testKeyImpValues == null) ? 0 : testKeyImpValues.hashCode());
        result = prime * result + (int) (testLong ^ (testLong >>> 32));
        result = prime * result + testShot;
        result = prime * result + ((testString == null) ? 0 : testString.hashCode());
        result = prime * result + ((testValueExpMap == null) ? 0 : testValueExpMap.hashCode());
        result = prime * result + ((testValueImpMap == null) ? 0 : testValueImpMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestObject other = (TestObject) obj;
        if (testAllExpMap == null) {
            if (other.testAllExpMap != null)
                return false;
        } else if (!testAllExpMap.equals(other.testAllExpMap))
            return false;
        if (testAllImpMap == null) {
            if (other.testAllImpMap != null)
                return false;
        } else if (!testAllImpMap.equals(other.testAllImpMap))
            return false;
        if (testBoolean != other.testBoolean)
            return false;
        if (testByte != other.testByte)
            return false;
        if (!Arrays.equals(testBytes, other.testBytes))
            return false;
        if (testChar != other.testChar)
            return false;
        if (Double.doubleToLongBits(testDouble) != Double.doubleToLongBits(other.testDouble))
            return false;
        if (Float.floatToIntBits(testFloat) != Float.floatToIntBits(other.testFloat))
            return false;
        if (testInt != other.testInt)
            return false;
        if (testIntValues == null) {
            if (other.testIntValues != null)
                return false;
        } else if (!testIntValues.equals(other.testIntValues))
            return false;
        if (testKeyExpMap == null) {
            if (other.testKeyExpMap != null)
                return false;
        } else if (!testKeyExpMap.equals(other.testKeyExpMap))
            return false;
        if (testKeyExpValues == null) {
            if (other.testKeyExpValues != null)
                return false;
        } else if (!testKeyExpValues.equals(other.testKeyExpValues))
            return false;
        if (testKeyImpMap == null) {
            if (other.testKeyImpMap != null)
                return false;
        } else if (!testKeyImpMap.equals(other.testKeyImpMap))
            return false;
        if (testKeyImpValues == null) {
            if (other.testKeyImpValues != null)
                return false;
        } else if (!testKeyImpValues.equals(other.testKeyImpValues))
            return false;
        if (testLong != other.testLong)
            return false;
        if (testShot != other.testShot)
            return false;
        if (testString == null) {
            if (other.testString != null)
                return false;
        } else if (!testString.equals(other.testString))
            return false;
        if (testValueExpMap == null) {
            if (other.testValueExpMap != null)
                return false;
        } else if (!testValueExpMap.equals(other.testValueExpMap))
            return false;
        if (testValueImpMap == null) {
            if (other.testValueImpMap != null)
                return false;
        } else if (!testValueImpMap.equals(other.testValueImpMap))
            return false;

        if(this.testAtomicInteger.get() != other.testAtomicInteger.get())
            return false;

        if(this.testAtomicLong.get() != other.testAtomicLong.get())
            return false;

        if(this.testAtomicBoolean.get() != other.testAtomicBoolean.get())
            return false;
        if (!Arrays.equals(this.ints, other.ints))
            return false;
        if (!Arrays.equals(this.integers, other.integers))
            return false;
        if (!Arrays.equals(this.keys, other.keys))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("testLong", testLong)
                .add("testShot", testShot)
                .add("testInt", testInt)
                .add("testByte", testByte)
                .add("testFloat", testFloat)
                .add("testDouble", testDouble)
                .add("testString", testString)
                .add("testBytes", Arrays.toString(testBytes))
                .add("testChar", testChar)
                .add("testBoolean", testBoolean)
                .add("testIntValues", testIntValues)
                .add("testKeyExpValues", testKeyExpValues)
                .add("testKeyImpValues", testKeyImpValues)
                .add("testValueExpMap", testValueExpMap)
                .add("testValueImpMap", testValueImpMap)
                .add("testKeyExpMap", testKeyExpMap)
                .add("testKeyImpMap", testKeyImpMap)
                .add("testAllExpMap", testAllExpMap)
                .add("testAllImpMap", testAllImpMap)
                .add("testAtomicInteger", testAtomicInteger)
                .add("testAtomicLong", testAtomicLong)
                .add("testAtomicBoolean", testAtomicBoolean)
                .add("ints", ints)
                .add("integers", integers)
                .toString();
    }
}
