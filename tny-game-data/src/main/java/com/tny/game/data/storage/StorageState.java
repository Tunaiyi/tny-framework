package com.tny.game.data.storage;

/**
 * 异步持久化实体状态枚举
 *
 * @author KGTny
 */
public enum StorageState {

    /**
     * 正常状态
     */
    NORMAL(StorageOperation.NONE),

    /**
     * 已删除
     */
    DELETED(StorageOperation.NONE),

    /**
     * 更新状态 属于提交持久化状态
     */
    UPDATE(StorageOperation.UPDATE),

    /**
     * 插入状态 属于提交持久化状态
     */
    INSERT(StorageOperation.INSERT),

    /**
     * 保存状态 属于提交持久化状态
     */
    SAVE(StorageOperation.SAVE),

    /**
     * 删除状态 属于提交持久化状态
     */
    DELETE(StorageOperation.DELETE),

    //
    ;

    private StorageOperation operation;

    StorageState(StorageOperation operation) {
        this.operation = operation;
    }

    public StorageOperation getOperation() {
        return operation;
    }
}
