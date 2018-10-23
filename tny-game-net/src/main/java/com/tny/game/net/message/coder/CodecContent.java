package com.tny.game.net.message.coder;

public class CodecContent {

    public static final String SECURITY_FILE = "<cross-domain-policy>"
            + "<site-control permitted-cross-domain-policies=\"all\"/>"
            + "<allow-access-from domain=\"*\" to-ports=\"*\" />"
            + "</cross-domain-policy>" + "\0";

    public static final String POLICY_FILE = "<policy-file-request/>\0";

    public static final byte[] FRAME_MAGIC = "tny.".getBytes();

    public static final int OPTION_SIZE = 1;

    public static final int MESSAGE_LENGTH_SIZE = 4;

    public static final byte PONG_OPTION = (byte) 1;
    public static final byte PING_OPTION = (byte) (1 << 1);


    public static final byte OPTION_COMPRESS = (byte) (1 << 2); // 选项(是否有加密)
    public static final byte OPTION_VERIFY = (byte) (1 << 3); // 选项(是否有校验)
    public static final byte OPTION_ENCRYPT = (byte) (1 << 4); // 选项(是否有加密)
    public static final byte OPTION_WASTE_BYTES = (byte) (1 << 5); // 选项(是否有加密)

    public static final int OPTION_LENGTH = 1;                  // 选项长度
    public static final int VERIFIER_CODE_LENGTH = 8;           // 校验码长度
    public static final int PACK_SIZE_LENGTH = 4;               // 校验码长度

    public static final int RANDOM_BYTES_SIZE = 32;     // 校验码长度
    public static final int RANDOM_WASTE_BIT_SIZE = 32; // 包体随机位移字节数

    /**
     * 是否是文件头
     *
     * @param magics
     * @return
     */
    public static boolean isMagic(final byte[] magics) {
        for (int index = 0; index < magics.length; index++) {
            if (CodecContent.FRAME_MAGIC[index] != magics[index])
                return false;
        }
        return true;
    }


    public static boolean isOption(byte option, byte mark) {
        return (option & mark) != 0;
    }

    public static byte setOption(byte option, byte mark, boolean effect) {
        if (effect)
            return (byte) (option | mark);
        return option;
    }
}
