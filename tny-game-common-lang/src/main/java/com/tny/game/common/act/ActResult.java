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
