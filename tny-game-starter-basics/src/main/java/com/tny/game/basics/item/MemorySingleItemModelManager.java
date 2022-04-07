package com.tny.game.basics.item;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public abstract class MemorySingleItemModelManager<IM extends ItemModel> extends MemoryItemModelManager<IM> {

	private IM model;

	protected MemorySingleItemModelManager(Class<? extends IM> modelClass) {
		super(modelClass);
	}

	public IM getModel() {
		return model;
	}

	@Override
	protected void parseComplete(List<IM> models) {
		if (models.isEmpty()) {
			throw new IllegalArgumentException(format("{} model 列表为空"));
		}
		if (models.size() > 1) {
			throw new IllegalArgumentException(format("{} model 列表数量多于1"));
		}
		model = models.get(0);
		parseComplete(model);
		super.parseComplete(models);
	}

	protected void parseComplete(IM model) {
	}

	@Override
	protected List<IM> loadAllModels() {
		return Collections.singletonList(createModel());
	}

	protected abstract IM createModel();

}
