/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.codec;

import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/12 1:50 上午
 */
public class DataPackCodecOptions {

    // 密钥
    private String[] securityKeys = new String[]{};

    // 密钥字节
    private volatile byte[][] securityKeysBytes;

    // 是否加密
    private boolean encryptEnable = false;

    // 是否设置废字节
    private boolean wasteBytesEnable = false;

    // 是否校验
    private boolean verifyEnable = false;

    // 可跳过包数步长
    private long skipNumberStep = 30;

    // 包超时
    private long packetTimeout = 60000;

    // 最大废字节数
    private int maxWasteBitSize = 30;

    // 默认最大包大小
    private int maxPayloadLength = 0xFFFF;

    public String[] getSecurityKeys() {
        return securityKeys;
    }

    public byte[] getSecurityKeyBytes(int value) {
        byte[][] securityKeysBytes = securityKeysBytes();
        return securityKeysBytes[value % securityKeysBytes.length];
    }

    public String getSecurityKeys(long value) {
        if (ArrayUtils.isEmpty(this.securityKeys)) {
            return "";
        }
        return this.securityKeys[(int)(value % this.securityKeys.length)];
    }

    public long getSkipNumberStep() {
        return this.skipNumberStep;
    }

    public boolean isEncryptEnable() {
        return this.encryptEnable;
    }

    public boolean isWasteBytesEnable() {
        return this.wasteBytesEnable;
    }

    public boolean isVerifyEnable() {
        return this.verifyEnable;
    }

    public int getMaxWasteBitSize() {
        return this.maxWasteBitSize;
    }

    public long getPacketTimeout() {
        return this.packetTimeout;
    }

    public int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }

    public DataPackCodecOptions setSecurityKeys(String[] securityKeys) {
        this.securityKeys = securityKeys;
        return this;
    }

    public DataPackCodecOptions setEncryptEnable(boolean encryptEnable) {
        this.encryptEnable = encryptEnable;
        return this;
    }

    public DataPackCodecOptions setWasteBytesEnable(boolean wasteBytesEnable) {
        this.wasteBytesEnable = wasteBytesEnable;
        return this;
    }

    public DataPackCodecOptions setVerifyEnable(boolean verifyEnable) {
        this.verifyEnable = verifyEnable;
        return this;
    }

    public DataPackCodecOptions setSkipNumberStep(long skipNumberStep) {
        this.skipNumberStep = skipNumberStep;
        return this;
    }

    public DataPackCodecOptions setPacketTimeout(long packetTimeout) {
        this.packetTimeout = packetTimeout;
        return this;
    }

    public DataPackCodecOptions setMaxWasteBitSize(int maxWasteBitSize) {
        this.maxWasteBitSize = maxWasteBitSize;
        return this;
    }

    public DataPackCodecOptions setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
        return this;
    }

    private byte[][] securityKeysBytes() {
        if (this.securityKeysBytes != null) {
            return this.securityKeysBytes;
        }
        synchronized (this) {
            if (this.securityKeysBytes != null) {
                return this.securityKeysBytes;
            }
            byte[][] securityBytesKeys = new byte[this.securityKeys.length][];
            for (int i = 0; i < this.securityKeys.length; i++) {
                securityBytesKeys[i] = this.securityKeys[i].getBytes();
            }
            this.securityKeysBytes = securityBytesKeys;
        }
        return this.securityKeysBytes;
    }

}
