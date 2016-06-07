package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.ServerPreStart;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
@Profile({"suite.base", "suite.all"})
@SuppressWarnings({"rawtypes", "unchecked"})
public class GameExplorer implements ItemExplorer, OwnerExplorer, ItemModelExplorer, ApplicationContextAware, ServerPreStart {

    private final Map<Class<?>, GameManager<Object>> managerMap = new HashMap<>();

    private final Map<Class<?>, ModelManager<ItemModel>> modelManagerMap = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public <IM extends ItemModel> IM getItemModel(int itemID) {
        return (IM) this.getModelManager(itemID).getModel(itemID);
    }

    @Override
    public <IM extends ItemModel> IM getItemModelByAlias(String itemAlias) {
        return (IM) this.getModelManager(itemAlias).getModelByAlias(itemAlias);
    }

    @Override
    public <I extends Item<?>> I getItem(long playerID, int itemID, Object... object) {
        Object[] params = new Object[object.length + 1];
        params[0] = itemID;
        if (object.length > 0)
            System.arraycopy(object, 0, params, 1, object.length);
        GameManager<Object> manager = this.getItemManager(itemID);
        if (manager == null)
            return null;
        return (I) manager.get(playerID, params);
    }

    @Override
    public boolean inserItem(Item<?>... items) {
        boolean result = true;
        for (Item<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemID());
            if (manager == null)
                return true;
            if (!manager.insert(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Item<?>> Collection<I> inserItem(
            Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.inserItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public boolean updateItem(Item<?>... items) {
        boolean result = true;
        for (Item<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemID());
            if (manager == null)
                return true;
            if (!manager.update(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Item<?>> Collection<I> updateItem(
            Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.updateItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public boolean saveItem(Item<?>... items) {
        boolean result = true;
        for (Item<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemID());
            if (manager == null)
                return true;
            if (!manager.save(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Item<?>> Collection<I> saveItem(Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.saveItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public boolean deleteItem(Item<?>... items) {
        boolean result = true;
        for (Item<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getItemID());
            if (manager == null)
                return true;
            if (!manager.delete(item))
                result = false;
        }
        return result;
    }

    @Override
    public <I extends Item<?>> Collection<I> deleteItem(Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.deleteItem(item))
                fail.add(item);
        }
        return fail;
    }

    @Override
    public <O extends Owner<?>> O getOwner(long playerID, int itemID, Object... object) {
        Object[] params = new Object[object.length];
        if (object.length > 0)
            System.arraycopy(object, 0, params, 0, object.length);
        GameManager<Object> manager = this.getOwnerManager(itemID);
        if (manager == null)
            return null;
        return (O) manager.get(playerID, params);
    }

    @Override
    public boolean insertOwner(Owner<?>... owners) {
        boolean result = true;
        for (Owner<?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getOwnerItemType().getID());
            if (manager == null)
                return true;
            if (!manager.insert(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?>> Collection<O> insertOwner(
            Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.insertOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean updateOwner(Owner<?>... owners) {
        boolean result = true;
        for (Owner<?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getOwnerItemType().getID());
            if (manager == null)
                return true;
            if (!manager.update(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?>> Collection<O> updateOwner(
            Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.updateOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean saveOwner(Owner<?>... owners) {
        boolean result = true;
        for (Owner<?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getOwnerItemType().getID());
            if (manager == null)
                return true;
            if (!manager.save(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?>> Collection<O> saveOwner(Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.saveOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean deleteOwner(Owner<?>... owners) {
        boolean result = true;
        for (Owner<?> owner : owners) {
            GameManager<Object> manager = this.getOwnerManager(owner.getOwnerItemType().getID());
            if (manager == null)
                return true;
            if (!manager.delete(owner))
                result = false;
        }
        return result;
    }

    @Override
    public <O extends Owner<?>> Collection<O> deleteOwner(Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (!this.deleteOwner(owner))
                fail.add(owner);
        }
        return fail;
    }

    protected void saveTradeChange(DealedResult tradeChange) {
        this.saveOwner(tradeChange.getChangeOwnerSet());
        this.saveItem(tradeChange.getChangeStuffSet());
    }

    private GameManager<Object> getOwnerManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemID(itemID);
        if (itemType.getOwnerManagerClass() == null)
            return null;
        GameManager<Object> manager = this.managerMap
                .get(itemType.getOwnerManagerClass());
        if (manager == null)
            throw new NullPointerException(MessageFormat.format(
                    "获取{0}事物的owner manager[{1}]为null", itemType,
                    itemType.getOwnerManagerClass()));
        return manager;
    }

    private GameManager<Object> getItemManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemID(itemID);
        if (itemType.getItemManagerClass() == null)
            return null;
        GameManager<Object> manager = this.managerMap
                .get(itemType.getItemManagerClass());
        if (manager == null)
            throw new NullPointerException(MessageFormat.format(
                    "获取{0}事物的item manager[{1}]为null", itemType,
                    itemType.getItemManagerClass()));
        return manager;
    }

    private ModelManager<ItemModel> getModelManager(int itemID) {
        ItemType itemType = ItemTypes.ofItemID(itemID);
        return this.getModelManager(itemType);
    }

    public ModelManager<ItemModel> getModelManager(ItemType itemType) {
        ModelManager<ItemModel> manager = this.modelManagerMap
                .get(itemType.getItemModelManagerClass());
        if (itemType.getItemModelManagerClass() == null)
            return null;
        if (manager == null)
            throw new NullPointerException(MessageFormat.format(
                    "获取{0}事物的model manager[{1}]为null", itemType,
                    itemType.getItemModelManagerClass()));
        return manager;
    }

    private ModelManager<ItemModel> getModelManager(String alias) {
        ItemType itemType = ItemTypes.ofAlias(alias);
        ModelManager<ItemModel> manager = this.modelManagerMap
                .get(itemType.getItemModelManagerClass());
        if (manager == null)
            throw new NullPointerException(MessageFormat.format(
                    "获取{0}事物的model manager[{1}]为null", itemType,
                    itemType.getItemModelManagerClass()));
        return manager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void insertObject(Warehouse warehouse) {

    }

    public Manager<?> getManager(Class<?> clazz) {
        return this.managerMap.get(clazz);
    }

    @Override
    public void initialize() {
        Map<String, GameManager> map = this.applicationContext.getBeansOfType(GameManager.class);
        for (GameManager<Object> manager : map.values()) {
            this.managerMap.put(manager.getClass(), manager);
        }
        Map<String, ModelManager> modelMap = this.applicationContext
                .getBeansOfType(ModelManager.class);
        for (ModelManager<ItemModel> manager : modelMap.values()) {
            this.modelManagerMap.put(manager.getClass(), manager);
        }
    }

    @Override
    public InitLevel getInitLevel() {
        return InitLevel.LEVEL_7;
    }

}
