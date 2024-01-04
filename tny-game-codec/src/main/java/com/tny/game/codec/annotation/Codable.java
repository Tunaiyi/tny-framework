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

package com.tny.game.codec.annotation;

import com.tny.game.codec.*;

import java.lang.annotation.*;

/**
 * 注册 Json2Redis 持久化类
 * <p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Codable {

    /**
     * aa/bb 方式 mimeType 优先
     *
     * @return 等同 mimeType
     */
    String value();

    /**
     * aa/bb 方式
     *
     * @return 格式化
     */
    String mimeType() default MimeTypeAide.NONE;

}
