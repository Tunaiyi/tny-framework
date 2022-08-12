/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.result;

import com.tny.game.common.utils.*;

import java.util.Optional;
import java.util.function.*;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
public abstract class BaseDoneResult<M, D extends BaseDoneResult<M, D>> implements DoneResult<M>, DoneMessage<M, D> {

    protected ResultCode code;

    protected M returnValue;

    protected String message;

    protected BaseDoneResult(ResultCode code, M returnValue) {
        this.code = code;
        this.returnValue = returnValue;
        this.message = null;
    }

    protected BaseDoneResult(ResultCode code, M returnValue, String message) {
        this.code = code;
        this.returnValue = returnValue;
        if (StringAide.isNoneBlank(message)) {
            this.message = message;
        }
    }

    /**
     * @return 是否成功 code == ResultCode.SUCCESS
     */
    @Override
    public boolean isSuccess() {
        return ResultAide.isSuccess(this.code);
    }

    /**
     * @return 是否成功 code != ResultCode.SUCCESS
     */
    @Override
    public boolean isFailure() {
        return !ResultAide.isSuccess(this.code);
    }

    /**
     * @return 是否有结果值呈现
     */
    @Override
    public boolean isPresent() {
        return this.returnValue != null;
    }

    /**
     * 设置消息产生
     *
     * @param message 消息, 可用占位符 {}
     * @param params  消息参数列表, 替换占位符
     * @return 返回
     */
    @Override
    public D withMessage(String message, Object... params) {
        this.message = StringAide.format(message, params);
        return ObjectAide.as(this);
    }

    /**
     * 设置 code message 填充参数
     *
     * @param params 消息参数列表
     * @return 返回
     */
    @Override
    public D withMessageParams(Object... params) {
        this.message = StringAide.format(this.code.getMessage(), params);
        return ObjectAide.as(this);
    }

    /**
     * @return 转成Optional
     */
    @Override
    public Optional<M> optional() {
        return Optional.ofNullable(this.returnValue);
    }

    /**
     * 是否成功
     *
     * @param consumer 数值填充
     */
    @Override
    public void ifSuccess(Consumer<? super M> consumer) {
        if (this.isSuccess()) {
            consumer.accept(get());
        }
    }

    @Override
    public void ifFailure(BiConsumer<ResultCode, ? super M> consumer) {
        if (!this.isSuccess()) {
            consumer.accept(this.code, get());
        }
    }

    @Override
    public void then(BiConsumer<ResultCode, ? super M> consumer) {
        consumer.accept(this.code, get());
    }

    /**
     * @return 获取结果值
     */
    @Override
    public ResultCode getCode() {
        return this.code;
    }

    /**
     * @return 获取返回结果
     */
    @Override
    public M get() {
        return this.returnValue;
    }

    @Override
    public String getMessage() {
        if (this.message != null) {
            return this.message;
        } else {
            return this.code.getMessage();
        }
    }

    @Override
    public String toString() {
        return "ResultDone [code=" + this.code + ", returnValue=" + this.returnValue + "]";
    }

}
