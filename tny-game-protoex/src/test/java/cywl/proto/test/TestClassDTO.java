package cywl.proto.test;

import com.tny.game.common.reflect.javassist.*;

public class TestClassDTO {

    private byte[] paramBytes = new byte[]{1, 2, 3, 4, 5};

    public TestClassDTO() {
    }

    public byte[] getParamBytes() {
        return paramBytes;
    }

    public void setParamBytes(byte[] paramBytes) {
        this.paramBytes = paramBytes;
    }

    public static void main(String[] args) {
        JavassistAccessors.getGClass(TestClassDTO.class);
    }

    //	", friend=" + ArrayUtils.toString(friendIDList) + ", equip=" + equip + ", goodsList=" + ArrayUtils.toString(goodsList) +

}
