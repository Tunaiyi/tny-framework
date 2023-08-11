package com.tny.game.suite.base.module;

import com.tny.game.basics.mould.*;
import com.tny.game.common.io.config.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.runtime.*;
import com.tny.game.suite.utils.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.*;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.utils.Configs.*;

public abstract class ModuleService<DTO> implements AppPrepareStart, ApplicationContextAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(SuiteLog.MODULE);

    private final Map<Module, GameModuleHandler<?, ?>> handlerMap = new HashMap<>();

    @Autowired
    protected FeatureExplorerManager featureExplorerManager;

    private ApplicationContext applicationContext;

    private final Config DEVELOP = Configs.DEVELOP_CONFIG;

    public ModuleService() {
    }

    public boolean openModule(GameFeatureExplorer explorer, Collection<Module> modules) {
        this.doOpenModule(explorer, modules);
        for (Module module : modules) {
            if (module.isValid() && !explorer.isModuleOpened(module)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isOpened(long playerId, Module moduleType) {
        return this.doIsOpen(playerId, moduleType);
    }

    public void loadModule(FeatureExplorer explorer, Collection<Module> modules) {
        this.doLoadModule(explorer, modules);
    }

    private boolean doIsOpen(long playerId, Module moduleType) {
        FeatureExplorer explorer = this.featureExplorerManager.getExplorer(playerId);
        return moduleType.isValid() && explorer.isModuleOpened(moduleType);
    }

    private void doOpenModule(GameFeatureExplorer explorer, Collection<Module> moduleTypes) {
        //        List<Module> successList = new ArrayList<>();
        boolean consuming = this.DEVELOP.getBoolean(DEVELOP_MODULE_TIME_CONSUMING, false);
        for (Module module : moduleTypes) {
            if (!module.isValid() || explorer.isModuleOpened(module)) {
                continue;
            }
            try {
                synchronized (explorer) {
                    if (!explorer.isModuleOpened(module)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("{} 玩家开启 {} 模块", explorer.getPlayerId(), module);
                        }
                        ModuleHandler handler = this.handlerMap.get(module);
                        if (handler == null) {
                            throw new NullPointerException(format("{} module handler is null", module));
                        }
                        if (consuming) {
                            RunChecker.traceWithPrint(module);
                        }
                        if (handler.openModule(explorer)) {
                            explorer.open(module);
                            //                            successList.add(module);
                        }
                        if (consuming) {
                            RunChecker.end(module);
                        }
                    }
                }
            } catch (Throwable e) {
                LOGGER.error("玩家[{}] 开启 {} 功能失败", explorer.getPlayerId(), module, e);
            }
        }
    }

    protected void doLoadModule(final FeatureExplorer explorer, final Collection<Module> modules) {
        for (Module module : modules) {
            doLoadModule(explorer, module);
        }
    }

    protected void doLoadModule(final FeatureExplorer explorer, final Module module) {
        try {
            if (module.isValid() && explorer.isModuleOpened(module)) {
                GameModuleHandler<?, ?> moduleHandler = this.handlerMap.get(module);
                moduleHandler.loadModule(explorer);
            }
        } catch (Throwable e) {
            LOGGER.error("玩家[{}] 预加载 {} 模块异常", explorer.getPlayerId(), module, e);
        }
    }

    protected DTO updateDTO(FeatureExplorer featureExplorer, Collection<? extends Module> modules, DTO dto) {
        for (Module moduleType : modules) {
            try {
                if (moduleType.isValid() && featureExplorer.isModuleOpened(moduleType)) {
                    GameModuleHandler<Module, DTO> module = module(moduleType);
                    doUpdateDTO(module, featureExplorer, dto);
                }
            } catch (Throwable e) {
                LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", featureExplorer.getPlayerId(), moduleType, e);
            }
        }
        return dto;
    }

    public DTO updateDTO(DTO dto, FeatureExplorer featureExplorer, Module moduleType) {
        if (!moduleType.isValid()) {
            return dto;
        }
        GameModuleHandler<Module, DTO> module = module(moduleType);
        doUpdateDTO(module, featureExplorer, dto);
        return dto;
    }

    public DTO updateDTO(FeatureExplorer featureExplorer, DTO dto) {
        for (Module moduleType : featureExplorer.getOpenedModules()) {
            if (!moduleType.isValid()) {
                continue;
            }
            GameModuleHandler<Module, DTO> module = module(moduleType);
            doUpdateDTO(module, featureExplorer, dto);
        }
        return dto;
    }

    private GameModuleHandler<Module, DTO> module(Module moduleType) {
        return as(this.handlerMap.get(moduleType));
    }

    private void doUpdateDTO(GameModuleHandler<Module, DTO> handler, FeatureExplorer featureExplorer, DTO dto) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("读取 {} 玩家 {} 模块获取重新登录信息", featureExplorer.getPlayerId(), handler.getModule());
            }
            handler.updateDTO(featureExplorer, dto);
        } catch (Throwable e) {
            LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", featureExplorer.getPlayerId(), handler.getModule(), e);
        }
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public PrepareStarter getPrepareStarter() {
        return PrepareStarter.value(this.getClass(), LifecycleLevel.SYSTEM_LEVEL_4);
    }

    @Override
    public void prepareStart() throws Exception {
        Collection<GameModuleHandler<?, ?>> collection = as(this.applicationContext.getBeansOfType(GameModuleHandler.class).values());
        List<GameModuleHandler<?, ?>> moduleList = new ArrayList<>(collection);
        for (GameModuleHandler<?, ?> module : moduleList) {
            this.handlerMap.put(module.getModule(), module);
        }
        for (Module module : Modules.all()) { //TODO AA 临时注释掉的
            if (!this.handlerMap.containsKey(module)) {
                this.handlerMap.put(module, new DefaultModuleHandler(module));
            }
        }
    }

}
