package com.tny.game.net.command;

import com.tny.game.common.result.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * @author KGTny
 */
public interface RpcResult<T> extends RpcReturn<T> {

    /**
     * 获取结果状态码
     * <p>
     * <p>
     * 获取结果状态码<br>
     *
     * @return 返回结果状态码
     */
    default int getCode() {
        return this.getResultCode().getCode();
    }

    /**
     * 获取结果状态码
     * <p>
     * 获取结果状态码<br>
     *
     * @return 返回结果状态码
     */
    ResultCode getResultCode();

    /**
     * @return 是否成功
     */
    default boolean isSuccess() {
        return ResultCodes.isSuccess(this.getCode());
    }

    /**
     * @return 是否失败
     */
    default boolean isFailure() {
        return !ResultCodes.isSuccess(this.getCode());
    }

    /**
     * @return 消息描述(开发用, 请勿作为提示)
     */
    String getDescription();

    /**
     * @return 成功时返回数据, 如果失败返回 null
     */
    default T get() {
        if (isSuccess()) {
            Object value = getBody();
            return as(value);
        }
        return null;
    }

    /**
     * 获取数据(无论失败或者成功)
     *
     * @param tClass body 类型
     * @return 返回 body
     */
    default <F> F checkBody(Class<F> tClass) {
        Object value = getBody();
        if (value == null) {
            return null;
        }
        if (tClass.isInstance(value)) {
            return tClass.cast(value);
        }
        throw new ClassCastException(format("{} cast {} exception", value.getClass(), tClass));
    }

    /**
     * 获取数据(无论失败或者成功)
     *
     * @param tClass body 类型
     * @return 返回 body
     */
    default <F> F getBody(Class<F> tClass) {
        Object value = getBody();
        if (value == null) {
            return null;
        }
        if (tClass.isInstance(value)) {
            return tClass.cast(value);
        }
        return null;
    }

    /**
     * 获取响应消息体
     * <p>
     * <p>
     * 获取响应消息体<br>
     *
     * @return 返回响应消息体
     */
    Object getBody();

}
