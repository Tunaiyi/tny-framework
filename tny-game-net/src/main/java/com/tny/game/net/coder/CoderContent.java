package com.tny.game.net.coder;

public class CoderContent {

    public static final String SECURITY_FILE = "<cross-domain-policy>"
            + "<site-control permitted-cross-domain-policies=\"all\"/>"
            + "<allow-access-from domain=\"*\" to-ports=\"*\" />"
            + "</cross-domain-policy>" + "\0";

    public static final String POLICY_FILE = "<policy-file-request/>\0";

    public static final byte[] FRAME_MAGIC = "funs".getBytes();

    public static final int OPTION_SIZE = 1;

    public static final int MESSAGE_LENGTH_SIZE = 4;

    public static final byte COMPRESS_OPTION = 1;
    public static final byte RESPONSE_OPTION = (byte) (1 << 7);
    public static final byte PING_OPTION = (byte) (1 << 2);
    public static final byte PONG_OPTION = (byte) (1 << 1);
    public static final byte[] PING_BYTES = {PING_OPTION};
    public static final byte[] PONG_BYTES = {PONG_OPTION};

}
