package com.tny.game.net.codec;

/**
 * Created by Kun Yang on 2018/8/13.
 */
public class DataPacketConfig {

    // 密钥
    private String[] securityKeys;
    // 密钥字节
    private final byte[][] securityBytesKeys;
    private boolean encryptOpen;
    private boolean wasteBytesOpen;
    private boolean verifyOpen;
    private long packetMaxSkipNumber;
    private long packetTimeout;
    private int maxWasteBitSize;

    public DataPacketConfig(boolean encryptOpen, boolean wasteBytesOpen, boolean verifyOpen, String[] securityKeys) {
        this.encryptOpen = encryptOpen;
        this.wasteBytesOpen = wasteBytesOpen;
        this.verifyOpen = verifyOpen;
        this.securityKeys = securityKeys;
        this.securityBytesKeys = new byte[securityKeys.length][];
        for (int i = 0; i < securityKeys.length; i++) {
            this.securityBytesKeys[i] = securityKeys[i].getBytes();
        }
    }

    public String[] getSecurityKeys() {
        return securityKeys;
    }

    public byte[] getSecurityKey(int value) {
        return securityBytesKeys[value % securityBytesKeys.length];
    }

    public String getSecurityKeys(long value) {
        return securityKeys[(int) (value % securityKeys.length)];
    }

    public long getPacketMaxSkipNumber() {
        return packetMaxSkipNumber;
    }

    public boolean isEncryptOpen() {
        return encryptOpen;
    }

    public boolean isWasteBytesOpen() {
        return wasteBytesOpen;
    }

    public boolean isVerifyOpen() {
        return verifyOpen;
    }

    public int getMaxWasteBitSize() {
        return maxWasteBitSize;
    }

    public long getPacketTimeout() {
        return this.packetTimeout;
    }
}
