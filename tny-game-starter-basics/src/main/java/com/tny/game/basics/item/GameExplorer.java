package com.tny.game.basics.item;

import com.google.common.collect.ImmutableMap;
import com.tny.game.basics.item.annotation.*;
import com.tny.game.common.lifecycle.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.*;

import static com.tny.game.basics.item.ItemType.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GameExplorer implements ItemExplorer, StuffOwnerExplorer, ModelExplorer, ApplicationContextAware, AppPrepareStart {

	private Map<Class<?>, GameManager<Object>> managerMap = ImmutableMap.of();

	private Map<ItemType, GameManager<Object>> typeStorageManagerMap = ImmutableMap.of();

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
	public <IM extends Model> IM getModel(int itemID) {
		return (IM)this.getModelManager(itemID).getModel(itemID);
	}

	@Override
	public <IM extends Model> IM getModelByAlias(String itemAlias) {
		return (IM)this.getModelManager(itemAlias).getModelByAlias(itemAlias);
	}

	@Override
	public boolean hasItemManager(ItemType itemType) {
		return this.typeManagerMap.containsKey(itemType);
	}

	@Override
	public <I extends Entity<?>> I getItem(long playerId, int itemID) {
		GameManager<Object> manager = this.getItemManager(itemID);
		return (I)manager.get(playerId, itemID);
	}

	@Override
	public boolean insertItem(Entity<?>... items) {
		boolean result = true;
		for (Entity<?> item : items) {
			GameManager<Object> manager = this.getItemManager(item.getModelId());
			if (!manager.insert(item)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public <I extends Entity<?>> Collection<I> insertItem(
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
	public boolean updateItem(Entity<?>... items) {
		boolean result = true;
		for (Entity<?> item : items) {
			GameManager<Object> manager = this.getItemManager(item.getModelId());
			if (!manager.update(item)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public <I extends Entity<?>> Collection<I> updateItem(
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
	public boolean saveItem(Entity<?>... items) {
		boolean result = true;
		for (Entity<?> item : items) {
			GameManager<Object> manager = this.getItemManager(item.getModelId());
			if (!manager.save(item)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public <I extends Entity<?>> Collection<I> saveItem(Collection<I> itemCollection) {
		Collection<I> fail = new LinkedList<>();
		for (I item : itemCollection) {
			if (!this.saveItem(item)) {
				fail.add(item);
			}
		}
		return fail;
	}

	@Override
	public void deleteItem(Entity<?>... items) {
		for (Entity<?> item : items) {
			GameManager<Object> manager;
			manager = this.getItemManager(item.getModelId());
			manager.delete(item);
		}
	}

	@Override
	public <I extends Entity<?>> void deleteItem(Collection<I> itemCollection) {
		for (I item : itemCollection) {
			this.deleteItem(item);
		}
	}

	@Override
	public <O extends StuffOwner<?, ?>> O getStorage(long playerId, int itemID) {
		GameManager<Object> manager = this.getStorageManager(itemID);
		return (O)manager.get(playerId);
	}

	@Override
	public boolean insertStorage(StuffOwner<?, ?>... ownerArray) {
		boolean result = true;
		for (StuffOwner<?, ?> owner : ownerArray) {
			GameManager<Object> manager = this.getStorageManager(owner.getModelId());
			if (manager.insert(owner)) {
				continue;
			}
			result = false;
		}
		return result;
	}

	@Override
	public <O extends StuffOwner<?, ?>> Collection<O> insertStorage(
			Collection<O> ownerCollection) {
		Collection<O> fail = new LinkedList<>();
		for (O owner : ownerCollection) {
			if (this.insertStorage(owner)) {
				continue;
			}
			fail.add(owner);
		}
		return fail;
	}

	@Override
	public boolean updateStorage(StuffOwner<?, ?>... ownerArray) {
		boolean result = true;
		for (StuffOwner<?, ?> owner : ownerArray) {
			GameManager<Object> manager = this.getStorageManager(owner.getModelId());
			if (manager.update(owner)) {
				continue;
			}
			result = false;
		}
		return result;
	}

	@Override
	public <O extends StuffOwner<?, ?>> Collection<O> updateStorage(
			Collection<O> ownerCollection) {
		Collection<O> fail = new LinkedList<>();
		for (O owner : ownerCollection) {
			if (this.updateStorage(owner)) {
				continue;
			}
			fail.add(owner);
		}
		return fail;
	}

	@Override
	public boolean saveStorage(StuffOwner<?, ?>... ownerArray) {
		boolean result = true;
		for (StuffOwner<?, ?> owner : ownerArray) {
			GameManager<Object> manager = this.getStorageManager(owner.getModelId());
			if (!manager.save(owner)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public <O extends StuffOwner<?, ?>> Collection<O> saveStorage(Collection<O> ownerCollection) {
		Collection<O> fail = new LinkedList<>();
		for (O owner : ownerCollection) {
			if (this.saveStorage(owner)) {
				continue;
			}
			fail.add(owner);
		}
		return fail;
	}

	@Override
	public void deleteStorage(StuffOwner<?, ?>... ownerArray) {
		for (StuffOwner<?, ?> owner : ownerArray) {
			GameManager<Object> manager = this.getStorageManager(owner.getModelId());
			manager.delete(owner);
		}
	}

	@Override
	public <O extends StuffOwner<?, ?>> void deleteStorage(Collection<O> ownerCollection) {
		for (O owner : ownerCollection) {
			this.deleteStorage(owner);
		}
	}

	private GameManager<Object> getStorageManager(int itemID) {
		ItemType itemType = ItemTypes.ofItemId(itemID);
		GameManager<Object> manager = this.typeStorageManagerMap.get(itemType);
		if (manager == null) {
			throw new NullPointerException(MessageFormat.format("获取 {0} 事物的owner manager 为null", itemType));
		}
		return manager;
	}

	private GameManager<Object> getItemManager(int itemID) {
		ItemType itemType = ItemTypes.ofItemId(itemID);
		GameManager<Object> manager = this.typeManagerMap.get(itemType);
		if (manager == null) {
			throw new NullPointerException(MessageFormat.format("获取 {0} 事物的item manager 为null", itemType));
		}
		return manager;
	}

	private ModelManager<Model> getModelManager(int itemID) {
		ItemType itemType = ItemTypes.ofItemId(itemID);
		return this.getModelManager(itemType);
	}

	@Override
	public <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType) {
		ModelManager<Model> manager = this.typeModelManagerMap.get(itemType);
		if (manager == null) {
			throw new NullPointerException(MessageFormat.format("获取 {0} 事物的model manager 为null", itemType));
		}
		return (M)manager;
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
				if (id / ID_TAIL_SIZE == 0) {
					id *= ID_TAIL_SIZE;
				}
				putManager(managerMap, ItemTypes.check(id), manager);
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
				if (id / ID_TAIL_SIZE == 0) {
					id *= ID_TAIL_SIZE;
				}
				managerItemTypes.add(ItemTypes.check(id));
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
				if (id / ID_TAIL_SIZE == 0) {
					id *= ID_TAIL_SIZE;
				}
				putManager(managerMap, ItemTypes.check(id), manager);
			}
		}
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
