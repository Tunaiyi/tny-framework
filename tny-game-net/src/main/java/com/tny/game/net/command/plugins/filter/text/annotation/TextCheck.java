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

package com.tny.game.net.command.plugins.filter.text.annotation;

import java.lang.annotation.*;

/**
 * 名字限制 过滤字
 *
 * @author KunYang
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TextCheck {

    int lowLength() default 2;

    int highLength() default 5;

    /**
     * 默认错误 code
     *
     * @return 207
     */
    int illegalCode() default 0;

    /**
     * 长度错误 code
     *
     * @return 0
     */
    int lengthIllegalCode() default 0;

    /**
     * 内容错误 code
     *
     * @return 0
     */
    int contentIllegalCode() default 0;

}
