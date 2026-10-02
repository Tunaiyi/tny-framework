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
package com.tny.game.net.application;

import com.tny.game.common.result.*;

import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * @author KGTny
 */
public interface RpcResult<T> extends RpcReturn<T> {

    /**
     * 获取结果状态码
     * <p>
     * 获取结果状态码<br>
     *
     * @return 返回结果状态码
     */
    ResultCode resultCode();

    /**
     * 获取结果状态码
     * <p>
     * <p>
     * 获取结果状态码<br>
     *
     * @return 返回结果状态码
     */
    default int getCode() {
        return this.resultCode().getCode();
    }

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
     * 转 DoneResult
     *
     * @return
     */
    default DoneResult<T> toDoneResult() {
        return DoneResults.done(resultCode(), get());
    }

    /**
     * 转 void DoneResult
     *
     * @return
     */
    default DoneResult<Void> toVoidResult() {
        return DoneResults.done(resultCode(), null);
    }

    default <U> DoneResult<U> toDoneResult(Function<T, U> format) {
        if (resultCode().isSuccess()) {
            return DoneResults.success(format.apply(get()));
        } else {
            return DoneResults.failure(resultCode());
        }
    }

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
