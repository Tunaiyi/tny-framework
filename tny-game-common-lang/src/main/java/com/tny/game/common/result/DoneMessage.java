package com.tny.game.common.result;

/**
 * Done 的消息载体
 * <p>
 *
 * @author Kun Yang
 */
public interface DoneMessage<M, D extends Done<M>> {

    /**
     * 以message为模板设置消息内容
     *
     * @param params 消息参数
     * @return 返回 DoneResult<M>
     */
    D withMessageParams(Object... params);

    /**
     * 设置消息
     *
     * @param message 消息模板
     * @param params  消息参数
     * @return 返回 DoneResult<M>
     */
    D withMessage(String message, Object... params);

}
