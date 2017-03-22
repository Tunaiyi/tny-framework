package com.tny.game.net.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.ResultFactory;
import com.tny.game.net.command.CommandResult;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.plugin.ControllerPlugin;
import com.tny.game.net.plugin.PluginContext;
import com.tny.game.net.session.Session;
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
    @SuppressWarnings("unchecked")
    public CommandResult execute(Session<UID> session, Message<UID> message, CommandResult result, PluginContext context) throws Exception {
        MethodControllerHolder methodHolder = context.getMethodHolder();
        Set<Class<?>> classSet = methodHolder.getParamAnnotationClass();
        for (Class<?> filterClass : classSet) {
            ParamFilter paramFilter = this.filterMap.get(filterClass);
            if (paramFilter != null) {
                ResultCode resultCode = paramFilter.filter(methodHolder, session, message);
                if (resultCode != CoreResponseCode.SUCCESS) {
                    return ResultFactory.fail(resultCode);
                }
            }
        }
        return context.passToNext(session, message, result);
    }

}
