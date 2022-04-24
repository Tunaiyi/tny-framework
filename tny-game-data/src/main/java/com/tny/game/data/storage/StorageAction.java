package com.tny.game.data.storage;

import com.tny.game.common.utils.*;
import com.tny.game.data.exception.*;

import java.util.*;

/**
 * 持久化操作
 *
 * @author KGTny
 */
public enum StorageAction {

    /**
     * 插入数据
     */
    INSERT(StorageOperator.INSERT,
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.DELETE, StorageOperator.UPDATE}, // NL
                    {StorageOperator.DELETED, StorageOperator.INSERT} // NL
            },
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.INSERT}, // NL
                    {StorageOperator.DELETED, StorageOperator.DELETED}, // NL
                    {StorageOperator.UPDATE, StorageOperator.INSERT}, // NL
                    {StorageOperator.SAVE, StorageOperator.INSERT}, // NL insert失败的时候 当前状态是save的时候 是要insert 还是要 save
                    {StorageOperator.DELETE, StorageOperator.DELETED}, // NL
            }),

    /**
     * 更新数据
     */
    UPDATE(StorageOperator.UPDATE,
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.UPDATE}, // NL
                    {StorageOperator.INSERT, StorageOperator.INSERT}, // NL
                    {StorageOperator.SAVE, StorageOperator.SAVE}, // NL
                    {StorageOperator.UPDATE, StorageOperator.UPDATE}
            },
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.UPDATE}, // NL
                    {StorageOperator.DELETED, StorageOperator.DELETED}, // NL
                    {StorageOperator.UPDATE, StorageOperator.UPDATE}, // NL
                    {StorageOperator.SAVE, StorageOperator.UPDATE}, // NL
                    {StorageOperator.DELETE, StorageOperator.DELETE}, // NL
            }),

    /**
     * 保存数据
     */
    SAVE(StorageOperator.SAVE,
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.SAVE}, // NL
                    {StorageOperator.INSERT, StorageOperator.SAVE}, // NL
                    {StorageOperator.DELETE, StorageOperator.SAVE}, // NL
                    {StorageOperator.UPDATE, StorageOperator.SAVE},// NL
                    {StorageOperator.DELETED, StorageOperator.SAVE}, // NL
                    {StorageOperator.SAVE, StorageOperator.SAVE}
            },
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.SAVE}, // NL
                    {StorageOperator.DELETED, StorageOperator.DELETED}, // NL
                    {StorageOperator.UPDATE, StorageOperator.SAVE}, // NL
                    {StorageOperator.SAVE, StorageOperator.SAVE}, // NL
                    {StorageOperator.DELETE, StorageOperator.DELETE}, // NL
            }),

    /**
     * 删除数据
     */
    DELETE(StorageOperator.DELETE,
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.DELETE}, // NL
                    {StorageOperator.INSERT, StorageOperator.DELETE}, // NL
                    {StorageOperator.SAVE, StorageOperator.DELETE}, // NL
                    {StorageOperator.UPDATE, StorageOperator.DELETE}, // NL
                    {StorageOperator.DELETE, StorageOperator.DELETE}, // NL
                    {StorageOperator.DELETED, null}
            },
            new StorageOperator[][]{
                    // NL
                    {StorageOperator.NORMAL, StorageOperator.DELETE}, // NL
                    {StorageOperator.DELETED, StorageOperator.DELETED}, // NL
                    {StorageOperator.INSERT, StorageOperator.SAVE}, // NL
                    {StorageOperator.SAVE, StorageOperator.SAVE}, // NL
                    {StorageOperator.DELETE, StorageOperator.DELETE}, // NL
            });

    private final StorageOperator defaultOperator;

    private final Map<StorageOperator, StorageOperator> operateStorageMap;

    private final Map<StorageOperator, StorageOperator> redoStorageMap;

    /**
     * @param defaultOperator    默认状态
     * @param operateChangeState 操作时候当前状态->操作状态 转换关系
     * @param failedRedoState    操作失败时候的状态->回滚状态 转换关系
     */
    <T> StorageAction(StorageOperator defaultOperator, StorageOperator[][] operateChangeState, StorageOperator[][] failedRedoState) {
        this.defaultOperator = defaultOperator;
        Map<StorageOperator, StorageOperator> OPERATION_CHANGE_MAP = new HashMap<>();
        for (StorageOperator[] states : operateChangeState) {
            OPERATION_CHANGE_MAP.put(states[0], states[1]);
        }
        this.operateStorageMap = Collections.unmodifiableMap(OPERATION_CHANGE_MAP);
        Map<StorageOperator, StorageOperator> FAILED_REDO_MAP = new HashMap<>();
        for (StorageOperator[] states : failedRedoState) {
            FAILED_REDO_MAP.put(states[0], states[1]);
        }
        this.redoStorageMap = Collections.unmodifiableMap(FAILED_REDO_MAP);
    }

    public StorageOperator getDefaultOperator() {
        return this.defaultOperator;
    }

    /**
     * 是否可以操作
     *
     * @param current 当前状态
     * @return 可以 true 否则 false
     */
    public boolean isCanOperate(StorageOperator current) {
        return this.operateStorageMap.containsKey(current);
    }

    /**
     * 根据当前状态获取操作对应的状态
     *
     * @param current 操作状态
     * @return 返回操作转换的状态
     */
    public StorageOperator changeFrom(StorageOperator current) {
        StorageOperator newState = this.operateStorageMap.get(current);
        Asserts.check(newState != null, new StoreOperateException("can not {} in {} state", this, current));
        return newState;
    }

    public boolean isCanFailRedo(StorageOperator current) {
        return this.redoStorageMap.containsKey(current);
    }

    public StorageOperator failRedoFrom(StorageOperator current) {
        return this.redoStorageMap.get(current);
    }

}
