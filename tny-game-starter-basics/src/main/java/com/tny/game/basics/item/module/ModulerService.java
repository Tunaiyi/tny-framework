package com.tny.game.basics.item.module;

import com.tny.game.basics.develop.*;
import com.tny.game.basics.logger.*;
import com.tny.game.basics.module.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.runtime.*;
import org.slf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;

import javax.annotation.Nonnull;
import java.util.*;

import static com.tny.game.basics.develop.ItemModelPaths.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public abstract class ModulerService implements AppPrepareStart, ApplicationContextAware {

	private final static Logger LOGGER = LoggerFactory.getLogger(BasicsLoggerNames.MODULE);

	private final Map<Moduler, ModulerHandler> handlerMap = new HashMap<>();

	private final FeatureLauncherManager featureLauncherManager;

	private ApplicationContext applicationContext;

	public ModulerService(FeatureLauncherManager featureLauncherManager) {
		this.featureLauncherManager = featureLauncherManager;
	}

	public boolean openModule(GameFeatureLauncher launcher, Collection<Moduler> modules) {
		this.doOpenModule(launcher, modules);
		for (Moduler module : modules) {
			if (module.isValid() && !launcher.isModuleOpened(module)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isOpened(long playerId, Moduler moduleType) {
		return this.doIsOpen(playerId, moduleType);
	}

	public void loadModule(FeatureLauncher launcher, Collection<Moduler> modules) {
		this.doLoadModule(launcher, modules);
	}

	private boolean doIsOpen(long playerId, Moduler moduleType) {
		FeatureLauncher launcher = this.featureLauncherManager.getLauncher(playerId);
		return moduleType.isValid() && launcher.isModuleOpened(moduleType);
	}

	private void doOpenModule(GameFeatureLauncher launcher, Collection<Moduler> moduleTypes) {
		boolean consuming = DevelopEnvs.envs().getBoolean(DEVELOP_MODULE_TIME_CONSUMING, false);
		for (Moduler module : moduleTypes) {
			if (!module.isValid() || launcher.isModuleOpened(module)) {
				continue;
			}
			try {
				synchronized (launcher) {
					if (!launcher.isModuleOpened(module)) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("{} 玩家开启 {} 模块", launcher.getPlayerId(), module);
						}
						ModulerHandler handler = this.handlerMap.get(module);
						if (handler == null) {
							throw new NullPointerException(format("{} module handler is null", module));
						}
						if (consuming) {
							RunChecker.traceWithPrint(module);
						}
						launcher.openModuler(module, handler);
						if (consuming) {
							RunChecker.end(module);
						}
					}
				}
			} catch (Throwable e) {
				LOGGER.error("玩家[{}] 开启 {} 功能失败", launcher.getPlayerId(), module, e);
			}
		}
	}

	protected void doLoadModule(final FeatureLauncher launcher, final Collection<Moduler> modules) {
		for (Moduler module : modules) {
			doLoadModule(launcher, module);
		}
	}

	protected void doLoadModule(final FeatureLauncher launcher, final Moduler module) {
		try {
			if (module.isValid() && launcher.isModuleOpened(module)) {
				ModulerHandler moduleHandler = this.handlerMap.get(module);
				moduleHandler.loadModule(launcher);
			}
		} catch (Throwable e) {
			LOGGER.error("玩家[{}] 预加载 {} 模块异常", launcher.getPlayerId(), module, e);
		}
	}

	protected <C> C updateContext(FeatureLauncher launcher, Collection<? extends Moduler> modules, C context) {
		for (Moduler moduleType : modules) {
			try {
				if (moduleType.isValid() && launcher.isModuleOpened(moduleType)) {
					BaseModulerHandler<Moduler, C> module = module(moduleType);
					doUpdateContext(module, launcher, context);
				}
			} catch (Throwable e) {
				LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", launcher.getPlayerId(), moduleType, e);
			}
		}
		return context;
	}

	public <C> C loadContext(C context, FeatureLauncher launcher, Moduler moduleType) {
		if (!moduleType.isValid()) {
			return context;
		}
		BaseModulerHandler<Moduler, C> module = module(moduleType);
		doUpdateContext(module, launcher, context);
		return context;
	}

	public <C> C loadContext(FeatureLauncher launcher, C context) {
		for (Moduler moduleType : launcher.getOpenedModules()) {
			if (!moduleType.isValid()) {
				continue;
			}
			BaseModulerHandler<Moduler, C> module = module(moduleType);
			doUpdateContext(module, launcher, context);
		}
		return context;
	}

	private <C> BaseModulerHandler<Moduler, C> module(Moduler moduleType) {
		return as(this.handlerMap.get(moduleType));
	}

	private <C> void doUpdateContext(BaseModulerHandler<Moduler, C> handler, FeatureLauncher launcher, C context) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("读取 {} 玩家 {} 模块获取重新登录信息", launcher.getPlayerId(), handler.getModule());
			}
			handler.loadContext(launcher, context);
		} catch (Throwable e) {
			LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", launcher.getPlayerId(), handler.getModule(), e);
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
	public void prepareStart() {
		Collection<ModulerHandler> collection = as(this.applicationContext.getBeansOfType(ModulerHandler.class).values());
		List<ModulerHandler> moduleList = new ArrayList<>(collection);
		for (ModulerHandler module : moduleList) {
			this.handlerMap.put(module.getModule(), module);
		}
		for (Moduler module : Modulers.all()) {
			if (!this.handlerMap.containsKey(module)) {
				this.handlerMap.put(module, new DefaultModulerHandler(module));
			}
		}
	}

}
