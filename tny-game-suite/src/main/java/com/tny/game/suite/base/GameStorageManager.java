package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.suite.base.exception.*;
import org.slf4j.*;

import java.util.*;

/**
 * 存储当前存储的Item存储在一个Storage上的Manager
 *
 * @param <O>
 * @author KGTny
 */
public abstract class GameStorageManager<O> extends GameCacheManager<O> {

    public static final Logger LOGGER = LoggerFactory.getLogger(GameStorageManager.class);

    private Class<?>[] saveItemClasses;

    private ItemType itemType;

    /**
     * @param entityClass      Storage类
     * @param itemType         itemType
     * @param otherSaveClasses 通过Storage持久化的其他对象Class
     *                         如 AStorage class 管理 BItem, AItem持久化于AStorage
     *                         可通过 配置AItem class @{@link com.tny.game.suite.auto.persistent.annotation.AutoDBBy} manager = AStorageManager.class
     *                         通过AStorageManager 自动持久化
     */
    protected GameStorageManager(Class<? extends O> entityClass, ItemType itemType, Class<?>... otherSaveClasses) {
        super(entityClass);
        this.itemType = itemType;
        this.saveItemClasses = otherSaveClasses;
    }

    /**
     * @param entityClass      Storage类
     * @param itemType         itemType
     * @param otherSaveClasses 通过Storage持久化的其他对象Class
     *                         如 AStorage class 管理 BItem, AItem持久化于AStorage
     *                         可通过 配置AItem class @{@link com.tny.game.suite.auto.persistent.annotation.AutoDBBy} manager = AStorageManager.class
     *                         通过AStorageManager 自动持久化
     */
    protected GameStorageManager(Class<? extends O> entityClass, ItemType itemType, Collection<? extends Class<?>> otherSaveClasses) {
        super(entityClass);
        this.itemType = itemType;
        this.saveItemClasses = otherSaveClasses.toArray(this.saveItemClasses);
    }

    public O getStorage(long playerId) {
        if (playerId == 0L)
            return null;
        return this.get(playerId);
    }

    @Override
    protected O get(long playerId, Object... object) {
        return super.get(playerId, this.itemType.getId());
    }

    @Override
    public boolean save(O item) {
        if (this.entityClass.isInstance(item))
            return super.save(item);
        if (!this.isSaveItem(item)) {
            LOGGER.error("{} 无法存储 {} 类型对象", this.getClass(), item.getClass(), new WrongClassException());
            return false;
        }
        if (item instanceof Item) {
            O storage = this.getStorage(((Item<?>) item).getPlayerId());
            if (storage != null)
                return super.save(storage);
        }
        return false;
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        Collection<O> saveCollection = new ArrayList<>();
        for (O item : itemCollection) {
            if (this.entityClass.isInstance(item))
                saveCollection.add(item);
            if (!this.isSaveItem(item)) {
                LOGGER.error("{} 无法存储 {} 类型对象", this.getClass(), item.getClass(), new WrongClassException());
                continue;
            }
            if (item instanceof Item) {
                O storage = this.getStorage(((Item<?>) item).getPlayerId());
                if (storage != null)
                    saveCollection.add(storage);
            }
        }
        return super.save(saveCollection);
    }

    private boolean isSaveItem(Object object) {
        for (Class<?> clazz : this.saveItemClasses) {
            if (clazz.isInstance(object))
                return true;
        }
        return false;
    }

}
