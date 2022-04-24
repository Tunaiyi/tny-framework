package com.tny.game.basics.item.mould;

import com.tny.game.basics.mould.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:03 下午
 */
public class DefaultFeatureService extends FeatureService {

    public DefaultFeatureService(
            FeatureLauncherManager featureLauncherManager,
            MouldService mouldService,
            FeatureModelManager<? extends FeatureModel> featureModelManager) {
        super(featureLauncherManager, mouldService, featureModelManager);
    }

}
