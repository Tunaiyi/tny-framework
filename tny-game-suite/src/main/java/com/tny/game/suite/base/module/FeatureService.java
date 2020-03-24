package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.version.*;
import com.tny.game.suite.utils.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Resource;
import java.util.*;

public abstract class FeatureService<DTO> implements AppPrepareStart, ApplicationContextAware {

    private final Logger LOGGER = LoggerFactory.getLogger(SuiteLog.MODULE);

    private static FeatureService FUNC_SYS_SERVICE;

    @Resource
    private FeatureExplorerManager featureExplorerManager;

    @Resource
    private ModuleService<DTO> moduleService;

    @Resource
    private FeatureModelManager<? extends FeatureModel> featureModelManager;

    private ApplicationContext applicationContext;

    private Map<Feature, FeatureHandler> handlerMap = new HashMap<>();

    public FeatureService() {
        FUNC_SYS_SERVICE = this;
    }

    public static boolean isFeatureOpened(long explorerID, Feature feature) {
        return FUNC_SYS_SERVICE.doIsOpen(explorerID, feature);
    }

    public boolean isOpened(long explorerID, Feature feature) {
        return this.doIsOpen(explorerID, feature);
    }

    public <C> void openFeature(GameFeatureExplorer explorer, OpenMode openMode, C context) {
        this.doOpenFeature(explorer, openMode, context);
    }

    public void loadFeature(GameFeatureExplorer explorer) {
        this.doLoadFeature(explorer);
    }

    private boolean doIsOpen(long explorerID, Feature feature) {
        GameFeatureExplorer explorer = this.featureExplorerManager.getExplorer(explorerID);
        return explorer.isFeatureOpened(feature);
    }

    private FeatureModel getModel(Feature feature) {
        return this.featureModelManager.getAndCheckModelBy(feature);
    }

    public boolean isEffect(Feature feature) {
        return this.featureModelManager.getModelBy(feature) != null;
    }

    // private boolean isActiveFeature(Version current, FeatureModel model) {
    //     if (current == null)
    //         return true;
    //     return model.getOpenVersion()
    //             .map(ver -> ver.greaterEqualsThan(current))
    //             .orElse(true);
    // }

    public Optional<Version> getFeatureVersion() {
        return this.featureModelManager.getVersionHolder().getFeatureVersion();
    }

    @SuppressWarnings("unchecked")
    private <C> void doOpenFeature(GameFeatureExplorer explorer, OpenMode openMode, C context) {
        for (FeatureModel model : this.featureModelManager.getModels(openMode)) {
            FeatureHandler handler = this.handlerMap.get(model.getFeature());
            if (handler == null || !model.isEffect() || !openMode.check(explorer, model, context))
                continue;
            if (!model.isCanOpen(explorer, openMode)
                && model.getParent().map(f -> !explorer.isFeatureOpened(f)).orElse(false))
                continue;
            Feature feature = handler.getFeature();
            try {
                synchronized (explorer) {
                    if (explorer.isFeatureOpened(feature))
                        continue;
                    if (this.LOGGER.isInfoEnabled()) {
                        RunningChecker.start(model.getFeature());
                        this.LOGGER.debug("{} 玩家开启 {} 功能....", explorer.getPlayerId(), feature);
                    }
                    if (this.moduleService.openModule(explorer, feature.dependModules()) && handler.openFeature(explorer)) {
                        explorer.open(model);
                    }
                    if (this.LOGGER.isInfoEnabled()) {
                        long time = RunningChecker.end(model.getFeature()).cost();
                        this.LOGGER.debug("{} 玩家开启 {} 功能成功! 耗时 {} ms", explorer.getPlayerId(), feature, time);
                    }
                }
            } catch (Throwable e) {
                this.LOGGER.error("玩家[{}] 开启 {} 功能失败", explorer.getPlayerId(), feature, e);
            }
        }
        // if (LOGGER.isInfoEnabled() && alRunningCheck != null) {
        //     long time = RunningChecker.end(alRunningCheck).cost();
        //     LOGGER.info("{} 玩家尝试开启功能完成! 总耗时 {} ms", explorer.getPlayerID(), time);
        // }
    }

    private void doLoadFeature(final GameFeatureExplorer explorer) {
        HashSet<Module> moduleSet = new HashSet<>();
        for (Feature feature : explorer.getOpenedFeatures()) {
            if (explorer.isFeatureOpened(feature)) {
                for (Module module : feature.dependModules()) {
                    if (!moduleSet.add(module))
                        continue;
                    this.moduleService.doLoadModule(explorer, module);
                }
                if (feature.isValid())
                    doLoadFeature(explorer, feature);
            }
        }
    }

    private void doLoadFeature(final FeatureExplorer explorer, final Feature feature) {
        try {
            if (feature.isValid()) {
                FeatureHandler featureHandler = this.handlerMap.get(feature);
                featureHandler.loadFeature(explorer);
            }
        } catch (Throwable e) {
            this.LOGGER.error("玩家[{}] 预加载 {} 功能异常", explorer.getPlayerId(), feature, e);
        }
    }

    protected abstract DTO createDTO();

    public DTO updateDTO(GameFeatureExplorer explorer) {
        DTO dto = createDTO();
        this.moduleService.updateDTO(explorer, dto);
        return dto;
    }

    public DTO updateDTO(GameFeatureExplorer explorer, Collection<? extends Module> modules) {
        DTO dto = createDTO();
        this.moduleService.updateDTO(explorer, modules, dto);
        return dto;
    }

    public void updateFeatureVersion(String featureVersion) {
        this.featureModelManager.getVersionHolder().updateVersion(featureVersion);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void prepareStart() throws Exception {
        List<GameFeatureHandler> featureHandlers = new ArrayList<>(this.applicationContext.getBeansOfType(GameFeatureHandler.class).values());
        for (GameFeatureHandler feature : featureHandlers) {
            this.handlerMap.put(feature.getFeature(), feature);
        }
        for (Feature feature : Features.values()) { //TODO AA 临时注释掉的
            if (!this.handlerMap.containsKey(feature)) {
                FeatureHandler handler = new DefaultFuncSysHandler(feature);
                this.handlerMap.put(feature, handler);
            }
        }
    }

}
