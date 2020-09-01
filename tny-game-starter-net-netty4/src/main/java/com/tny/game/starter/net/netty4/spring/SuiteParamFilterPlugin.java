package com.tny.game.starter.net.netty4.spring;

import com.tny.game.net.command.plugins.filter.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

public class SuiteParamFilterPlugin<UID> extends ParamFilterPlugin<UID> implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter<?>> appFilter = as(applicationContext.getBeansOfType(ParamFilter.class));
        this.addParamFilters(appFilter.values());
    }

}
