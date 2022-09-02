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
package com.tny.game.basics.configuration;

import com.tny.game.basics.develop.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.common.*;
import com.tny.game.basics.item.loader.*;
import com.tny.game.basics.item.loader.xstream.*;
import com.tny.game.basics.item.mapper.*;
import com.tny.game.basics.persistent.*;
import com.tny.game.basics.transaction.*;
import com.tny.game.expr.*;
import com.tny.game.expr.mvel.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        DefaultItemModelProperties.class,
})
public class BasicsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DefaultItemModelManager.class)
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
    public ItemModelContext itemModelContext(GameExplorer gameExplorer, Optional<ExprHolderFactoryInitiator> initiatorOpt) {
        ExprHolderFactory exprHolderFactory = new MvelExpressionHolderFactory(true);
        initiatorOpt.ifPresent(initiator -> initiator.init(exprHolderFactory));
        return new GameItemModelContext(gameExplorer, exprHolderFactory);
    }

    @Bean
    @ConditionalOnMissingBean(ModelLoaderFactory.class)
    public ModelLoaderFactory modelLoaderFactory(ItemModelContext context) {
        return new XStreamModelLoaderFactory(context.getExprHolderFactory());
    }

    @Bean
    ItemModelJsonSerializer itemModelJsonSerializer() {
        return new ItemModelJsonSerializer();
    }

    @Bean
    ItemModelJsonDeserializer itemModelJsonDeserializer(GameExplorer gameExplorer) {
        return new ItemModelJsonDeserializer(gameExplorer);
    }

    @Bean
    BasicsObjectMapperCustomizer gameBasicsObjectMapperCustomizer(
            ItemModelJsonSerializer serializer, ItemModelJsonDeserializer deserializer) {
        return new BasicsObjectMapperCustomizer(serializer, deserializer);
    }

    @Bean
    ItemJsonDeserializer itemJsonDeserializer(GameExplorer gameExplorer) {
        return new ItemJsonDeserializer(gameExplorer);
    }

    @Bean
    ItemJsonSerializer itemJsonSerializer() {
        return new ItemJsonSerializer();
    }

    @Bean
    public AnyCacheKeyMakerFactory anyCacheKeyMakerFactory() {
        return new AnyCacheKeyMakerFactory();
    }

    @Bean
    public AnyEntityIdConverterFactory anyEntityIdConverterFactory() {
        return new AnyEntityIdConverterFactory();
    }

    @Bean
    public AutoManageAdvice autoManageAdvice(GameExplorer gameExplorer) {
        return AutoManageAdvice.init(gameExplorer);
    }

    @Bean
    public MessageCommandTransactionHandler messageCommandTransactionHandler() {
        return new MessageCommandTransactionHandler();
    }

}
