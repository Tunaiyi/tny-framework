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
