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

public class RpcInvokeException extends RpcException {

    public RpcInvokeException(ResultCode code) {
        super(code);
    }

    public RpcInvokeException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public RpcInvokeException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public RpcInvokeException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

    public RpcInvokeException(ResultCode code, Object body) {
        super(code, body);
    }

    public RpcInvokeException(ResultCode code, Object body, String message, Object... messageParams) {
        super(code, body, message, messageParams);
    }

    public RpcInvokeException(ResultCode code, Object body, Throwable cause, String message, Object... messageParams) {
        super(code, body, cause, message, messageParams);
    }

}
