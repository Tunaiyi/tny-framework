package com.tny.game.suite.base;

import com.google.common.collect.ImmutableMap;
import com.tny.game.base.item.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.suite.base.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

import static com.tny.game.base.item.ItemType.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM, GAME})
@SuppressWarnings({"rawtypes", "unchecked"})
public class GameExplorer implements ItemExplorer, StorageExplorer, ModelExplorer, ApplicationContextAware, AppPrepareStart {

    private Map<Class<?>, GameManager<Object>> managerMap = ImmutableMap.of();

    private Map<ItemType, GameManager<Object>> typeStorageManagerMap = ImmutableMap.of();

    private Map<ItemType, GameManager<Object>> typeManagerMap = ImmutableMap.of();

    private Map<ItemType, ModelManager<Model>> typeModelManagerMap = ImmutableMap.of();

    private ApplicationContext applicationContext;

    private static GameExplorer EXPLORER;

    public GameExplorer() {
        if (EXPLORER == null)
            EXPLORER = this;
    }

    public static GameExplorer getInstance() {
        return EXPLORER;
    }

    @Override
    public <IM extends Model> IM getModel(int itemID) {
        return (IM) this.getModelManager(itemID).getModel(itemID);
    }

    @Override
    public <IM extends Model> IM getModelByAlias(String itemAlias) {
        return (IM) this.getModelManager(itemAlias).getModelByAlias(itemAlias);
    }

    @Override
    public boolean hasItemManager(ItemType itemType) {
        return this.typeManagerMap.containsKey(itemType);
    }

    @Override
    public <I extends Any<?>> I getItem(long playerID, int itemID, Object... object) {
        GameManager<Object> manager = this.getItemManager(itemID);
        if (manager == null)
            return null;
        Object[] params = new Object[object.length + 1];
        params[0] = itemID;
        if (object.length > 0)
            System.arraycopy(object, 0, params, 1, object.length);
        return (I) manager.get(playerID, params);
    }

