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

import com.tny.game.basics.item.annotation.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class AnyIdConverter {

    private final boolean single;

    private final String idHeader;

    public AnyIdConverter(EntityScheme scheme) {
        if (scheme.isHasPrefix()) {
            idHeader = scheme.prefix() + ":";
        } else {
            idHeader = "";
        }
        Class<?> entityClass = scheme.getEntityClass();
        SingleEntity single = entityClass.getAnnotation(SingleEntity.class);
        if (single != null) {
            this.single = single.value();
        } else {
            //			if (StuffOwner.class.isAssignableFrom(entityClass)) {
            //				this.single = true;
            //			} else if (Stuff.class.isAssignableFrom(entityClass)) {
            //				this.single = false;
            //			} else if (Item.class.isAssignableFrom(entityClass)) {
            //				this.single = true;
            //			} else {
            this.single = false;
            //			}
        }

    }

    public String anyId2Key(AnyId key) {
        if (single) {
            return idHeader + AnyId.formatId(key.getPlayerId(), 0);
        }
        return idHeader + key.toUuid();
    }

    public String any2Key(Any object) {
        if (single) {
            return idHeader + AnyId.formatId(object.getPlayerId(), 0);
        }
        return idHeader + AnyId.formatId(object);
    }

    public AnyId any2AnyId(Any object) {
        if (single) {
            return AnyId.idOf(object);
        }
        return AnyId.idOf(object.getPlayerId(), object.getId());
    }

}
