package com.tny.game.basics.configuration;

import com.tny.game.basics.develop.*;
import com.tny.game.basics.item.*;
import com.tny.game.expr.*;
import com.tny.game.expr.mvel.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties({
		DefaultItemModelProperties.class,
})
public class BasicsAutoConfiguration {

	@Bean
	DefaultItemModelManager defaultItemModelManager(DefaultItemModelProperties setting) {
		return new DefaultItemModelManager(setting.getPaths());
	}

	@Bean
	GameExplorer gameExplorer() {
		return new GameExplorer();
	}

	@Bean
	DevelopEnvs developEnvs(Environment environment) {
		DevelopEnvs.init(environment);
		return DevelopEnvs.envs();
	}

	@Bean
	public GameItemModelContext itemModelContext(GameExplorer gameExplorer, Optional<ExprHolderFactoryInitiator> initiatorOpt) {
		ExprHolderFactory exprHolderFactory = new MvelExpressionHolderFactory();
		initiatorOpt.ifPresent(initiator -> initiator.init(exprHolderFactory));
		return new GameItemModelContext(gameExplorer, exprHolderFactory);
	}

	@Bean
	public AnyEntityKeyMakerFactory anyEntityKeyMakerFactory() {
		return new AnyEntityKeyMakerFactory();
	}

	@Bean
	public AnyEntityIdConverterFactory anyEntityIdConverterFactory() {
		return new AnyEntityIdConverterFactory();
	}

}