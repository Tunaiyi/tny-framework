package com.tny.game.suite.base.spring;

import com.tny.game.expr.*;
import com.tny.game.expr.mvel.*;
import com.tny.game.suite.base.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import javax.annotation.Resource;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 2018/6/6.
 */
@Configuration
@Profile({ITEM, GAME})
public class ItemConfiguration {

    @Resource
    private GameExplorer explorer;

    @Autowired(required = false)
    private ExprHolderFactoryInitiator exprHolderFactoryInitiator;

    @Bean
    public GameItemModelContext itemModelContext() {
        ExprHolderFactory exprHolderFactory = new MvelExpressionHolderFactory();
        if (this.exprHolderFactoryInitiator != null)
            this.exprHolderFactoryInitiator.init(exprHolderFactory);
        return new GameItemModelContext(this.explorer, exprHolderFactory);
    }


}
