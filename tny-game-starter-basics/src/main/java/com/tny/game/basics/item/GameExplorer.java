/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.google.common.collect.ImmutableMap;
import com.tny.game.common.lifecycle.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GameExplorer implements ItemExplorer, StuffOwnerExplorer, ModelExplorer, ApplicationContextAware, AppPrepareStart {

    private Map<Class<?>, GameManager<Object>> managerMap = ImmutableMap.of();

    private Map<ItemType, GameManager<Object>> typeManagerMap = ImmutableMap.of();

    private Map<ItemType, ModelManager<Model>> typeModelManagerMap = ImmutableMap.of();

    private ApplicationContext applicationContext;

    private static GameExplorer EXPLORER;

    public GameExplorer() {
        if (EXPLORER == null) {
            EXPLORER = this;
        }
    }

    public static GameExplorer getInstance() {
        return EXPLORER;
    }

    @Override
    public <IM extends Model> IM getModel(int modelId) {
        return (IM) this.getModelManager(modelId).getModel(modelId);
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
    public <I extends Subject<?>> I getItem(long playerId, int modelId) {
        GameManager<Object> manager = this.getItemManager(modelId);
        return (I) manager.get(playerId, modelId);
    }

    @Override
    public <I extends Subject<?>> I getItem(AnyId anyId) {
        ItemType itemType = ItemTypes.ofItemId(anyId.getId());
        GameManager<Object> manager = this.typeManagerMap.get(itemType);
        return (I) manager.get(anyId);
    }

    public <I> I getItem(Class<? extends Manager<?>> managerClass, AnyId anyId) {
        GameManager<I> manager = this.getManager(managerClass);
        return manager.get(anyId);
    }

    public <I> List<I> getItems(Class<? extends Manager<?>> managerClass, Collection<AnyId> anyIds) {
        GameManager<I> manager = this.getManager(managerClass);
        return manager.get(anyIds);
    }

    @Override
    public boolean insertItem(Subject<?>... items) {
        boolean result = true;
        for (Subject<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getModelId());
            if (!manager.insert(item)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public <I extends Subject<?>> Collection<I> insertItem(
            Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.insertItem(item)) {
                fail.add(item);
            }
        }
        return fail;
    }

    @Override
    public boolean updateItem(Subject<?>... items) {
        boolean result = true;
        for (Subject<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getModelId());
            if (!manager.update(item)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public <I extends Subject<?>> Collection<I> updateItem(
            Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.updateItem(item)) {
                fail.add(item);
            }
        }
        return fail;
    }

    @Override
    public boolean saveItem(Subject<?>... items) {
        boolean result = true;
        for (Subject<?> item : items) {
            GameManager<Object> manager = this.getItemManager(item.getModelId());
            if (!manager.save(item)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public <I extends Subject<?>> Collection<I> saveItem(Collection<I> itemCollection) {
        Collection<I> fail = new LinkedList<>();
        for (I item : itemCollection) {
            if (!this.saveItem(item)) {
                fail.add(item);
            }
        }
        return fail;
    }

    @Override
    public void deleteItem(Subject<?>... items) {
        for (Subject<?> item : items) {
            GameManager<Object> manager;
            manager = this.getItemManager(item.getModelId());
            manager.delete(item);
        }
    }

    @Override
    public <I extends Subject<?>> void deleteItem(Collection<I> itemCollection) {
        for (I item : itemCollection) {
            this.deleteItem(item);
        }
    }

    @Override
    public <O extends StuffOwner<?, ?>> O getOwner(long playerId, ItemType ownerType) {
        StuffOwnerManger<O> manager = this.getOwnerManager(ownerType);
        return manager.getOwner(playerId);
    }

    @Override
    public boolean insertOwner(StuffOwner<?, ?>... ownerArray) {
        boolean result = true;
        for (StuffOwner<?, ?> owner : ownerArray) {
            StuffOwnerManger<StuffOwner<?, ?>> manager = this.getOwnerManager(owner.getItemType());
            if (manager.insert(owner)) {
                continue;
            }
            result = false;
        }
        return result;
    }

    @Override
    public <O extends StuffOwner<?, ?>> Collection<O> insertOwner(
            Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (this.insertOwner(owner)) {
                continue;
            }
            fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean updateOwner(StuffOwner<?, ?>... ownerArray) {
        boolean result = true;
        for (StuffOwner<?, ?> owner : ownerArray) {
            StuffOwnerManger<StuffOwner<?, ?>> manager = this.getOwnerManager(owner.getItemType());
            if (manager.update(owner)) {
                continue;
            }
            result = false;
        }
        return result;
    }

    @Override
    public <O extends StuffOwner<?, ?>> Collection<O> updateOwner(
            Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (this.updateOwner(owner)) {
                continue;
            }
            fail.add(owner);
        }
        return fail;
    }

    @Override
    public boolean saveOwner(StuffOwner<?, ?>... ownerArray) {
        boolean result = true;
        for (StuffOwner<?, ?> owner : ownerArray) {
            StuffOwnerManger<StuffOwner<?, ?>> manager = this.getOwnerManager(owner.getItemType());
            if (!manager.save(owner)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public <O extends StuffOwner<?, ?>> Collection<O> saveOwner(Collection<O> ownerCollection) {
        Collection<O> fail = new LinkedList<>();
        for (O owner : ownerCollection) {
            if (this.saveOwner(owner)) {
                continue;
            }
            fail.add(owner);
        }
        return fail;
    }

    @Override
    public void deleteOwner(StuffOwner<?, ?>... ownerArray) {
        for (StuffOwner<?, ?> owner : ownerArray) {
            StuffOwnerManger<StuffOwner<?, ?>> manager = this.getOwnerManager(owner.getItemType());
            manager.delete(owner);
        }
    }

    @Override
    public <O extends StuffOwner<?, ?>> void deleteOwner(Collection<O> ownerCollection) {
        for (O owner : ownerCollection) {
            this.deleteOwner(owner);
        }
    }

    private <O extends StuffOwner<?, ?>> StuffOwnerManger<O> getOwnerManager(ItemType itemType) {
        GameManager<Object> manager = this.typeManagerMap.get(itemType);
        if (manager == null) {
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的owner manager 为null", itemType));
        }
        if (manager instanceof StuffOwnerManger) {
            return as(manager);
        }
        throw new IllegalArgumentException(MessageFormat.format("获取 {0} 事物的owner manager {} 未实现 {}",
                itemType, manager.getClass(), StuffOwnerManger.class));
    }

    private GameManager<Object> getItemManager(int modelId) {
        ItemType itemType = ItemTypes.ofModelId(modelId);
        GameManager<Object> manager = this.typeManagerMap.get(itemType);
        if (manager == null) {
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的item manager 为null", itemType));
        }
        return manager;
    }

    private GameManager<Object> getItemManager(long id) {
        ItemType itemType = ItemTypes.ofItemId(id);
        GameManager<Object> manager = this.typeManagerMap.get(itemType);
        if (manager == null) {
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的item manager 为null", itemType));
        }
        return manager;
    }

    private ModelManager<Model> getModelManager(int modelId) {
        ItemType itemType = ItemTypes.ofModelId(modelId);
        return this.getModelManager(itemType);
    }

    @Override
    public <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType) {
        ModelManager<Model> manager = this.typeModelManagerMap.get(itemType);
        if (manager == null) {
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的model manager 为null", itemType));
        }
        return (M) manager;
    }

    private ModelManager<Model> getModelManager(String alias) {
        ItemType itemType = ItemTypes.ofAlias(alias);
        ModelManager<Model> manager = this.typeModelManagerMap.get(itemType);
        if (manager == null) {
            throw new NullPointerException(MessageFormat.format("获取 {0} 事物的model manager 为null", itemType));
        }
        return manager;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public <T extends Object> GameManager<T> getManager(Class<? extends Manager<?>> clazz) {
        return as(this.managerMap.get(clazz));
    }

    @Override
    public void prepareStart() {
        Map<String, GameManager> map = this.applicationContext.getBeansOfType(GameManager.class);
        Map<Class<?>, GameManager<Object>> managerMap = new HashMap<>();
        Map<ItemType, GameManager<Object>> typeManagerMap = new HashMap<>();
        Map<ItemType, ModelManager<Model>> modelManagerMap = new HashMap<>();
        for (GameManager<Object> manager : map.values()) {
            putManager(typeManagerMap, manager);
            putManager(managerMap, manager.getClass(), manager);
        }
        Map<String, ModelManager> modelMap = this.applicationContext.getBeansOfType(ModelManager.class);
        for (ModelManager<Model> manager : modelMap.values()) {
            putManager(modelManagerMap, manager);
        }
        this.managerMap = ImmutableMap.copyOf(managerMap);
        this.typeManagerMap = ImmutableMap.copyOf(typeManagerMap);
        this.typeModelManagerMap = ImmutableMap.copyOf(modelManagerMap);
    }

    private <M> void putManager(Map<ItemType, M> managerMap, M manager) {
        Set<ItemType> types = manageItemTypes(manager);
        for (ItemType type : types) {
            putManager(managerMap, type, manager);
        }
    }

    private Set<ItemType> manageItemTypes(Object manager) {
        Set<ItemType> managerItemTypes = new HashSet<>();
        if (manager instanceof ItemTypesManager) {
            ItemTypesManager manage = as(manager);
            managerItemTypes.addAll(manage.manageTypes());
        }
        return managerItemTypes;
    }

    public Set<ItemType> getManageItemTypes(GameManager<?> manager) {
        return manageItemTypes(manager);
    }

    public Set<ItemType> getManageItemTypes(ModelManager<?> manager) {
        return manageItemTypes(manager);
    }

    private <K, M> void putManager(Map<K, M> managerMap, K itemType, M manager) {
        M oldManager = managerMap.putIfAbsent(itemType, manager);
        if (oldManager != null && oldManager != manager) {
            throw new IllegalArgumentException(format("{} 与 {} 管理着相同的ItemType {}", manager.getClass(), oldManager.getClass(), itemType));
        }
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_6);
    }

}
