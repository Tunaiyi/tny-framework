package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.version.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class FeatureService implements AppPrepareStart, ApplicationContextAware {

	private final Logger LOGGER = LoggerFactory.getLogger(FeatureService.class);

	private static FeatureService FUNC_SYS_SERVICE;

	private final FeatureLauncherManager featureLauncherManager;

	private final ModulerService moduleService;

	private final FeatureModelManager<? extends FeatureModel> featureModelManager;

	private ApplicationContext applicationContext;

	private final Map<Feature, FeatureHandler> handlerMap = new HashMap<>();

	public FeatureService(
			FeatureLauncherManager featureLauncherManager,
			ModulerService moduleService,
			FeatureModelManager<? extends FeatureModel> featureModelManager) {
		this.featureLauncherManager = featureLauncherManager;
		this.moduleService = moduleService;
		this.featureModelManager = featureModelManager;
	}

	public static boolean isFeatureOpened(long playerId, Feature feature) {
		return FUNC_SYS_SERVICE.doIsOpen(playerId, feature);
	}

	public boolean isOpened(long playerId, Feature feature) {
		return this.doIsOpen(playerId, feature);
	}

	public <T> void openFeature(GameFeatureLauncher launcher, FeatureOpenMode<?> openMode, T context) {
		this.doOpenFeature(launcher, openMode, context);
	}

	public void loadFeature(GameFeatureLauncher launcher) {
		this.doLoadFeature(launcher);
	}

	private boolean doIsOpen(long explorerID, Feature feature) {
		GameFeatureLauncher launcher = this.featureLauncherManager.getLauncher(explorerID);
		return launcher.isFeatureOpened(feature);
	}

	public boolean isEffect(Feature feature) {
		return this.featureModelManager.getModelBy(feature) != null;
	}

	public Optional<Version> getFeatureVersion() {
		return this.featureModelManager.getVersionHolder().getFeatureVersion();
	}

	private <T> void doOpenFeature(GameFeatureLauncher launcher, FeatureOpenMode<? extends FeatureModel> openMode, T context) {
		for (FeatureModel model : this.featureModelManager.getModels(openMode)) {
			FeatureHandler handler = this.handlerMap.get(model.getFeature());
			if (handler == null || !model.isEffect() || !openMode.check(launcher, as(model), context)) {
				continue;
			}
			if (!model.isCanOpen(launcher, openMode)
					&& model.getParent().map(f -> !launcher.isFeatureOpened(f)).orElse(false)) {
				continue;
			}
			Feature feature = handler.getFeature();
			try {
				synchronized (launcher) {
					if (launcher.isFeatureOpened(feature)) {
						continue;
					}
					if (this.LOGGER.isInfoEnabled()) {
						RunChecker.trace(model.getFeature());
						this.LOGGER.debug("{} 玩家开启 {} 功能....", launcher.getPlayerId(), feature);
					}
					if (this.moduleService.openModule(launcher, feature.dependModules())) {
						launcher.openFeature(model, handler);
					}
					if (this.LOGGER.isInfoEnabled()) {
						long time = RunChecker.end(model.getFeature()).costTime();
						this.LOGGER.debug("{} 玩家开启 {} 功能成功! 耗时 {} ms", launcher.getPlayerId(), feature, time);
					}
				}
			} catch (Throwable e) {
				this.LOGGER.error("玩家[{}] 开启 {} 功能失败", launcher.getPlayerId(), feature, e);
			}
		}
	}

	private void doLoadFeature(final GameFeatureLauncher launcher) {
		HashSet<Moduler> moduleSet = new HashSet<>();
		for (Feature feature : launcher.getOpenedFeatures()) {
			if (launcher.isFeatureOpened(feature)) {
				for (Moduler module : feature.dependModules()) {
					if (!moduleSet.add(module)) {
						continue;
					}
					this.moduleService.doLoadModule(launcher, module);
				}
				if (feature.isValid()) {
					doLoadFeature(launcher, feature);
				}
			}
		}
	}

	private void doLoadFeature(final FeatureLauncher launcher, final Feature feature) {
		try {
			if (feature.isValid()) {
				FeatureHandler featureHandler = this.handlerMap.get(feature);
				featureHandler.loadFeature(launcher);
			}
		} catch (Throwable e) {
			this.LOGGER.error("玩家[{}] 预加载 {} 功能异常", launcher.getPlayerId(), feature, e);
		}
	}

	//	protected abstract <T> T createContext();

	public <T> T updateContext(GameFeatureLauncher launcher, T context) {
		this.moduleService.loadContext(launcher, context);
		return context;
	}

	public <T> T updateContext(GameFeatureLauncher launcher, T context, Collection<? extends Moduler> modules) {
		this.moduleService.updateContext(launcher, modules, context);
		return context;
	}

	public void updateFeatureVersion(String featureVersion) {
		this.featureModelManager.getVersionHolder().updateVersion(featureVersion);
	}

	@Override
	public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void prepareStart() throws Exception {
		Collection<BaseFeatureHandler<?>> collection = as(this.applicationContext.getBeansOfType(BaseFeatureHandler.class).values());
		List<BaseFeatureHandler<?>> featureHandlers = new ArrayList<>(collection);
		for (BaseFeatureHandler<?> feature : featureHandlers) {
			this.handlerMap.put(feature.getFeature(), feature);
		}
		for (Feature feature : Features.all()) {
			if (!this.handlerMap.containsKey(feature)) {
				FeatureHandler handler = new DefaultFuncSysHandler(feature);
				this.handlerMap.put(feature, handler);
			}
		}
	}

}
