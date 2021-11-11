package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;

import java.util.List;

import static com.tny.game.common.utils.StringAide.*;

public abstract class SingleItemModelManager<IM extends ItemModel> extends BaseItemModelManager<IM> {

	private IM model;

	protected SingleItemModelManager(Class<? extends IM> modelClass, String... paths) {
		super(modelClass, paths);
	}

	protected SingleItemModelManager(Class<? extends IM> modelClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		super(modelClass, optionClass, paths);
	}

	protected SingleItemModelManager(Class<? extends IM> modelClass,
			Class<? extends Enum<? extends DemandType>> demandTypeClass,
			Class<? extends Enum<? extends Ability>> abilityClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		super(modelClass, demandTypeClass, abilityClass, optionClass, paths);
	}

	protected SingleItemModelManager(Class<? extends IM> modelClass,
			Class<? extends Enum<? extends Ability>> abilityClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		super(modelClass, abilityClass, optionClass, paths);
	}

	protected SingleItemModelManager(Class<? extends IM> modelClass, Class<? extends Enum<?>>[] enumClasses, String... paths) {
		super(modelClass, enumClasses, paths);
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
		doParseComplete(model);
		super.parseComplete(models);
	}

	protected void doParseComplete(IM model) {
	}

	public IM getModel() {
		return model;
	}

}