package com.tny.game.suite.net.spring;

import com.tny.game.net.command.plugins.filter.ParamFilterPlugin;
import com.tny.game.net.command.plugins.filter.ParamFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SuiteParamFilterPlugin<UID> extends ParamFilterPlugin<UID> implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter> appFilter = applicationContext.getBeansOfType(ParamFilter.class);
        this.addParamFilters(appFilter.values());
    }

}
