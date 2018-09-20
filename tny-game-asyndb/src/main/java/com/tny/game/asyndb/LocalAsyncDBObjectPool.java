package com.tny.game.asyndb;

import com.tny.game.asyndb.annotation.Persistent;
import com.tny.game.asyndb.log.LogName;
import com.tny.game.common.concurrent.CoreThreadFactory;
import org.slf4j.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 本地内存异步持久化对象对象池
 * <p>
 * 负责管理维护异步持久化对象的容器，负责回收释放的对象。
 *
 * @author KGTny
 */
public class LocalAsyncDBObjectPool implements DBObjectPool {

    private final static Logger LOGGER = LoggerFactory.getLogger(LogName.ASYN_DB);

    private final static Logger POOL_LOGGER = LoggerFactory.getLogger(LogName.LOACL_ASYN_BDOBJECT_POOL);

    /**
     * 异步对象类管理器Map
     */
    private final ConcurrentMap<Class<?>, AsynDBClassHolder> asynDBClassHolderMap = new ConcurrentHashMap<>();

    /**
     * 异步对象实体Map
     */
    private final ConcurrentMap<String, AsyncDBEntity> entityMap = new ConcurrentHashMap<>();

    /**
     * 回收异步对象线程
     */
    private final ScheduledExecutorService recycleExecutor = Executors.newScheduledThreadPool(1, new CoreThreadFactory("AsynBDObjectPool-RecycleExecutor", false));

    /**
     * 释放策略
     */
    protected ReleaseStrategyFactory releaseStrategyFactory;

    /**
     * 异步持久化执行器
     */
    protected SyncDBExecutor syncDBExecutor;

    /**
     * 异步持久化器f管理器
     */
    protected SynchronizerHolder synchronizerHolder;

    public LocalAsyncDBObjectPool(SyncDBExecutor syncDBExecutor, SynchronizerHolder synchronizerHolder, final long recycleTime) {
        this(syncDBExecutor, new DefaultReleaseStrategyFactory(900 * 1000), synchronizerHolder, recycleTime);
    }

