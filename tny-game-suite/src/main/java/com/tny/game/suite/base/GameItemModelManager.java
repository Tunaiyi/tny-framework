package com.tny.game.suite.base;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.utils.*;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

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
    protected void loadAndInitModel(String path, InputStream inputStream, boolean reload)
            throws IOException, InstantiationException, IllegalAccessException {
        super.loadAndInitModel(path, inputStream, reload);
        this.itemTypes = ImmutableSet.copyOf(this.modelMap.values().stream()
                .map(m -> Asserts.checkNotNull(m.getItemType(), "{}.getItemType() is null", m))
                .collect(Collectors.toSet()));
    }

    @Override
    public Set<ItemType> manageTypes() {
        return this.itemTypes;
    }

}