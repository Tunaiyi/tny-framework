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

package com.tny.game.data.mongodb.exception;

import com.tny.game.common.exception.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 3:24 下午
 */
public class GetEntityIdException extends CommonRuntimeException {

    public GetEntityIdException() {
    }

    public GetEntityIdException(String message, Object... messageParams) {
        super(message, messageParams);
    }

    public GetEntityIdException(Throwable cause) {
        super(cause);
    }

    public GetEntityIdException(Throwable cause, String message, Object... messageParams) {
        super(cause, message, messageParams);
    }

}
