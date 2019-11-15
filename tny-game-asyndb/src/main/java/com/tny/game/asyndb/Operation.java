package com.tny.game.asyndb;

import java.util.*;

/**
 * 持久化操作
 *
 * @author KGTny
 */
public enum Operation {

    /**
     * 插入数据
     */
    INSERT(new AsyncDBState[][]{
            // NL
            {AsyncDBState.DELETE, AsyncDBState.UPDATE}, // NL
            {AsyncDBState.DELETED, AsyncDBState.INSERT} // NL
    }, new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.INSERT}, // NL
            {AsyncDBState.DELETED, AsyncDBState.DELETED}, // NL
            {AsyncDBState.UPDATE, AsyncDBState.INSERT}, // NL
            {AsyncDBState.SAVE, AsyncDBState.INSERT}, // NL insert失败的时候 当前状态是save的时候 是要insert 还是要 save
            {AsyncDBState.DELETE, AsyncDBState.DELETED}, // NL
    }),

    /**
     * 更新数据
     */
    UPDATE(new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.UPDATE}, // NL
            {AsyncDBState.INSERT, null}, // NL
            {AsyncDBState.SAVE, null}, // NL
            {AsyncDBState.UPDATE, null}
    }, new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.UPDATE}, // NL
            {AsyncDBState.DELETED, AsyncDBState.DELETED}, // NL
            {AsyncDBState.UPDATE, AsyncDBState.UPDATE}, // NL
            {AsyncDBState.SAVE, AsyncDBState.UPDATE}, // NL
            {AsyncDBState.DELETE, AsyncDBState.DELETE}, // NL
    }),

    /**
     * 保存数据
     */
    SAVE(new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.SAVE}, // NL
            {AsyncDBState.INSERT, AsyncDBState.SAVE}, // NL
            {AsyncDBState.DELETE, AsyncDBState.SAVE}, // NL
            {AsyncDBState.UPDATE, AsyncDBState.SAVE},// NL
            {AsyncDBState.DELETED, AsyncDBState.SAVE}, // NL
            {AsyncDBState.SAVE, null}
    }, new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.SAVE}, // NL
            {AsyncDBState.DELETED, AsyncDBState.DELETED}, // NL
            {AsyncDBState.UPDATE, AsyncDBState.SAVE}, // NL
            {AsyncDBState.SAVE, AsyncDBState.SAVE}, // NL
            {AsyncDBState.DELETE, AsyncDBState.DELETE}, // NL
    }),

    /**
     * 删除数据
     */
    DELETE(new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.DELETE}, // NL
            {AsyncDBState.INSERT, AsyncDBState.DELETE}, // NL
            {AsyncDBState.SAVE, AsyncDBState.DELETE}, // NL
            {AsyncDBState.UPDATE, AsyncDBState.DELETE}, // NL
            {AsyncDBState.DELETE, null}, // NL
            {AsyncDBState.DELETED, null}
    }, new AsyncDBState[][]{
            // NL
            {AsyncDBState.NORMAL, AsyncDBState.DELETE}, // NL
            {AsyncDBState.DELETED, AsyncDBState.DELETED}, // NL
            {AsyncDBState.INSERT, AsyncDBState.SAVE}, // NL
            {AsyncDBState.SAVE, AsyncDBState.SAVE}, // NL
            {AsyncDBState.DELETE, AsyncDBState.DELETE}, // NL
    });

    private final Map<AsyncDBState, AsyncDBState> OPERATION_CHANGE_MAP;
    private final Map<AsyncDBState, AsyncDBState> FAILED_REDO_MAP;

    Operation(AsyncDBState[][] optionChangeState, AsyncDBState[][] failedRedoState) {
        Map<AsyncDBState, AsyncDBState> OPERATION_CHANGE_MAP = new HashMap<>();
        for (AsyncDBState[] states : optionChangeState) {
            OPERATION_CHANGE_MAP.put(states[0], states[1]);
        }
        this.OPERATION_CHANGE_MAP = Collections.unmodifiableMap(OPERATION_CHANGE_MAP);
        Map<AsyncDBState, AsyncDBState> FAILED_REDO_MAP = new HashMap<>();
        for (AsyncDBState[] states : failedRedoState) {
            FAILED_REDO_MAP.put(states[0], states[1]);
        }
        this.FAILED_REDO_MAP = Collections.unmodifiableMap(FAILED_REDO_MAP);
    }

    public boolean isCanOperationAt(AsyncDBState currentState) {
        return this.OPERATION_CHANGE_MAP.containsKey(currentState);
    }

    public AsyncDBState getChangeTo(AsyncDBState currentState) {
        AsyncDBState newState = this.OPERATION_CHANGE_MAP.get(currentState);
        if (newState == null)
            return currentState;
        return newState;
    }

    public boolean isCanFailRedoAt(AsyncDBState currentState) {
        return this.FAILED_REDO_MAP.containsKey(currentState);
    }

    public AsyncDBState getFailRedoTo(AsyncDBState currentState) {
        return this.FAILED_REDO_MAP.get(currentState);
    }
}
