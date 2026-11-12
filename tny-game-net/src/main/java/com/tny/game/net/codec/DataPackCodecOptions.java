/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        if (securityKeysBytes.length == 0) {
            // 启动期已 fail-fast（checkSecurityConfig），此处为运行期纵深防御：明确异常替代除零崩溃
            throw new IllegalStateException("codec security keys not configured but requested by packet number " + value);
        }
        // 包号 int 溢出可为负：floorMod 保证下标恒在 [0, length)
        return securityKeysBytes[Math.floorMod(value, securityKeysBytes.length)];
    }

    public String getSecurityKeys(long value) {
        if (ArrayUtils.isEmpty(this.securityKeys)) {
            return "";
        }
        return this.securityKeys[(int) Math.floorMod(value, this.securityKeys.length)];
    }

    /**
     * 安全配置完备性校验：启用加密或完整性校验时必须配置非空密钥。
     * 由编解码装配单元在启动期（prepareStart）调用，使缺配部署以明确原因启动失败，
     * 而非延迟为首包编解码的运行时异常。
     */
    public void checkSecurityConfig() {
        if ((this.encryptEnable || this.verifyEnable) && ArrayUtils.isEmpty(this.securityKeys)) {
            throw new IllegalStateException("codec config invalid: security keys must be configured when encrypt or verify is enabled");
        }
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
        this.securityKeysBytes = null; // 派生缓存必须随密钥重设失效，否则旧密钥永久生效
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
