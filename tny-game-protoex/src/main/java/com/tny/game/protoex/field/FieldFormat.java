package com.tny.game.protoex.field;

import com.tny.game.protoex.ProtoExInputStream;
import com.tny.game.protoex.ProtoExOutputStream;

/**
 * 整形数字(short int long)类型编码方式
 *
 * @author KGTny
 */
public enum FieldFormat {

    /**
     * 默认编码方式 : 动态字节编码
     * 7位+1位是否有下一字节的
     */
    DEFAULT(0) {
        @Override
        public void writeNoTag(int value, ProtoExOutputStream output) {
            output.writeInt(value);
        }

        @Override
        public void writeNoTag(long value, ProtoExOutputStream output) {
            output.writeLong(value);
        }

        @Override
        public int readInt(ProtoExInputStream inputStream) {
            return inputStream.readInt();
        }

        @Override
        public long readLong(ProtoExInputStream inputStream) {
            return inputStream.readLong();

        }

    },

    /**
     * zigzag压缩方式编码 : zigzag + 动态字节编码
     * 数据存储为
     * 0  => 0
     * -1 => 1
     * 1  => 2
     * -2 => 3
     * 2  => 4
     * -3 => 5
     * 3  => 6
     */
    ZigZag(1) {
        @Override
        public void writeNoTag(int value, ProtoExOutputStream output) {
            output.writeSignInt(value);
        }

        @Override
        public void writeNoTag(long value, ProtoExOutputStream output) {
            output.writeSignLong(value);
        }

        @Override
        public int readInt(ProtoExInputStream inputStream) {
            return inputStream.readSignInt();
        }

        @Override
        public long readLong(ProtoExInputStream inputStream) {
            return inputStream.readSignLong();
        }

    },

    /**
     * 固定字节编码
     * short 32位
     * int   32位
     * long  64位
     */
    Fixed(2) {
        @Override
        public void writeNoTag(int value, ProtoExOutputStream output) {
            output.writeFixInt(value);
        }

        @Override
        public void writeNoTag(long value, ProtoExOutputStream output) {
            output.writeFixLong(value);
        }

        @Override
        public int readInt(ProtoExInputStream inputStream) {
            return inputStream.readFixedInt();
        }

        @Override
        public long readLong(ProtoExInputStream inputStream) {
            return inputStream.readFixLong();
        }
    };

    public final int ID;

    private FieldFormat(int iD) {
        this.ID = iD;
    }

    public abstract void writeNoTag(int value, ProtoExOutputStream outputStream);

    public abstract void writeNoTag(long value, ProtoExOutputStream outputStream);

    public abstract int readInt(ProtoExInputStream inputStream);

    public abstract long readLong(ProtoExInputStream inputStream);

    public static FieldFormat get(int typeID) {
        if (typeID == 1)
            return ZigZag;
        else if (typeID == 2)
            return Fixed;
        return DEFAULT;
    }

}
