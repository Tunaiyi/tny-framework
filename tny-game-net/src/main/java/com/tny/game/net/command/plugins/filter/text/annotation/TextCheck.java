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
