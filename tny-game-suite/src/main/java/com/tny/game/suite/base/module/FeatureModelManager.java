package com.tny.game.suite.base.module;

import com.google.common.collect.ImmutableList;
import com.thoughtworks.xstream.XStream;
import com.tny.game.LogUtils;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureModel;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.OpenMode;
import com.tny.game.suite.base.GameItemModelManager;
import com.tny.game.suite.utils.Configs;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


@Component
@Profile({"suite.base", "suite.all"})
public class FeatureModelManager extends GameItemModelManager<FeatureModel> {

    private Map<Feature, FeatureModel> typeMap;

    private List<FeatureModel> featureOpenList;

    protected FeatureModelManager() {
        super(GameFeatureModel.class, Configs.SUITE_CONFIG.getStr(Configs.SUITE_FEATURE_MODEL_CONFIG_PATH, Configs.FEATURE_MODEL_CONFIG_PATH));
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
    protected void parseComplete() {
        Map<Feature, FeatureModel> typeMap = new HashMap<>();
        for (FeatureModel model : this.modelMap.values()) {
            typeMap.put(model.getFeature(), model);
        }
        this.typeMap = Collections.unmodifiableMap(typeMap);
        Set<FeatureModel> modelSet = new TreeSet<>((one, other) -> {
            int levelComp;
            if ((levelComp = one.getOpenLevel() - other.getOpenLevel()) != 0)
                return levelComp;
            int proComp;
            if ((proComp = one.getPriority() - other.getPriority()) != 0)
                return proComp;
            return other.getID() - one.getID();
        });
        modelSet.addAll(typeMap.values());
        this.featureOpenList = ImmutableList.copyOf(modelSet);
    }

    public FeatureModel getAndCheckModelBy(Feature feature) {
        FeatureModel model = this.typeMap.get(feature);
        if (model == null)
            throw new NullPointerException(LogUtils.format("{} 系统 model 为 null", feature));
        return model;
    }

    public List<FeatureModel> getFeatureOpenList() {
        return this.featureOpenList;
    }

    public FeatureModel getModelBy(Feature feature) {
        return this.typeMap.get(feature);
    }

}
