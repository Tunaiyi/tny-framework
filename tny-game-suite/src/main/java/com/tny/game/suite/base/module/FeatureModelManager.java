package com.tny.game.suite.base.module;

import com.google.common.collect.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.base.module.*;
import com.tny.game.common.lifecycle.*;
import static com.tny.game.common.utils.StringAide.*;
import com.tny.game.common.utils.version.Version;
import com.tny.game.suite.base.GameModelManager;
import com.tny.game.suite.utils.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.as;

public class FeatureModelManager<FM extends GameFeatureModel> extends GameModelManager<FM> implements AppPrepareStart {

    public static final Logger LOGGER = LoggerFactory.getLogger(SuiteLog.MODULE);

    private Map<Feature, FM> typeMap;

    private Set<Feature> features;

    private Map<OpenMode, List<FM>> modelsMap;

    private FeatureVersionHolder versionHolder = new FeatureVersionHolder();

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
        // xStream.alias("openModes", Set.class);
        xStream.alias("mode", OpenMode.class);
        xStream.alias("openPlan", OpenPlan.class);
        xStream.registerConverter(versionConverter);
    }

    private static class FeatureComparator<FM extends FeatureModel> implements Comparator<FM> {

        private OpenMode<?> mode;

        private FeatureComparator(OpenMode<?> mode) {
            this.mode = mode;
        }

        @Override
        public int compare(FM o1, FM o2) {
        int levelComp;
        if ((levelComp = o1.getOpenLevel(mode) - o2.getOpenLevel(mode)) != 0)
            return levelComp;
        int proComp;
        if ((proComp = o1.getPriority() - o2.getPriority()) != 0)
            return proComp;
        return o1.getId() - o2.getId();
        }
    }

    @Override
    protected void parseAllComplete() {
        Version version = versionHolder.getFeatureVersion().orElse(null);
        LOGGER.info("当前版本 {} ", version);
        Optional<Version> current = versionHolder.getFeatureVersion();
        Map<Feature, FM> typeMap = new HashMap<>();
        Map<OpenMode, SortedSet<FM>> modelSetMap = new HashMap<>();
        for (FM model : this.modelMap.values()) {
            if (current.map(currVer -> model.getOpenVersion().map(v -> v.lessEqualsThan(currVer)).orElse(true)).orElse(true)) {
                typeMap.put(model.getFeature(), model);
                model.getOpenPlan().forEach(plan -> modelSetMap.computeIfAbsent(plan.getMode(), k -> new TreeSet<>(new FeatureComparator<>(k))).add(model));
            } else {
                LOGGER.warn("当前版本 {} | 功能 {}({}) | 激活版本 [{}] | 未激活", version, model.getDesc(), model.getAlias(), model.getOpenVersion().orElse(null));
            }
        }
        this.typeMap = Collections.unmodifiableMap(typeMap);
        this.features = ImmutableSet.copyOf(typeMap.keySet());
        Map<OpenMode, List<FM>> modelsMap = new HashMap<>();
        modelSetMap.forEach((openMode, models) -> modelsMap.put(openMode, ImmutableList.copyOf(models)));
        this.modelsMap = ImmutableMap.copyOf(modelsMap);
    }

    @Override
    public void prepareStart() throws Exception {
        this.parseAllComplete();
        FeatureVersionHolder.ON_CHANGE.addListener(h -> this.parseAllComplete());
    }

    public FeatureModel getAndCheckModelBy(Feature feature) {
        FeatureModel model = this.typeMap.get(feature);
        if (model == null)
            throw new NullPointerException(format("{} 系统 model 为 null", feature));
        return model;
    }

    protected FeatureVersionHolder getVersionHolder() {
        return versionHolder;
    }

    public Collection<FM> getModels() {
        return this.typeMap.values();
    }

    public Set<Feature> getFeatures() {
        return features;
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

    public void updateFeatureVersion(String featureVersion) {
        this.versionHolder.updateVersion(featureVersion);
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_1);
    }

}
