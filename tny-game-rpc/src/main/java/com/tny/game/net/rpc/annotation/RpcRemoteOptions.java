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
