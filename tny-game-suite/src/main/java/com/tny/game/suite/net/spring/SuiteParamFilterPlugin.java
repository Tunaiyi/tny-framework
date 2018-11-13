package com.tny.game.suite.net.spring;

import com.tny.game.net.command.plugins.filter.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.Map;

public class SuiteParamFilterPlugin<UID> extends ParamFilterPlugin<UID> implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter> appFilter = applicationContext.getBeansOfType(ParamFilter.class);
        this.addParamFilters(appFilter.values());
    }

}
