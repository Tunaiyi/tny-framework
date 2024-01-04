/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;

public class NetCodecException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static NetCodecException causeEncodeFailed(Throwable cause, String message, Object... params) {
        return new NetCodecException(NetResultCode.ENCODE_FAILED, cause, message, params);
    }

    public static NetCodecException causeEncodeFailed(String message, Object... params) {
        return new NetCodecException(NetResultCode.ENCODE_FAILED, message, params);
    }

    public static NetCodecException causeEncodeError(Throwable cause, String message, Object... params) {
        return new NetCodecException(NetResultCode.ENCODE_ERROR, cause, message, params);
    }

    public static NetCodecException causeEncodeError(String message, Object... params) {
        return new NetCodecException(NetResultCode.ENCODE_ERROR, message, params);
    }

    public static NetCodecException causeDecodeError(Throwable cause, String message, Object... params) {
        return new NetCodecException(NetResultCode.DECODE_ERROR, cause, message, params);
    }

    public static NetCodecException causeDecodeError(String message, Object... params) {
        return new NetCodecException(NetResultCode.DECODE_ERROR, message, params);
    }

    public static NetCodecException causeDecodeFailed(Throwable cause, String message, Object... params) {
        return new NetCodecException(NetResultCode.DECODE_FAILED, cause, message, params);
    }

    public static NetCodecException causeDecodeFailed(String message, Object... params) {
        return new NetCodecException(NetResultCode.DECODE_FAILED, message, params);
    }

    public static NetCodecException causeTimeout(String message, Object... params) {
        return new NetCodecException(NetResultCode.PACKET_TIMEOUT, message, params);
    }

    public static NetCodecException causeVerify(String message, Object... params) {
        return new NetCodecException(NetResultCode.PACKET_VERIFY_FAILED, message, params);
    }

    private NetCodecException(ResultCode code, String message, Object... params) {
        super(code, message, params);
    }

    private NetCodecException(ResultCode code, Throwable cause, String message, Object... params) {
        super(code, cause, message, params);
    }

}
