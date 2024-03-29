/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.mould;

import com.tny.game.basics.mould.*;
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

    private final FeatureLauncherManager featureLauncherManager;

    private final MouldService mouldService;

    private final FeatureModelManager<? extends FeatureModel> featureModelManager;

    private ApplicationContext applicationContext;

    private final Map<Feature, FeatureHandler> handlerMap = new HashMap<>();

    public FeatureService(
            FeatureLauncherManager featureLauncherManager,
            MouldService mouldService,
            FeatureModelManager<? extends FeatureModel> featureModelManager) {
        this.featureLauncherManager = featureLauncherManager;
        this.mouldService = mouldService;
        this.featureModelManager = featureModelManager;
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
                    if (this.mouldService.openMould(launcher, feature.dependMoulds())) {
                        launcher.openFeature(model, handler);
                    }
                    if (this.LOGGER.isInfoEnabled()) {
                        long time = RunChecker.end(model.getFeature()).costMillisTime();
                        this.LOGGER.debug("{} 玩家开启 {} 功能成功! 耗时 {} ms", launcher.getPlayerId(), feature, time);
                    }
                }
            } catch (Throwable e) {
                this.LOGGER.error("玩家[{}] 开启 {} 功能失败", launcher.getPlayerId(), feature, e);
            }
        }
    }

    private void doLoadFeature(final GameFeatureLauncher launcher) {
        HashSet<Mould> mouldSet = new HashSet<>();
        for (Feature feature : launcher.getOpenedFeatures()) {
            if (launcher.isFeatureOpened(feature)) {
                for (Mould mould : feature.dependMoulds()) {
                    if (!mouldSet.add(mould)) {
                        continue;
                    }
                    this.mouldService.doLoadMould(launcher, mould);
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

    public <T> T loadContext(GameFeatureLauncher launcher, T context) {
        this.mouldService.loadContext(launcher, context);
        return context;
    }

    public <T> T loadContext(GameFeatureLauncher launcher, T context, Collection<? extends Mould> moulds) {
        this.mouldService.updateContext(launcher, moulds, context);
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
                FeatureHandler handler = new DefaultFeatureHandler(feature);
                this.handlerMap.put(feature, handler);
            }
        }
    }

}
