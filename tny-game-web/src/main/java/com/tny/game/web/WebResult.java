package com.tny.game.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.DoneResult;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class WebResult {

    @JsonProperty
    private int result;

    @JsonProperty
    private Object message;

    @JsonProperty
    private Object body;

    //	public static WebResult fail(ResultCode code) {
    //		WebResult result = new WebResult();
    //		result.result = code.getCode();
    //		return result;
    //	}

    public static WebResult fail(ResultCode code) {
        WebResult result = new WebResult();
        result.result = code.getCode();
        result.message = code.getMessage();
        return result;
    }

    public static WebResult as(DoneResult<?> done) {
        WebResult result = new WebResult();
        ResultCode code = done.getCode();
        result.result = code.getCode();
        result.message = code.getMessage();
        result.body = done.get();
        return result;
    }

    public static WebResult fail(ResultCode code, Object body) {
        WebResult result = new WebResult();
        result.result = code.getCode();
        result.message = code.getMessage();
        result.body = body;
        return result;
    }

    public static WebResult succ(Object message, Object body) {
        WebResult result = new WebResult();
        result.result = ResultCode.SUCCESS_CODE;
        result.body = body;
        result.message = message;
        return result;
    }

    public static WebResult succ(Object body) {
        WebResult result = new WebResult();
        result.result = ResultCode.SUCCESS_CODE;
        result.body = body;
//		result.message = body;
        return result;
    }

    //	public static WebResult fail(ResultCode code, Object body) {
    //		WebResult result = new WebResult();
    //		result.result = code.getCode();
    //		result.message = code.getMessage();
    //		result.body = body;
    //		return result;
    //	}

    public int getResult() {
        return this.result;
    }

    public Object getBody() {
        return this.body;
    }

    public Object getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "WebResult [result=" + result + ", message=" + message + ", body=" + body + "]";
    }

}
