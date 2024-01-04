/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.result;

import java.util.function.*;

/**
 * <p>
 *
 * @author Kun Yang
 */
public interface DoneResult<M> extends Done<M> {

    /**
     * @return 获取结果码
     */
    ResultCode getCode();

    /**
     * 如果失败则调用 consumer
     *
     * @param consumer 参数
     */
    void ifFailure(BiConsumer<ResultCode, ? super M> consumer);

    /**
     * 调用 consumer
     *
     * @param consumer 接收
     */
    void then(BiConsumer<ResultCode, ? super M> consumer);

    //    default <T> DoneResult<T> mapOnSuccess(Function<M, T> mapper) {
    //        if (this.isSuccess()) {
    //            return DoneResults.success(mapper.apply(this.get()));
    //        } else {
    //            return DoneResults.failure(this);
    //        }
    //    }
    //
    //    default <T> DoneResult<T> mapOnFailed(BiFunction<ResultCode, String, T> mapper) {
    //        if (this.isFailure()) {
    //            return DoneResults.map(this, mapper.apply(this.getCode(), this.getMessage()));
    //        } else {
    //            return DoneResults.success();
    //        }
    //    }
    //
    //    default <T> DoneResult<T> mapOnFailed(Function<DoneResult<M>, T> mapper) {
    //        if (this.isFailure()) {
    //            return DoneResults.map(this, mapper.apply(this));
    //        } else {
    //            return DoneResults.success();
    //        }
    //    }

    default <T> DoneResult<T> mapIfPresent(BiFunction<M, ResultCode, T> mapper) {
        M value = this.get();
        if (value != null) {
            T returnValue = mapper.apply(value, this.getCode());
            return DoneResults.map(this, returnValue);
        } else {
            return DoneResults.map(this, (T) null);
        }
    }

}
