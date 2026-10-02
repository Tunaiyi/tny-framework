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

package com.tny.game.net.annotation;

import com.tny.game.net.command.plugins.*;

import java.lang.annotation.*;

/**
 * @author KGTny
 * @ClassName: Plugin
 * @Description: 控制器插件註解
 * @date 2011-9-22 下午5:00:55
 * <p>
 * 控制器插件註解
 * <p>
 * 被標記的控制器會在調用方法的前後調用所標識的插件, METHOD的優先級高於TYPE<br>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(BeforePlugins.class)
@Documented
public @interface BeforePlugin {

    /**
     * 插件类型数组
     * <p>
     * <p>
     * 在调用Controller的方法后调用execute;<br>
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    Class<? extends CommandPlugin> value();

    /**
     * 插件参数, 已@开始为公式
     *
     * @return 获取插件参数
     */
    String attribute() default "@null";

}
