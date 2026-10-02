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
