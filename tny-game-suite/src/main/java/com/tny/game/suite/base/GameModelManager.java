package com.tny.game.suite.base;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.xml.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Resource;

public abstract class GameModelManager<M extends Model> extends AbstractXMLModelManager<M> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}