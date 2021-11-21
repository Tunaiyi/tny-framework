package com.tny.game.basics.item.xml;

import com.tny.game.basics.item.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class TempExplorer implements ItemExplorer, ModelExplorer {

	private Map<String, ItemModel> model = new HashMap<>();

	private Map<Integer, ItemModel> idModel = new HashMap<>();

	private Item<?> item;

	public TempExplorer(ItemModel model, Item<?> item) {
		this.item = item;
		this.model.put(model.getAlias(), model);
		this.idModel.put(model.getId(), model);
	}

	public TempExplorer(ItemModel... modelMap) {
		for (ItemModel model : modelMap) {
			this.model.put(model.getAlias(), model);
			this.idModel.put(model.getId(), model);
		}
	}

	@Override
	public <IM extends Model> IM getModel(int itemId) {
		return (IM)this.idModel.get(itemId);
	}

	@Override
	public <IM extends Model> IM getModelByAlias(String itemAlias) {
		return (IM)this.model.get(itemAlias);
	}

	@Override
	public <M extends ModelManager<? extends Model>> M getModelManager(ItemType itemType) {
		return null;
	}

	@Override
	public boolean hasItemManager(ItemType itemType) {
		return true;
	}

	@Override
	public <I extends Entity<?>> I getItem(long playerId, int id) {
		return null;
	}

	@Override
	public boolean insertItem(Entity<?>[] items) {
		return false;
	}

	@Override
	public <I extends Entity<?>> Collection<I> insertItem(Collection<I> itemCollection) {
		return null;
	}

	@Override
	public boolean updateItem(Entity<?>[] items) {
		return false;
	}

	@Override
	public <I extends Entity<?>> Collection<I> updateItem(Collection<I> itemCollection) {
		return null;
	}

	@Override
	public boolean saveItem(Entity<?>[] items) {
		return false;
	}

	@Override
	public <I extends Entity<?>> Collection<I> saveItem(Collection<I> itemCollection) {
		return null;
	}

	@Override
	public void deleteItem(Entity<?>... items) {

	}

	@Override
	public <I extends Entity<?>> void deleteItem(Collection<I> itemCollection) {

	}

}
