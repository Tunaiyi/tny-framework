//package com.tny.game.net.message.codec.list;
//
//import com.tny.game.net.message.codec.*;
//
///**
// * <p>
// *
// * @author : kgtny
// * @date : 2021/7/12 5:40 下午
// */
//class ListParamTest {
//
//    public static void main(String[] args) throws Exception {
//        ProtobufObjectCodecorFactory factory = new ProtobufObjectCodecorFactory();
//
//        //        TT_ListParam wTT_Param = new TT_ListParam();
//        //        ProtobufCodec<TT_ListParam> TT_Codec = factory.getCodecor(TT_ListParam.class);
//        //        byte[] TT_Bytes = TT_Codec.encode(wTT_Param);
//        //        TT_ListParam rTT_Param = TT_Codec.decode(TT_Bytes);
//        //        System.out.println(rTT_Param);
//
//        StringListParam wStringParam = new StringListParam("11", "22", "3333333", "fffffff", "dsfadsfafa");
//        ProtobufCodec<StringListParam> StringCodec = factory.getCodecor(StringListParam.class);
//        byte[] StringBytes = StringCodec.encode(wStringParam);
//        StringListParam rStringParam = StringCodec.decode(StringBytes);
//        System.out.println(rStringParam);
//
//        ByteListParam wByteParam = new ByteListParam((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, Byte.MAX_VALUE, Byte.MIN_VALUE);
//        ProtobufCodec<ByteListParam> ByteCodec = factory.getCodecor(ByteListParam.class);
//        byte[] ByteBytes = ByteCodec.encode(wByteParam);
//        ByteListParam rByteParam = ByteCodec.decode(ByteBytes);
//        System.out.println(rByteParam);
//
//        ShortListParam wShortParam = new ShortListParam((short)1, (short)2, (short)3, (short)4, (short)5, Short.MAX_VALUE, Short.MIN_VALUE);
//        ProtobufCodec<ShortListParam> ShortCodec = factory.getCodecor(ShortListParam.class);
//        byte[] ShortBytes = ShortCodec.encode(wShortParam);
//        ShortListParam rShortParam = ShortCodec.decode(ShortBytes);
//        System.out.println(rShortParam);
//
//        IntListParam wIntParam = new IntListParam(1, 2, 3, 4, 5, 6, 7, 8, Integer.MAX_VALUE, Integer.MIN_VALUE);
//        ProtobufCodec<IntListParam> IntCodec = factory.getCodecor(IntListParam.class);
//        byte[] IntBytes = IntCodec.encode(wIntParam);
//        IntListParam rIntParam = IntCodec.decode(IntBytes);
//        System.out.println(rIntParam);
//
//        LongListParam wLongParam = new LongListParam(1, 2, 3, 4, 5, 6, 7, 8, Long.MAX_VALUE, Long.MIN_VALUE);
//        ProtobufCodec<LongListParam> LongCodec = factory.getCodecor(LongListParam.class);
//        byte[] LongBytes = LongCodec.encode(wLongParam);
//        LongListParam rLongParam = LongCodec.decode(LongBytes);
//        System.out.println(rLongParam);
//
//        FloatListParam wFloatParam = new FloatListParam(1.F, 2.F, 3.F, 4.F, 5.F, 6.F, 7.F, 8.F, Float.MAX_VALUE, Float.MIN_VALUE);
//        ProtobufCodec<FloatListParam> FloatCodec = factory.getCodecor(FloatListParam.class);
//        byte[] FloatBytes = FloatCodec.encode(wFloatParam);
//        FloatListParam rFloatParam = FloatCodec.decode(FloatBytes);
//        System.out.println(rFloatParam);
//
//        DoubleListParam wDoubleParam = new DoubleListParam(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, Double.MAX_VALUE, Double.MIN_VALUE);
//        ProtobufCodec<DoubleListParam> DoubleCodec = factory.getCodecor(DoubleListParam.class);
//        byte[] DoubleBytes = DoubleCodec.encode(wDoubleParam);
//        DoubleListParam rDoubleParam = DoubleCodec.decode(DoubleBytes);
//        System.out.println(rDoubleParam);
//
//        BooleanListParam wBooleanParam = new BooleanListParam(Boolean.TRUE, Boolean.FALSE);
//        ProtobufCodec<BooleanListParam> BooleanCodec = factory.getCodecor(BooleanListParam.class);
//        byte[] BooleanBytes = BooleanCodec.encode(wBooleanParam);
//        BooleanListParam rBooleanParam = BooleanCodec.decode(BooleanBytes);
//        System.out.println(rBooleanParam);
//
//    }
//
//}