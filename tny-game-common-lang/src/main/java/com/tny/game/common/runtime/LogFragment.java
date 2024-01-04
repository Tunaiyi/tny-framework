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
