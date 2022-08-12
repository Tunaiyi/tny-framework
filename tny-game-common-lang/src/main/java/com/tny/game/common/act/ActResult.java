/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.act;

import com.tny.game.common.result.*;

import java.util.*;
import java.util.function.Function;

/**
 * 行动结果
 * <p>
 *
 * @author kgtny
 * @date 2022/8/12 16:09
 **/
public class ActResult<S extends ActStatus, T> {

    private final S status;

    private final T value;

    public static <S extends ActStatus, T> ActResult<S, T> of(S status) {
        return new ActResult<>(status);
    }

    public static <S extends ActStatus, T> ActResult<S, T> of(S status, T result) {
        return new ActResult<>(status, result);
    }

    protected ActResult(S status) {
        this.status = status;
        this.value = null;
    }

    protected ActResult(S status, T value) {
        this.status = status;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public S getStatus() {
        return status;
    }

    public boolean isStatus(ActStatus status) {
        return Objects.equals(this.status, status);
    }

    public boolean anyStatus(ActStatus... statuses) {
        for (ActStatus status : statuses) {
            if (Objects.equals(this.status, status)) {
                return true;
            }
        }
        return false;
    }

    public boolean nonStatus(ActStatus... statuses) {
        for (ActStatus status : statuses) {
            if (Objects.equals(this.status, status)) {
                return false;
            }
        }
        return true;
    }

    public boolean isNotStatus(ActStatus status) {
        return !Objects.equals(this.status, status);
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public ResultCode getResultCode() {
        return status.resultCode();
    }

    public DoneResult<T> toDone() {
        if (status.resultCode().isSuccess()) {
            return DoneResults.successNullable(value);
        }
        return DoneResults.done(status.resultCode(), value);
    }

    public <D> DoneResult<D> toDoneWithCode() {
        if (status.resultCode().isSuccess()) {
            return DoneResults.success();
        }
        return DoneResults.failure(status.resultCode());
    }

    public <D> DoneResult<D> toDone(Function<T, D> mapper) {
        D value = null;
        if (mapper != null && this.value != null) {
            value = mapper.apply(this.value);
        }
        if (status.resultCode().isSuccess()) {
            return DoneResults.success(value);
        }
        return DoneResults.done(status.resultCode(), value);
    }

    public <D> ActResult<S, D> map(Function<T, D> mapper) {
        D value = null;
        if (mapper != null && this.value != null) {
            value = mapper.apply(this.value);
        }
        return ActResult.of(status, value);
    }

}
