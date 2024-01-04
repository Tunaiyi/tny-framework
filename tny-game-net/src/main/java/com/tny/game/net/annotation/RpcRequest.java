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

package com.tny.game.net.annotation;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/4 8:01 下午
 */

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcRequest {

    /**
     * 处理协议号
     * <p>
     * <p>
     * 被Controller标记的类的模塊ID <br>
     * 被Controller标记的方法的业务方法ID <br>
     *
     * @return 处理协议号
     */
    int value();

    /**
     * @return 线路id
     */
    int line() default 0;

}
