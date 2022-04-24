package com.tny.game.basics.item.mould;

import com.tny.game.basics.develop.*;
import com.tny.game.basics.logger.*;
import com.tny.game.basics.mould.*;
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

public abstract class MouldService implements AppPrepareStart, ApplicationContextAware {

    private final static Logger LOGGER = LoggerFactory.getLogger(BasicsLoggerNames.MOULD);

    private final Map<Mould, MouldHandler> handlerMap = new HashMap<>();

    private final FeatureLauncherManager featureLauncherManager;

    private ApplicationContext applicationContext;

    public MouldService(FeatureLauncherManager featureLauncherManager) {
        this.featureLauncherManager = featureLauncherManager;
    }

    public boolean openMould(GameFeatureLauncher launcher, Collection<Mould> moulds) {
        this.doOpenMould(launcher, moulds);
        for (Mould mould : moulds) {
            if (mould.isValid() && !launcher.isMouldOpened(mould)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isOpened(long playerId, Mould mould) {
        return this.doIsOpen(playerId, mould);
    }

    public void loadMould(FeatureLauncher launcher, Collection<Mould> moulds) {
        this.doLoadMould(launcher, moulds);
    }

    private boolean doIsOpen(long playerId, Mould mould) {
        FeatureLauncher launcher = this.featureLauncherManager.getLauncher(playerId);
        return mould.isValid() && launcher.isMouldOpened(mould);
    }

    private void doOpenMould(GameFeatureLauncher launcher, Collection<Mould> moulds) {
        boolean consuming = DevelopEnvs.envs().getBoolean(DEVELOP_MOULD_TIME_CONSUMING, false);
        moulds.stream()
                .filter(mould -> mould.isValid() && !launcher.isMouldOpened(mould))
                .forEach(mould -> {
                    try {
                        synchronized (launcher) {
                            if (!launcher.isMouldOpened(mould)) {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("{} 玩家开启 {} 模块", launcher.getPlayerId(), mould);
                                }
                                MouldHandler handler = this.handlerMap.get(mould);
                                if (handler == null) {
                                    throw new NullPointerException(format("{} mould handler is null", mould));
                                }
                                if (consuming) {
                                    RunChecker.traceWithPrint(mould);
                                }
                                launcher.openMould(mould, handler);
                                if (consuming) {
                                    RunChecker.end(mould);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.error("玩家[{}] 开启 {} 功能失败", launcher.getPlayerId(), mould, e);
                    }
                });
    }

    private void doLoadMould(final FeatureLauncher launcher, final Collection<Mould> moulds) {
        for (Mould mould : moulds) {
            doLoadMould(launcher, mould);
        }
    }

    protected void doLoadMould(final FeatureLauncher launcher, final Mould mould) {
        try {
            if (mould.isValid() && launcher.isMouldOpened(mould)) {
                MouldHandler mouldHandler = this.handlerMap.get(mould);
                mouldHandler.loadMould(launcher);
            }
        } catch (Throwable e) {
            LOGGER.error("玩家[{}] 预加载 {} 模块异常", launcher.getPlayerId(), mould, e);
        }
    }

    protected <C> C updateContext(FeatureLauncher launcher, Collection<? extends Mould> moulds, C context) {
        for (Mould mouldType : moulds) {
            try {
                if (mouldType.isValid() && launcher.isMouldOpened(mouldType)) {
                    BaseMouldHandler<Mould, C> mould = mould(mouldType);
                    doUpdateContext(mould, launcher, context);
                }
            } catch (Throwable e) {
                LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", launcher.getPlayerId(), mouldType, e);
            }
        }
        return context;
    }

    public <C> C loadContext(C context, FeatureLauncher launcher, Mould mouldType) {
        if (!mouldType.isValid()) {
            return context;
        }
        BaseMouldHandler<Mould, C> mould = mould(mouldType);
        doUpdateContext(mould, launcher, context);
        return context;
    }

    public <C> C loadContext(FeatureLauncher launcher, C context) {
        for (Mould mouldType : launcher.getOpenedMoulds()) {
            if (!mouldType.isValid()) {
                continue;
            }
            BaseMouldHandler<Mould, C> mould = mould(mouldType);
            doUpdateContext(mould, launcher, context);
        }
        return context;
    }

    private <C> BaseMouldHandler<Mould, C> mould(Mould mouldType) {
        return as(this.handlerMap.get(mouldType));
    }

    private <C> void doUpdateContext(BaseMouldHandler<Mould, C> handler, FeatureLauncher launcher, C context) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("读取 {} 玩家 {} 模块获取重新登录信息", launcher.getPlayerId(), handler.getMould());
            }
            handler.loadContext(launcher, context);
        } catch (Throwable e) {
            LOGGER.error("玩家[{}] 读取 {} 模块获取重新登录信息失败", launcher.getPlayerId(), handler.getMould(), e);
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
        Collection<MouldHandler> collection = as(this.applicationContext.getBeansOfType(MouldHandler.class).values());
        List<MouldHandler> mouldList = new ArrayList<>(collection);
        for (MouldHandler mould : mouldList) {
            this.handlerMap.put(mould.getMould(), mould);
        }
        for (Mould mould : Moulds.all()) {
            if (!this.handlerMap.containsKey(mould)) {
                this.handlerMap.put(mould, new DefaultMouldHandler(mould));
            }
        }
    }

}
