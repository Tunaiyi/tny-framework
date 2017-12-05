package com.tny.game.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.common.utils.DoneResult;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class WebResult<O> {

    @JsonProperty
    private int code;

    @JsonProperty
    private String message;

    @JsonProperty
    private O body;

    //	public static WebResult fail(ResultCode code) {
    //		WebResult code = new WebResult();
    //		code.code = code.getCode();
    //		return code;
    //	}

    public static <O> WebResult<O> fail(ResultCode code) {
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

    public static <O> WebResult<O> as(DoneResult<O> done) {
        WebResult<O> result = new WebResult<>();
        ResultCode code = done.getCode();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = done.get();
        return result;
    }

    public static <O> WebResult<O> fail(ResultCode code, O body) {
        WebResult<O> result = new WebResult<>();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = body;
        return result;
    }

    public static <O> WebResult<O> succ(String message, O body) {
        WebResult<O> result = new WebResult<>();
        result.code = ResultCode.SUCCESS_CODE;
        result.body = body;
        result.message = message;
        return result;
    }

    public static <O> WebResult<O> succ(O body) {
        WebResult<O> result = new WebResult<>();
        result.code = ResultCode.SUCCESS_CODE;
        result.body = body;
//		code.message = body;
        return result;
    }

    //	public static WebResult fail(ResultCode code, Object body) {
    //		WebResult code = new WebResult();
    //		code.code = code.getCode();
    //		code.message = code.getMessage();
    //		code.body = body;
    //		return code;
    //	}

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
