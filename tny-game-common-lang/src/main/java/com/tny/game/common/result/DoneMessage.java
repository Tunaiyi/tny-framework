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
