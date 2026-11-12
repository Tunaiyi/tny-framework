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
package com.tny.game.net.command.plugins.filter;

import com.tny.game.common.result.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.lang.annotation.Annotation;

public interface ParamFilter {

    /**
     * 获取绑定的注解
     *
     * @return
     */
    Class<? extends Annotation> getAnnotationClass();

    /**
     * 过滤方法
     *
     * @param holder  调用的业务方法持有者
     * @param tunnel  通道
     * @param message 消息
     * @return 返回CoreResponseCode.SUCCESS(100, " 请求处理成功 ")这继续执行下面的逻辑
     * 否则返回响应ResponseCode到客户端,并停止执行接下去的逻辑
     */
    ResultCode filter(MethodControllerHolder holder, Tunnel tunnel, Message message) throws RpcInvokeException;

}