    @Override
    public boolean insertItem(Any<?>... items) {
        boolean result = true;
        for (Any<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemId());
            if (manager == null)
                return true;
            if (!manager.insert(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Any<?>> Collection<I> insertItem(
            Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.insertItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public boolean updateItem(Any<?>... items) {
        boolean result = true;
        for (Any<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemId());
            if (manager == null)
                return true;
            if (!manager.update(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Any<?>> Collection<I> updateItem(
            Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.updateItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public boolean saveItem(Any<?>... items) {
        boolean result = true;
        for (Any<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemId());
            if (manager == null)
                return true;
            if (!manager.save(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Any<?>> Collection<I> saveItem(Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.saveItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public boolean deleteItem(Any<?>... items) {
        boolean result = true;
        for (Any<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemId());
            if (manager == null)
                return true;
            if (!manager.delete(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Any<?>> Collection<I> deleteItem(Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.deleteItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public <O extends Storage<?, ?>> O getStorage(long playerID, int itemID, Object... object) {
        GameManager<Object> manager = this.getStorageManager(itemID);
        if (manager == null)
            return null;
        return (O) manager.get(playerID, object);
    }

    @Override
    public boolean insertStorage(Storage<?, ?>... storageArray) {
        boolean result = true;
        for (Storage<?, ?> storage : storageArray) {
            GameManager<Object> manager = this.getStorageManager(storage.getItemId());
            if (manager == null)
                return true;
            if (!manager.insert(storage))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Storage<?, ?>> Collection<O> insertStorage(
            Collection<O> storageCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O storage : storageCollection) {
            if (!this.insertStorage(storage))
                fail.add(storage);
        }
        return fail;
    }

    @Override
    public boolean updateStorage(Storage<?, ?>... storageArray) {
        boolean result = true;
        for (Storage<?, ?> storage : storageArray) {
            GameManager<Object> manager = this.getStorageManager(storage.getItemId());
            if (manager == null)
                return true;
            if (!manager.update(storage))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Storage<?, ?>> Collection<O> updateStorage(
            Collection<O> storageCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O storage : storageCollection) {
            if (!this.updateStorage(storage))
                fail.add(storage);
        }
        return fail;
    }

    @Override
    public boolean saveStorage(Storage<?, ?>... storageArray) {
        boolean result = true;
        for (Storage<?, ?> storage : storageArray) {
            GameManager<Object> manager = this.getStorageManager(storage.getItemId());
            if (manager == null)
                return true;
            if (!manager.save(storage))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Storage<?, ?>> Collection<O> saveStorage(Collection<O> storageCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O storage : storageCollection) {
            if (!this.saveStorage(storage))
                fail.add(storage);
        }
        return fail;
    }

    @Override
    public boolean deleteStorage(Storage<?, ?>... storageArray) {
        boolean result = true;
        for (Storage<?, ?> storage : storageArray) {
            GameManager<Object> manager = this.getStorageManager(storage.getItemId());
            if (manager == null)
                return true;
            if (!manager.delete(storage))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Storage<?, ?>> Collection<O> deleteStorage(Collection<O> storageCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O storage : storageCollection) {
            if (!this.deleteStorage(storage))
                fail.add(storage);
        }
        return fail;
    }

    private GameManager<Object> getStorageManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemId(itemID);
        GameManager<Object> manager = this.typeStorageManagerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的storage manager 为null", itemType));
        return manager;
    }

    private GameManager<Object> getItemManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemId(itemID);
        GameManager<Object> manager = this.typeManagerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的item manager 为null", itemType));
        return manager;
    }

    private ModelManager<Model> getModelManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemId(itemID);
        return this.getModelManager(itemType);
    }

    @Override
    public <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType) {
        ModelManager<Model> manager = this.typeModelManagerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的model manager 为null", itemType));
        return (M) manager;
    }

    private ModelManager<Model> getModelManager(String alias) {
        ItemType itemType = ItemTypes.ofAlias(alias);
        ModelManager<Model> manager = this.typeModelManagerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的model manager 为null", itemType));
        return manager;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Manager<?> getManager(Class<?> clazz) {
        return this.managerMap.get(clazz);
    }

    @Override
    public void prepareStart() {
        Map<String, GameManager> map = this.applicationContext.getBeansOfType(GameManager.class);
        Map<Class<?>, GameManager<Object>> managerMap = new HashMap<>();
        Map<ItemType, GameManager<Object>> typeManagerMap = new HashMap<>();
        Map<ItemType, GameManager<Object>> typeStorageManagerMap = new HashMap<>();
        Map<ItemType, ModelManager<Model>> modelManagerMap = new HashMap<>();
        for (GameManager<Object> manager : map.values()) {
            putManager(typeManagerMap, manager);
            putManager(managerMap, manager.getClass(), manager);
            putOwnManager(typeStorageManagerMap, manager);
        }
        Map<String, ModelManager> modelMap = this.applicationContext.getBeansOfType(ModelManager.class);
        for (ModelManager<Model> manager : modelMap.values()) {
            putManager(modelManagerMap, manager);
        }
        this.managerMap = ImmutableMap.copyOf(managerMap);
        this.typeManagerMap = ImmutableMap.copyOf(typeManagerMap);
        this.typeStorageManagerMap = ImmutableMap.copyOf(typeStorageManagerMap);
        this.typeModelManagerMap = ImmutableMap.copyOf(modelManagerMap);
    }

    private <M> void putManager(Map<ItemType, M> managerMap, M manager) {
        Class<?> mClass = manager.getClass();
        ManageItemType manageItemType = mClass.getAnnotation(ManageItemType.class);
        if (manageItemType != null) {
            for (int id : manageItemType.value()) {
                if (id / ID_TAIL_SIZE == 0)
                    id *= ID_TAIL_SIZE;
                putManager(managerMap, ItemTypes.of(id), manager);
            }
        }
        if (manager instanceof ItemTypeManageable) {
            ItemTypeManageable manageable = as(manager);
            for (ItemType itemType : manageable.manageTypes())
                putManager(managerMap, itemType, manager);
        }
    }

    public Set<ItemType> getManageItemTypes(GameManager<?> manager) {
        return manageItemTypes(manager);
    }

    public Set<ItemType> getManageItemTypes(ModelManager<?> manager) {
        return manageItemTypes(manager);
    }

    private Set<ItemType> manageItemTypes(Object manager) {
        Set<ItemType> managerItemTypes = new HashSet<>();
        ManageItemType manageItemType = manager.getClass().getAnnotation(ManageItemType.class);
        if (manageItemType != null) {
            for (int id : manageItemType.value()) {
                if (id / ID_TAIL_SIZE == 0)
                    id *= ID_TAIL_SIZE;
                managerItemTypes.add(ItemTypes.of(id));
            }
        }
        if (manager instanceof ItemTypeManageable) {
            ItemTypeManageable manageable = as(manager);
            managerItemTypes.addAll(manageable.manageTypes());
        }
        return managerItemTypes;
    }

    private <M> void putOwnManager(Map<ItemType, M> managerMap, M manager) {
        Class<?> mClass = manager.getClass();
        OwnItemType ownItemType = mClass.getAnnotation(OwnItemType.class);
        if (ownItemType != null) {
            for (int id : ownItemType.value()) {
                if (id / ID_TAIL_SIZE == 0)
                    id *= ID_TAIL_SIZE;
                putManager(managerMap, ItemTypes.of(id), manager);
            }
        }
    }


    private <K, M> void putManager(Map<K, M> managerMap, K itemType, M manager) {
        M oldManager = managerMap.putIfAbsent(itemType, manager);
        if (oldManager != null && oldManager != manager)
            throw new IllegalArgumentException(format("{} 与 {} 管理着相同的ItemType {}", manager.getClass(), oldManager.getClass(), itemType));
    }


    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
    }


}
