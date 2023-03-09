///*
// * Copyright (c) 2020 Tunaiyi
// * Tny Framework is licensed under Mulan PSL v2.
// * You can use this software according to the terms and conditions of the Mulan PSL v2.
// * You may obtain a copy of Mulan PSL v2 at:
// *          http://license.coscl.org.cn/MulanPSL2
// * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// * See the Mulan PSL v2 for more details.
// */
//
//package com.tny.game.net.netty4.configuration.processor.disruptor;
//
//import com.google.common.collect.ImmutableMap;
//import com.tny.game.net.netty4.processor.disruptor.*;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.context.properties.*;
//
//import java.util.Map;
//
///**
// * 不主要加 @Configuration
// * 通过 ImportCommandTaskProcessorBeanDefinitionRegistrar 注册
// *
// * @author KGTny
// */
//@ConditionalOnMissingBean(DisruptorEndpointCommandTaskProcessorProperties.class)
//@ConfigurationProperties(prefix = "tny.net.command.processor.disruptor")
//public class DisruptorEndpointCommandTaskProcessorProperties {
//
//    @NestedConfigurationProperty
//    private DisruptorEndpointCommandTaskBoxProcessorSetting setting = new DisruptorEndpointCommandTaskBoxProcessorSetting()
//            .setEnable(false);
//
//    private Map<String, DisruptorEndpointCommandTaskBoxProcessorSetting> settings = ImmutableMap.of();
//
//    public DisruptorEndpointCommandTaskBoxProcessorSetting getSetting() {
//        return this.setting;
//    }
//
//    public DisruptorEndpointCommandTaskProcessorProperties setSetting(
//            DisruptorEndpointCommandTaskBoxProcessorSetting setting) {
//        this.setting = setting;
//        return this;
//    }
//
//    public Map<String, DisruptorEndpointCommandTaskBoxProcessorSetting> getSettings() {
//        return this.settings;
//    }
//
//    public DisruptorEndpointCommandTaskProcessorProperties setSettings(Map<String, DisruptorEndpointCommandTaskBoxProcessorSetting> settings) {
//        this.settings = settings;
//        return this;
//    }
//
//}