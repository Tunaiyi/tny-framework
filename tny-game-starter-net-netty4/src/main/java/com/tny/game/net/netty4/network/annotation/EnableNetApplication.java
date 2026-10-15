/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.netty4.network.annotation;

import com.tny.game.net.netty4.configuration.*;
import com.tny.game.net.netty4.configuration.processor.*;
import com.tny.game.net.netty4.network.guide.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-10-31 14:15
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        NetAutoConfiguration.class,
        ImportNetBootstrapDefinitionRegistrar.class,
        ImportCommandExecutorFactoryBeanDefinitionRegistrar.class,
})
@EnableConfigurationProperties({
        SpringBootNetBootstrapProperties.class
})
public @interface EnableNetApplication {

}
