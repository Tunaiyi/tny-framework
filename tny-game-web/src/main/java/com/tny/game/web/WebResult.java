package com.tny.game.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.web.exception.*;

import static com.tny.game.common.utils.ObjectAide.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class WebResult<O> {

    @JsonProperty
    private int code;

    @JsonProperty
    private String message;

    @JsonProperty
    private O body;

    public static <O> WebResult<O> exception(WebException cause) {
        WebResult<Object> result = new WebResult<>();
        result.code = cause.getResultCode().getCode();
        result.message = cause.getMessage();
        result.body = cause.getBody();
        return as(result);
    }

    public static <O> WebResult<O> of(ResultCode code) {
        WebResult<O> result = new WebResult<>();
        result.code = code.getCode();
        result.message = code.getMessage();
        return result;
    }

    public static <O> WebResult<O> of(ResultCode code, O object) {
        WebResult<O> result = new WebResult<>();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = object;
        return result;
    }

    public static <O> WebResult<O> of(DoneResult<O> done) {
        WebResult<O> result = new WebResult<>();
        ResultCode code = done.getCode();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = done.get();
        return result;
    }

    public static <O> WebResult<O> success(O body, String message) {
        WebResult<O> result = new WebResult<>();
        result.code = ResultCode.SUCCESS_CODE;
        result.body = body;
        result.message = message;
        return result;
    }

    public static <O> WebResult<O> success(O body) {
        WebResult<O> result = new WebResult<>();
        result.code = ResultCode.SUCCESS_CODE;
        result.body = body;
        return result;
    }

    /**
     * 设置 message
     *
     * @param message 消息模板
     * @param params  参数
     * @return return this
     */
    public WebResult<O> withMessage(String message, Object... params) {
        this.message = StringAide.format(message, params);
        return this;
    }

    /**
     * 填充消息, 以 message 为模板
     *
     * @param params 消息参数
     * @return return this
     */
    public WebResult<O> withMessageParams(Object... params) {
        this.message = StringAide.format(message, params);
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public O getBody() {
        return this.body;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isSuccess() {
        return ResultCodes.isSuccess(this.code);
    }

    public boolean isFailed() {
        return !ResultCodes.isSuccess(this.code);
    }

    public ResultCode resultCode() {
        return ResultCodes.of(this.code);
    }

    @Override
    public String toString() {
        return "WebResult [code=" + code + ", message=" + message + ", body=" + body + "]";
    }

}
