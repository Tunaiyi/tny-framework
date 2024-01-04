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
@Repeatable(AfterPlugins.class)
@Documented
public @interface AfterPlugin {

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
