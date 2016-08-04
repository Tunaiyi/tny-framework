package com.tny.game.suite.scheduler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tny.game.cache.CacheFormatter;
import com.tny.game.scheduler.TimeTask;
import com.tny.game.suite.proto.PBSuite261.SchedulerBackupProto;
import com.tny.game.suite.proto.PBSuite261.TimeTaskProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile({"suite.scheduler.cache_store"})
public class CacheSchedulerBackupFormatter extends CacheFormatter<CacheSchedulerBackup, byte[]> {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(CacheSchedulerBackupFormatter.class);

    @Override
    public Object format2Save(String key, CacheSchedulerBackup backup) {
        Collection<TimeTask> taskCollection = backup.getTimeTaskList();
        List<TimeTaskProto> taskList = taskCollection.stream().map(task -> TimeTaskProto.newBuilder()
                .setExecutTime(task.getExecuteTime())
                .addAllHandlerList(task.getHandlerList())
                .build()).collect(Collectors.toList());
        return SchedulerBackupProto.newBuilder()
                .setStopTime(backup.getStopTime())
                .addAllTimeTaskQueue(taskList)
                .build().toByteArray();
    }

    @Override
    public Object format4Load(String key, byte[] data) {
        SchedulerBackupProto proto;
        try {
            proto = SchedulerBackupProto.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("解析 SchedulerBackupProto 异常", e);
            throw new RuntimeException(e);
        }
        CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup();
        cacheSchedulerBackup.setStopTime(proto.getStopTime());
        List<TimeTask> taskList = proto.getTimeTaskQueueList().stream()
                .map(taskProto -> new TimeTask(taskProto.getHandlerListList(), taskProto.getExecutTime()))
                .collect(Collectors.toList());
        cacheSchedulerBackup.setTimeTaskList(taskList);
        return cacheSchedulerBackup;
    }

}
