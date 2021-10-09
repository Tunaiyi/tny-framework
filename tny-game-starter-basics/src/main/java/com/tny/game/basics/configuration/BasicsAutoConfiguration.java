package com.tny.game.basics.configuration;

import com.tny.game.basics.item.*;
import com.tny.game.expr.*;
import com.tny.game.expr.mvel.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.Optional;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@EnableConfigurationProperties({
		DefaultItemModelProperties.class
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
	public GameItemModelContext itemModelContext(GameExplorer gameExplorer, Optional<ExprHolderFactoryInitiator> initiatorOpt) {
		ExprHolderFactory exprHolderFactory = new MvelExpressionHolderFactory();
		initiatorOpt.ifPresent(initiator -> initiator.init(exprHolderFactory));
		return new GameItemModelContext(gameExplorer, exprHolderFactory);
	}

}
