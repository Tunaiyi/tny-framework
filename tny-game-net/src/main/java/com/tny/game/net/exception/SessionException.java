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

public class SessionException extends NetException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SessionException(Throwable cause) {
        super(cause);
    }

    public SessionException(ResultCode code) {
        super(code);
    }

    public SessionException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public SessionException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

    public SessionException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public SessionException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public SessionException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
