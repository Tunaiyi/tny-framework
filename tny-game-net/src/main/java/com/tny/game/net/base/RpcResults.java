package com.tny.game.net.base;

import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * @author KGTny
 */
public class RpcResults {

    /**
     * 成功返回数据
     */
    public final static RpcResult<?> SUCCESS = new RpcResultImpl<>(NetResultCode.SUCCESS, null);

    /**
     * 创建成功响应结果
     * <p>
     * <p>
     * 创建成功响应结果<br>
     *
     * @param body 信息体
     * @return 返回响应结果
     */
    public static <T> RpcResult<T> success(T body) {
        return new RpcResultImpl<>(NetResultCode.SUCCESS, body);
    }

    /**
     * 请求结果
     *
     * @param code 消息码
     * @return 返回
     */
    public static <T> RpcResult<T> result(ResultCode code) {
        if (code.isSuccess()) {
            return success();
        } else {
            return fail(code);
        }
    }

    /**
     * 请求结果
     *
     * @param result 结果
     * @param mapper 转换器
     * @return 返回
     */
    public static <T, S, U> RpcResult<U> result(DoneResult<S> result, Function<S, U> mapper) {
        if (result.isSuccess()) {
            return success(mapper.apply(result.get()));
        } else {
            return fail(result.getCode());
        }
    }

    /**
     * 请求结果
     *
     * @param result 结果
     * @return 返回
     */
    public static <T, S> RpcResult<S> result(DoneResult<S> result) {
        return result(result, ObjectAide::self);
    }

    /**
     * 创建成功响应结果
     * <p>
     * <p>
     * 创建成功响应结果<br>
     *
     * @return 返回响应结果
     */
    public static <T> RpcResult<T> success() {
        return as(SUCCESS);
    }

    /**
     * 创建响应结果
     *
     * @param code 结果码
     * @param body 消息体
     * @return 返回结果
     */
    public static <T> RpcResult<T> result(ResultCode code, Object body) {
        return new RpcResultImpl<>(code, body);
    }

    /**
     * 创建失败响应结果
     * <p>
     * <p>
     * 创建失败响应结果<br>
     *
     * @param code 结果码
     * @param body 消息体
     * @return 返回响应结果
     */
    public static <T> RpcResult<T> fail(ResultCode code, Object body) {
        return new RpcResultImpl<>(code, body);
    }

    /**
     * 创建失败响应结果
     * <p>
     * <p>
     * 创建失败响应结果<br>
     *
     * @param code 结果码
     * @return 返回响应结果
     */
    public static <T> RpcResult<T> fail(ResultCode code) {
        return new RpcResultImpl<>(code, null);
    }

    /**
     * 根据done创建响应结果
     *
     * @param done 错误结果
     * @return 返回响应结果
     */
    public static <T> RpcResult<T> fail(DoneResult<?> done) {
        return fail(done.getCode(), null);
    }

    //TODO Protocol 设置
    private static class RpcResultImpl<T> implements RpcResult<T> {

        private final ResultCode resultCode;

        private final Object body;

        private final String description;

        private RpcResultImpl(ResultCode code) {
            super();
            this.resultCode = code;
            this.description = code.getMessage();
            this.body = null;
        }

        private RpcResultImpl(ResultCode code, Object body) {
            super();
            this.resultCode = code;
            this.description = code.getMessage();
            this.body = body;
        }

        @Override
        public ResultCode getResultCode() {
            return this.resultCode;
        }

        @Override
        public String getDescription() {
            if (StringUtils.isNoneBlank(this.description)) {
                return this.description;
            }
            return this.resultCode.getMessage();
        }

        @Override
        public Object getBody() {
            return this.body;
        }

    }

}
