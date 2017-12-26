package com.tny.game.suite.base.module;

import com.google.common.collect.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.*;
import com.tny.game.base.module.*;
import com.tny.game.common.utils.*;
import com.tny.game.common.utils.version.*;
import com.tny.game.suite.base.*;
import com.tny.game.suite.utils.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

public class FeatureModelManager<FM extends FeatureModel> extends GameItemModelManager<FM> {

    private Map<Feature, FM> typeMap;

    private List<FM> models;

    private Map<OpenMode, List<FM>> modelsMap;

    private static final SingleValueConverter versionConverter = new SingleValueConverter() {

            @Override
            public String toString(Object o) {
                return as(o, Version.class).getFullVersion();
            }

            @Override
            public Object fromString(String s) {
                return Version.of(s);
            }

            @Override
            public boolean canConvert(Class aClass) {
                return aClass == Version.class;
            }
        };



    protected FeatureModelManager(Class<? extends FM> modelClass) {
        super(modelClass, Configs.SUITE_CONFIG.getStr(Configs.SUITE_FEATURE_MODEL_CONFIG_PATH, Configs.FEATURE_MODEL_CONFIG_PATH));
        Features.holder.getAllEnumClasses().forEach(this::addEnumClass);
        Modules.holder.getAllEnumClasses().forEach(this::addEnumClass);
        OpenModes.holder.getAllEnumClasses().forEach(this::addEnumClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void initXStream(XStream xStream) {
        xStream.alias("feature", Feature.class);
        xStream.alias("module", Module.class);
        xStream.alias("openMode", OpenMode.class);
        xStream.registerConverter(versionConverter);
    }

    @Override
    protected void parseAllComplete() {
        Map<Feature, FM> typeMap = new HashMap<>();
        Map<OpenMode, SortedSet<FM>> modelSetMap = new HashMap<>();
        for (FM model : this.modelMap.values()) {
            typeMap.put(model.getFeature(), model);
            modelSetMap.computeIfAbsent(model.getOpenMode(), k -> new TreeSet<>()).add(model);
        }
        this.typeMap = Collections.unmodifiableMap(typeMap);
        Map<OpenMode, List<FM>> modelsMap = new HashMap<>();
        modelSetMap.forEach((openMode, models) -> modelsMap.put(openMode, ImmutableList.copyOf(models)));
        this.modelsMap = ImmutableMap.copyOf(modelsMap);
    }

    public FeatureModel getAndCheckModelBy(Feature feature) {
        FeatureModel model = this.typeMap.get(feature);
        if (model == null)
            throw new NullPointerException(Logs.format("{} 系统 model 为 null", feature));
        return model;
    }

    public List<FM> getModels() {
        return this.models;
    }

    public FM getModelBy(Feature feature) {
        return this.typeMap.get(feature);
    }

    public List<FM> getModels(OpenMode<?> openMode) {
        return this.modelsMap.getOrDefault(openMode, ImmutableList.of());
    }

    public Map<OpenMode, List<FM>> getModelsMap() {
        return this.modelsMap;
    }

}
