package com.tny.game.suite.base;

import com.google.common.collect.*;
import com.thoughtworks.xstream.*;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;

import java.util.*;
import java.util.stream.*;

public abstract class GameItemModelManager<IM extends ItemModel> extends GameModelManager<IM> implements ItemTypeManageable {

    private volatile Set<ItemType> itemTypes = ImmutableSet.of();

    protected GameItemModelManager(Class<? extends IM> modelClass, String... paths) {
        super(modelClass, paths);
    }

    protected GameItemModelManager(Class<? extends IM> modelClass, Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(Class<? extends IM> modelClass,
                                   Class<? extends Enum<? extends DemandType>> demandTypeClass,
                                   Class<? extends Enum<? extends Ability>> abilityClass,
                                   Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(demandTypeClass);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(Class<? extends IM> modelClass,
                                   Class<? extends Enum<? extends Ability>> abilityClass,
                                   Class<? extends Enum<? extends Option>> optionClass, String... paths) {
        this(modelClass, paths);
        this.addEnumClass(abilityClass);
        this.addEnumClass(optionClass);
    }

    protected GameItemModelManager(
            Class<? extends IM> modelClass,
            Class<? extends Enum<?>>[] enumClasses,
            String... paths) {
        this(modelClass, paths);
        for (Class<? extends Enum<?>> clazz : enumClasses) {
            this.addEnumClass(clazz);
        }
    }

    @Override
    protected void initXStream(XStream xStream) {
        super.initXStream(xStream);
        this.itemTypes = ImmutableSet.copyOf(this.modelMap.values().stream()
                .map(ItemModel::getItemType)
                .collect(Collectors.toSet()));
        // for (ItemType itemType : v)
    }

    @Override
    public Set<ItemType> manageTypes() {
        return itemTypes;
    }
}