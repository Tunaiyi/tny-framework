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
