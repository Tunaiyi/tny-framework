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
package com.tny.game.net.relay.link.exception;

import com.tny.game.common.result.*;
import com.tny.game.net.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/24 4:00 下午
 */
public class RelayException extends NetException {

    public RelayException(Throwable cause) {
        super(cause);
    }

    public RelayException(ResultCode code) {
        super(code);
    }

    public RelayException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public RelayException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

    public RelayException(ResultCode code, String message, Object... messageParams) {
        super(code, message, messageParams);
    }

    public RelayException(ResultCode code, Throwable cause) {
        super(code, cause);
    }

    public RelayException(ResultCode code, Throwable cause, String message, Object... messageParams) {
        super(code, cause, message, messageParams);
    }

}
