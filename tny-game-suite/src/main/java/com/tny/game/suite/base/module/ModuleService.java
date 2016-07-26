package com.tny.game.suite.base.module;

import com.tny.game.LogUtils;
import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.base.module.Module;
import com.tny.game.base.module.ModuleHandler;
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
import java.util.List;
import java.util.Map;

public abstract class ModuleService<DTO> implements ServerPreStart, ApplicationContextAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(SuiteLog.MODULE);

    private Map<Module, GameModuleHandler> handlerMap = new HashMap<>();

    @Autowired
    protected FeatureModelManager featureModelManager;

    @Autowired
    protected FeatureExplorerManager featureExplorerManager;

    private ApplicationContext applicationContext;

    public ModuleService() {
    }

    public boolean openModule(GameFeatureExplorer explorer, Collection<Module> modules) {
        this.doOpenModule(explorer, modules);
        for (Module module : modules) {
            if (module.isValid() && !explorer.isModuleOpened(module))
                return false;
        }
        return true;
    }

    protected boolean isOpened(long playerID, Module moduleType) {
        return this.doIsOpen(playerID, moduleType);
    }

    public void loadModule(FeatureExplorer explorer, Collection<Module> modules) {
        this.doLoadModule(explorer, modules);
    }

    private boolean doIsOpen(long playerID, Module moduleType) {
        FeatureExplorer explorer = this.featureExplorerManager.getExplorer(playerID);
        return moduleType.isValid() && explorer.isModuleOpened(moduleType);
    }

    private List<Module> doOpenModule(GameFeatureExplorer explorer, Collection<Module> moduleTypes) {
        List<Module> succList = new ArrayList<>();
        for (Module module : moduleTypes) {
            if (!module.isValid() || explorer.isModuleOpened(module))
                continue;
            try {
                synchronized (explorer) {
                    if (!explorer.isModuleOpened(module)) {
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("{} 玩家开启 {} 模块", explorer.getPlayerID(), module);
                        ModuleHandler handler = this.handlerMap.get(module);
                        if (handler == null)
                            throw new NullPointerException(LogUtils.format("{} module handler is null", module));
                        if (handler == null || handler.openModule(explorer)) {
                            explorer.open(module);
                            succList.add(module);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("玩家[{}] 开启 {} 功能失败", explorer.getPlayerID(), module, e);
            }
        }
        return succList;
    }

    protected void doLoadModule(final FeatureExplorer explorer, final Collection<Module> modules) {
        for (Module moduleType : modules) {
            try {
                if (moduleType.isValid() && explorer.isModuleOpened(moduleType)) {
                    GameModuleHandler module = this.handlerMap.get(moduleType);
                    module.loadModule(explorer);
                }
            } catch (Exception e) {
                LOGGER.error("玩家[{}] 预加载 {} 模块异常", explorer.getPlayerID(), moduleType, e);
            }
        }
    }

    protected DTO updateDTO(FeatureExplorer featureExplorer, Collection<? extends Module> modules, DTO dto) {
        for (Module moduleType : modules) {
            try {
                if (moduleType.isValid() && featureExplorer.isModuleOpened(moduleType)) {
                    GameModuleHandler module = this.handlerMap.get(moduleType);
                    doUpdateDTO(module, featureExplorer, dto);
                }
            } catch (Exception e) {
                LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", featureExplorer.getPlayerID(), moduleType, e);
            }
        }
        return dto;
    }

    public DTO updateDTO(DTO dto, FeatureExplorer featureExplorer, Module moduleType) {
        if (!moduleType.isValid())
            return dto;
        GameModuleHandler module = this.handlerMap.get(moduleType);
        doUpdateDTO(module, featureExplorer, dto);
        return dto;
    }

    public DTO updateDTO(FeatureExplorer featureExplorer, DTO dto) {
        for (Module moduleType : featureExplorer.getOpenedModules()) {
            if (!moduleType.isValid())
                continue;
            GameModuleHandler module = this.handlerMap.get(moduleType);
            doUpdateDTO(module, featureExplorer, dto);
        }
        return dto;
    }

    @SuppressWarnings("unchecked")
    private void doUpdateDTO(GameModuleHandler handler, FeatureExplorer featureExplorer, DTO dto) {
        try {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("读取 {} 玩家 {} 模块获取重新登录信息", featureExplorer.getPlayerID(), handler.getModule());
            handler.updateDTO(featureExplorer, dto);
        } catch (Exception e) {
            LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", featureExplorer.getPlayerID(), handler.getModule(), e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public PerIniter getIniter() {
        return PerIniter.initer(this.getClass(), InitLevel.LEVEL_4);
    }

    @Override
    public void initialize() throws Exception {
        List<GameModuleHandler> moduleList = new ArrayList<>(this.applicationContext.getBeansOfType(GameModuleHandler.class).values());
        for (GameModuleHandler module : moduleList) {
            this.handlerMap.put(module.getModule(), module);
        }
        for (Module module : Modules.values()) { //TODO AA 临时注释掉的
            if (module.isHasHandler()) {
                if (!this.handlerMap.containsKey(module))
                    throw new IllegalArgumentException(LogUtils.format("{} module handler is null", module));
            } else {
                this.handlerMap.put(module, new DefaultModuleHandler(module));
            }
        }
    }

}
