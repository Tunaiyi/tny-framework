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

package com.tny.game.net.rpc.annotation;

import com.tny.game.net.rpc.*;

import java.lang.annotation.*;

/**
 * Rpc远程选项
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/1 23:57
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcRemoteOptions {

    /**
     * @return 调用方式
     */
    RpcInvokeMode mode() default RpcInvokeMode.DEFAULT;

    /**
     * @return 是否是寂寞方式(不抛出异常)
     */
    boolean silently() default false;

    /**
     * -1 为 setting 配置时间,
     * >= 0 超时
     *
     * @return 超时
     */
    long timeout() default -1;

    /**
     * @return 路由器类
     */
    Class<? extends RpcRouter> router() default RpcRouter.class;

}
