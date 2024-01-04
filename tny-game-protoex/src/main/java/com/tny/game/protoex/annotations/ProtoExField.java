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
 * 标记自定义ProtoEx类型字段
 * <p>
 * 若字段为Repeat(Collection)类型时还需要标记 @ProtoExElement
 * 若字段为Map类型时还需要标记 @ProtoExEntry
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExField {

    /**
     * ProtoEx类型字段ID 取值方位 1 - 536870911
     *
     * @return
     */
    int value();

    /**
     * 配置字段编码方式
     *
     * @return
     */
    ProtoExConf conf() default @ProtoExConf;

}
