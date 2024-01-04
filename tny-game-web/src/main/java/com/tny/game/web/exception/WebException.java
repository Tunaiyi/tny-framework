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

package com.tny.game.web.exception;

import com.tny.game.common.result.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public class WebException extends RuntimeException {

    private ResultCode code;

    private String message;

    private Object body;

    public WebException(ResultCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public WebException(ResultCode code, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
    }

    public WebException(ResultCode code, Throwable cause, Object... messageParams) {
        super(code.message(messageParams), cause);
        this.code = code;
    }

    /**
     * 设置消息
     * message("xxx {} ddd {}", 10, 11); // xxx 10 ddd 11
     *
     * @param message       消息模板
     * @param messageParams 模板参数
     * @return 返回当前异常
     */
    public WebException message(String message, Object... messageParams) {
        this.message = format(message, messageParams);
        return this;
    }

    public WebException setBody(Object body) {
        this.body = body;
        return this;
    }

    public ResultCode getResultCode() {
        return code;
    }

    @Override
    public String getMessage() {
        if (StringUtils.isNotBlank(message)) {
            return this.message;
        }
        return super.getMessage();
    }

    public Object getBody() {
        return body;
    }

}
