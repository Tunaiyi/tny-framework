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

package com.tny.game.common.runtime;

import com.tny.game.common.utils.*;
import org.slf4j.Logger;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/11 11:02 下午
 */
public class LogFragment {

    private String message;

    private Object[] params;

    public static LogFragment message(String message, Object... params) {
        return new LogFragment(message, params);
    }

    private LogFragment(String message, Object... params) {
        this.message = message;
        this.params = params;
    }

    public LogFragment append(String message, Object... params) {
        if (StringAide.isNoneBlank(message)) {
            if (this.message == null) {
                this.message = message;
            } else {
                this.message += message;
            }
        }
        if (params.length > 0) {
            this.params = add(this.params, params);
        }
        return this;
    }

    public void log(Logger logger) {
        logger.debug(this.message, this.params);
    }

    private boolean isEmpty(Object[] params) {
        return params == null || params.length == 0;
    }

    private Object[] add(Object[] source, Object... params) {
        if (isEmpty(source)) {
            return params;
        } else if (isEmpty(params)) {
            return source;
        }
        Object[] array = new Object[source.length + params.length];
        System.arraycopy(source, 0, array, 0, source.length);
        System.arraycopy(params, 0, array, source.length, params.length);
        return array;
    }

}
