package com.tny.game.suite.scheduler.cache;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tny.game.cache.CacheFormatter;
import com.tny.game.common.number.IntLocalNum;
import com.tny.game.common.scheduler.TimeTask;
import com.tny.game.protobuf.PBCommon.*;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SCHEDULER_CACHE, SCHEDULER_DB, GAME})
public class CacheSchedulerBackupFormatter extends CacheFormatter<CacheSchedulerBackup, byte[]> {

    /**
     * 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(CacheSchedulerBackupFormatter.class);

    private static final Logger TEST_LOG = LoggerFactory.getLogger("test");

    @Override
    public Object format2Save(String key, CacheSchedulerBackup backup) {
        Collection<TimeTask> taskCollection = backup.getTimeTaskList();
        IntLocalNum index = new IntLocalNum(0);
        Map<String, Integer> handlers = new HashMap<>();
        List<TimeTaskProto> taskList = taskCollection.stream().map(task -> TimeTaskProto.newBuilder()
                .setExecutTime(task.getExecuteTime())
                .addAllHandlers(task.getHandlerList().stream()
                        .map(handler -> handlers.computeIfAbsent(handler, (h) -> index.add(1)))
                        .collect(Collectors.toList()))
                .build()).collect(Collectors.toList());
        byte[] data = SchedulerBackupProto.newBuilder()
                .setStopTime(backup.getStopTime())
                .addAllTimeTaskQueue(taskList)
                .putAllHandlers(handlers.entrySet().stream().collect(Collectors.toMap(
                        Entry::getValue,
                        Entry::getKey
                )))
                .build().toByteArray();
        TEST_LOG.info("CacheSchedulerBackupFormatter | data size : {} To Save", data.length);
        return data;
    }

    @Override
    public Object format2Load(String key, byte[] data) {
        SchedulerBackupProto proto;
        try {
            TEST_LOG.info("CacheSchedulerBackupFormatter | data size : {} To Load", data.length);
            proto = SchedulerBackupProto.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("解析 SchedulerBackupProto 异常", e);
            throw new RuntimeException(e);
        }
        Map<Integer, String> handles = proto.getHandlersMap();
        CacheSchedulerBackup cacheSchedulerBackup = new CacheSchedulerBackup();
        cacheSchedulerBackup.setStopTime(proto.getStopTime());
        List<TimeTask> taskList = proto.getTimeTaskQueueList().stream()
                .map(taskProto -> proto2TimeTask(taskProto, handles))
                .collect(Collectors.toList());
        cacheSchedulerBackup.setTimeTaskList(taskList);
        return cacheSchedulerBackup;
    }

    private TimeTask proto2TimeTask(TimeTaskProto proto, Map<Integer, String> handleMap) {
        List<String> handlers = proto.getHandlersList().stream().map(handleMap::get).filter(Objects::nonNull).collect(Collectors.toList());
        return new TimeTask(handlers.isEmpty() ? proto.getHandlerListList() : handlers, proto.getExecutTime());
    }

}
