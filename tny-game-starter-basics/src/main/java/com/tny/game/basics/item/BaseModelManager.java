package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.xml.*;

import javax.annotation.Resource;

public abstract class BaseModelManager<M extends Model> extends AbstractXMLModelManager<M> {

	/**
	 * 事物对象管理器
	 */
	@Resource
	protected ItemModelContext context;

	@Override
	protected ItemModelContext context() {
		return this.context;
	}

	// @Override
	// protected SingleValueConverter getFormulaConverter() {
	//     if (formulaConverter != null)
	//         return this.formulaConverter;
	//     synchronized (this) {
	//         if (this.formulaConverter != null)
	//             return this.formulaConverter;
	//         this.formulaConverter = this.applicationContext.getBean(MvelFormulaConverter.class);
	//     }
	//     return formulaConverter;
	// }
	//
	// @Override
	// protected SingleValueConverter getRandomConverter() {
	//     if (this.randomConverter != null)
	//         return this.randomConverter;
	//     synchronized (this) {
	//         if (this.randomConverter != null)
	//             return this.randomConverter;
	//         Map<String, RandomCreatorFactory> factoryMap = new HashMap<>();
	//         for (RandomCreatorFactory factory : RandomCreators.getFactories()) {
	//             factoryMap.put(factory.getName(), factory);
	//         }
	//         RandomCreatorFactory factory = new SequenceRandomCreatorFactory();
	//         factoryMap.put(factory.getName(), factory);
	//         factory = new AllRandomCreatorFactory();
	//         factoryMap.put(factory.getName(), factory);
	//         this.randomConverter = new String2RandomCreator(factoryMap);
	//     }
	//     return this.randomConverter;
	// }

	protected BaseModelManager(Class<? extends M> modelClass, String... paths) {
		super(modelClass, paths);
		ItemTypes.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Abilities.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Actions.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Behaviors.enumerator().allEnumClasses().forEach(this::addEnumClass);
		DemandTypes.enumerator().allEnumClasses().forEach(this::addEnumClass);
		DemandParams.enumerator().allEnumClasses().forEach(this::addEnumClass);
	}

	protected BaseModelManager(Class<? extends M> modelClass, Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		this(modelClass, paths);
		this.addEnumClass(optionClass);
	}

	protected BaseModelManager(Class<? extends M> modelClass,
			Class<? extends Enum<? extends DemandType>> demandTypeClass,
			Class<? extends Enum<? extends Ability>> abilityClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		this(modelClass, paths);
		this.addEnumClass(abilityClass);
		this.addEnumClass(demandTypeClass);
		this.addEnumClass(optionClass);
	}

	protected BaseModelManager(Class<? extends M> modelClass,
			Class<? extends Enum<? extends Ability>> abilityClass,
			Class<? extends Enum<? extends Option>> optionClass, String... paths) {
		this(modelClass, paths);
		this.addEnumClass(abilityClass);
		this.addEnumClass(optionClass);
	}

	protected BaseModelManager(
			Class<? extends M> modelClass,
			Class<? extends Enum<?>>[] enumClasses,
			String... paths) {
		this(modelClass, paths);
		for (Class<? extends Enum<?>> clazz : enumClasses) {
			this.addEnumClass(clazz);
		}
	}

}