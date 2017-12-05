package com.tny.game.suite.net.spring;

import com.tny.game.net.command.filter.FilterPlugin;
import com.tny.game.net.command.filter.ParamFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SuiteFilterPlugin<UID> extends FilterPlugin<UID> implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter> appFilter = applicationContext.getBeansOfType(ParamFilter.class);
        this.addParamFilters(appFilter.values());
    }

}
