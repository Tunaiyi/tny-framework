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

import com.tny.game.protoex.annotations.*;

import java.util.*;

@ProtoEx(2000)
public class CTestObject {

    @ProtoExField(1)
    public Long testLong;

    @ProtoExField(2)
    public Short testShot;

    @ProtoExField(3)
    public Integer testInt;

    @ProtoExField(4)
    public Byte testByte;

    @ProtoExField(5)
    public Float testFloat;

    @ProtoExField(6)
    public Double testDouble;

    @ProtoExField(7)
    public String testString;

    @ProtoExField(8)
    public byte[] testBytes;

    @ProtoExField(9)
    public Character testChar;

    @ProtoExField(10)
    public Boolean testBoolean;

    @ProtoExField(11)
    @ProtoExElement
    public Collection<Integer> testIntValues;

    @ProtoExField(12)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public Collection<TestKey> testKeyExpValues;

    @ProtoExField(13)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public Collection<TestKey> testKeyImpValues;

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

    @ProtoExField(value = 20, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public TestKey testObject;

    @ProtoExField(value = 21, conf = @ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public TestKey testImpObject;

    @ProtoExField(value = 22, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public TestKey testExcessObject;

    @ProtoExField(value = 23)
    public Integer testExcessInt;

    @ProtoExField(24)
    @Packed(false)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    public Collection<TestKey> testKeyUnpackedExpValues;

    @ProtoExField(25)
    @Packed(false)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.IMPLICIT))
    public Collection<TestKey> testKeyUnpackedImpValues;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.testAllExpMap == null) ? 0 : this.testAllExpMap.hashCode());
        result = prime * result + ((this.testAllImpMap == null) ? 0 : this.testAllImpMap.hashCode());
        result = prime * result + ((this.testBoolean == null) ? 0 : this.testBoolean.hashCode());
        result = prime * result + ((this.testByte == null) ? 0 : this.testByte.hashCode());
        result = prime * result + Arrays.hashCode(this.testBytes);
        result = prime * result + ((this.testChar == null) ? 0 : this.testChar.hashCode());
        result = prime * result + ((this.testDouble == null) ? 0 : this.testDouble.hashCode());
        result = prime * result + ((this.testExcessInt == null) ? 0 : this.testExcessInt.hashCode());
        result = prime * result + ((this.testExcessObject == null) ? 0 : this.testExcessObject.hashCode());
        result = prime * result + ((this.testFloat == null) ? 0 : this.testFloat.hashCode());
        result = prime * result + ((this.testImpObject == null) ? 0 : this.testImpObject.hashCode());
        result = prime * result + ((this.testInt == null) ? 0 : this.testInt.hashCode());
        result = prime * result + ((this.testIntValues == null) ? 0 : this.testIntValues.hashCode());
        result = prime * result + ((this.testKeyExpMap == null) ? 0 : this.testKeyExpMap.hashCode());
        result = prime * result + ((this.testKeyExpValues == null) ? 0 : this.testKeyExpValues.hashCode());
        result = prime * result + ((this.testKeyImpMap == null) ? 0 : this.testKeyImpMap.hashCode());
        result = prime * result + ((this.testKeyImpValues == null) ? 0 : this.testKeyImpValues.hashCode());
        result = prime * result + ((this.testKeyUnpackedExpValues == null) ? 0 : this.testKeyUnpackedExpValues.hashCode());
        result = prime * result + ((this.testKeyUnpackedImpValues == null) ? 0 : this.testKeyUnpackedImpValues.hashCode());
        result = prime * result + ((this.testLong == null) ? 0 : this.testLong.hashCode());
        result = prime * result + ((this.testObject == null) ? 0 : this.testObject.hashCode());
        result = prime * result + ((this.testShot == null) ? 0 : this.testShot.hashCode());
        result = prime * result + ((this.testString == null) ? 0 : this.testString.hashCode());
        result = prime * result + ((this.testValueExpMap == null) ? 0 : this.testValueExpMap.hashCode());
        result = prime * result + ((this.testValueImpMap == null) ? 0 : this.testValueImpMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CTestObject other = (CTestObject)obj;
        if (this.testAllExpMap == null) {
            if (other.testAllExpMap != null) {
                return false;
            }
        } else if (!this.testAllExpMap.equals(other.testAllExpMap)) {
            return false;
        }
        if (this.testAllImpMap == null) {
            if (other.testAllImpMap != null) {
                return false;
            }
        } else if (!this.testAllImpMap.equals(other.testAllImpMap)) {
            return false;
        }
        if (this.testBoolean == null) {
            if (other.testBoolean != null) {
                return false;
            }
        } else if (!this.testBoolean.equals(other.testBoolean)) {
            return false;
        }
        if (this.testByte == null) {
            if (other.testByte != null) {
                return false;
            }
        } else if (!this.testByte.equals(other.testByte)) {
            return false;
        }
        if (!Arrays.equals(this.testBytes, other.testBytes)) {
            return false;
        }
        if (this.testChar == null) {
            if (other.testChar != null) {
                return false;
            }
        } else if (!this.testChar.equals(other.testChar)) {
            return false;
        }
        if (this.testDouble == null) {
            if (other.testDouble != null) {
                return false;
            }
        } else if (!this.testDouble.equals(other.testDouble)) {
            return false;
        }
        if (this.testExcessInt == null) {
            if (other.testExcessInt != null) {
                return false;
            }
        } else if (!this.testExcessInt.equals(other.testExcessInt)) {
            return false;
        }
        if (this.testExcessObject == null) {
            if (other.testExcessObject != null) {
                return false;
            }
        } else if (!this.testExcessObject.equals(other.testExcessObject)) {
            return false;
        }
        if (this.testFloat == null) {
            if (other.testFloat != null) {
                return false;
            }
        } else if (!this.testFloat.equals(other.testFloat)) {
            return false;
        }
        if (this.testImpObject == null) {
            if (other.testImpObject != null) {
                return false;
            }
        } else if (!this.testImpObject.equals(other.testImpObject)) {
            return false;
        }
        if (this.testInt == null) {
            if (other.testInt != null) {
                return false;
            }
        } else if (!this.testInt.equals(other.testInt)) {
            return false;
        }
        if (this.testIntValues == null) {
            if (other.testIntValues != null) {
                return false;
            }
        } else if (!this.testIntValues.equals(other.testIntValues)) {
            return false;
        }
        if (this.testKeyExpMap == null) {
            if (other.testKeyExpMap != null) {
                return false;
            }
        } else if (!this.testKeyExpMap.equals(other.testKeyExpMap)) {
            return false;
        }
        if (this.testKeyExpValues == null) {
            if (other.testKeyExpValues != null) {
                return false;
            }
        } else if (!this.testKeyExpValues.equals(other.testKeyExpValues)) {
            return false;
        }
        if (this.testKeyImpMap == null) {
            if (other.testKeyImpMap != null) {
                return false;
            }
        } else if (!this.testKeyImpMap.equals(other.testKeyImpMap)) {
            return false;
        }
        if (this.testKeyImpValues == null) {
            if (other.testKeyImpValues != null) {
                return false;
            }
        } else if (!this.testKeyImpValues.equals(other.testKeyImpValues)) {
            return false;
        }
        if (this.testKeyUnpackedExpValues == null) {
            if (other.testKeyUnpackedExpValues != null) {
                return false;
            }
        } else if (!this.testKeyUnpackedExpValues.equals(other.testKeyUnpackedExpValues)) {
            return false;
        }
        if (this.testKeyUnpackedImpValues == null) {
            if (other.testKeyUnpackedImpValues != null) {
                return false;
            }
        } else if (!this.testKeyUnpackedImpValues.equals(other.testKeyUnpackedImpValues)) {
            return false;
        }
        if (this.testLong == null) {
            if (other.testLong != null) {
                return false;
            }
        } else if (!this.testLong.equals(other.testLong)) {
            return false;
        }
        if (this.testObject == null) {
            if (other.testObject != null) {
                return false;
            }
        } else if (!this.testObject.equals(other.testObject)) {
            return false;
        }
        if (this.testShot == null) {
            if (other.testShot != null) {
                return false;
            }
        } else if (!this.testShot.equals(other.testShot)) {
            return false;
        }
        if (this.testString == null) {
            if (other.testString != null) {
                return false;
            }
        } else if (!this.testString.equals(other.testString)) {
            return false;
        }
        if (this.testValueExpMap == null) {
            if (other.testValueExpMap != null) {
                return false;
            }
        } else if (!this.testValueExpMap.equals(other.testValueExpMap)) {
            return false;
        }
        if (this.testValueImpMap == null) {
            if (other.testValueImpMap != null) {
                return false;
            }
        } else if (!this.testValueImpMap.equals(other.testValueImpMap)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CTestObject [\n"
                + "\t testLong=" + this.testLong + ",\n"
                + "\t testShot=" + this.testShot + ",\n"
                + "\t testInt=" + this.testInt + ",\n"
                + "\t testByte=" + this.testByte + ",\n"
                + "\t testFloat=" + this.testFloat + "\n"
                + "\t testDouble=" + this.testDouble + ",\n"
                + "\t testString=" + this.testString + ",\n"
                + "\t testBytes=" + Arrays.toString(this.testBytes) + ",\n"
                + "\t testChar=" + this.testChar + ",\n"
                + "\t testBoolean=" + this.testBoolean + ",\n"
                + "\t testIntValues=" + this.testIntValues + ",\n"
                + "\t testKeyExpValues=" + this.testKeyExpValues + ",\n"
                + "\t testKeyImpValues=" + this.testKeyImpValues + ",\n"
                + "\t testValueExpMap=" + this.testValueExpMap + ",\n"
                + "\t testValueImpMap=" + this.testValueImpMap + ",\n"
                + "\t testKeyExpMap=" + this.testKeyExpMap + ",\n"
                + "\t testKeyImpMap=" + this.testKeyImpMap + ",\n"
                + "\t testAllExpMap=" + this.testAllExpMap + ",\n"
                + "\t testAllImpMap=" + this.testAllImpMap + ",\n"
                + "\t testObject=" + this.testObject + ",\n"
                + "\t testImpObject=" + this.testImpObject + ",\n"
                + "\t testExcessObject=" + this.testExcessObject + ",\n"
                + "\t testExcessInt=" + this.testExcessInt + ",\n"
                + "\t testKeyUnpackedExpValues=" + this.testKeyUnpackedExpValues + ",\n"
                + "\t testKeyUnpackedImpValues=" + this.testKeyUnpackedImpValues + "\n]";
    }

}
