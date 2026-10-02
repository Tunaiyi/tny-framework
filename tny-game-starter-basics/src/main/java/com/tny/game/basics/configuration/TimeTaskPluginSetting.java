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

package com.tny.game.basics.configuration;

import com.tny.game.common.scheduler.*;

import java.util.*;

import static com.tny.game.net.application.ContactType.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:23 下午
 */
public class TimeTaskPluginSetting {

    private Map<String, TaskReceiverType> receiverTypeMapper = new HashMap<>();

    public TimeTaskPluginSetting() {
        this.receiverTypeMapper.put(DEFAULT_USER_TYPE, DefaultTaskReceiverType.PLAYER);
    }

    public Map<String, TaskReceiverType> getReceiverTypeMapper() {
        return Collections.unmodifiableMap(receiverTypeMapper);
    }

    public TimeTaskPluginSetting setReceiverTypeMapper(
            Map<String, TaskReceiverType> receiverTypeMapper) {
        this.receiverTypeMapper = receiverTypeMapper;
        return this;
    }

    public TaskReceiverType getReceiverType(String userType) {
        return this.receiverTypeMapper.get(userType);
    }

}
