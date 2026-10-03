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

package com.tny.game.basics.item.exception;

/**
 * Created by Kun Yang on 2016/10/26.
 */
public class WrongClassException extends RuntimeException {

    public WrongClassException() {
    }

    public WrongClassException(String message) {
        super(message);
    }

    public WrongClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongClassException(Throwable cause) {
        super(cause);
    }

    public WrongClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
