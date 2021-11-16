package com.tny.game.basics.configuration;

import com.tny.game.basics.item.module.*;
import com.tny.game.basics.module.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties(BasicsFeatureProperties.class)
@ConditionalOnProperty(name = BASICS_FEATURE_MANAGER_ENABLE, havingValue = "true")
public class BasicsFeatureConfiguration {

	@Bean
	@ConditionalOnMissingBean(FeatureModelManager.class)
	public FeatureModelManager<?> featureModelManager(BasicsFeatureProperties properties) {
		return new FeatureModelManager<>(properties.getPath(), properties.getModelClass());
	}

	@Bean
	@ConditionalOnBean({FeatureLauncherManager.class})
	@ConditionalOnMissingBean(ModulerService.class)
	public ModulerService moduleService(FeatureLauncherManager featureLauncherManager) {
		return new DefaultModulerService(featureLauncherManager);
	}

	@Bean
	@ConditionalOnBean({FeatureLauncherManager.class})
	@ConditionalOnMissingBean(FeatureService.class)
	public FeatureService featureService(
			FeatureLauncherManager featureLauncherManager,
			ModulerService moduleService,
			FeatureModelManager<? extends FeatureModel> featureModelManager) {
		return new DefaultFeatureService(featureLauncherManager, moduleService, featureModelManager);
	}

}
