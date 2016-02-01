package com.tny.game.suite.scheduler;

import com.tny.game.asyndb.annotation.Persistent;
import com.tny.game.cache.annotation.ToCache;
import com.tny.game.cache.async.CacheSynchronizer;
import com.tny.game.scheduler.SchedulerBackup;
import com.tny.game.scheduler.TimeTask;
import com.tny.game.scheduler.TimeTaskQueue;
import com.tny.game.scheduler.TimeTaskScheduler;
import com.tny.game.suite.SuiteDBHead;

import java.util.Collection;

@Persistent(asyn = false, synchronizerClass = CacheSynchronizer.class)
@ToCache(prefix = SuiteDBHead.CACHE_KEY_GAME_SERVER, triggers = CacheSchedulerBackupFormatter.class,
        cacheKeys = {"key"})
public class CacheSchedulerBackup extends SchedulerBackup {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String BACK_UP_KEY = "BACKUP";

    private Object key = BACK_UP_KEY;

    public Object getKey() {
        return this.key;
    }

    protected CacheSchedulerBackup() {
    }

    public CacheSchedulerBackup(Object key, TimeTaskScheduler scheduler) {
        super(scheduler);
        this.key = key;
    }

    @Override
    protected long getStopTime() {
        return this.stopTime;
    }

    protected Collection<TimeTask> getTimeTaskList() {
        return this.timeTaskQueue.getTimeTaskList();
    }

    protected void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    protected void setTimeTaskList(Collection<TimeTask> taskCollection) {
        this.timeTaskQueue = new TimeTaskQueue();
        for (TimeTask task : taskCollection) {
            this.timeTaskQueue.put(task);
        }
    }

    //	public byte[] saveTo() {
    //		List<TimeTaskProto> taskList = new ArrayList<TimeTaskProto>(this.timeTaskQueue.size());
    //		for (TimeTask task : this.timeTaskQueue.getTimeTaskList()) {
    //			taskList.add(TimeTaskProto.newBuilder()
    //					.setExecutTime(task.getExecutTime())
    //					.addAllHandlerList(task.getHandlerList())
    //					.build());
    //		}
    //		return SchedulerBackupProto.newBuilder()
    //				.setStopTime(this.getStopTime())
    //				.addAllTimeTaskQueue(taskList)
    //				.build().toByteArray();
    //	}
    //
    //	public void loadFrom(byte[] bytes) throws InvalidProtocolBufferException {
    //		SchedulerBackupProto proto = SchedulerBackupProto.parseFrom(bytes);
    //		this.stopTime = proto.getStopTime();
    //		this.timeTaskQueue = new TimeTaskQueue();
    //		for (TimeTaskProto taskProto : proto.getTimeTaskQueueList()) {
    //			this.timeTaskQueue.put(new TimeTask(taskProto.getHandlerListList(), taskProto.getExecutTime()));
    //		}
    //	}

}
