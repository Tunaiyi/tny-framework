package com.tny.game.suite.base;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.base.converter.MvelFormulaConverter;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.DemandType;
import com.tny.game.base.item.behavior.Option;
import com.tny.game.base.item.probability.AllRandomCreatorFactory;
import com.tny.game.base.item.probability.DefaultRandomCreatorFactory;
import com.tny.game.base.item.probability.RandomCreatorFactory;
import com.tny.game.base.item.xml.AbstractXMLModelManager;
import com.tny.game.base.item.xml.String2RandomCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public abstract class GameItemModelManager<M extends Model> extends AbstractXMLModelManager<M> implements ApplicationContextAware, SingleValueConverter {

    private ApplicationContext applicationContext;

    protected volatile SingleValueConverter formulaConverter;

    protected volatile SingleValueConverter randomConverter;

    @Override
    protected SingleValueConverter getFormulaConverter() {
        if (formulaConverter != null)
            return this.formulaConverter;
        synchronized (this) {
            if (this.formulaConverter != null)
                return this.formulaConverter;
            this.formulaConverter = this.applicationContext.getBean(MvelFormulaConverter.class);
        }
        return formulaConverter;
    }

    @Override
    protected SingleValueConverter getRandomConverter() {
        if (this.randomConverter != null)
            return this.randomConverter;
        synchronized (this) {
            if (this.randomConverter != null)
                return this.randomConverter;
            Map<String, RandomCreatorFactory> factoryMap = new HashMap<>();
            Map<String, RandomCreatorFactory> springBeanMap = this.applicationContext.getBeansOfType(RandomCreatorFactory.class);
            for (RandomCreatorFactory factory : springBeanMap.values()) {
                factoryMap.put(factory.getName(), factory);
            }
            RandomCreatorFactory factory = new DefaultRandomCreatorFactory();
            factoryMap.put(factory.getName(), factory);
            factory = new AllRandomCreatorFactory();
            factoryMap.put(factory.getName(), factory);
            this.randomConverter = new String2RandomCreator(factoryMap);
        }
        return this.randomConverter;
    }

    protected GameItemModelManager(Class<? extends M> modelClass, String... paths) {
        super(modelClass, paths);
        ItemTypes.holder.getAllEnumClasses().forEach(this::addEnumClass);
        Abilities.holder.getAllEnumClasses().forEach(this::addEnumClass);
        Actions.holder.getAllEnumClasses().forEach(this::addEnumClass);
        Behaviors.holder.getAllEnumClasses().forEach(this::addEnumClass);
        DemandTypes.holder.getAllEnumClasses().forEach(this::addEnumClass);
        DemandParams.holder.getAllEnumClasses().forEach(this::addEnumClass);
    }

    protected GameItemModelManager(Class<? extends M> modelClass, Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(Class<? extends M> modelClass,
                                   Class<? extends Enum<? extends DemandType>> demandTypeClass,
                                   Class<? extends Enum<? extends Ability>> abilityClass,
                                   Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(demandTypeClass);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(Class<? extends M> modelClass,
                                   Class<? extends Enum<? extends Ability>> abilityClass,
                                   Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(
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

    @Override
    public String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    @Override
    public Object fromString(String str) {
        return this.getAndCheckModelByAlias(str);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return this.modelClass.isAssignableFrom(type);
    }

}