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
package com.tny.game.net.annotation;

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;

import java.lang.annotation.*;

/**
 * Rpc发起点
 * <p>
 * 可以传入的参数类型:
 * 1. com.tny.game.net.message.Contact : Rpc 发送者
 * 2. com.tny.game.net.base.RpcServicer : Rpc 发起调用的服务
 * 3. com.tny.game.net.base.RpcServicerPoint : Rpc 发起调用的服务点(指定连接)
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
public @interface RpcFrom {

}
