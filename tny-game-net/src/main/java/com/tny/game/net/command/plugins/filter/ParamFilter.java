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
