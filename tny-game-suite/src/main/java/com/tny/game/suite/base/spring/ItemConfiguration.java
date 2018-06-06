package com.tny.game.suite.base.spring;

import com.tny.game.expr.ExprHolderFactory;
import com.tny.game.expr.mvel.MvelExpressionHolderFactory;
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
    private ExprHolderFactoryIniter exprHolderFactoryIniter;

    @Bean
    public GameItemModelContext itemModelContext() {
        ExprHolderFactory exprHolderFactory = new MvelExpressionHolderFactory();
        if (exprHolderFactoryIniter != null)
            exprHolderFactoryIniter.init(exprHolderFactory);
        return new GameItemModelContext(explorer, exprHolderFactory);
    }


}