package com.tny.game.asyndb;

import com.tny.game.asyndb.log.*;
import org.slf4j.*;

import java.util.*;

/**
 * 异步持久化实体状态枚举
 *
 * @author KGTny
 */
@SuppressWarnings("unchecked")
public enum AsyncDBState {

    /**
     * 正常状态
     */
    NORMAL(),

    DELETED,

    /**
     * 更新状态 属于提交持久化状态
     */
    UPDATE() {
        @Override
        public boolean doOperation(Synchronizer<?> synchronizer, Object object) {
            return ((Synchronizer<Object>)synchronizer).update(object);
        }

        @Override
        public Operation getOperation() {
            return Operation.UPDATE;
        }

        @Override
        public Collection<Object> doOperation(Synchronizer<?> synchronizer, Collection<Object> objects) {
            return ((Synchronizer<Object>)synchronizer).update(objects);
        }
    },

    /**
     * 插入状态 属于提交持久化状态
     */
    INSERT() {
        @Override
        public boolean doOperation(Synchronizer<?> synchronizer, Object object) {
            if (!((Synchronizer<Object>)synchronizer).insert(object)) {
                LOGGER.warn("{} 对象插入失败", object);
            }
            return true;
        }

        @Override
        public Operation getOperation() {
            return Operation.INSERT;
        }

        @Override
        public Collection<Object> doOperation(Synchronizer<?> synchronizer, Collection<Object> objects) {
            Collection<Object> result = ((Synchronizer<Object>)synchronizer).insert(objects);
            if (!result.isEmpty()) {
                for (Object object : objects)
                    LOGGER.warn("{} 对象插入失败", object);
            }
            return Collections.emptyList();
        }
    },

    /**
     * 保存状态 属于提交持久化状态
     */
    SAVE() {
        @Override
        public boolean doOperation(Synchronizer<?> synchronizer, Object object) {
            return ((Synchronizer<Object>)synchronizer).save(object);
        }

        @Override
        public Operation getOperation() {
            return Operation.SAVE;
        }

        @Override
        public Collection<Object> doOperation(Synchronizer<?> synchronizer, Collection<Object> objects) {
            return ((Synchronizer<Object>)synchronizer).save(objects);
        }
    },

    /**
     * 删除状态 属于提交持久化状态
     */
    DELETE() {
        @Override
        public boolean doOperation(Synchronizer<?> synchronizer, Object object) {
            return ((Synchronizer<Object>)synchronizer).delete(object);
        }

        @Override
        public Operation getOperation() {
            return Operation.DELETE;
        }

        @Override
        public Collection<Object> doOperation(Synchronizer<?> synchronizer, Collection<Object> objects) {
            return ((Synchronizer<Object>)synchronizer).delete(objects);
        }
    };

    private final static Logger LOGGER = LoggerFactory.getLogger(LogName.SYNC_DB_EXECUTOR);

    /**
     * 持久化 对象
     *
     * @param synchronizer 持久器
     * @param object       对象
     * @return 持久化成功返回true 持久化失败放回false
     */
    public boolean doOperation(Synchronizer<?> synchronizer, Object object) {
        return true;
    }

    public Collection<Object> doOperation(Synchronizer<?> synchronizer, Collection<Object> objects) {
        return Collections.emptyList();
    }

    public boolean isDelete() {
        return this == DELETE || this == DELETED;
    }

    public boolean hasOperation() {
        return this.getOperation() != null;
    }

    public Operation getOperation() {
        return null;
    }

}
