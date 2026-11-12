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
package com.tny.game.net.netty4.apm.skywalking;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.tny.game.net.netty4.apm.skywalking.SkywalkingPropertiesConstants.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/9 04:34
 **/
@ConfigurationProperties(prefix = SKYWALKING_PREFIX)
public class SkywalkingRpcMonitorProperties {

    private boolean enable = true;

    private boolean enableCollectArguments = true;

    private int collectArgumentsMaxLength = 125;

    public boolean isDisable() {
        return !enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public SkywalkingRpcMonitorProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isEnableCollectArguments() {
        return enableCollectArguments;
    }

    public SkywalkingRpcMonitorProperties setEnableCollectArguments(boolean enableCollectArguments) {
        this.enableCollectArguments = enableCollectArguments;
        return this;
    }

    public int getCollectArgumentsMaxLength() {
        return collectArgumentsMaxLength;
    }

    public SkywalkingRpcMonitorProperties setCollectArgumentsMaxLength(int collectArgumentsMaxLength) {
        this.collectArgumentsMaxLength = collectArgumentsMaxLength;
        return this;
    }

}
