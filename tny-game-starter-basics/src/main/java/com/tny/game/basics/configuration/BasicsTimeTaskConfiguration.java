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

package com.tny.game.basics.configuration;

import com.tny.game.basics.scheduler.*;
import com.tny.game.common.scheduler.*;
import com.tny.game.net.command.plugins.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.stream.Collectors;

import static com.tny.game.basics.configuration.BasicsPropertyConstants.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BasicsTimeTaskProperties.class)
@ConditionalOnProperty(name = BASICS_TIME_TASK_ENABLE, havingValue = "true")
public class BasicsTimeTaskConfiguration {

    @Bean
    TimeTaskHandlerHolder timeTaskHandlerHolder(ObjectProvider<TimeTaskHandler> objectProvider) {
        return new DefaultTimeTaskHandlerHolder(objectProvider.stream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnBean({SchedulerBackupFactory.class, SchedulerBackupManager.class})
    SchedulerStore schedulerStore(BasicsTimeTaskProperties properties,
            SchedulerBackupFactory backupFactory,
            SchedulerBackupManager schedulerBackupManager) {
        return new DefaultSchedulerStore(properties, backupFactory, schedulerBackupManager);
    }

    @Bean
    @ConditionalOnBean({GameTaskReceiverManager.class, GameTaskReceiverFactory.class})
    TimeTaskService timeTaskService(BasicsTimeTaskProperties properties, TimeTaskHandlerHolder handlerHolder,
            SchedulerStore schedulerStore, GameTaskReceiverManager taskReceiverManager, GameTaskReceiverFactory taskReceiverFactory) {
        return new DefaultTimeTaskService(properties, handlerHolder, schedulerStore, taskReceiverManager, taskReceiverFactory);
    }

    @Bean
    @ConditionalOnClass(VoidCommandPlugin.class)
    TaskReceiverSchedulerPlugin taskReceiverSchedulerPlugin(BasicsTimeTaskProperties properties, TimeTaskService service) {
        return new TaskReceiverSchedulerPlugin(properties, service);
    }

}
