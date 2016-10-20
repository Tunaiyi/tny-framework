package com.tny.game.suite.base.module;

import com.google.common.collect.ImmutableList;
import com.tny.game.LogUtils;
import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.base.module.FeatureHandler;
import com.tny.game.base.module.FeatureModel;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.OpenMode;
import com.tny.game.common.RunningChecker;
import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PerIniter;
import com.tny.game.net.initer.ServerPreStart;
import com.tny.game.suite.utils.SuiteLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class FeatureService<DTO> implements ServerPreStart, ApplicationContextAware {

    private final Logger LOGGER = LoggerFactory.getLogger(SuiteLog.MODULE);

    private static FeatureService FUNC_SYS_SERVICE;

    @Autowired
    private FeatureExplorerManager featureExplorerManager;

    @Autowired
    private ModuleService<DTO> moduleService;

    @Autowired
    protected FeatureModelManager<? extends FeatureModel> featureModelManager;

    private ApplicationContext applicationContext;

    private Map<Feature, FeatureHandler> handlerMap = new HashMap<>();

    private Map<OpenMode, List<FeatureHandler>> handlersMap = new HashMap<>();

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
        GameFeatureExplorer featureOwner = this.featureExplorerManager.getExplorer(explorerID);
        return featureOwner.isFeatureOpened(feature);
    }

    private FeatureModel getModel(Feature feature) {
        return featureModelManager.getAndCheckModelBy(feature);
    }

    @SuppressWarnings("unchecked")
    private <C> List<Feature> doOpenFeature(GameFeatureExplorer explorer, OpenMode openMode, C context) {
        List<Feature> okList = new ArrayList<>();
        List<FeatureHandler> handlers = this.handlersMap.getOrDefault(openMode, ImmutableList.of());
        // String alRunningCheck = null;
        // if (LOGGER.isInfoEnabled()) {
        //     alRunningCheck = explorer.getPlayerID() + "-" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        //     RunningChecker.start("doOpenFeature");
        //     LOGGER.info("{} 玩家尝试开启功能", explorer.getPlayerID());
        // }
        for (FeatureHandler handler : handlers) {
            FeatureModel featureModel = getModel(handler.getFeature());
            if (!featureModel.isEffect() || !openMode.check(explorer, featureModel, context))
                continue;
            if (!featureModel.isCanOpen(explorer, openMode))
                continue;
            Feature feature = handler.getFeature();
            try {
                synchronized (explorer) {
                    if (explorer.isFeatureOpened(feature))
                        continue;
                    if (LOGGER.isInfoEnabled()) {
                        RunningChecker.start(featureModel.getFeature());
                        LOGGER.info("{} 玩家开启 {} 功能....", explorer.getPlayerID(), feature);
                    }
                    if (this.moduleService.openModule(explorer, feature.dependModules()) &&
                            handler.openFeature(explorer)) {
                        explorer.open(feature);
                        okList.add(feature);
                    }
                    if (LOGGER.isInfoEnabled()) {
                        long time = RunningChecker.end(featureModel.getFeature()).cost();
                        LOGGER.info("{} 玩家开启 {} 功能成功! 耗时 {} ms", explorer.getPlayerID(), feature, time);
                    }
                }
            } catch (Throwable e) {
                LOGGER.error("玩家[{}] 开启 {} 功能失败", explorer.getPlayerID(), feature, e);
            }
        }
        // if (LOGGER.isInfoEnabled() && alRunningCheck != null) {
        //     long time = RunningChecker.end(alRunningCheck).cost();
        //     LOGGER.info("{} 玩家尝试开启功能完成! 总耗时 {} ms", explorer.getPlayerID(), time);
        // }
        return okList;
    }

    private void doLoadFeature(final GameFeatureExplorer explorer) {
        HashSet<Module> moduleSet = new HashSet<>();
        for (Feature feature : explorer.getOpenedFeatures()) {
            if (explorer.isFeatureOpened(feature)) {
                for (Module module : feature.dependModules()) {
                    if (!moduleSet.add(module))
                        continue;
                    moduleService.doLoadModule(explorer, module);
                }
                if (feature.isValid())
                    doLoadFeature(explorer, feature);
            }
        }
    }

    private void doLoadFeature(final FeatureExplorer explorer, final Feature feature) {
        try {
            if (feature.isValid()) {
                FeatureHandler featureHandler = handlerMap.get(feature);
                featureHandler.loadFeature(explorer);
            }
        } catch (Throwable e) {
            LOGGER.error("玩家[{}] 预加载 {} 功能异常", explorer.getPlayerID(), feature, e);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_5);
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
        this.featureModelManager.getModelsMap().forEach(
                (openMode, featureModels) -> {
                    ArrayList<FeatureHandler> handlers = new ArrayList<>();
                    for (FeatureModel model : featureModels) {
                        FeatureHandler handler = this.handlerMap.get(model.getFeature());
                        if (handler == null)
                            throw new NullPointerException(LogUtils.format("{} feature handler is null", model.getFeature()));
                        handlers.add(handler);
                    }
                    this.handlersMap.put(openMode, handlers);
                }
        );
    }

    @Override
    public boolean waitInitialized() {
        return true;
    }
}
