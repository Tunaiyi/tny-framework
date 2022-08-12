/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.configuration;

import com.tny.game.common.scheduler.*;

import java.util.*;

import static com.tny.game.net.base.MessagerType.*;

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
