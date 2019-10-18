package com.tny.game.common.utils;

import com.tny.game.common.result.*;

import java.util.function.BiConsumer;

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

}
