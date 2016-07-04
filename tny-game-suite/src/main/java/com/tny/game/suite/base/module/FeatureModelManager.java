package com.tny.game.suite.base.module;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.thoughtworks.xstream.XStream;
import com.tny.game.LogUtils;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureModel;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.OpenMode;
import com.tny.game.suite.base.GameItemModelManager;
import com.tny.game.suite.utils.Configs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class FeatureModelManager<FM extends FeatureModel> extends GameItemModelManager<FM> {

    private Map<Feature, FM> typeMap;

    private List<FM> models;

    private Map<OpenMode, List<FM>> modelsMap;


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
    }

    @Override
    protected void parseAllComplete() {
        Map<Feature, FM> typeMap = new HashMap<>();
        Map<OpenMode, SortedSet<FM>> modelSetMap = new HashMap<>();
        for (FM model : this.modelMap.values()) {
            typeMap.put(model.getFeature(), model);
            SortedSet<FM> models = modelSetMap.get(model.getOpenMode());
            if (models == null)
                modelSetMap.put(model.getOpenMode(), models = new TreeSet<>());
            models.add(model);
        }
        this.typeMap = Collections.unmodifiableMap(typeMap);
        Map<OpenMode, List<FM>> modelsMap = new HashMap<>();
        modelSetMap.forEach((openMode, models) -> modelsMap.put(openMode, ImmutableList.copyOf(models)));
        this.modelsMap = ImmutableMap.copyOf(modelsMap);
    }

    public FeatureModel getAndCheckModelBy(Feature feature) {
        FeatureModel model = this.typeMap.get(feature);
        if (model == null)
            throw new NullPointerException(LogUtils.format("{} 系统 model 为 null", feature));
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
