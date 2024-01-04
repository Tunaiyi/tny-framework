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

package com.tny.game.net.netty4.configuration.command;

import com.tny.game.net.command.plugins.filter.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

public class SpringBootParamFilterPlugin extends ParamFilterPlugin implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ParamFilter> appFilter = as(applicationContext.getBeansOfType(ParamFilter.class));
        this.addParamFilters(appFilter.values());
    }

}
