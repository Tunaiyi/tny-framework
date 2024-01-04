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

package com.tny.game.basics.item;

import com.tny.game.data.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class AnyEntityIdConverter implements EntityIdConverter<AnyId, Any, String> {

    private final AnyIdConverter converter;

    public AnyEntityIdConverter(EntityScheme scheme) {
        converter = new AnyIdConverter(scheme);
    }

    @Override
    public String keyToId(AnyId key) {
        return converter.anyId2Key(key);
    }

    @Override
    public String entityToId(Any object) {
        return converter.any2Key(object);
    }

}
