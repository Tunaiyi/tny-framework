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

package com.tny.game.boot.event.annotation;

import com.tny.game.common.event.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author KGTny
 * @ClassName: Listener
 * @Description: 监听器配置注解
 * @date 2011-9-21 ????11:55:26
 * <p>
 * 监听器配置注解
 * <p>
 * 将监听器绑定到与dispatcherClasses对应的Dispatcher<br>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
@Documented
public @interface EventHandler {

    GlobalEventListener listener() default @GlobalEventListener;

}
