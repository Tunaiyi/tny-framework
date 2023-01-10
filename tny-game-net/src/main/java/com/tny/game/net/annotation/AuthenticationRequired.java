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

import com.tny.game.net.command.auth.*;

import java.lang.annotation.*;

/**
 * Contoller需要认证
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthenticationRequired {

    /**
     * 用户组名称
     * <p>
     * <p>
     * 当userType System<br>
     */
    String[] value() default {};

    /**
     * @return 验证器
     */
    Class<? extends AuthenticationValidator> validator() default AuthenticationValidator.class;

    boolean enable() default true;

}