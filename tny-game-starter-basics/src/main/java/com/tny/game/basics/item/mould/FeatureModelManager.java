package com.tny.game.basics.item.mould;

import com.google.common.collect.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.mould.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.version.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public class FeatureModelManager<FM extends DefaultFeatureModel> extends GameModelManager<FM> implements AppPrepareStart {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureModelManager.class);

	private Map<Feature, FM> typeMap;

	private Set<Feature> features;

	private Map<FeatureOpenMode<FM>, List<FM>> modelsMap;

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

	public FeatureModelManager(String path, Class<? extends FM> modelClass) {
		super(modelClass, path);
		Features.enumerator().allEnumClasses().forEach(this::addEnumClass);
		Moulds.enumerator().allEnumClasses().forEach(this::addEnumClass);
		FeatureOpenModes.enumerator().allEnumClasses().forEach(this::addEnumClass);
	}

	@Override
	protected void onLoadCreate(ModelLoader<FM> loader) {
		loader.setContextHandler((context) -> {
			if (context instanceof XStream) {
				XStream xStream = (XStream)context;
				xStream.alias("feature", Feature.class);
				xStream.alias("mould", Mould.class);
				xStream.alias("mode", FeatureOpenMode.class);
				xStream.alias("openPlan", FeatureOpenPlan.class);
				xStream.registerConverter(versionConverter);
			}
		});
	}

	private static class FeatureComparator<FM extends FeatureModel> implements Comparator<FM> {

		private final FeatureOpenMode<?> mode;

		private FeatureComparator(FeatureOpenMode<?> mode) {
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
		Map<FeatureOpenMode<FM>, SortedSet<FM>> modelSetMap = new HashMap<>();
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
		Map<FeatureOpenMode<FM>, List<FM>> modelsMap = new HashMap<>();
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

	public List<FM> getModels(FeatureOpenMode<?> openMode) {
		return this.modelsMap.getOrDefault(openMode, ImmutableList.of());
	}

	public Map<FeatureOpenMode<FM>, List<FM>> getModelsMap() {
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
