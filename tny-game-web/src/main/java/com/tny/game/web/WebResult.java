package com.tny.game.web;

import com.fasterxml.jackson.annotation.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.web.exception.*;

import java.util.Optional;
import java.util.function.*;

import static com.tny.game.common.utils.ObjectAide.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
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

    public static <O> WebResult<O> codeOf(DoneResult<?> done) {
        WebResult<O> result = new WebResult<>();
        ResultCode code = done.getCode();
        result.code = code.getCode();
        result.message = code.getMessage();
        return result;
    }

    public static <O> WebResult<O> map(DoneResult<?> done, O body) {
        WebResult<O> result = new WebResult<>();
        ResultCode code = done.getCode();
        result.code = code.getCode();
        result.message = code.getMessage();
        result.body = body;
        return result;
    }

    public static <V, D> WebResult<D> mapOn(DoneResult<V> done, D successBody, D failureBody) {
        if (done.isFailure()) {
            return WebResult.map(done, failureBody);
        }
        return WebResult.success(successBody);
    }

    public static <V, D> WebResult<D> mapOnSuccess(DoneResult<V> done, D body) {
        if (done.isFailure()) {
            return WebResult.codeOf(done);
        }
        return WebResult.success(body);
    }

    public static <V, D> WebResult<D> mapOnSuccess(DoneResult<V> done, Function<V, D> mapper) {
        if (done.isFailure()) {
            return WebResult.codeOf(done);
        }
        return WebResult.success(mapper.apply(done.get()));
    }

    public static <V, D> WebResult<D> mapOnFailure(DoneResult<V> done, D body) {
        if (done.isFailure()) {
            return WebResult.map(done, body);
        }
        return WebResult.success();
    }

    public static <V, D> WebResult<D> mapOnFailure(DoneResult<V> done, Function<V, D> mapper) {
        if (done.isFailure()) {
            return WebResult.map(done, mapper.apply(done.get()));
        }
        return WebResult.success();
    }

    public static <V, D> WebResult<D> mapIf(DoneResult<V> done, Function<DoneResult<V>, D> mapper) {
        return WebResult.map(done, mapper.apply(done));
    }

    public static <O> WebResult<O> map(DoneResult<O> done) {
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
        result.message = ResultCode.SUCCESS.getMessage();
        result.body = body;
        return result;
    }

    public static <O> WebResult<O> success() {
        return success(null);
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

    public O orElseBody(O other) {
        O object = getBody();
        return object != null ? object : other;
    }

    public O orElseGetBody(Supplier<? extends O> other) {
        O object = getBody();
        return object != null ? object : other.get();
    }

    public void ifPresent(Consumer<? super O> consumer) {
        O object = getBody();
        if (object != null) {
            consumer.accept(object);
        }
    }

    public Optional<O> optional() {
        return Optional.ofNullable(this.body);
    }

    public void ifSuccess(Consumer<? super O> consumer) {
        if (this.isSuccess()) {
            consumer.accept(this.body);
        }
    }

    public void ifFailure(Consumer<? super O> consumer) {
        if (this.isFailure()) {
            consumer.accept(this.body);
        }
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isSuccess() {
        return ResultCodes.isSuccess(this.code);
    }

    public boolean isFailure() {
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
