package com.tny.game.net.command.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.transport.message.Message;
import com.tny.game.net.transport.Tunnel;

import java.lang.annotation.Annotation;

public interface ParamFilter<UID> {

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
     * @return 返回CoreResponseCode.SUCCESS(100, "请求处理成功")这继续执行下面的逻辑
     * 否则返回响应ResponseCode到客户端,并停止执行接下去的逻辑
     */
    ResultCode filter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message<UID> message) throws DispatchException;

}
