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
package com.tny.game.net.command.dispatcher;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/12/20 14:32
 **/
public interface RpcInvocationContext extends RpcContext {

    /**
     * @return 准备
     */
    boolean prepare();

    /**
     * 失败并响应
     *
     * @param error 错误原因
     * @return 是否完成成功
     */
    boolean complete(Throwable error);

    /**
     * 成功并响应
     *
     * @return 是否完成成功
     */
    boolean complete();

    /**
     * @return 获取错误原因
     */
    Throwable getCause();

    /**
     * @return 是否错误(异常)
     */
    boolean isError();

}
