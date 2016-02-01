package com.tny.game.net.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;

import java.lang.annotation.Annotation;

public interface ParamFilter {

    /**
     * 获取绑定的注解
     *
     * @return
     */
    public Class<? extends Annotation> getAnnotationClass();

    /**
     * 过滤方法
     *
     * @param holder  调用的业务方法持有者
     * @param request 请求对象
     * @return 返回CoreResponseCode.SUCCESS(100, "请求处理成功")这继续执行下面的逻辑
     * 否则返回响应ResponseCode到客户端,并停止执行接下去的逻辑
     */
    public ResultCode filter(MethodHolder holder, Request request);

}
