package com.tny.game.suite.base;

import com.google.common.collect.*;
import com.tny.game.base.item.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.utils.*;
import com.tny.game.suite.base.annotation.*;
import org.springframework.beans.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import java.text.*;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({ITEM, GAME})
@SuppressWarnings({"rawtypes", "unchecked"})
public class GameExplorer implements ItemExplorer, OwnerExplorer, ModelExplorer, ApplicationContextAware, ServerPrepareStart {

    private Map<ItemType, GameManager<Object>> managerMap = ImmutableMap.of();

    private Map<ItemType, ModelManager<Model>> modelManagerMap = ImmutableMap.of();

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
            GameManager<Object> manager = this.getItemManager(item.getItemID());
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
            GameManager<Object> manager = this.getItemManager(item.getItemID());
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
            GameManager<Object> manager = this.getItemManager(item.getItemID());
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
            GameManager<Object> manager = this.getItemManager(item.getItemID());
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
    public <O extends Owner<?, ?>> O getOwner(long playerID, int itemID, Object... object) {
        GameManager<Object> manager = this.getOwnerManager(itemID);
        if (manager == null)
            return null;
        return (O) manager.get(playerID, object);
    }

    @Override
    public boolean insertOwner(Owner<?, ?>... owners) {
        boolean result = true;
        for (Owner<?, ?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getItemType().getID());
            if (manager == null)
                return true;
            if (!manager.insert(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?, ?>> Collection<O> insertOwner(
            Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.insertOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean updateOwner(Owner<?, ?>... owners) {
        boolean result = true;
        for (Owner<?, ?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getItemType().getID());
            if (manager == null)
                return true;
            if (!manager.update(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?, ?>> Collection<O> updateOwner(
            Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.updateOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean saveOwner(Owner<?, ?>... owners) {
        boolean result = true;
        for (Owner<?, ?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getItemType().getID());
            if (manager == null)
                return true;
            if (!manager.save(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?, ?>> Collection<O> saveOwner(Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.saveOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean deleteOwner(Owner<?, ?>... owners) {
        boolean result = true;
        for (Owner<?, ?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getItemType().getID());
            if (manager == null)
                return true;
            if (!manager.delete(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?, ?>> Collection<O> deleteOwner(Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.deleteOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    private GameManager<Object> getOwnerManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemID(itemID);
        GameManager<Object> manager = this.managerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取{0}事物的owner manager 为null", itemType));
        return manager;
    }

    private GameManager<Object> getItemManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemID(itemID);
        GameManager<Object> manager = this.managerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取{0}事物的item manager 为null", itemType));
        return manager;
    }

    private ModelManager<Model> getModelManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemID(itemID);
        return this.getModelManager(itemType);
    }

    @Override
    public <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType) {
        ModelManager<Model> manager = this.modelManagerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取{0}事物的model manager 为null", itemType));
        return (M) manager;
    }

    private ModelManager<Model> getModelManager(String alias) {
        ItemType itemType = ItemTypes.ofAlias(alias);
        ModelManager<Model> manager = this.modelManagerMap.get(itemType);
        if (manager == null)
            throw new NullPointerException(MessageFormat.format("获取{0}事物的model manager 为null", itemType));
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
        Map<ItemType, GameManager<Object>> managerMap = new HashMap<>();
        Map<ItemType, ModelManager<Model>> modelManagerMap = new HashMap<>();
        for (GameManager<Object> manager : map.values()) {
            putMananger(managerMap, manager);
        }
        Map<String, ModelManager> modelMap = this.applicationContext.getBeansOfType(ModelManager.class);
        for (ModelManager<Model> manager : modelMap.values()) {
            putMananger(modelManagerMap, manager);
        }
        this.managerMap = ImmutableMap.copyOf(managerMap);
        this.modelManagerMap = ImmutableMap.copyOf(modelManagerMap);
    }

    public <M> void putMananger(Map<ItemType, M> managerMap, M manager) {
        Class<?> mClass = manager.getClass();
        ManageItemType manageItemType = mClass.getAnnotation(ManageItemType.class);
        if (manageItemType != null) {
            for (int id : manageItemType.value())
                putMananger(managerMap, ItemTypes.of(id), manager);
        }
        if (manager instanceof ItemTypeManageable) {
            ItemTypeManageable manageable = as(manager);
            for (ItemType itemType : manageable.manageTypes())
                putMananger(managerMap, itemType, manager);
        }

    }


    public <M> void putMananger(Map<ItemType, M> managerMap, ItemType itemType, M manager) {
        M oldManager = managerMap.putIfAbsent(itemType, manager);
        if (oldManager != null && oldManager != manager)
            throw new NullPointerException(Logs.format("{} 与 {} 管理着相同的ItemType {}", manager.getClass(), oldManager.getClass(), itemType));
    }


    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_10);
    }


}
