package com.tny.game.basics.configuration;

import com.tny.game.basics.item.mould.*;
import com.tny.game.basics.mould.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import static com.tny.game.basics.configuration.BasicsPropertiesConstants.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
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
	@ConditionalOnMissingBean(MouldService.class)
	public MouldService moduleService(FeatureLauncherManager featureLauncherManager) {
		return new DefaultMouldService(featureLauncherManager);
	}

	@Bean
	@ConditionalOnBean({FeatureLauncherManager.class})
	@ConditionalOnMissingBean(FeatureService.class)
	public FeatureService featureService(
			FeatureLauncherManager featureLauncherManager,
			MouldService moduleService,
			FeatureModelManager<? extends FeatureModel> featureModelManager) {
		return new DefaultFeatureService(featureLauncherManager, moduleService, featureModelManager);
	}

}
