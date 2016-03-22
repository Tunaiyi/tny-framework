package com.tny.game.suite.base.module;

import com.tny.game.LogUtils;
import com.tny.game.base.module.*;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.suite.utils.SuiteLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

public abstract class FeatureService<DTO> implements ServerPreStart, ApplicationContextAware {

    private final Logger LOGGER = LoggerFactory.getLogger(SuiteLog.MODULE);

    private static FeatureService FUNC_SYS_SERVICE;

    @Autowired
    private FeatureExplorerManager featureExplorerManager;

    @Autowired
    private ModuleService<DTO> moduleService;

    @Autowired
    protected FeatureModelManager featureModelManager;

    private ApplicationContext applicationContext;

    private Map<Feature, FeatureHandler> handlerMap = new HashMap<>();

    private List<FeatureHandler> handlerList = null;


    public FeatureService() {
        FUNC_SYS_SERVICE = this;
    }

    // public void openModule(ModuleOwner explorer) {
    // MODULE_SERVICE.doOpenModule(explorer, null, null, false, true);
    // }
    //
    // public boolean openModule(ModuleOwner explorer, Module moduleType) {
    // return MODULE_SERVICE.doOpenModule(explorer, null, moduleType, false, true).contains(moduleType);
    // }
    //
    // public boolean openModule(ModuleOwner explorer, Module moduleType, boolean enforce) {
    // return MODULE_SERVICE.doOpenModule(explorer, null, moduleType, enforce, true).contains(moduleType);
    // }
    //
    // public boolean openModule(ModuleOwner explorer, Module moduleType, boolean enforce, boolean checkOpened) {
    // return MODULE_SERVICE.doOpenModule(explorer, null, moduleType, enforce, checkOpened).contains(moduleType);
    // }

    public void openFeature(GameFeatureExplorer explorer, OpenMode openMode) {
        this.doOpenFeature(explorer, openMode);
    }

    public static boolean isFeatureOpened(long explorerID, Feature feature) {
        return FUNC_SYS_SERVICE.doIsOpen(explorerID, feature);
    }

    public boolean isOpened(long explorerID, Feature feature) {
        return this.doIsOpen(explorerID, feature);
    }

    public void loadFeature(GameFeatureExplorer explorer) {
        this.doLoadFeature(explorer);
    }

    private boolean doIsOpen(long explorerID, Feature feature) {
        GameFeatureExplorer featureOwner = this.featureExplorerManager.getExplorer(explorerID);
        return featureOwner.isFeatureOpened(feature);
    }

    private FeatureModel getModel(Feature feature) {
        return featureModelManager.getAndCheckModelBy(feature);
    }

    private List<Feature> doOpenFeature(GameFeatureExplorer explorer, OpenMode openMode) {
        List<Feature> okList = new ArrayList<>();
        for (FeatureHandler handler : this.handlerList) {
            FeatureModel featureModel = getModel(handler.getFeature());
            if (!featureModel.isEffect() || featureModel.getOpenLevel() > explorer.getLevel())
                continue;
            if (!featureModel.isCanOpen(explorer, openMode))
                continue;
            Feature feature = handler.getFeature();
            try {
                synchronized (explorer) {
                    if (explorer.isFeatureOpened(feature))
                        continue;
                    LOGGER.debug("{} 玩家开启 {} 功能", explorer.getPlayerID(), feature);
                    if (this.moduleService.openModule(explorer, feature.dependModules()) &&
                            handler.openFeature(explorer)) {
                        explorer.open(feature);
                        okList.add(feature);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("玩家[{}] 开启 {} 功能失败", explorer.getPlayerID(), feature, e);
            }
        }
        return okList;
    }

    private void doLoadFeature(final GameFeatureExplorer explorer) {
        HashSet<Module> moduleSet = new HashSet<>();
        for (Feature feature : explorer.getOpenedFeatures()) {
            if (explorer.isFeatureOpened(feature))
                moduleSet.addAll(feature.dependModules());
        }
        moduleService.loadModule(explorer, moduleSet);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public InitLevel getInitLevel() {
        return InitLevel.LEVEL_5;
    }

    @Override
    public void initialize() throws Exception {
        List<GameFeatureHandler> featureHandlers = new ArrayList<>(this.applicationContext.getBeansOfType(GameFeatureHandler.class).values());
        for (GameFeatureHandler feature : featureHandlers) {
            this.handlerMap.put(feature.getFeature(), feature);
        }
        for (Feature feature : Features.values()) { //TODO AA 临时注释掉的
            if (feature.isHasHandler()) {
                if (!this.handlerMap.containsKey(feature))
                    throw new IllegalArgumentException(LogUtils.format("{} feature handler is null", feature));
            } else {
                FeatureHandler handler = new DefaultFuncSysHandler(feature);
                this.handlerMap.put(feature, handler);
            }
        }
        this.handlerList = new ArrayList<>();
        for (FeatureModel model : this.featureModelManager.getFeatureOpenList()) {
            FeatureHandler handler = this.handlerMap.get(model.getFeature());
            if (handler == null)
                throw new NullPointerException(LogUtils.format("{} feature handler is null", model.getFeature()));
            this.handlerList.add(handler);
        }
    }

    @Override
    public boolean waitInitialized() {
        return true;
    }
}
