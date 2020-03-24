package com.tny.game.data.storage;

import com.tny.game.common.utils.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.exception.*;

import java.util.*;
import java.util.function.BiFunction;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 持久化操作
 *
 * @author KGTny
 */
public enum StorageOperation {

    NONE(StorageState.NORMAL, new StorageState[][]{}, new StorageState[][]{}, null, null),

    /**
     * 插入数据
     */
    INSERT(StorageState.INSERT,
            new StorageState[][]{
                    // NL
                    {StorageState.DELETE, StorageState.UPDATE}, // NL
                    {StorageState.DELETED, StorageState.INSERT} // NL
            },
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.INSERT}, // NL
                    {StorageState.DELETED, StorageState.DELETED}, // NL
                    {StorageState.UPDATE, StorageState.INSERT}, // NL
                    {StorageState.SAVE, StorageState.INSERT}, // NL insert失败的时候 当前状态是save的时候 是要insert 还是要 save
                    {StorageState.DELETE, StorageState.DELETED}, // NL
            },
            ObjectAccessor::insert,
            ObjectAccessor::insert),

    /**
     * 更新数据
     */
    UPDATE(StorageState.UPDATE,
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.UPDATE}, // NL
                    {StorageState.INSERT, null}, // NL
                    {StorageState.SAVE, null}, // NL
                    {StorageState.UPDATE, null}
            },
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.UPDATE}, // NL
                    {StorageState.DELETED, StorageState.DELETED}, // NL
                    {StorageState.UPDATE, StorageState.UPDATE}, // NL
                    {StorageState.SAVE, StorageState.UPDATE}, // NL
                    {StorageState.DELETE, StorageState.DELETE}, // NL
            },
            ObjectAccessor::update,
            ObjectAccessor::update),

    /**
     * 保存数据
     */
    SAVE(StorageState.SAVE,
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.SAVE}, // NL
                    {StorageState.INSERT, StorageState.SAVE}, // NL
                    {StorageState.DELETE, StorageState.SAVE}, // NL
                    {StorageState.UPDATE, StorageState.SAVE},// NL
                    {StorageState.DELETED, StorageState.SAVE}, // NL
                    {StorageState.SAVE, null}
            },
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.SAVE}, // NL
                    {StorageState.DELETED, StorageState.DELETED}, // NL
                    {StorageState.UPDATE, StorageState.SAVE}, // NL
                    {StorageState.SAVE, StorageState.SAVE}, // NL
                    {StorageState.DELETE, StorageState.DELETE}, // NL
            },
            ObjectAccessor::save,
            ObjectAccessor::save),

    /**
     * 删除数据
     */
    DELETE(StorageState.DELETED,
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.DELETE}, // NL
                    {StorageState.INSERT, StorageState.DELETE}, // NL
                    {StorageState.SAVE, StorageState.DELETE}, // NL
                    {StorageState.UPDATE, StorageState.DELETE}, // NL
                    {StorageState.DELETE, null}, // NL
                    {StorageState.DELETED, null}
            },
            new StorageState[][]{
                    // NL
                    {StorageState.NORMAL, StorageState.DELETE}, // NL
                    {StorageState.DELETED, StorageState.DELETED}, // NL
                    {StorageState.INSERT, StorageState.SAVE}, // NL
                    {StorageState.SAVE, StorageState.SAVE}, // NL
                    {StorageState.DELETE, StorageState.DELETE}, // NL
            },
            ObjectAccessor::delete,
            ObjectAccessor::delete);

    private final StorageState defaultStatus;

    private final Map<StorageState, StorageState> operateStorageStateMap;

    private final Map<StorageState, StorageState> redoStorageStateMap;

    private final BiFunction<ObjectAccessor<?, ?>, Object, Boolean> singleOperate;

    private final BiFunction<ObjectAccessor<?, ?>, Collection<?>, Collection<?>> multiOperate;

    /**
     * @param defaultStatus      默认状态
     * @param operateChangeState 操作时候当前状态->操作状态转换关系
     */
    <T> StorageOperation(StorageState defaultStatus, StorageState[][] operateChangeState, StorageState[][] failedRedoState,
            BiFunction<ObjectAccessor<?, T>, T, Boolean> singleOperate,
            BiFunction<ObjectAccessor<?, T>, Collection<T>, Collection<T>> multiOperate) {
        this.defaultStatus = defaultStatus;
        Map<StorageState, StorageState> OPERATION_CHANGE_MAP = new HashMap<>();
        for (StorageState[] states : operateChangeState) {
            OPERATION_CHANGE_MAP.put(states[0], states[1]);
        }
        this.operateStorageStateMap = Collections.unmodifiableMap(OPERATION_CHANGE_MAP);
        Map<StorageState, StorageState> FAILED_REDO_MAP = new HashMap<>();
        for (StorageState[] states : failedRedoState) {
            FAILED_REDO_MAP.put(states[0], states[1]);
        }
        this.redoStorageStateMap = Collections.unmodifiableMap(FAILED_REDO_MAP);
        this.singleOperate = as(singleOperate);
        this.multiOperate = as(multiOperate);
    }

    public StorageState getDefaultStatus() {
        return this.defaultStatus;
    }

    /**
     * 是否可以操作
     *
     * @param currentState 当前状态
     * @return 可以 true 否则 false
     */
    public boolean isCanOperate(StorageState currentState) {
        return this.operateStorageStateMap.containsKey(currentState);
    }

    /**
     * 根据当前状态获取操作对应的状态
     *
     * @param state 当前状态
     * @return 返回操作转换的状态
     */
    public StorageState getOperateState(StorageState state) {
        StorageState newState = this.operateStorageStateMap.get(state);
        ThrowAide.check(newState != null, new StoreOperateException("can not {} in {} state", this, state));
        return newState;
    }

    public boolean isCanFailRedoAt(StorageState currentState) {
        return this.redoStorageStateMap.containsKey(currentState);
    }

    public StorageState getFailRedoTo(StorageState currentState) {
        return this.redoStorageStateMap.get(currentState);
    }

    public boolean isNeedOperate() {
        return this.multiOperate != null && this.singleOperate != null;
    }

    public <O> boolean operate(ObjectAccessor<?, O> accessor, O object) {
        if (this.singleOperate == null)
            return true;
        return this.singleOperate.apply(accessor, object);
    }

    public <O> Collection<O> operate(ObjectAccessor<?, O> accessor, Collection<O> objects) {
        if (this.multiOperate == null)
            return Collections.emptyList();
        return as(this.multiOperate.apply(accessor, objects));
    }

}
