///*
// * Copyright (c) 2020 Tunaiyi
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
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