    public LocalAsyncDBObjectPool(SyncDBExecutor syncDBExecutor, ReleaseStrategyFactory releaseStrategyFactory, SynchronizerHolder synchronizerHolder, final long recycleTime) {
        this.releaseStrategyFactory = releaseStrategyFactory;
        this.syncDBExecutor = syncDBExecutor;
        this.synchronizerHolder = synchronizerHolder;
        this.recycleExecutor.schedule(new Runnable() {

            @Override
            public void run() {
                int size = LocalAsyncDBObjectPool.this.entityMap.size();
                int removeSize = 0;
                long releaseAt = System.currentTimeMillis();
                for (Entry<String, AsyncDBEntity> entry : LocalAsyncDBObjectPool.this.entityMap.entrySet()) {
                    try {
                        if (entry.getValue().release(releaseAt)) {
                            if (LocalAsyncDBObjectPool.this.entityMap.remove(entry.getKey(), entry.getValue()))
                                removeSize++;
                            POOL_LOGGER.debug("释放 {} ", entry.getKey());
                        } else {
                            POOL_LOGGER.debug("存在 {} {}", new Object[]{entry.getKey(), entry.getValue().getState()});
                        }
                    } catch (Throwable e) {
                        POOL_LOGGER.error("{}回收异常", entry.getKey(), e);
                    }
                }
                POOL_LOGGER.info("对象池中存在 {} 个对象, 释放 {} 个对象, 剩余 {} 个对象", size, removeSize, LocalAsyncDBObjectPool.this.entityMap.size());
                if (!LocalAsyncDBObjectPool.this.recycleExecutor.isShutdown())
                    LocalAsyncDBObjectPool.this.recycleExecutor.schedule(this, recycleTime, TimeUnit.MILLISECONDS);
            }

        }, recycleTime, TimeUnit.MILLISECONDS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, String key) {
        AsyncDBEntity asyncDBEntity = this.entityMap.get(key);
        if (asyncDBEntity != null) {
            if (asyncDBEntity.isDelete())
                return null;
            Object object = asyncDBEntity.visit();
            if (object != null)
                return (T) object;
            this.entityMap.remove(key, asyncDBEntity);
        }
        AsynDBClassHolder holder = this.getAsynDBClassHolder(clazz);
        Synchronizer<Object> synchronizer = this.getSynchronizer(holder);
        Object object = synchronizer.get(clazz, key);
        if (object != null)
            return (T) this.put(key, object);
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, ? extends T> getsMap(Class<T> clazz, Collection<String> keys) {
        if (keys.isEmpty())
            return Collections.emptyMap();
        Map<String, T> returnMap = new HashMap<>(keys.size());
        List<String> noCacheKeys = new ArrayList<>();
        for (String key : keys) {
            AsyncDBEntity asyncDBEntity = this.entityMap.get(key);
            if (asyncDBEntity != null) {
                if (asyncDBEntity.isDelete())
                    continue;
                Object object = asyncDBEntity.visit();
                if (object == null) {
                    this.entityMap.remove(key, asyncDBEntity);
                    noCacheKeys.add(key);
                } else {
                    returnMap.put(key, (T) object);
                }
            } else {
                noCacheKeys.add(key);
            }
        }
        AsynDBClassHolder holder = this.getAsynDBClassHolder(clazz);
        Synchronizer<Object> synchronizer = this.getSynchronizer(holder);
        Map<String, ?> objectsMap = synchronizer.get(clazz, noCacheKeys);
        for (Entry<String, ?> entry : objectsMap.entrySet()) {
            Object value = entry.getValue();
            if (value == null)
                continue;
            value = this.put(entry.getKey(), value);
            returnMap.put(entry.getKey(), (T) value);
        }
        return returnMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<? extends T> gets(Class<T> clazz, Collection<String> keys) {
        if (keys.isEmpty())
            return Collections.emptyList();
        Collection<T> returnCollection = new ArrayList<>(keys.size());
        List<String> noCacheKeys = new ArrayList<>();
        for (String key : keys) {
            AsyncDBEntity asyncDBEntity = this.entityMap.get(key);
            if (asyncDBEntity != null) {
                if (asyncDBEntity.isDelete())
                    continue;
                Object object = asyncDBEntity.visit();
                if (object == null) {
                    this.entityMap.remove(key, asyncDBEntity);
                    noCacheKeys.add(key);
                } else {
                    returnCollection.add((T) object);
                }
            } else {
                noCacheKeys.add(key);
            }
        }
        AsynDBClassHolder holder = this.getAsynDBClassHolder(clazz);
        Synchronizer<Object> synchronizer = this.getSynchronizer(holder);
        Map<String, ?> objectsMap = synchronizer.get(clazz, noCacheKeys);
        for (Entry<String, ?> entry : objectsMap.entrySet()) {
            Object value = entry.getValue();
            if (value == null)
                continue;
            value = this.put(entry.getKey(), value);
            returnCollection.add((T) value);
        }
        return returnCollection;
    }

    //	@Override
    private Object put(String key, Object object) {
        AsynDBClassHolder holder = this.getAsynDBClassHolder(object.getClass());
        return this.getAndCreateAsynDBEntity(key, object, AsyncDBState.NORMAL, holder).visit();
    }

    @Override
    public boolean insert(String key, Object object) {
        AsynDBClassHolder holder = this.getAsynDBClassHolder(object.getClass());
        if (holder.persistent.asyn()) {
            AsyncDBEntity asyncDBEntity = this.getAsynDBEntity(key);
            if (asyncDBEntity == null) {
                ReleaseStrategy strategy = this.releaseStrategyFactory.createStrategy(object, System.currentTimeMillis());
                asyncDBEntity = new AsyncDBEntity(object, this.getSynchronizer(holder), AsyncDBState.DELETED, strategy);
                try {
                    asyncDBEntity.mark(Operation.INSERT, object);
                } catch (AsyncDBReleaseException | SubmitAtWrongStateException e) {
                    LOGGER.error(MessageFormat.format("#LoaclAsynBDObjectPool#提交状态为{0}对象{1},进行{2}操作异常", AsyncDBState.DELETED, object, AsyncDBState.INSERT), e);
                }
                return this.entityMap.putIfAbsent(key, asyncDBEntity) == null
                        && this.syncDBExecutor.sumit(asyncDBEntity);
            } else {
                if (asyncDBEntity.isDelete()) {
                    try {
                        if (asyncDBEntity.mark(Operation.INSERT, object))
                            return this.syncDBExecutor.sumit(asyncDBEntity);
                    } catch (AsyncDBReleaseException e) {
                        // 对象被释放，尝试插入
                        this.entityMap.remove(key, asyncDBEntity);
                        return this.insert(key, object);
                    } catch (SubmitAtWrongStateException e) {
                        // 对象状态无法进行该操作
                        LOGGER.error(MessageFormat.format("#LoaclAsynBDObjectPool#提交状态为{0}对象{1},进行{2}操作异常", e.getState(), object, e.getOperation()), e);
                    }
                }
            }
            return false;
        }
        return this.getSynchronizer(holder).insert(object);
    }

    @Override
    public boolean update(String key, Object object) {
        AsynDBClassHolder holder = this.getAsynDBClassHolder(object.getClass());
        if (holder.persistent.asyn())
            return this.doOperation(key, object, Operation.UPDATE, holder, true);
        return this.getSynchronizer(holder).update(object);
    }

    @Override
    public boolean save(String key, Object object) {
        AsynDBClassHolder holder = this.getAsynDBClassHolder(object.getClass());
        if (holder.persistent.asyn())
            return this.doOperation(key, object, Operation.SAVE, holder, false);
        return this.getSynchronizer(holder).save(object);
    }

    @Override
    public boolean delete(String key, Object object) {
        AsynDBClassHolder holder = this.getAsynDBClassHolder(object.getClass());
        if (holder.persistent.asyn())
            return this.doOperation(key, object, Operation.DELETE, holder, true);
        return this.getSynchronizer(holder).delete(object);
    }

    @Override
    public boolean flushAll() {
        this.entityMap.clear();
        return true;
    }

    private boolean doOperation(String key, Object object, Operation operation, AsynDBClassHolder holder, boolean checkNull) {
        AsyncDBEntity asyncDBEntity = this.getAsynDBEntity(key);
        if (asyncDBEntity == null || asyncDBEntity.isDelete()) {
            if (checkNull) {
                return false;
            } else {
                if (asyncDBEntity == null)
                    asyncDBEntity = this.getAndCreateAsynDBEntity(key, object, AsyncDBState.NORMAL, holder);
            }
        }
        Object currentObject = asyncDBEntity.getValue();
        if (!asyncDBEntity.isDelete() && !asyncDBEntity.isCanReplace() && asyncDBEntity.getValue() != object)
            throw new IllegalArgumentException(format("对象 [key:{}] 执行 {} , 但 当前对象 {} 与 更改对象 {} 不是同一个对象", key, operation, currentObject, object));
        try {
            LOGGER.debug("#LoaclAsynBDObjectPool#提交更改对象池中{} 状态: {} -> {}", asyncDBEntity, asyncDBEntity.getState(), operation);
            boolean submit = asyncDBEntity.mark(operation, object);
            if (submit) {
                LOGGER.debug("#LoaclAsynBDObjectPool#将要提交{}到同步队列", asyncDBEntity);
                boolean result = this.syncDBExecutor.sumit(asyncDBEntity);
                LOGGER.debug("#LoaclAsynBDObjectPool#提交{}[状态:{}]同步队列结果{}", asyncDBEntity, asyncDBEntity.getState(), submit);
                return result;
            }
            return true;
        } catch (AsyncDBReleaseException e) {
            // 对象被释放，尝试插入
            this.entityMap.remove(key, asyncDBEntity);
        } catch (SubmitAtWrongStateException e) {
            // 对象状态无法进行该操作
            LOGGER.error(MessageFormat.format("#LoaclAsynBDObjectPool#提交对象{0}[状态:{2}],进行{2}操作异常", object, e.getState(), e.getOperation()), e);
        }
        return false;
    }

    private AsyncDBEntity getAndCreateAsynDBEntity(String key, Object object, AsyncDBState asyncDBState, AsynDBClassHolder holder) {
        return this.getAndCreateAsynDBEntity(key, new AsyncDBEntity(object, this.getSynchronizer(holder), asyncDBState, this.releaseStrategyFactory.createStrategy(object, holder.persistent.lifeTime())));
    }

    private AsyncDBEntity getAndCreateAsynDBEntity(String key, AsyncDBEntity asyncDBEntity) {
        AsyncDBEntity existEntity = this.getAsynDBEntity(key);
        if (existEntity != null && !existEntity.isDelete()) {
            return existEntity;
        }
        while (true) {
            AsyncDBEntity oldAsyncDBEntity = this.entityMap.putIfAbsent(key, asyncDBEntity);
            if (oldAsyncDBEntity == null) {
                return asyncDBEntity;
            } else {
                // oldAsynDBEntity！=null表示有存在的实体，尝试访问实体，
                if (oldAsyncDBEntity.tryVisit()) {
                    return oldAsyncDBEntity;
                } else {
                    // 不成功表示被释放, 移出释放实体
                    this.entityMap.remove(key, oldAsyncDBEntity);
                }
            }
        }
    }

    private AsyncDBEntity getAsynDBEntity(String key) {
        AsyncDBEntity asyncDBEntity = this.entityMap.get(key);
        if (asyncDBEntity != null) {
            // 尝试反问对象，
            if (asyncDBEntity.visit() != null)
                return asyncDBEntity;
            // 失败表示对象被释放，移出实体
            this.entityMap.remove(key, asyncDBEntity);
            return null;
        }
        return null;
    }

    private AsynDBClassHolder getAsynDBClassHolder(Class<?> objectClass) {
        AsynDBClassHolder holder = this.asynDBClassHolderMap.get(objectClass);
        if (holder != null)
            return holder;
        holder = new AsynDBClassHolder(objectClass);
        AsynDBClassHolder oldHolder = this.asynDBClassHolderMap.putIfAbsent(objectClass, holder);
        return oldHolder == null ? holder : oldHolder;
    }

    private Synchronizer<Object> getSynchronizer(AsynDBClassHolder holder) {
        Synchronizer<Object> synchronizer = this.synchronizerHolder.getSynchronizer(holder.persistent.synchronizerClass());
        if (synchronizer == null)
            throw new NullPointerException(holder.objectClass + "'s " + holder.persistent.synchronizerClass() + " synchronizer is null");
        return synchronizer;
    }

    private static class AsynDBClassHolder {

        private final Persistent persistent;

        private final Class<?> objectClass;

        public AsynDBClassHolder(Class<?> objectClass) {
            this.objectClass = objectClass;
            this.persistent = objectClass.getAnnotation(Persistent.class);
            if (this.persistent == null)
                throw new NullPointerException(objectClass + " without @Persistent");
        }

    }

    @Override
    public void shutdown() throws InterruptedException {
        if (this.syncDBExecutor.shutdown()) {
            this.recycleExecutor.shutdownNow();
        }
    }
}
