package com.tny.game.net.command.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.suite.app.CoreResponseCode;
import com.tny.game.net.command.ControllerPlugin;
import com.tny.game.net.command.InvokeContext;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilterPlugin<UID> implements ControllerPlugin<UID>, ApplicationContextAware {

    private Map<Class<?>, ParamFilter> filterMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter> appFilter = applicationContext.getBeansOfType(ParamFilter.class);
        for (ParamFilter filter : appFilter.values()) {
            this.filterMap.put(filter.getAnnotationClass(), filter);
        }
    }


    @Override
    public void execute(Tunnel<UID> tunnel, Message<UID> message, InvokeContext context) throws Exception {
        MethodControllerHolder methodHolder = context.getController();
        Set<Class<?>> classSet = methodHolder.getParamAnnotationClass();
        for (Class<?> filterClass : classSet) {
            ParamFilter paramFilter = this.filterMap.get(filterClass);
            if (paramFilter != null) {
                ResultCode resultCode = paramFilter.filter(methodHolder, tunnel, message);
                if (resultCode != CoreResponseCode.SUCCESS) {
                    // 完成 不继续执行
                    context.done(resultCode);
                }
            }
        }
    }
}
