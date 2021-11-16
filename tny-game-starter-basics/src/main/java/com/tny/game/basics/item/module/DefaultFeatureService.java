package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/15 10:03 下午
 */
public class DefaultFeatureService extends FeatureService {

	public DefaultFeatureService(
			FeatureLauncherManager featureLauncherManager,
			ModulerService moduleService,
			FeatureModelManager<? extends FeatureModel> featureModelManager) {
		super(featureLauncherManager, moduleService, featureModelManager);
	}

}
