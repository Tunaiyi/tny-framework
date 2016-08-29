package com.tny.game.net.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.plugin.ControllerPlugin;
import com.tny.game.net.dispatcher.plugin.PluginContext;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.ResultFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilterPlugin implements ControllerPlugin, ApplicationContextAware {

    private Map<Class<?>, ParamFilter> filterMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter> appFilter = applicationContext.getBeansOfType(ParamFilter.class);
        for (ParamFilter filter : appFilter.values()) {
            this.filterMap.put(filter.getAnnotationClass(), filter);
        }
    }

    @Override
    public CommandResult execute(Request request, CommandResult result, PluginContext context) throws Exception {
        MethodHolder methodHolder = context.getMethodHolder();
        Set<Class<?>> classSet = methodHolder.getParamAnnotationClass();
        for (Class<?> filterClass : classSet) {
            ParamFilter paramFliter = this.filterMap.get(filterClass);
            if (paramFliter != null) {
                ResultCode resultCode = paramFliter.filter(methodHolder, request);
                if (resultCode != CoreResponseCode.SUCCESS) {
                    return ResultFactory.fail(resultCode);
                }
            }
        }
        return context.passToNext(request, result);
    }
}
