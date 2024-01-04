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

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;

import java.lang.annotation.*;

/**
 * Rpc调用目标
 * <p>
 * 可以传入的参数类型:
 * 1. com.tny.game.net.message.Contact : Rpc 接收者
 * 2. com.tny.game.net.base.RpcServicer : Rpc 被调用服务
 * 3. com.tny.game.net.base.RpcServicerPoint : Rpc 被调用服务点(指定连接)
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/28 20:54
 * @see Contact
 * @see RpcServicer
 * @see RpcAccessPoint
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcTo {

}
