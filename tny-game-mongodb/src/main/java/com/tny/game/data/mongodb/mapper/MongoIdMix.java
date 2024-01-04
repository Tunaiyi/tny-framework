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

package com.tny.game.data.mongodb.mapper;

import com.fasterxml.jackson.annotation.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 12:09
 */
public abstract class MongoIdMix<O, I> {

    @JsonGetter("_id")
    public abstract O getId();

    @JsonSetter("_id")
    public abstract O setId(I id);

}