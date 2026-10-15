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
package com.tny.game.common.concurrent.exception;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/9/27 15:12
 **/
public class WorkerExecuteException extends RuntimeException {

    public WorkerExecuteException() {
    }

    public WorkerExecuteException(String message) {
        super(message);
    }

    public WorkerExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkerExecuteException(Throwable cause) {
        super(cause);
    }

    public WorkerExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
