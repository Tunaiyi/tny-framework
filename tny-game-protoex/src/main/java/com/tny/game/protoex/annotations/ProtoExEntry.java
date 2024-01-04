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

package com.tny.game.protoex.annotations;

import java.lang.annotation.*;

/**
 * ProtoEx Map字段中Key和Value编码方式
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExEntry {

    /**
     * key编码方式配置 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf key() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

    /**
     * value编码方式配置 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf value() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

}
