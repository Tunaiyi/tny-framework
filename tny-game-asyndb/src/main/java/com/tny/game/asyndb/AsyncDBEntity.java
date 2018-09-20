package com.tny.game.asyndb;

import com.tny.game.asyndb.annotation.Replace;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 异步持久化实体
 *
 * @author KGTny
 */
public class AsyncDBEntity implements Synchronizable {

    //	private final static Logger LOGGER = LoggerFactory.getLogger(LogName.ASYN_DB);

    /**
     * 持久化对象
     */
    private volatile WeakReference<?> value;

    /**
     * 同步引用对象，防止被内存回收
     */
    @SuppressWarnings("unused")
    private volatile Object syncObject;

    /**
     * 持有对象，确保一定条件内不被回收
     */
    private Object holdObject;

    /**
     * 实体状态
     */
    private volatile AsyncDBState state;

    /**
     * 锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 释放策略
     */
    private ReleaseStrategy releaseStrategy;

    /**
     * 同步器
     */
    private Synchronizer<?> synchronizer;

    private Replace replace;

    AsyncDBEntity(Object value, Synchronizer<?> synchronizer, AsyncDBState state, ReleaseStrategy releaseStrategy) {
        if (value == null)
            throw new NullPointerException();
        this.setValue(value);
        this.holdObject = value;
        this.synchronizer = synchronizer;
        this.state = state;
        this.releaseStrategy = releaseStrategy;
        this.visit();
    }

    private void setValue(Object value) {
        this.replace = value.getClass().getAnnotation(Replace.class);
        this.value = new WeakReference<>(value);
    }

    /**
     * 尝试同步数据库
     *
     * @return 成功返回true
     */
    @Override
    public TrySyncDone trySync() {
        AsyncDBState currentState = this.state;
        if (currentState == AsyncDBState.NORMAL || currentState == AsyncDBState.DELETED)
            return TrySyncDone.FAIL;
        this.lock.lock();
        try {
            currentState = this.state;
            Object value = this.value.get();
            if (currentState == AsyncDBState.NORMAL || currentState == AsyncDBState.DELETED)
                return TrySyncDone.FAIL;
            this.state = currentState != AsyncDBState.DELETE ? AsyncDBState.NORMAL : AsyncDBState.DELETED;
            this.syncObject = null;
            return new TrySyncDone(currentState, value);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean syncFail(AsyncDBState currentState) {
        Operation operation = currentState.getOperation();
        if (operation == null)
            return false;
        AsyncDBState nowAsyncDBState = this.state;
        if (!operation.isCanFailRedoAt(nowAsyncDBState))
            return false;
        this.lock.lock();
        try {
            nowAsyncDBState = this.state;
            if (!operation.isCanFailRedoAt(nowAsyncDBState))
                return false;
            AsyncDBState redoState = operation.getFailRedoTo(nowAsyncDBState);
            if (redoState == null)
                return false;
            this.state = redoState;
            this.syncObject = this.getValue();
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * @param operation 操作
     * @param object    操作的对象
     * @return 返回是否需要提交
     * @throws AsyncDBReleaseException     对象已经施放时抛出异常
     * @throws SubmitAtWrongStateException 对象提交状态错误异常
     */
    boolean mark(Operation operation, Object object) throws AsyncDBReleaseException, SubmitAtWrongStateException {
        if (this.release(System.currentTimeMillis()))
            throw new AsyncDBReleaseException(format("[{}] submit exception", this.value));
        AsyncDBState currentState = this.state;
        // 判断操作是否能在当前状态操作
        if (!operation.isCanOperationAt(currentState))
            throw new SubmitAtWrongStateException(format("[{}] submit exception", this.value), currentState, operation);
        this.lock.lock();
        try {
            currentState = this.state;
            if (!operation.isCanOperationAt(currentState))
                throw new SubmitAtWrongStateException(format("[{}] submit exception", this.value), currentState, operation);
            Object currentObject = this.value.get();
            this.state = operation.getChangeTo(currentState, currentState);
            if ((currentState.isDelete() && object != null) ||
                    (this.isCanReplace() && currentObject != object)) {
                this.setValue(object);
                this.visit();
            }
            this.releaseStrategy.update();
            this.syncObject = this.value.get();
            return currentState == AsyncDBState.NORMAL || currentState == AsyncDBState.DELETED;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public Synchronizer<?> getSynchronizer() {
        return this.synchronizer;
    }

    /**
     * 尝试访问对象
     *
     * @return 访问成功返回true 失败为false
     */
    boolean tryVisit() {
        return this.visit() != null;
    }

    /**
     * 访问对象
     *
     * @return 返回生命值
     */
    Object visit() {
        Object returnObject = this.value.get();
        if (returnObject != null) {
            this.releaseStrategy.update();
            this.holdObject = returnObject;
        }
        return returnObject;
    }

    boolean isCanReplace() {
        return this.replace != null;
    }

    /**
     * 释放对象
     *
     * @return 释放成功返回true，失败返回false
     */
    boolean release(long releaseAt) {
        AsyncDBState currentState = this.state;
        Object object = this.holdObject;
        if ((object != null && currentState == AsyncDBState.NORMAL && this.releaseStrategy.release(this, releaseAt)) || currentState == AsyncDBState.DELETED) {
            this.holdObject = null;
            this.syncObject = null;
        }
        return this.value.get() == null;
    }

    @Override
    public Object getValue() {
        return this.value.get();
    }

    @Override
    public AsyncDBState getState() {
        return this.state;
    }

    public boolean isDelete() {
        AsyncDBState currentState = this.state;
        return currentState == AsyncDBState.DELETE || currentState == AsyncDBState.DELETED;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        AsyncDBEntity other = (AsyncDBEntity) obj;
        if (this.value == null) {
            if (other.value != null)
                return false;
        } else if (!this.value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        Object object = null;
        if (this.value != null) {
            object = this.value.get();
        }
        return "AsynDBEntity [state=" + this.state + "\t] [" + (object != null ? object.getClass().getSimpleName() : this.value) + "] [value=" + object + "]";
    }

}
