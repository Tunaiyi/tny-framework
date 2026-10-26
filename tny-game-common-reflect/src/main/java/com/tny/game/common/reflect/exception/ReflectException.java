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

package com.tny.game.common.reflect.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/22 12:19 下午
 */
public class ReflectException extends CommonRuntimeException {

    public ReflectException() {
    }

    public ReflectException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }

    public ReflectException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
