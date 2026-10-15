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
package com.tny.game.common.result;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 做完的结果
 *
 * @author KGTny
 */
public class AsyncDoneResults {

    /**
     * 返回一个成功的结果, value 为null
     *
     * @return
     */
    public static <M> CompletableFuture<DoneResult<M>> success() {
        return future(DoneResults.success());
    }

    /**
     * 返回一个成功的结果, value 不能为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> CompletableFuture<DoneResult<M>> success(MC value) {
        return future(DoneResults.success(value));
    }

    /**
     * 返回一个成功的结果, value 可为null
     *
     * @param value
     * @return
     */
    public static <M, MC extends M> CompletableFuture<DoneResult<M>> successNullable(MC value) {
        return future(DoneResults.successNullable(value));
    }

    /**
     * 返回一个结果, 如果value为null时返回结果码为nullCode的结果,否则返回包含value的成功结果
     *
     * @param value    成功时结果内容
     * @param nullCode 失败时结果码
     * @return 返回结果
     */
    public static <M, MC extends M> CompletableFuture<DoneResult<M>> successIfNotNull(MC value, ResultCode nullCode) {
        return future(DoneResults.successIfNotNull(value, nullCode));
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value 结果值
     * @param code  结果码
     * @return 返回结果
     */
    public static <M, MC extends M> CompletableFuture<DoneResult<M>> done(ResultCode code, MC value) {
        return future(DoneResults.done(code, value));
    }

    /**
     * 返回一个结果 可成功或失败, 由code决定
     *
     * @param value   结果值
     * @param code    结果码
     * @param message 消息
     * @return 返回结果
     */
    public static <M, MC extends M> CompletableFuture<DoneResult<M>> done(ResultCode code, MC value, String message, Object... messageParams) {
        return future(DoneResults.done(code, value, message, messageParams));
    }

    // /**
    //  * 返回一个结果 可成功或失败, 由code决定
    //  *
    //  * @param code 结果码
    //  * @return 返回结果
    //  */
    // public static DoneMessage<Void, ? extends DoneResult<Void>> with(ResultCode code) {
    //     return new DefaultDoneResult<>(code, null);
    // }
    //
    // /**
    //  * 返回一个结果 可成功或失败, 由code决定
    //  *
    //  * @param value 结果值
    //  * @param code  结果码
    //  * @return 返回结果
    //  */
    // public static <M, MC extends M> DoneMessage<M, ? extends DoneResult<M>> with(ResultCode code, MC value) {
    //     return new DefaultDoneResult<>(code, value, null);
    // }

    /**
     * 返回结果
     *
     * @param <M>
     * @return
     */
    public static <M> CompletableFuture<DoneResult<M>> failure() {
        return future(DoneResults.failure());
    }

    /**
     * 返回一个以code为结果码的失败结果
     *
     * @param code 结果码
     * @return 返回结果
     */
    public static <M> CompletableFuture<DoneResult<M>> failure(ResultCode code) {
        return future(DoneResults.failure(code));
    }

    /**
     * 返回一个以code为结果码的失败结果, 并设置 message
     *
     * @param code 结果码
     * @return 返回结果
     */
    public static <M> CompletableFuture<DoneResult<M>> failure(ResultCode code, String message, Object... messageParams) {
        return future(DoneResults.failure(code, message, messageParams));
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返回结果
     */
    public static <M> CompletableFuture<DoneResult<M>> failure(DoneResult<?> result) {
        return future(DoneResults.failure(result));
    }

    /**
     * 返回一个结果码为result的结果码的失败结果
     *
     * @param result 失败结果
     * @return 返回结果
     */
    public static <M> CompletableFuture<DoneResult<M>> failure(DoneResult<?> result, String message, Object... messageParams) {
        return future(DoneResults.failure(result, message, messageParams));
    }

    /**
     * 返回一个DoneResults, 结果码为result的结果码, 返回内容为value的.
     *
     * @param result 失败结果
     * @param value  返回值
     * @param <M>    返回类型
     * @return DoneResults
     */
    public static <M> CompletableFuture<DoneResult<M>> map(DoneResult<?> result, M value) {
        return future(DoneResults.map(result, value));
    }

    /**
     * 返回一个DoneResults, 其结果码为result的结果码, 返回内容为mapper的返回结果.
     *
     * @param result 失败结果
     * @param mapper 返回值的mapper函数
     * @param <M>    返回类型
     * @return DoneResults
     */
    public static <M, S> CompletableFuture<DoneResult<M>> map(DoneResult<S> result, Function<S, M> mapper) {
        return future(DoneResults.map(result, mapper));
    }

    public static <M> CompletableFuture<DoneResult<M>> future(DoneResult<M> result) {
        return CompletableFuture.completedFuture(result);
    }


}
