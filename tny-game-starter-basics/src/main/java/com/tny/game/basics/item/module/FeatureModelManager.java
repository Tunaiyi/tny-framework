package com.tny.game.basics.item.module;

import com.google.common.collect.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.basics.develop.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.module.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.version.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public class FeatureModelManager<FM extends GameFeatureModel> extends BaseModelManager<FM> implements AppPrepareStart {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureModelManager.class);

	private Map<Feature, FM> typeMap;

	private Set<Feature> features;

	private Map<OpenMode<FM>, List<FM>> modelsMap;

	private final FeatureVersionHolder versionHolder = new FeatureVersionHolder();

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

	protected FeatureModelManager(String path, Class<? extends FM> modelClass) {
		super(modelClass, path, ItemModelPaths.FEATURE_MODEL_CONFIG_PATH);
		Features.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Modules.enumerator().allEnumClasses().forEach(this::addEnumClass);
		OpenModes.enumerator().allEnumClasses().forEach(this::addEnumClass);
	}

	@Override
	protected void initXStream(XStream xStream) {
		xStream.alias("feature", Feature.class);
		xStream.alias("module", Module.class);
		xStream.alias("mode", OpenMode.class);
		xStream.alias("openPlan", OpenPlan.class);
		xStream.registerConverter(versionConverter);
	}

	private static class FeatureComparator<FM extends FeatureModel> implements Comparator<FM> {

		private final OpenMode<?> mode;

		private FeatureComparator(OpenMode<?> mode) {
			this.mode = mode;
		}

		@Override
		public int compare(FM o1, FM o2) {
			int levelComp;
			if ((levelComp = o1.getOpenLevel(this.mode) - o2.getOpenLevel(this.mode)) != 0) {
				return levelComp;
			}
			int proComp;
			if ((proComp = o1.getPriority() - o2.getPriority()) != 0) {
				return proComp;
			}
			return o1.getId() - o2.getId();
		}

	}

	@Override
	protected void parseAllComplete() {
		Version version = this.versionHolder.getFeatureVersion().orElse(null);
		LOGGER.info("当前版本 {} ", version);
		Optional<Version> current = this.versionHolder.getFeatureVersion();
		Map<Feature, FM> typeMap = new HashMap<>();
		Map<OpenMode<FM>, SortedSet<FM>> modelSetMap = new HashMap<>();
		for (FM model : this.modelMap.values()) {
			if (current.map(currVer -> model.getOpenVersion().map(v -> v.lessEqualsThan(currVer)).orElse(true)).orElse(true)) {
				typeMap.put(model.getFeature(), model);
				model.getOpenPlan()
						.forEach(plan -> modelSetMap.computeIfAbsent(plan.getMode(), k -> new TreeSet<>(new FeatureComparator<>(k))).add(model));
			} else {
				LOGGER.warn("当前版本 {} | 功能 {}({}) | 激活版本 [{}] | 未激活", version, model.getDesc(), model.getAlias(),
						model.getOpenVersion().orElse(null));
			}
		}
		this.typeMap = Collections.unmodifiableMap(typeMap);
		this.features = ImmutableSet.copyOf(typeMap.keySet());
		Map<OpenMode<FM>, List<FM>> modelsMap = new HashMap<>();
		modelSetMap.forEach((openMode, models) -> modelsMap.put(openMode, ImmutableList.copyOf(models)));
		this.modelsMap = ImmutableMap.copyOf(modelsMap);
	}

	@Override
	public void prepareStart() {
		this.parseAllComplete();
		FeatureVersionHolder.ON_CHANGE.addListener(h -> this.parseAllComplete());
	}

	public FeatureModel getAndCheckModelBy(Feature feature) {
		FeatureModel model = this.typeMap.get(feature);
		if (model == null) {
			throw new NullPointerException(format("{} 系统 model 为 null", feature));
		}
		return model;
	}

	protected FeatureVersionHolder getVersionHolder() {
		return this.versionHolder;
	}

	public Collection<FM> getModels() {
		return this.typeMap.values();
	}

	public Set<Feature> getFeatures() {
		return this.features;
	}

	public FM getModelBy(Feature feature) {
		return this.typeMap.get(feature);
	}

	public List<FM> getModels(OpenMode<?> openMode) {
		return this.modelsMap.getOrDefault(openMode, ImmutableList.of());
	}

	public Map<OpenMode<FM>, List<FM>> getModelsMap() {
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
