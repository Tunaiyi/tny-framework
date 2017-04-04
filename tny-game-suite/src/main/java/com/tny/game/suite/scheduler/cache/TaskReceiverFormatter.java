package com.tny.game.suite.scheduler.cache;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tny.game.cache.CacheFormatter;
import com.tny.game.protobuf.PBCommon.TaskReceiverProto;
import com.tny.game.suite.scheduler.GameTaskReceiver;
import com.tny.game.suite.scheduler.ReceiverType;
import com.tny.game.suite.scheduler.TaskReceiverBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;


@Component
@Profile({SCHEDULER_CACHE, SCHEDULER_DB, GAME})
public class TaskReceiverFormatter extends CacheFormatter<GameTaskReceiver, byte[]> {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaskReceiverFormatter.class);

    @Override
    public Object format2Save(String key, GameTaskReceiver object) {
        try {
            byte[] bytes = TaskReceiverProto.newBuilder()
                    .setPlayerID(object.getPlayerID())
                    .setGroup(object.getGroup().toString())
                    .setLastHandlerTime(object.getLastHandlerTime())
                    .setActualLastHandlerTime(object.getActualLastHandlerTime())
                    .build()
                    .toByteArray();
            return bytes;
        } catch (Throwable e) {
            LOG.error("recevier id format exception", object.getPlayerID(), e);
            return null;
        }
    }

    @Override
    public Object format2Load(String key, byte[] data) {
        TaskReceiverProto proto;
        try {
            proto = TaskReceiverProto.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("解析 TaskReceiverProto 异常", e);
            throw new RuntimeException(e);
        }
        return TaskReceiverBuilder.create()
                .setPlayerID(proto.getPlayerID())
                .setLastHandlerTime(proto.getLastHandlerTime())
                .setGroup(ReceiverType.valueOf(proto.getGroup()))
                .setActualLastHandlerTime(proto.hasActualLastHandlerTime() ? proto.getLastHandlerTime() : proto.getActualLastHandlerTime())
                .build();
    }

}
