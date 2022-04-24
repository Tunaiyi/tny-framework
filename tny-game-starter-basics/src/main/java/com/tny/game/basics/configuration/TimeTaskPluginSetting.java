package com.tny.game.basics.configuration;

import com.tny.game.common.scheduler.*;

import java.util.*;

import static com.tny.game.net.command.Certificates.*;

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
