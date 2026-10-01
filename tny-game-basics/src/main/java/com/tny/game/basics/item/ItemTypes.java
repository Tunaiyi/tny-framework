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

import com.tny.game.basics.utlis.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.tny.game.basics.item.ItemType.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class ItemTypes extends ClassImporter {

    private static final EnumerableSymbol<ItemType, String> ALIAS_HEAD_SYMBOL = EnumerableSymbol.symbolOf(
            ItemType.class, "aliasHead", ItemType::getAliasHead);

    protected static EnumeratorHolder<ItemType> holder = new EnumeratorHolder<ItemType>() {

        @Override
        protected void postRegister(ItemType object) {
            putAndCheckSymbol(ALIAS_HEAD_SYMBOL, object);
        }
    };

    private ItemTypes() {
    }

    static void register(ItemType value) {
        EnumRegistrySupport.register(holder, value);
    }

    //    自有扩展保留（D5）：别名前缀查找为 ItemTypes 独有，heads[0] 空串越界现状在册禁修
    public static <T extends ItemType> T ofAlias(String alias) {
        String[] heads = StringUtils.split(alias, '$');
        return holder.checkBySymbol(ALIAS_HEAD_SYMBOL, heads[0], "获取 别名前缀 {} 的 ItemType 不存在", heads[0]);
    }

    public static <T extends ItemType> T check(String key) {
        return EnumRegistrySupport.check(holder, key, "ItemType");
    }

    public static <T extends ItemType> T check(int id) {
        return EnumRegistrySupport.check(holder, id, "ItemType");
    }

    public static <T extends ItemType> T of(int id) {
        return EnumRegistrySupport.of(holder, id);
    }

    public static <T extends ItemType> T of(String key) {
        return EnumRegistrySupport.of(holder, key);
    }

    public static <T extends ItemType> Optional<T> option(int id) {
        return EnumRegistrySupport.option(holder, id);
    }

    public static <T extends ItemType> Optional<T> option(String key) {
        return EnumRegistrySupport.option(holder, key);
    }

    public static <T extends ItemType> Collection<T> all() {
        return EnumRegistrySupport.all(holder);
    }

    public static Enumerator<ItemType> enumerator() {
        return EnumRegistrySupport.enumerator(holder);
    }

    public static <T extends ItemType> T ofModelId(int modelId) {
        int typeId = modelId / ID_TAIL_SIZE * ID_TAIL_SIZE;
        return of(typeId);
    }

    public static <T extends ItemType> T ofItemId(long id) {
        if (id < 10000L) {
            return ofModelId((int) id);
        }
        if (id < 100000L) {
            return ofModelId((int) (id / 10L));
        }
        if (id < 1000000L) {
            return ofModelId((int) (id / 100L));
        }
        if (id < 10000000L) {
            return ofModelId((int) (id / 1000L));
        }
        if (id < 100000000L) {
            return ofModelId((int) (id / 10000L));
        }
        if (id < 1000000000L) {
            return ofModelId((int) (id / 100000L));
        }
        if (id < 10000000000L) {
            return ofModelId((int) (id / 1000000L));
        }
        if (id < 100000000000L) {
            return ofModelId((int) (id / 10000000L));
        }
        if (id < 1000000000000L) {
            return ofModelId((int) (id / 100000000L));
        }
        if (id < 10000000000000L) {
            return ofModelId((int) (id / 1000000000L));
        }
        if (id < 100000000000000L) {
            return ofModelId((int) (id / 10000000000L));
        }
        if (id < 1000000000000000L) {
            return ofModelId((int) (id / 100000000000L));
        }
        if (id < 10000000000000000L) {
            return ofModelId((int) (id / 1000000000000L));
        }
        if (id < 100000000000000000L) {
            return ofModelId((int) (id / 10000000000000L));
        }
        return ofModelId((int) (id / 100000000000000L));
    }

}
