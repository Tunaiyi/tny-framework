/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
