package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.xml.*;

import javax.annotation.Resource;

public abstract class GameModelManager<M extends Model> extends XMLModelManager<M> {

	/**
	 * 事物对象管理器
	 */
	@Resource
	protected ItemModelContext context;

	@Override
	protected ItemModelContext context() {
		return this.context;
	}

	protected GameModelManager(Class<? extends M> modelClass, String... paths) {
		super(modelClass, paths);
		ItemTypes.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Abilities.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Actions.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Behaviors.enumerator().allEnumClasses().forEach(this::addEnumClass);
		DemandTypes.enumerator().allEnumClasses().forEach(this::addEnumClass);
		DemandParams.enumerator().allEnumClasses().forEach(this::addEnumClass);
	}

	protected GameModelManager(Class<? extends M> modelClass, Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		this(modelClass, paths);
		this.addEnumClass(optionClass);
	}

	protected GameModelManager(Class<? extends M> modelClass,
			Class<? extends Enum<? extends DemandType>> demandTypeClass,
			Class<? extends Enum<? extends Ability>> abilityClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		this(modelClass, paths);
		this.addEnumClass(abilityClass);
		this.addEnumClass(demandTypeClass);
		this.addEnumClass(optionClass);
	}

	protected GameModelManager(Class<? extends M> modelClass,
			Class<? extends Enum<? extends Ability>> abilityClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		this(modelClass, paths);
		this.addEnumClass(abilityClass);
		this.addEnumClass(optionClass);
	}

	protected GameModelManager(
			Class<? extends M> modelClass,
			Class<? extends Enum<?>>[] enumClasses,
			String... paths) {
		this(modelClass, paths);
		for (Class<? extends Enum<?>> clazz : enumClasses) {
			this.addEnumClass(clazz);
		}
	}

}