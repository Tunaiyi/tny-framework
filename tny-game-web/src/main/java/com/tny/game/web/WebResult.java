package com.tny.game.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.DoneResult;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class WebResult {

    @JsonProperty
    private int code;

    @JsonProperty
    private String message;

    @JsonProperty
    private Object body;

    //	public static WebResult fail(ResultCode code) {
    //		WebResult code = new WebResult();
    //		code.code = code.getCode();
    //		return code;
    //	}

    public static WebResult fail(ResultCode code) {
        WebResult result = new WebResult();
        result.code = code.getCode();
        result.message = code.getMessage();
        return result;
    }

    public static WebResult as(DoneResult<?> done) {
        WebResult result = new WebResult();
        ResultCode code = done.getCode();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = done.get();
        return result;
    }

    public static WebResult fail(ResultCode code, Object body) {
        WebResult result = new WebResult();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = body;
        return result;
    }

    public static WebResult succ(String message, Object body) {
        WebResult result = new WebResult();
        result.code = ResultCode.SUCCESS_CODE;
        result.body = body;
        result.message = message;
        return result;
    }

    public static WebResult succ(Object body) {
        WebResult result = new WebResult();
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

    public Object getBody() {
        return this.body;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "WebResult [code=" + code + ", message=" + message + ", body=" + body + "]";
    }

}
